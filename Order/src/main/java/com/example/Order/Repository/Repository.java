package com.example.Order.Repository;

import com.example.Order.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface Repository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
