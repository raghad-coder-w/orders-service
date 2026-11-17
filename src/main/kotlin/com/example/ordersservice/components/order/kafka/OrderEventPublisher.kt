package com.example.ordersservice.components.order.kafka

import com.example.ordersservice.components.order.entity.Order
import com.example.ordersservice.components.order.enums.OrderStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import tools.jackson.databind.json.JsonMapper

@Service
class OrderEventPublisher (
    private val kafkaTemplate: KafkaTemplate<String, String>,
    private val jsonMapper : JsonMapper
) {

    fun publishOrderCreated(order: Order) {

        val orderId = requireNotNull(order.id)
        val event = mapOf("eventType" to "OrderCreated",
            "orderId" to orderId,
            "userId" to order.userId,
            "status" to order.status.name,
            "totalAmount" to order.totalAmount)

        kafkaTemplate.send("order-events", orderId.toString(),
        jsonMapper.writeValueAsString(event))

    }

    fun publishOrderStatusChanged(order: Order, previousStatus: OrderStatus) {

        val orderId = requireNotNull(order.id)
        val event = mapOf("eventType" to "OrderStatusChanged",
            "orderId" to orderId,
            "userId" to order.userId,
            "previousStatus" to previousStatus.name,
            "newStatus" to order.status.name)

        kafkaTemplate.send("order-events", orderId.toString(),
            jsonMapper.writeValueAsString(event))

    }

}