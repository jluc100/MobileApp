package com.example.fromscratch;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/api/signup") // Replace with your real endpoint
    Call<Void> signUp(@Body SignupRequest signupRequest);

    @POST("/api/login") // Replace with your real endpoint
    Call<LoginResponse> login(@Body LoginRequest loginRequest);  // FIXED: changed from Void to LoginResponse
}
