package com.szniloycoder.mychat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.szniloycoder.mychat.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {

    ActivityStartBinding binding;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //hide statusBar:
        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        // Show splash screen for 2 seconds, then redirect
        new Handler().postDelayed(() -> {
            if (currentUser != null) {
                // User is logged in, navigate to MainActivity
                startActivity(new Intent(StartActivity.this, MainActivity.class));
            } else {
                // User not logged in, navigate to LoginActivity
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
            finish();
        }, 2000);
    }
}