package no.digdir.catalog_view_api.unit

import no.digdir.catalog_view_api.elastic.ConceptViewRepository
import no.digdir.catalog_view_api.elastic.ElasticUpdater
import no.digdir.catalog_view_api.model.Concept
import no.digdir.catalog_view_api.service.ConceptsService
import no.digdir.catalog_view_api.utils.EMPTY_CONCEPT
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.data.elasticsearch.core.SearchHitsIterator
import org.springframework.data.elasticsearch.core.TotalHitsRelation
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates
import org.springframework.data.elasticsearch.core.query.Query
import java.time.Duration

@Tag("unit")
class ElasticUpdaterTest {

    private lateinit var conceptsService: ConceptsService
    private lateinit var conceptViewRepository: ConceptViewRepository
    private lateinit var elasticsearchOperations: ElasticsearchOperations
    private lateinit var updater: ElasticUpdater

    @BeforeEach
    fun setUp() {
        conceptsService = mock()
        conceptViewRepository = mock()
        elasticsearchOperations = mock()
        updater = ElasticUpdater(conceptsService, conceptViewRepository, elasticsearchOperations)
    }

    private fun conceptWithId(id: String) = EMPTY_CONCEPT.copy(id = id)

    private fun searchHitOf(id: String): SearchHit<Concept> =
        SearchHit(
            "catalog-view-concepts", id, null, 0f,
            emptyArray(), null, null, null, null, null,
            conceptWithId(id)
        )

    private fun stubSourceConcepts(vararg ids: String) {
        whenever(conceptsService.getAndMapAllConcepts())
            .thenReturn(ids.map { conceptWithId(it) })
    }

    private fun stubIndexedIds(vararg ids: String) {
        val iterator = FakeSearchHitsIterator(ids.map { searchHitOf(it) })
        whenever(
            elasticsearchOperations.searchForStream(
                any<Query>(), eq(Concept::class.java), any<IndexCoordinates>()
            )
        ).thenReturn(iterator)
    }

    @Test
    fun `Saves all source concepts and deletes stale ones`() {
        stubSourceConcepts("a", "b")
        stubIndexedIds("a", "c")

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository).deleteAllById(listOf("c"))
        verify(conceptViewRepository).saveAll(listOf(conceptWithId("a"), conceptWithId("b")))
    }

    @Test
    fun `Does not delete when there are no stale documents`() {
        stubSourceConcepts("a", "b")
        stubIndexedIds("a", "b")

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository, never()).deleteAllById(any())
        verify(conceptViewRepository).saveAll(listOf(conceptWithId("a"), conceptWithId("b")))
    }

    @Test
    fun `Deletes all indexed documents when source is empty`() {
        stubSourceConcepts()
        stubIndexedIds("a", "b")

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository).deleteAllById(listOf("a", "b"))
        verify(conceptViewRepository, never()).saveAll(any<List<Concept>>())
    }

    @Test
    fun `Handles empty index on first run`() {
        stubSourceConcepts("a", "b")
        stubIndexedIds()

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository, never()).deleteAllById(any())
        verify(conceptViewRepository).saveAll(listOf(conceptWithId("a"), conceptWithId("b")))
    }

    @Test
    fun `Returns early when source fetch fails`() {
        whenever(conceptsService.getAndMapAllConcepts()).thenThrow(RuntimeException("mongo down"))

        updater.updateConceptsViewIndex()

        verify(elasticsearchOperations, never())
            .searchForStream(any<Query>(), eq(Concept::class.java), any<IndexCoordinates>())
        verify(conceptViewRepository, never()).saveAll(any<List<Concept>>())
        verify(conceptViewRepository, never()).deleteAllById(any())
    }

    @Test
    fun `Returns early when index stream fails`() {
        stubSourceConcepts("a")
        whenever(
            elasticsearchOperations.searchForStream(
                any<Query>(), eq(Concept::class.java), any<IndexCoordinates>()
            )
        ).thenThrow(RuntimeException("elastic down"))

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository, never()).saveAll(any<List<Concept>>())
        verify(conceptViewRepository, never()).deleteAllById(any())
    }

    @Test
    fun `Continues saving when a delete batch fails`() {
        stubSourceConcepts("a")
        stubIndexedIds("a", "stale")

        whenever(conceptViewRepository.deleteAllById(any()))
            .thenThrow(RuntimeException("delete failed"))

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository).saveAll(listOf(conceptWithId("a")))
    }

    @Test
    fun `Continues with remaining batches when one save batch fails`() {
        val concepts = (1..3).map { conceptWithId("id-$it") }
        whenever(conceptsService.getAndMapAllConcepts()).thenReturn(concepts)
        stubIndexedIds()

        var callCount = 0
        whenever(conceptViewRepository.saveAll(any<List<Concept>>())).doAnswer {
            callCount++
            if (callCount == 1) throw RuntimeException("first batch failed")
            @Suppress("UNCHECKED_CAST")
            it.arguments[0] as List<Concept>
        }

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository).saveAll(concepts)
    }

    @Test
    fun `Saves in multiple batches when concepts exceed batch size`() {
        val concepts = (1..502).map { conceptWithId("id-$it") }
        whenever(conceptsService.getAndMapAllConcepts()).thenReturn(concepts)
        stubIndexedIds()

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository, times(2)).saveAll(any<List<Concept>>())
        verify(conceptViewRepository).saveAll(concepts.take(500))
        verify(conceptViewRepository).saveAll(concepts.drop(500))
    }

    @Test
    fun `Deletes in multiple batches when stale IDs exceed batch size`() {
        stubSourceConcepts()
        stubIndexedIds(*(1..502).map { "stale-$it" }.toTypedArray())

        updater.updateConceptsViewIndex()

        verify(conceptViewRepository, times(2)).deleteAllById(any())
    }

    @Test
    fun `Closes the search stream even when iteration fails`() {
        stubSourceConcepts("a")

        val mockIterator = mock<SearchHitsIterator<Concept>>()
        whenever(mockIterator.hasNext()).thenThrow(RuntimeException("stream error"))
        whenever(
            elasticsearchOperations.searchForStream(
                any<Query>(), eq(Concept::class.java), any<IndexCoordinates>()
            )
        ).thenReturn(mockIterator)

        updater.updateConceptsViewIndex()

        verify(mockIterator).close()
        verify(conceptViewRepository, never()).saveAll(any<List<Concept>>())
    }

    private class FakeSearchHitsIterator(
        private val hits: List<SearchHit<Concept>>
    ) : SearchHitsIterator<Concept> {
        private val delegate = hits.iterator()
        override fun hasNext() = delegate.hasNext()
        override fun next() = delegate.next()
        override fun remove() = throw UnsupportedOperationException()
        override fun close() {}
        override fun getAggregations() = null
        override fun getMaxScore() = 0f
        override fun getExecutionDuration(): Duration = Duration.ZERO
        override fun getTotalHits() = hits.size.toLong()
        override fun getTotalHitsRelation() = TotalHitsRelation.EQUAL_TO
    }
}
