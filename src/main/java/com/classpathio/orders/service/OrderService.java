package com.classpathio.orders.service;

import com.classpathio.orders.model.Order;
import com.classpathio.orders.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));
    }

    public Order createOrder(Order order) {
        System.out.println("Came inside the create order method ====================");
        System.out.printf("OrderService.createOrder: order.getId() = %d\n", order.getId());
        return orderRepository.save(order);
    }

    public Order updateOrder(Long id, Order updatedOrder) {
        Order order = getOrderById(id);
        order.setCustomerName(updatedOrder.getCustomerName());
        order.setTotalPrice(updatedOrder.getTotalPrice());
        order.setCustomerEmail(updatedOrder.getCustomerEmail());
        System.out.println("OrderService.updateOrder ======================================");
        System.out.printf("OrderService.updateOrder: order.getId() = %d\n", order.getId());
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }
}
