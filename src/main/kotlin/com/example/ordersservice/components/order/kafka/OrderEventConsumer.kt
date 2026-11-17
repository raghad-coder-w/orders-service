package com.example.ordersservice.components.order.kafka

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class OrderEventConsumer {

    private val logger = LoggerFactory.getLogger(OrderEventConsumer::class.java)

    // Listen for order events and log each received message.
    @KafkaListener(topics = ["order-events"])
    fun consumeOrderEvent(message: String) {
        logger.info("Received Kafka event: {}", message)
    }

}