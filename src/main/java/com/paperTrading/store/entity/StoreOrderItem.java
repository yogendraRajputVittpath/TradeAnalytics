package com.paperTrading.store.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    private String productName;

    private Double productPrice;

    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private StoreOrder storeOrder;
}