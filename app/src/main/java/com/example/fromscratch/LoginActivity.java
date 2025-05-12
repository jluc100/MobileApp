package com.example.fromscratch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fromscratch.databinding.ActivityLoginBinding;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupLoginButton();
        setupSignUpLink();
    }

    private void setupLoginButton() {
        binding.loginButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (validateInputs(username, password)) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.loginButton.setEnabled(false);

                LoginRequest request = new LoginRequest(username, password);
                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                apiService.login(request).enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.loginButton.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            LoginResponse loginResponse = response.body();
                            String token = loginResponse.getToken();

                            // Store token in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("token", token);
                            editor.apply();

                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();

                            // Updated navigation with token passing
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("AUTH_TOKEN", token);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    String errorBody = response.errorBody().string();
                                    JSONObject jsonObject = new JSONObject(errorBody);
                                    String errorMessage = jsonObject.optString("error", "Login failed");
                                    showToast(errorMessage);
                                } else {
                                    showToast("Login failed: Unknown error");
                                }
                            } catch (Exception e) {
                                showToast("Login failed: Error parsing response");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginResponse> call, Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.loginButton.setEnabled(true);
                        showToast("Network error: " + t.getMessage());
                    }
                });
            }
        });
    }

    // Rest of your existing code remains unchanged...
    private void setupSignUpLink() {
        binding.signUpTextView.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs(String username, String password) {
        boolean isValid = true;
        if (username.isEmpty()) {
            binding.usernameLayout.setError("Username cannot be empty");
            isValid = false;
        } else {
            binding.usernameLayout.setError(null);
        }
        if (password.isEmpty()) {
            binding.passwordLayout.setError("Password cannot be empty");
            isValid = false;
        } else {
            binding.passwordLayout.setError(null);
        }
        return isValid;
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}