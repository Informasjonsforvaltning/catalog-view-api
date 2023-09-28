package no.digdir.catalog_view_api.controller

import no.digdir.catalog_view_api.model.Concept
import no.digdir.catalog_view_api.service.ConceptsService
import no.digdir.catalog_view_api.service.PermissionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/{catalog}/concepts"], produces = ["application/json"])
class ConceptsController(
    private val conceptsService: ConceptsService,
    private val permissionService: PermissionService
) {

    @GetMapping
    fun getConcepts(
        @AuthenticationPrincipal jwt: Jwt,
        @PathVariable catalog: String
    ): ResponseEntity<List<Concept>> =
        if (permissionService.hasOrgReadPermission(jwt, catalog)) {
            ResponseEntity(conceptsService.getConcepts(catalog), HttpStatus.OK)
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
            ResponseEntity(conceptsService.getConceptById(catalog, id), HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }

}