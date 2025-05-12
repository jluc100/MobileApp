package com.example.fromscratch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fromscratch.databinding.ActivitySignUpBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize view binding
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupWindowInsets();
        setupSignUpButton();
        setupLoginLink();  // Added method to handle login link click
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupSignUpButton() {
        binding.signUpButton.setOnClickListener(v -> {
            String username = binding.usernameEditText.getText().toString().trim();
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            if (validateInputs(username, email, password)) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.signUpButton.setEnabled(false);

                SignupRequest request = new SignupRequest(username, email, password);
                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                apiService.signUp(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.signUpButton.setEnabled(true);

                        if (response.isSuccessful()) {
                            try {
                                // Extract the success message from the response body (if any)
                                String message = new JSONObject(response.errorBody().string()).optString("message", "User Created Successfully");
                                showToast(message);
                            } catch (Exception e) {
                                showToast("User Created Successfully");
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody().string();
                                JSONObject jsonObject = new JSONObject(errorBody);
                                String errorMessage = jsonObject.optString("error", "Signup failed");
                                showToast(errorMessage);
                            } catch (IOException | JSONException e) {
                                showToast("Signup failed with unexpected error.");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.signUpButton.setEnabled(true);
                        showToast("Network error: " + t.getMessage());
                    }
                });
            }
        });
    }

    private void setupLoginLink() {
        // Handle click on the "Login" link to navigate to the login screen
        binding.loginTextView.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);  // Navigate to LoginActivity
            startActivity(intent);
        });
    }

    private boolean validateInputs(String username, String email, String password) {
        boolean isValid = true;

        if (username.isEmpty()) {
            binding.usernameLayout.setError("Username cannot be empty");
            isValid = false;
        } else {
            binding.usernameLayout.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailLayout.setError("Enter a valid email address");
            isValid = false;
        } else {
            binding.emailLayout.setError(null);
        }

        if (password.isEmpty() || password.length() < 5) {
            binding.passwordLayout.setError("Password must be at least 5 characters");
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
