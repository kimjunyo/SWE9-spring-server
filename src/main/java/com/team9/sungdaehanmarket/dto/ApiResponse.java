package com.team9.sungdaehanmarket.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int status;
    private String message;
    private T content;  // T로 설정하여 유연성 확보

    public ApiResponse(int status, String message, T content) {
        this.status = status;
        this.message = message;
        this.content = content;
    }
}