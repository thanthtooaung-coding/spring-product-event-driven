package com.product.eventdriven.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent implements Serializable {
    private String productId;
    private String productName;
    private BigDecimal price;
    private Instant timestamp;
}