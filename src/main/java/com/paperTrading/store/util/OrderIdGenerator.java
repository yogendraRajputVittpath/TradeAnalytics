package com.paperTrading.store.util;

public class OrderIdGenerator {

    public static String generateOrderId() {

        return "ORD" + System.currentTimeMillis();
    }
}