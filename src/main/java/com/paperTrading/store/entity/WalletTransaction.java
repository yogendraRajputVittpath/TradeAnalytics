package com.paperTrading.store.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.paperTrading.store.util.TransactionReason;
import com.paperTrading.store.util.TransactionStatus;
import com.paperTrading.store.util.TransactionType;


@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String txnId; 

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType txnType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionStatus status;

//    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String txnReason;

    private String note;
    private String errorMsg;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}