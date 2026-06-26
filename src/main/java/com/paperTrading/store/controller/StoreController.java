package com.paperTrading.store.controller;

import com.paperTrading.store.dto.request.PlaceOrderRequest;
import com.paperTrading.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class StoreController {

    private final StoreService storeService;

    @GetMapping("/items")
    public ResponseEntity<?> getAllProducts() {

        log.info(
                "GET API called for fetching store products");

        return ResponseEntity.ok(
                storeService.getAllProducts());
    }

    @PostMapping("/place-order")
    public ResponseEntity<?> placeOrder(

            @RequestBody
            @Valid
            PlaceOrderRequest request,

            @RequestHeader("Authorization")
            String token) {

        log.info(
                "POST API called for placing store order");

        return ResponseEntity.ok(
                storeService.placeOrder(request));
    }

    @GetMapping("/get-order")
    public ResponseEntity<?> getOrders(

            @RequestHeader("Authorization")
            String token) {

        log.info(
                "GET API called for fetching store orders");

        return ResponseEntity.ok(
                storeService.getOrders(token));
    }
}