package com.paperTrading.store.service.impl;

import com.paperTrading.store.dto.request.OrderItemRequest;
import com.paperTrading.store.dto.request.PlaceOrderRequest;
import com.paperTrading.store.dto.response.ApiResponse;
import com.paperTrading.store.entity.Product;
import com.paperTrading.store.entity.StoreOrder;
import com.paperTrading.store.entity.StoreOrderItem;
import com.paperTrading.store.entity.Wallet;
import com.paperTrading.store.entity.WalletTransaction;
import com.paperTrading.store.enums.OrderStatus;
import com.paperTrading.store.exception.InsufficientCoinsException;
import com.paperTrading.store.exception.OutOfStockException;
import com.paperTrading.store.exception.ProductNotFoundException;
import com.paperTrading.store.repository.ProductRepository;
import com.paperTrading.store.repository.StoreOrderRepository;
import com.paperTrading.store.repository.UserRepository;
import com.paperTrading.store.repository.WalletRepository;
import com.paperTrading.store.repository.WalletTransactionRepository;
import com.paperTrading.store.service.StoreService;
import com.paperTrading.store.util.OrderIdGenerator;
import com.paperTrading.store.util.TransactionReason;
import com.paperTrading.store.util.TransactionType;
import com.paperTrading.store.util.TransactionStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {

    private final ProductRepository productRepository;
    private final StoreOrderRepository storeOrderRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    
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
        
//        wallet.setTotalAmount(wallet.getTotalAmount()- totalAmount);
//
//        walletRepository.save(wallet);
        
        deductCoins(user.getId(), totalAmount,TransactionReason.STORE_PURCHASE, "Product purchased");

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
    
    
    @Transactional
    private void deductCoins(Long userId,
                            double coins,
                            TransactionReason reason,
                            String note) {

        log.info("Deduct coins started | userId={} | coins={}",
                userId, coins);

        try {

            Wallet wallet = walletRepository.findByUserId(userId)
                    .orElseThrow(() ->
                            new RuntimeException("Wallet not found"));

            if (wallet.getTotalAmount() < coins) {

                log.error("Insufficient balance | userId={}", userId);

                throw new RuntimeException("Insufficient wallet balance");
            }

            wallet.setTotalAmount(wallet.getTotalAmount() - coins);

            walletRepository.save(wallet);

            recordTransaction(
                    userId,
                    BigDecimal.valueOf(coins),
                    TransactionType.DEBIT,
                    TransactionStatus.SUCCESS,
                    reason,
                    note
            );

            log.info("Coins deducted successfully | userId={} | remaining={}",
                    userId,
                    wallet.getTotalAmount());

        } catch (Exception ex) {

            log.error("Failed to deduct coins | userId={}",
                    userId,
                    ex);

            recordTransaction(
                    userId,
                    BigDecimal.valueOf(coins),
                    TransactionType.DEBIT,
                    TransactionStatus.FAILED,
                    reason,
                    ex.getMessage()
            );

            throw ex;
        }
    }
    
    public void recordTransaction(Long userId, BigDecimal amount, TransactionType type, 
            TransactionStatus status, TransactionReason reason, String note) {
		WalletTransaction txn = WalletTransaction.builder()
		.txnId(UUID.randomUUID().toString().substring(0, 8).toUpperCase())
		.userId(userId)
		.amount(amount)
		.txnType(type)
		.status(status)
		.txnReason(reason.toString())
		.note(note)
		.createdAt(LocalDateTime.now())
		.build();
		
		transactionRepository.save(txn);
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

            	itemMap.put("orderId", order.getOrderId());
            	itemMap.put("orderDate", order.getCreatedAt()); 
            	itemMap.put("orderStatus", order.getStatus());
            	itemMap.put("totalAmount", order.getTotalAmount());

            	itemMap.put("customerName", order.getCustomerName());
            	itemMap.put("mobileNumber", order.getMobileNumber());
            	itemMap.put("email", order.getEmail());

            	itemMap.put("address", order.getAddress());
            	itemMap.put("city", order.getCity());
            	itemMap.put("state", order.getState());
            	itemMap.put("pincode", order.getPincode());

            	itemMap.put("productId", item.getProductId());
            	itemMap.put("productName", item.getProductName());
            	itemMap.put("productPrice", item.getProductPrice());

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