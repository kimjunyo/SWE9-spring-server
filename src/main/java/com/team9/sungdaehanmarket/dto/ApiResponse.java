package com.team9.sungdaehanmarket.dto;

public class ApiResponse<T> {
    private int status;
    private String message;
    private T content;  // T로 설정하여 유연성 확보

    public ApiResponse(int status, String message, T content) {
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

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}