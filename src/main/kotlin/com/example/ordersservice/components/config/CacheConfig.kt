package com.example.ordersservice.components.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@EnableCaching
class CacheConfig {
}