package com.paperTrading.store.repository;

import com.paperTrading.store.entity.StoreOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreOrderRepository
        extends JpaRepository<StoreOrder, Long> {

    List<StoreOrder>
    findByUserIdOrderByIdDesc(
            Long userId);
}