package com.example.fromscratch;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private TextView tokenTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize views
        tokenTextView = findViewById(R.id.tokenTextView); // Add this TextView to your layout

        // Get token from Intent (immediate access)
        String intentToken = getIntent().getStringExtra("AUTH_TOKEN");

        // Get token from SharedPreferences (persistent storage)
        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String sharedPrefToken = sharedPreferences.getString("token", null);

        // Determine which token to display (Intent takes priority)
        String displayToken = intentToken != null ? intentToken : sharedPrefToken;

        if (displayToken != null) {
            // Display the token
            tokenTextView.setText("Your Auth Token:\n" + displayToken);

            // You can also show a toast confirmation
            Toast.makeText(this, "Token received successfully!", Toast.LENGTH_SHORT).show();
        } else {
            // Handle case where no token is found
            tokenTextView.setText("No authentication token found");
            Toast.makeText(this, "Authentication required - please login", Toast.LENGTH_LONG).show();
            finish(); // Close this activity if no token is available
        }
    }
}