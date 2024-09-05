package com.szniloycoder.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.szniloycoder.mychat.databinding.ActivityPasswordManagerBinding;

import java.util.Objects;

public class PasswordManagerActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private ActivityPasswordManagerBinding binding;
    private ProgressDialog dialog;
    private String currentPass, newPass, confNewPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordManagerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.activity_color));

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading...");


        setUpListeners();
    }

    private void setUpListeners() {
        binding.btnBack.setOnClickListener(view -> finish());

        binding.btnForgetPassword.setOnClickListener(view ->
                startActivity(new Intent(this, ForgotPasswordActivity.class)));

        binding.btnSavePass.setOnClickListener(view -> {
            currentPass = binding.currentPass.getText().toString();
            newPass = binding.newPass.getText().toString();
            confNewPass = binding.conPass.getText().toString();

            if (validateInputs()) {
                changePassword(currentPass, newPass);
            }
        });
    }

    private boolean validateInputs() {
        if (currentPass.isEmpty()) {
            binding.currentPass.setError("Please enter your current password.");
            return false;
        } else if (newPass.isEmpty()) {
            binding.newPass.setError("Please enter your new password.");
            return false;
        } else if (confNewPass.isEmpty()) {
            binding.conPass.setError("Please enter confirm password.");
            return false;
        } else if (!newPass.equals(confNewPass)) {
            binding.conPass.setError("Passwords do not match.");
            return false;
        }
        return true;
    }

    private void changePassword(String currentPass, String newPass) {
        dialog.show();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            currentUser.reauthenticate(
                            EmailAuthProvider.getCredential(Objects.requireNonNull(currentUser.getEmail()), currentPass))

                    .addOnSuccessListener(aVoid -> {
                        currentUser.updatePassword(newPass)
                                .addOnCompleteListener(task -> {
                                    dialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(e -> {
                                    dialog.dismiss();
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

}