package com.example.ordersservice.components.user.service

import com.example.ordersservice.components.user.dto.RegisterRequest
import com.example.ordersservice.components.user.entity.User
import com.example.ordersservice.components.user.enums.Role
import com.example.ordersservice.components.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequest): User {

        if (userRepository.existsByEmail(request.email)){
            throw IllegalArgumentException("Email is already registered")
        }
        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password)?: throw
            IllegalStateException("Password encoding failed!"), // Use the value to the left of ' ?: ', if it is ' null ' execute what's on the right.
            role = Role.USER
        )
        return userRepository.save(user)

    }
}