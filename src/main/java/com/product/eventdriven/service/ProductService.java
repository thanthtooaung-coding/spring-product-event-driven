package com.product.eventdriven.service;

import com.product.eventdriven.request.ProductRequest;
import com.product.eventdriven.event.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.routingkey}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public ProductService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void createProduct(ProductRequest request) {

        log.info("Saving product to the database... (simulated)");

        ProductCreatedEvent event = new ProductCreatedEvent(
                UUID.randomUUID().toString(),
                request.productName(),
                request.price(),
                Instant.now()
        );

        rabbitTemplate.convertAndSend(exchangeName, routingKey, event);

        log.info("Published ProductCreatedEvent with Product ID: {}", event.getProductId());
    }
}