package com.example.ordersservice.components.order.dto

import com.example.ordersservice.components.order.enums.OrderStatus

data class UpdateOrderStatusRequest(
    val status: OrderStatus
)
