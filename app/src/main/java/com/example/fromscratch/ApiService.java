package com.example.fromscratch;

import com.example.fromscratch.SignupRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/signup") // replace with your real endpoint
    Call<Void> signUp(@Body SignupRequest signupRequest);
}
