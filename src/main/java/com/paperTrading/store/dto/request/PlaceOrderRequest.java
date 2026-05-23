package com.paperTrading.store.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlaceOrderRequest {

    @NotBlank(message = "Customer name required")
    private String customerName;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Invalid mobile number"
    )
    private String mobileNumber;

    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address required")
    @Size(min = 10, max = 300)
    private String address;

    @NotBlank(message = "City required")
    private String city;

    @NotBlank(message = "State required")
    private String state;

    @Pattern(
            regexp = "^[1-9][0-9]{5}$",
            message = "Invalid pincode"
    )
    private String pincode;

    @NotEmpty(message = "Items cannot be empty")
    private List<OrderItemRequest> items;
}