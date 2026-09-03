package com.support.backend.service;

import com.support.backend.entity.Order;
import com.support.backend.repository.OrderRepository;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order getByOrderCode(String orderCode) {
        return orderRepository.findByOrderCodeIgnoreCase(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("No order found with code " + orderCode));
    }
}
