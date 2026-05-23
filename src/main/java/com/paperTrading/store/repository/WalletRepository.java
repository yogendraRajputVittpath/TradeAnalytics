package com.paperTrading.store.repository;

import com.paperTrading.store.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository
        extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUserId(Long userId);
}