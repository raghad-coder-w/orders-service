package com.example.ordersservice.components.user.entity

import com.example.ordersservice.components.user.enums.Role
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class User (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false,
        unique = true)
    var email: String,

    @Column(nullable = false)
    var password: String,

    @Enumerated(EnumType.STRING) // Storing the enum class "Role" as a String in the database as "USER".
    @Column(nullable = false)
    var role: Role = Role.USER,

    @Column(name = "created_at", nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
) {}