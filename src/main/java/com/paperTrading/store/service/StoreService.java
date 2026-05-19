package com.paperTrading.store.service;

import com.paperTrading.store.dto.request.PlaceOrderRequest;
import com.paperTrading.store.dto.response.ApiResponse;

public interface StoreService {

    ApiResponse<?> getAllProducts();

    ApiResponse<?> placeOrder(
            PlaceOrderRequest request);

    Object getOrders(String token);
}