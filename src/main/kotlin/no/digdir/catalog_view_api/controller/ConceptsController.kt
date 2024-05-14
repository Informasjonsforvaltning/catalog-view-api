package no.digdir.catalog_view_api.controller

import no.digdir.catalog_view_api.model.Concept
import no.digdir.catalog_view_api.service.ConceptsViewService
import no.digdir.catalog_view_api.service.PermissionService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
@RequestMapping(value = ["/catalogs/{catalog}/concepts"], produces = ["application/json"])
class ConceptsController(
    private val conceptsViewService: ConceptsViewService,
    private val permissionService: PermissionService
) {

    @GetMapping
    fun getConcepts(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable catalog: String,
        @RequestParam(value = "domainCodes", required = false) domainCodes: List<String>?,
        @RequestParam(value = "changedAfter", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) changedAfter: Instant?
    ): ResponseEntity<List<Concept>> =
        if (permissionService.hasOrgReadPermission(jwt, catalog)) {
            ResponseEntity(conceptsViewService.getConcepts(catalog, changedAfter, domainCodes), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }

    @GetMapping("/{id}")
    fun getConceptById(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable catalog: String,
        @PathVariable id: String
    ): ResponseEntity<Concept> =
        if (permissionService.hasOrgReadPermission(jwt, catalog)) {
            ResponseEntity(conceptsViewService.getConceptById(catalog, id), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }

}
