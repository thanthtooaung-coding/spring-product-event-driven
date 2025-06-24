package com.product.eventdriven.request;

import java.math.BigDecimal;

public record ProductRequest(String productName, BigDecimal price) {}