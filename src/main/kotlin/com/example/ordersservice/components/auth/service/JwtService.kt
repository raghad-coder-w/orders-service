package com.example.ordersservice.components.auth.service

import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class JwtService (
    private val jwtEncoder: JwtEncoder
) {

    fun generateToken(email: String, role: String): String {
        val now = Instant.now()

        // Set up the information that will be in the JWT Payload.
        val claims = JwtClaimsSet.builder()
            .subject(email)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(1800))
            .claim("role", role)
            .build()

        // Set up the JWT Header.
        val header = JwsHeader.with(MacAlgorithm.HS256).build()

        // Creating the JWT for the user.
        return jwtEncoder.encode(JwtEncoderParameters.from
            (header, claims)).tokenValue
    }

}