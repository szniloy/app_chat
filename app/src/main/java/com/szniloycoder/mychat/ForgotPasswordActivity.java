package com.szniloycoder.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.szniloycoder.mychat.databinding.ActivityForgotPasswordBinding;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ActivityForgotPasswordBinding binding;
    private ProgressDialog progressDialog;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflate and bind layout
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set status bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.white));

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance();

        // Initialize ProgressDialog
        setupProgressDialog();

        // Setup button listeners
        setupListeners();
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");
    }

    private void setupListeners() {
        // Back button listener
        binding.btnBack.setOnClickListener(view -> finish());

        // Reset password button listener
        binding.btnResetPass.setOnClickListener(view -> {
            email = binding.emailTxt.getText().toString();

            if (email.isEmpty()) {
                binding.emailTxt.setError("Please enter your email address.");
            } else {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        progressDialog.show();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset mail sent to your email.", Toast.LENGTH_SHORT).show();
                        // Remove finish(); so the activity remains open
                    } else {
                        Toast.makeText(this, "Enter correct email address.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}