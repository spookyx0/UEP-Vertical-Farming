package com.example.vfapplication;
import android.widget.Toast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputPassword;
    private Button btnLogin;
    private TextView txtForgotPassword;

    // Define the client and media type for sending requests
    private OkHttpClient client = new OkHttpClient();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    // Tag for logging
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the views
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);

        btnLogin = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);

        // Set the click listener for the Login button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the input data
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Validate inputs
                if (email.isEmpty() || password.isEmpty()) {
                    // Show a message if any field is empty
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed with login if validation passes
                    Log.d(TAG, "Attempting login with email: " + email); // Debugging log
                    try {
                        // Make a POST request to the server
                        loginRequest(email, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "Error occurred during login", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Set the click listener for the Forgot Password text
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle forgot password logic here
            }
        });
    }

    // Function to send login data to the server
    private void loginRequest(String email, String password) throws IOException {
        String url = "https://testnet.vfsystem.io/api/userauth/login"; // Replace with your actual server URL
        String json = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"; // JSON body

        // Create request body
        RequestBody body = RequestBody.create(JSON, json);

        // Create request
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        // Send request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                Log.e(TAG, "Login request failed: " + e.getMessage()); // Debugging log
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Login failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Error: ", e); // Log the full error stack trace
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Ensure response body is closed after use
                try {
                    if (response.isSuccessful()) {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Login successful! Response: " + responseBody); // Debugging log

                        runOnUiThread(() -> {
                            // Show success message
                            Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, FirstActivity.class);
                            startActivity(intent);
                        });
                    } else {
                        String errorBody = response.body().string();
                        Log.e(TAG, "Login failed with response code: " + response.code() + " and message: " + errorBody);
                        runOnUiThread(() -> Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error handling the response: " + e.getMessage());
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show());
                } finally {
                    if (response.body() != null) {
                        response.body().close(); // Close the response body to avoid memory leaks
                    }
                }
            }
        });
    }
}
