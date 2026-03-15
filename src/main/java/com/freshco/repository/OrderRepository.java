package com.freshco.repository;

import com.freshco.entity.Order;
import com.freshco.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<Order> findByShopIdOrderByCreatedAtDesc(Long shopId);

    long countByShopId(Long shopId);

    long countByShopIdAndStatus(Long shopId, OrderStatus status);

}
