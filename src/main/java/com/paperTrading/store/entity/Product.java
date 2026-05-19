package com.paperTrading.store.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Product name required")
    @Size(min = 3, max = 100)
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "Price required")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    @NotBlank(message = "Image required")
    @Column(nullable = false)
    private String image;

    @NotBlank(message = "Description required")
    @Size(max = 500)
    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean stock = true;

//    @Min(value = 0,
//            message = "Quantity cannot be negative")
//    @Column(nullable = false)
//    private Integer quantity;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}