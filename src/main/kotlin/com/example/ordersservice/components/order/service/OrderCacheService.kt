package com.example.ordersservice.components.order.service

import com.example.ordersservice.components.order.entity.Order
import com.example.ordersservice.components.order.repository.OrderRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class OrderCacheService (
    private val orderRepository: OrderRepository
) {

    // Retrieves an order from Redis or MySQL.
    @Cacheable(
        cacheNames = ["orders"],
        key = "#p0",
        unless = "#result == null"
    )
    fun findById(id: Long): Order? {
        return orderRepository.findById(id).orElse(null)
    }

    // Removes an outdated order from the cache.
    @CacheEvict(
        cacheNames = ["orders"],
        key = "#p0",
    )
    fun evictOrder(id: Long) {}

}