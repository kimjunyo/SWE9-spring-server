package com.team9.sungdaehanmarket.dto;

import java.util.List;

public class ApiResponse<T> {
    private int status;
    private String message;
    private List<T> content;

    public ApiResponse(int status, String message, List<T> content) {
        this.status = status;
        this.message = message;
        this.content = content;
    }

    // Getter 및 Setter
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}