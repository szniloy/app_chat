package com.szniloycoder.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.szniloycoder.mychat.databinding.ActivitySignUpBinding;
import java.util.HashMap;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    ProgressDialog dialog;
    String email;
    String password;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.activity_color));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        currentUser = auth.getCurrentUser();

        ProgressDialog progressDialog = new ProgressDialog(this);
        this.dialog = progressDialog;
        progressDialog.setTitle("Creating your account");
        this.dialog.setMessage("Please wait...");

        binding.btnLogin.setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        binding.btnSignUp.setOnClickListener(view -> {
            userName = binding.userTxt.getText().toString();
            email = binding.emailTxt.getText().toString();
            password = binding.passTxt.getText().toString();

            if (this.email.isEmpty()) {
                this.binding.emailTxt.setError("Please enter your email address.");
            } else if (this.password.isEmpty()) {
                this.binding.passTxt.setError("Please enter your password.");
                this.binding.passTxt.setError(null);
            } else if (this.userName.isEmpty()) {
                this.binding.userTxt.setError("Please enter your name");
            } else {
                uploadData();
                this.binding.emailTxt.setError(null);
                this.binding.passTxt.setError(null);
                this.binding.userTxt.setError(null);
            }

        });

    }


    private void uploadData() {
        this.dialog.show();
        this.auth.createUserWithEmailAndPassword(
                        binding.emailTxt.getText().toString(),
                        this.binding.passTxt.getText().toString())
                .addOnCompleteListener(task -> {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        userName = binding.userTxt.getText().toString();
                        email = binding.emailTxt.getText().toString();
                        password = "";
                        String uid = (Objects.requireNonNull(auth.getCurrentUser())).getUid();
                        HashMap hashMap = new HashMap();
                        hashMap.put("userName", userName);
                        hashMap.put("email", email);
                        hashMap.put("id", uid);
                        hashMap.put("imageUrl", "default");
                        hashMap.put("OnlineStatus", true);


                        database.getReference().child("users")
                                .child((Objects.requireNonNull((task.getResult())
                                        .getUser())).getUid()).setValue(hashMap)
                                .addOnCompleteListener(task1 -> {

                                    if (task1.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                        finish();
                                        return;
                                    }
                                    Toast.makeText(SignUpActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                                });
                        return;
                    }
                    Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

    }
}