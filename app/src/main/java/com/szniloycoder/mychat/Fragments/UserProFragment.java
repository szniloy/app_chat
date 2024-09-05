package com.szniloycoder.mychat.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.szniloycoder.mychat.LoginActivity;
import com.szniloycoder.mychat.Models.User;
import com.szniloycoder.mychat.PasswordManagerActivity;
import com.szniloycoder.mychat.ProfileActivity;
import com.szniloycoder.mychat.R;
import com.szniloycoder.mychat.databinding.FragmentUserProBinding;

public class UserProFragment extends Fragment {
    FirebaseAuth auth;
    FragmentUserProBinding binding;
    Activity context;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    DatabaseReference reference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context = getActivity();
        binding = FragmentUserProBinding.inflate(getLayoutInflater());
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_user_pro, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        setUserData();

        // Set button click listeners
        binding.btnLogOut.setOnClickListener(v -> showAlertDialog());
        binding.btnProfile.setOnClickListener(v -> startActivity(new Intent(context, ProfileActivity.class)));
        binding.btnPasswordManager.setOnClickListener(v -> startActivity(new Intent(context, PasswordManagerActivity.class)));


//        binding.btnPrivacy.setOnClickListener(v ->
//                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show());
//        binding.btnHelp.setOnClickListener(v ->
//                Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setUserData() {
        reference = database.getReference().child("users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                binding.userProNameTxt.setText(user.getUserName());

                if ("default".equals(user.getImageUrl())) {
                    binding.proImgView.setImageResource(R.drawable.profile);
                } else {
                    Glide.with(context).load(user.getImageUrl()).into(binding.proImgView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle database error if necessary
            }
        });
    }

    @SuppressLint("MissingInflatedId")
    private void showAlertDialog() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btnYesLog).setOnClickListener(v -> {
            auth.signOut();
            startActivity(new Intent(context, LoginActivity.class));
        });

        dialogView.findViewById(R.id.btnCancelLog).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}