package no.digdir.catalog_view_api.utils

import no.digdir.catalog_view_api.model.Concept
import no.digdir.catalog_view_api.model.InternalConcept
import no.digdir.catalog_view_api.model.SemVer
import no.digdir.catalog_view_api.model.Virksomhet
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap

const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"
const val MONGO_PORT = 27017

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD
)

val EMPTY_CONCEPT = Concept(
    id = "concept-id",
    idOfOriginalVersion = "original-id",
    version = SemVer(1, 0, 0),
    publisher = "123456789",
    status = "http://publications.europa.eu/resource/authority/concept-status/CURRENT",
    preferredTerm = null,
    definition = null,
    note = null,
    example = null,
    dateValidFrom = null,
    dateValidThrough = null
)

val EMPTY_INTERNAL_CONCEPT = InternalConcept(
    id = "concept-id",
    originaltBegrep = "original-id",
    versjonsnr = SemVer(1, 0, 0),
    revisjonAv = null,
    statusURI = "http://publications.europa.eu/resource/authority/concept-status/CURRENT",
    erPublisert = true,
    anbefaltTerm = null,
    tillattTerm = null,
    frarådetTerm = null,
    definisjon = null,
    folkeligForklaring = null,
    rettsligForklaring = null,
    merknad = null,
    ansvarligVirksomhet = Virksomhet(id = "123456789"),
    eksempel = null,
    fagområde = null,
    fagområdeKoder = null,
    omfang = null,
    kontaktpunkt = null,
    gyldigFom = null,
    gyldigTom = null,
    endringslogelement = null,
    opprettet = null,
    opprettetAv = null,
    seOgså = null,
    erstattesAv = null,
    assignedUser = null,
    abbreviatedLabel = null,
    begrepsRelasjon = null,
    interneFelt = null
)
