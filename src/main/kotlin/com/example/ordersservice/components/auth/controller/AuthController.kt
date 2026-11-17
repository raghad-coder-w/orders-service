package com.example.ordersservice.components.auth.controller

import com.example.ordersservice.components.auth.dto.LoginRequest
import com.example.ordersservice.components.auth.service.JwtService
import com.example.ordersservice.components.user.dto.RegisterRequest
import com.example.ordersservice.components.user.entity.User
import com.example.ordersservice.components.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.AuthenticationException
@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    fun register( @Valid @RequestBody request: RegisterRequest ): ResponseEntity<Void> {

        userService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()

    }

    @PostMapping("/login")
    fun login( @RequestBody request: LoginRequest ): ResponseEntity<String> {

        /* Make the email and password to be one Object so authenticate() can accept it, by
        the UsernamePasswordAuthenticationToken. */
        val authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(
            request.email,
            request.password
        )
        return try{
            val authentication = authenticationManager.authenticate(authenticationRequest)

            val email = authentication.name

            // The Elvis Operator '?:' = if the value on its left is "null", use what's on the right.
            val authority = authentication.authorities.first().authority?:
            throw IllegalStateException("User role is missing")
            val role = authority.removePrefix("ROLE_")

            val token = jwtService.generateToken(email, role)
            ResponseEntity.ok(token)
        }
        catch (exception: AuthenticationException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password")
        }

    }

}