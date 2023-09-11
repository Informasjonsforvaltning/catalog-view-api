package no.digdir.catalog_view_api.service

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class PermissionService {

    fun hasOrgReadPermission(jwt: Jwt, catalogId: String): Boolean {
        val consumer = jwt.claims["consumer"] as? Map<*, *>?
        val consumerID = consumer?.get("ID") as? String?

        return when {
            !catalogIdIsValid(catalogId) -> false
            consumerID == null -> false
            consumerID.contains(catalogId) -> true
            else -> false
        }
    }

    private fun catalogIdIsValid(catalogId: String) =
        catalogId.length >= 9

}
