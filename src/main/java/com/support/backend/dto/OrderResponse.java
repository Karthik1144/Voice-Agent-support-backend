package com.support.backend.dto;

import com.support.backend.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OrderResponse {
    public String orderCode;
    public String status;
    public BigDecimal amount;
    public LocalDate expectedDelivery;
    public String customerName;

    public static OrderResponse from(Order order) {
        OrderResponse r = new OrderResponse();
        r.orderCode = order.getOrderCode();
        r.status = order.getStatus().name();
        r.amount = order.getAmount();
        r.expectedDelivery = order.getExpectedDelivery();
        r.customerName = order.getCustomer().getName();
        return r;
    }
}
