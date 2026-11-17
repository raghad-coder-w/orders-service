package com.example.ordersservice.components.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import java.util.Base64
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Configuration(proxyBeanMethods = false)
class JwtConfig (
    // To make Spring read the value of the specified property, that stored in "encodedSecret".
    // Using the '\' sign before ${...} so Kotlin passes the property name to Spring without interpreting it.
    @Value("\${app.jwt.secret}") private val encodedSecret: String
) {

    // Preparing the JWT secret key.
    @Bean
    fun jwtSecretKey(): SecretKey {
        // Decoding it from Base64 to Bytes.
        val keyBytes = Base64.getDecoder().decode(encodedSecret)

        require(keyBytes.size >= 32) {
            "JWT secret must be at least 32 bytes."
        }

        return SecretKeySpec(keyBytes, "HmacSHA256")
    }

    // Setting up the creation of the JWT.
    @Bean
    fun jwtEncoder(secretKey: SecretKey): JwtEncoder {
        return NimbusJwtEncoder.withSecretKey(secretKey).build()
    }

    @Bean
    fun jwtDecoder(secretKey: SecretKey): JwtDecoder {
        return NimbusJwtDecoder.withSecretKey(secretKey).build()
    }

}