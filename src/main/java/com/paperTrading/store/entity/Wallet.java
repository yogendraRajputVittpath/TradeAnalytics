package com.paperTrading.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "user_id")
    private Long userId;
}