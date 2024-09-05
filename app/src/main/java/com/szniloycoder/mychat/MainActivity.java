package com.szniloycoder.mychat;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.szniloycoder.mychat.Fragments.ChatsFragment;
import com.szniloycoder.mychat.Fragments.ProfileFragment;
import com.szniloycoder.mychat.Fragments.UserProFragment;
import com.szniloycoder.mychat.Models.User;
import com.szniloycoder.mychat.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ChatsFragment chatsFragment;
    FirebaseDatabase database;
    FirebaseUser firebaseUser;
    ProfileFragment profileFragment;
    DatabaseReference reference;
    FirebaseStorage storage;
    UserProFragment userProFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.gray));

        // Initialize Firebase components
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize fragments
        chatsFragment = new ChatsFragment();
        profileFragment = new ProfileFragment();
        userProFragment = new UserProFragment();


        // Check if user is logged in
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.chat) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, chatsFragment).commit();
            }
            if (item.getItemId() == R.id.people) {
                getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, profileFragment).commit();
            }
            if (item.getItemId() != R.id.profile) {
                return true;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, userProFragment).commit();
            return true;
        });

        // Set default fragment
        binding.bottomNavigation.setSelectedItemId(R.id.chat);

        // Load user data
        getData();

    }


    private void getData() {
        DatabaseReference child = database.getReference().child("users").child(firebaseUser.getUid());
        reference = child;
        child.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = (User) dataSnapshot.getValue(User.class);
                assert user != null;
                binding.userName.setText(user.getUserName());
                if (user.getImageUrl().equals("default")) {
                    binding.profileImg.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext()).load(user.getImageUrl())
                            .into(binding.profileImg);
                }
            }

            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

}