package com.freshco.repository;

import com.freshco.entity.Order;
import com.freshco.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    Page<Order> findByShopIdOrderByCreatedAtDesc(Long shopId, Pageable pageable);

    long countByShopId(Long shopId);

    long countByShopIdAndStatus(Long shopId, OrderStatus status);

}
