package com.example.ordersservice.components.order.dto

import java.math.BigDecimal

// Represents the data required to create a new order.
data class CreateOrderRequest(
    val totalAmount: BigDecimal
)
