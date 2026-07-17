package com.paperTrading.store.entity;

//import com.demo.papertrading.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.paperTrading.store.enums.OrderStatus;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "store_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private Long userId;

    private String customerName;

    private String mobileNumber;

    private String email;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private String trackingId;

    private String deliveryPartner;

    @OneToMany(mappedBy = "storeOrder",
            cascade = CascadeType.ALL)
    private List<StoreOrderItem> items;

    @CreationTimestamp
    private Timestamp createdAt;

}