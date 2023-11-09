package no.digdir.catalog_view_api.utils.jwk

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.util.*


class JwtToken (private val catalog: String, private val scope: String = "test.scope") {
    private val now = Date()
    private val exp = Date(now.time + 120 * 1000)

    private fun buildToken() : String{
        val claimset = JWTClaimsSet.Builder()
            .expirationTime(exp)
            .issueTime(now)
            .claim("iss", "http://localhost:5050/")
            .claim("scope", scope)
            .claim("client_amr", "private_key_jwt")
            .claim("token_type", "Bearer")
            .claim("client_id", "client-id")
            .claim("jti", "asdf--fdsa")
            .claim("consumer", mapOf(
                Pair("authority", "iso6523-actorid-upis"),
                Pair("ID", "0192:$catalog")))
            .build()

        val signed = SignedJWT(JwkStore.jwtHeader(), claimset)
        signed.sign(JwkStore.signer())

        return signed.serialize()
    }

    override fun toString(): String {
        return buildToken()
    }

}
