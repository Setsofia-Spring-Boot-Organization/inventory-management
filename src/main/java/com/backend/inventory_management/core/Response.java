package com.backend.inventory_management.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    private boolean success;
    private String message;
    private T data;
    private String timestamp;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .success(true)
                .message("Operation successful")
                .data(data)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static <T> Response<T> success(T data, String message) {
        return Response.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static <T> Response<T> error(String message) {
        return Response.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }

    public static <T> Response<T> error(String message, T data) {
        return Response.<T>builder()
                .success(false)
                .message(message)
                .data(data)
                .timestamp(java.time.Instant.now().toString())
                .build();
    }
}