package ru.rayovsky.disp.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiError {
    private String message;
    private Integer status;
    private LocalDateTime timeStamp;
}
