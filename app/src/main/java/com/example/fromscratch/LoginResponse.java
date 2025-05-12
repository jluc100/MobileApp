package com.example.fromscratch;

public class LoginResponse {
    private String token;

    // Constructor
    public LoginResponse(String token) {
        this.token = token;
    }

    // Getter method
    public String getToken() {
        return token;
    }
}
