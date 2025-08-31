package com.backend.inventory_management.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private int statusCode;
    private String message;
    private T data;

    public ResponseEntity<Response<?>> responseEntity(HttpStatus httpStatus, String message) {
        this.statusCode = httpStatus.value();
        this.message = message;
        return ResponseEntity.status(httpStatus).body(
                Response.builder()
                        .statusCode(this.statusCode)
                        .message(this.message)
                        .data(this.data)
                        .build()
        );
    }
}