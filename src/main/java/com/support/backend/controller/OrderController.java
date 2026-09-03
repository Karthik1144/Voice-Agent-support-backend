package com.support.backend.controller;

import com.support.backend.dto.OrderResponse;
import com.support.backend.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // GET /api/orders/ORD123
    @GetMapping("/{orderCode}")
    public OrderResponse getOrder(@PathVariable String orderCode) {
        return OrderResponse.from(orderService.getByOrderCode(orderCode));
    }
}
