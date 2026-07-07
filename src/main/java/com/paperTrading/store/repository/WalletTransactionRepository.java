package com.paperTrading.store.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.paperTrading.store.entity.WalletTransaction;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

}