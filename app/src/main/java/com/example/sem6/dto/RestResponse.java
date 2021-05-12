package com.example.sem6.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private boolean status;
    private String message;
    private T data;
}
