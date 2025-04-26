package com.vady.iamservice.exception;



import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorResponseDto {
    private String message;
    private HttpStatus code;
    private String path;
    private LocalDateTime errorTime;

    public ErrorResponseDto(String message, HttpStatus code, String path, LocalDateTime errorTime) {
        this.message = message;
        this.code = code;
        this.path = path;
        this.errorTime = errorTime;
    }
}
