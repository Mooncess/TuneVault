package ru.mooncess.auth_service.exception;

import lombok.Data;

import java.util.Date;

@Data
public class AppError {
    private String message;
    private Date timestamp;

    public AppError(String message) {
        this.message = message;
        this.timestamp = new Date();
    }
}
