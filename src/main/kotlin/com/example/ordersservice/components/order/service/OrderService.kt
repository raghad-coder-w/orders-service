package com.example.ordersservice.components.order.service

import com.example.ordersservice.components.order.dto.CreateOrderRequest
import com.example.ordersservice.components.order.dto.UpdateOrderRequest
import com.example.ordersservice.components.order.dto.UpdateOrderStatusRequest
import com.example.ordersservice.components.order.entity.Order
import com.example.ordersservice.components.order.enums.OrderStatus
import com.example.ordersservice.components.order.kafka.OrderEventPublisher
import com.example.ordersservice.components.order.repository.OrderRepository
import com.example.ordersservice.components.user.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.math.BigDecimal

// Handles order operations and controls access based on the user's role.
@Service
class OrderService (
    private val orderRepository: OrderRepository,
    private val userRepository: UserRepository,
    private val orderEventPublisher: OrderEventPublisher,
    private val orderCacheService: OrderCacheService
) {

    fun getOrders(authentication: Authentication): List<Order> {

        // 'authentication.name' will return the value of JWT subject that's in 'JwtService'.
        val email = authentication.name
        val user = userRepository.findByEmail(email)
            .orElseThrow { IllegalStateException("Authenticated user not found") }

        // 'authentication.authorities' will return the user's authority (e.g. ROLE_ADMIN).
        val isAdmin = authentication.authorities.any {
            it.authority == "ROLE_ADMIN"
        }

        return if (isAdmin) {
            orderRepository.findAll()
        } else {
            orderRepository.findAllByUserId(requireNotNull(user.id))
        }

    }

    fun createOrder(request: CreateOrderRequest, authentication: Authentication): Order {

        // Validate that the total amount is positive.
        if (request.totalAmount <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Total amount must be greater than zero.")
        }

        // Find the authenticated user by email.
        val email = authentication.name
        val user = userRepository.findByEmail(email)
            .orElseThrow { IllegalStateException("Authenticated user not found") }

        //Create an order linked to the authenticated user.
        val order = Order(
            userId = requireNotNull(user.id),
            totalAmount = request.totalAmount
        )

        // Save the order in the database first so MySQL generates the id when creating the Order.
        val savedOrder = orderRepository.save(order)
        //Publish an OrderCreated event to Kafka.
        orderEventPublisher.publishOrderCreated(savedOrder)

        return savedOrder

    }

    fun getOrderById(id: Long, authentication: Authentication): Order {

        val order = orderCacheService.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")

        val isAdmin = authentication.authorities.any {
            it.authority == "ROLE_ADMIN"
        }
        // Admins can access any existing order.
        if (isAdmin) {
            return order
        }

        // Find the authenticated user.
        val user = userRepository.findByEmail(authentication.name)
            .orElseThrow{ IllegalStateException("Authenticated user not found") }

        // Regular users can only access their own orders.
        if (order.userId != requireNotNull(user.id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found")
        }

        return order

    }

    fun updateOrder(id: Long, request: UpdateOrderRequest,
                    authentication: Authentication): Order {

        if (request.totalAmount <= BigDecimal.ZERO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Total amount must be greater than zero.")
        }

        val order = getOrderById(id,authentication)
        order.totalAmount = request.totalAmount

        val savedOrder = orderRepository.save(order)

        // Remove the outdated order from Redis.
        orderCacheService.evictOrder(id)

        return savedOrder

    }

    fun deleteOrder(id: Long, authentication: Authentication) {

        val order = getOrderById(id,authentication)
        orderRepository.delete(order)

        // Remove the outdated order from Redis.
        orderCacheService.evictOrder(id)

    }

    fun updateOrderStatus(id: Long, request: UpdateOrderStatusRequest,
                          authentication: Authentication): Order {

        val order = getOrderById(id,authentication)
        // Skip the update if the status has not changed.
        if (order.status == request.status) {
            return order
        }
        val previousStatus = order.status
        order.status = request.status
        val savedOrder = orderRepository.save(order)

        // Remove the outdated order from Redis.
        orderCacheService.evictOrder(id)

        orderEventPublisher.publishOrderStatusChanged(savedOrder, previousStatus)
        return savedOrder

    }

}