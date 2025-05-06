package com.example.fromscratch;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fromscratch.databinding.ActivitySignUpBinding;
import com.example.fromscratch.SignupRequest;
import com.example.fromscratch.ApiClient;
import com.example.fromscratch.ApiService;

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

        EdgeToEdge.enable(this);
        setupWindowInsets();
        setupSignUpButton();
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
            // Get user input
            String username = binding.usernameEditText.getText().toString().trim();
            String email = binding.emailEditText.getText().toString().trim();
            String password = binding.passwordEditText.getText().toString().trim();

            // Validate inputs
            if (validateInputs(username, email, password)) {
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.signUpButton.setEnabled(false);

                // Prepare the request
                SignupRequest request = new SignupRequest(username, email, password);
                ApiService apiService = ApiClient.getClient().create(ApiService.class);

                // Make the network call
                apiService.signUp(request).enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.signUpButton.setEnabled(true);

                        if (response.isSuccessful()) {
                            showToast("Signup successful for " + username);
                        } else {
                            showToast("Signup failed. Try again.");
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
