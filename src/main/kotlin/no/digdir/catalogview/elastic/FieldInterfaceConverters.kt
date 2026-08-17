package no.digdir.catalogview.elastic

import no.digdir.catalogview.model.FieldInterface
import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import tools.jackson.core.type.TypeReference
import tools.jackson.module.kotlin.jacksonObjectMapper

@ReadingConverter
class FieldInterfaceReadConverter : Converter<Map<String, Any>, FieldInterface> {
    private val objectMapper = jacksonObjectMapper()

    override fun convert(source: Map<String, Any>): FieldInterface = objectMapper.convertValue(source, FieldInterface::class.java)
}

@WritingConverter
class FieldInterfaceWriteConverter : Converter<FieldInterface, Map<String, Any>> {
    private val objectMapper = jacksonObjectMapper()

    override fun convert(source: FieldInterface): Map<String, Any> =
        objectMapper.convertValue(source, object : TypeReference<Map<String, Any>>() {})
}
