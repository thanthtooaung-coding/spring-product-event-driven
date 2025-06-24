package com.product.eventdriven.listener;

import com.product.eventdriven.event.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationListener.class);

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleProductCreatedEvent(ProductCreatedEvent event) {
        log.info("Received event: {}", event);

        log.info("Processing new product notification for Product ID: {}...", event.getProductId());

        log.info("Notification processed successfully for Product ID: {}", event.getProductId());
    }
}