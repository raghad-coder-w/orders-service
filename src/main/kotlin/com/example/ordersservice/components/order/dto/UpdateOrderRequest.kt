package com.example.ordersservice.components.order.dto

import java.math.BigDecimal

data class UpdateOrderRequest(
    val totalAmount: BigDecimal
)
