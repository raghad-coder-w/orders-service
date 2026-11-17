package com.example.ordersservice.components.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    // Security configuration for API access.
    @Bean
    fun securityFilterChain(http: HttpSecurity, jwtAuthenticationConverter:
                JwtAuthenticationConverter): SecurityFilterChain {

        http.csrf { it.disable() }
            .authorizeHttpRequests { auth -> auth.requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            }

        // Do not create HTTP sessions for authentication.
        http.sessionManagement {
            it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        }

        // Enable JWT authentication for incoming Bearer tokens.
        http.oauth2ResourceServer{
            oauth2 -> oauth2.jwt {
                jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
            }
        }

        return http.build()

    }

    @Bean
    fun authenticationManager( userDetailsService: UserDetailsService
                               , passwordEncoder: PasswordEncoder): AuthenticationManager {

        val authenticationProvider = DaoAuthenticationProvider(userDetailsService)
        authenticationProvider.setPasswordEncoder(passwordEncoder)

        // Object from the class that implements the interface.
        return ProviderManager(authenticationProvider)

    }

    // * Here's where the Spring Security use it to create the 'authentication' entity.
    // Converts the user's role from the JWT (e.g. USER) into a Spring Security authority (e.g. ROLE_USER).
    @Bean
    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {

        // Preparing an object to extract the Roles.
        val rolesConverter = JwtGrantedAuthoritiesConverter()
        rolesConverter.setAuthoritiesClaimName("role")
        rolesConverter.setAuthorityPrefix("ROLE_")

        // Create an authentication converter and configure it to use the rolesConverter.
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(rolesConverter)

        return jwtAuthenticationConverter

    }

}