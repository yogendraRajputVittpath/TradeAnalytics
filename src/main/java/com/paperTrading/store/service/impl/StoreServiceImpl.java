package com.paperTrading.store.service.impl;

import com.paperTrading.store.dto.request.OrderItemRequest;
import com.paperTrading.store.dto.request.PlaceOrderRequest;
import com.paperTrading.store.dto.response.ApiResponse;
import com.paperTrading.store.entity.Product;
import com.paperTrading.store.entity.StoreOrder;
import com.paperTrading.store.entity.StoreOrderItem;
import com.paperTrading.store.entity.Wallet;
import com.paperTrading.store.enums.OrderStatus;
import com.paperTrading.store.exception.InsufficientCoinsException;
import com.paperTrading.store.exception.OutOfStockException;
import com.paperTrading.store.exception.ProductNotFoundException;
import com.paperTrading.store.repository.ProductRepository;
import com.paperTrading.store.repository.StoreOrderRepository;
import com.paperTrading.store.repository.UserRepository;
import com.paperTrading.store.repository.WalletRepository;
import com.paperTrading.store.service.StoreService;
import com.paperTrading.store.util.OrderIdGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final ProductRepository productRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Override
    public ApiResponse<?> getAllProducts() {

        log.info("Fetching all store products");

        Map<String, Object> response = new HashMap<>();

        response.put("items",
                productRepository.findByStockTrue());

        return ApiResponse.builder()
                .status("SUCCESS")
                .code(200)
                .message("Products fetched successfully")
                .data(response)
                .build();
    }

    @Override
    @Transactional
    public ApiResponse<?> placeOrder(PlaceOrderRequest request) {

        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        var user = userRepository.findByEmail(email).orElseThrow(() ->new RuntimeException("User not found"));

        log.info("Store order initiated for userId : {}",user.getId());

        double totalAmount = 0;

        List<StoreOrderItem> orderItems = new ArrayList<>();

        StoreOrder storeOrder = new StoreOrder();

        for (OrderItemRequest item :request.getItems()) {

            Product product =productRepository.findById(item.getProductId()).orElseThrow(() ->new ProductNotFoundException("Product not found"));

            log.info("Validating product : {}",product.getName());

            if (!product.getStock()) {
                throw new OutOfStockException("Product out of stock");
            }

            double itemTotal =product.getPrice();

            totalAmount += itemTotal;

            StoreOrderItem orderItem =StoreOrderItem.builder()
                            .productId(product.getId())
                            .productName(product.getName())
                            .productPrice(product.getPrice())
                            .storeOrder(storeOrder)
                            .build();

            orderItems.add(orderItem);
        }

        Wallet wallet = walletRepository
                .findByUserId(user.getId())
                .orElseThrow(() ->new RuntimeException("Wallet not found"));

        if (wallet.getTotalAmount()< totalAmount) {

            throw new InsufficientCoinsException("Insufficient coins");
        }

        wallet.setTotalAmount(wallet.getTotalAmount()- totalAmount);

        walletRepository.save(wallet);

        String orderId =OrderIdGenerator.generateOrderId();

        storeOrder.setOrderId(orderId);
        storeOrder.setUserId(user.getId());
        storeOrder.setCustomerName(request.getCustomerName());
        storeOrder.setMobileNumber(request.getMobileNumber());
        storeOrder.setEmail(request.getEmail());
        storeOrder.setAddress(request.getAddress());
        storeOrder.setCity(request.getCity());
        storeOrder.setState(request.getState());
        storeOrder.setPincode(request.getPincode());

        storeOrder.setTotalAmount(totalAmount);

        storeOrder.setStatus(OrderStatus.PENDING);

        storeOrder.setItems(orderItems);

        storeOrderRepository.save(storeOrder);

        log.info("Order placed successfully with orderId : {}",orderId);

        Map<String, Object> response =new HashMap<>();

        response.put("orderId", orderId);

        return ApiResponse.builder()
                .status("SUCCESS")
                .code(200)
                .message("Order placed successfully")
                .data(response)
                .build();
    }
    
    @Override
    public ApiResponse<?> getOrders(String token) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("Fetching store orders for userId : {}", user.getId());

        List<StoreOrder> orders =
                storeOrderRepository.findByUserIdOrderByIdDesc(user.getId());

        List<Map<String, Object>> items = new ArrayList<>();

        for (StoreOrder order : orders) {

            for (StoreOrderItem item : order.getItems()) {

                Map<String, Object> itemMap = new LinkedHashMap<>();

                itemMap.put("product_id", item.getProductId());
                itemMap.put("product_name", item.getProductName());
                itemMap.put("price", item.getProductPrice());

                items.add(itemMap);
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("items", items);

        return ApiResponse.builder()
                .status("SUCCESS")
                .code(200)
                .message("Order fetched successfully")
                .data(response)
                .build();
    }
}