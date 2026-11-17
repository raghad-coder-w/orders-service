package com.example.ordersservice.components.order.controller

import com.example.ordersservice.components.order.dto.CreateOrderRequest
import com.example.ordersservice.components.order.dto.UpdateOrderRequest
import com.example.ordersservice.components.order.dto.UpdateOrderStatusRequest
import com.example.ordersservice.components.order.entity.Order
import com.example.ordersservice.components.order.enums.OrderStatus
import com.example.ordersservice.components.order.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController (
    private val orderService: OrderService
){

    @GetMapping
    fun getOrder(authentication: Authentication): List<Order>{
        return orderService.getOrders(authentication)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createOrder(@RequestBody request: CreateOrderRequest,
                    authentication: Authentication): Order {
        return orderService.createOrder(request, authentication)
    }

    @GetMapping("/{id}")
    fun getOrderById(@PathVariable("id") id: Long,
                     authentication: Authentication): Order {
        return orderService.getOrderById(id, authentication)
    }

    @PutMapping("/{id}")
    fun updateOrder(@PathVariable("id") id: Long,
                    @RequestBody request: UpdateOrderRequest,
                    authentication: Authentication) : Order {
        return orderService.updateOrder(id, request, authentication)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteOrder(@PathVariable("id") id: Long, authentication: Authentication) {
        return orderService.deleteOrder(id, authentication)
    }

    @PatchMapping("/{id}/status")
    fun updateOrderStatus(@PathVariable("id") id: Long,
                          @RequestBody request: UpdateOrderStatusRequest,
                          authentication: Authentication): Order {
        return orderService.updateOrderStatus(id, request, authentication)
    }

}