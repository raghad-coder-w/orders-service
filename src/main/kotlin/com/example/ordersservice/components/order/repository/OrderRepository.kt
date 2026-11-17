package com.example.ordersservice.components.order.repository

import com.example.ordersservice.components.order.entity.Order
import org.springframework.data.jpa.repository.JpaRepository

interface OrderRepository : JpaRepository<Order, Long> {

    fun findAllByUserId(userId: Long): List<Order>

    // Find a specific order only if it belongs to the given user.
    fun findByIdAndUserId(id: Long, userId: Long): Order?

}