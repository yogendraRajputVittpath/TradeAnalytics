package com.paperTrading.store.exception;

public class ProductNotFoundException
        extends RuntimeException {

    public ProductNotFoundException(
            String message) {

        super(message);
    }
}