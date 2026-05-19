package com.paperTrading.store.repository;

//import com.demo.papertrading.entity.StoreOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import com.paperTrading.store.entity.StoreOrderItem;

public interface StoreOrderItemRepository
        extends JpaRepository<StoreOrderItem, Long> {
}