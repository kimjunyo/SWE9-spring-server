package com.team9.sungdaehanmarket.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class ResponseAPI {
    private int code;
    private String content;
    private String message;

    public ResponseAPI(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
