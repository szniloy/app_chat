package com.szniloycoder.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.szniloycoder.mychat.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseDatabase database;
    ProgressDialog dialog;
    String email;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.activity_color));

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();


        ProgressDialog progressDialog = new ProgressDialog(this);
        this.dialog = progressDialog;
        progressDialog.setTitle("Log in your account");
        this.dialog.setMessage("Please wait...");

        binding.btnSignUp.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            intent.putExtra("fromSignup", true);
            startActivity(intent);
        });

        if (this.currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

//        binding.btnForgetPass.setOnClickListener(view -> {
//            Intent intent = new Intent(this, ForgetPassActivity.class);
//            startActivity(intent);
//        });

        binding.btnLogIn.setOnClickListener(view -> {
            email = binding.emailTxt.getText().toString();
            password = binding.passTxt.getText().toString();

            if (this.email.isEmpty()) {
                this.binding.emailTxt.setError("Please enter your email address.");
            } else if (this.password.isEmpty()) {
                this.binding.passTxt.setError("Please enter your password.");
                this.binding.emailTxt.setError((CharSequence) null);
            } else {
                uploadData();
                this.binding.emailTxt.setError((CharSequence) null);
                this.binding.passTxt.setError((CharSequence) null);
            }
        });

    }

    private void uploadData() {
        this.dialog.show();
        this.auth.signInWithEmailAndPassword(this.binding.emailTxt.getText().toString(),
                        this.binding.passTxt.getText().toString())
                .addOnCompleteListener(task -> {

                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                        return;
                    }
                    Toast.makeText(LoginActivity.this, "Incorrect Username and password", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e ->
                        Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());

    }


}