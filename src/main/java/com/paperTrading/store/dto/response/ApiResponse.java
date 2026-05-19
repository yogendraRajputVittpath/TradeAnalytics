package com.paperTrading.store.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private String status;
//    private int code;
    private String message;
    private T data;

    public static ApiResponse<?> success(String msg) {
        return ApiResponse.builder()
                .status("SUCCESS")
//                .code(200)
                .message(msg)
                .build();
    }
    public static <T> ApiResponse<T> success(T data, String msg) {
        return ApiResponse.<T>builder()
                .status("SUCCESS")
//                .code(200)
                .message(msg)
                .data(data)
                .build();
    }

    // (Optional but good practice)
    public static ApiResponse<?> error(String msg, int code) {
        return ApiResponse.builder()
                .status("ERROR")
//                .code(code)
                .message(msg)
                .build();
    }
    
}

