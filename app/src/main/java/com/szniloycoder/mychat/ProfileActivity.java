package com.szniloycoder.mychat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.szniloycoder.mychat.Models.User;
import com.szniloycoder.mychat.databinding.ActivityProfileBinding;

import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    private static final int IMAGE_REQ = 1;
    FirebaseAuth auth;
    FirebaseDatabase database;
    String email;
    FirebaseUser fUser;
    Uri imageUri;
    DatabaseReference reference;
    StorageReference storageReference;
    StorageTask uploadTask;
    String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(getResources().getColor(R.color.activity_color));

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = FirebaseStorage.getInstance().getReference().child("upproimg");

        setUserProfile();

        binding.nameTxt.setEnabled(false);
        binding.emlTxt.setEnabled(false);

        binding.proImgView.setOnClickListener(view -> {
            openImgFolder();
        });

        binding.btnUpdatePro.setOnClickListener(view -> {
            userName = binding.nameTxt.getText().toString().trim();
            email = binding.emlTxt.getText().toString().trim();
            updateUserProfile(userName);
        });

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });

        binding.editProfile.setOnClickListener(view -> {
            toggleEditMode();
        });
    }


    //toggle:
    private void toggleEditMode() {
        boolean z = !this.binding.nameTxt.isEnabled();
        this.binding.nameTxt.setEnabled(z);
        if (z) {
            this.binding.editProfile.setImageResource(R.drawable.edit_off);
            Toast.makeText(this, "Edit mode on", Toast.LENGTH_SHORT).show();
            return;
        }
        this.binding.editProfile.setImageResource(R.drawable.edit_on);
        Toast.makeText(this, "Edit mood off", Toast.LENGTH_SHORT).show();
    }

    //setUserData:

    private void setUserProfile() {
        String uid = (Objects.requireNonNull(auth.getCurrentUser())).getUid();
        if (uid != null) {
            database.getReference().child("users").child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user;
                            if (dataSnapshot.exists() && (user = (User) dataSnapshot.getValue(User.class)) != null) {
                                binding.nameTxt.setText(user.getUserName());
                                binding.emlTxt.setText(user.getEmail());
                                if (user.getImageUrl().equals("default")) {
                                    binding.proImgView.setImageResource(R.drawable.profile);
                                } else {
                                    Glide.with(ProfileActivity.this)
                                            .load(user.getImageUrl()).into(binding.proImgView);
                                }
                            }
                        }

                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }

                    });
        }
    }


    private void updateUserProfile(String name) {
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        if (uid != null) {
            DatabaseReference userRef = database.getReference("users").child(uid);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setUserName(name);
                        userRef.setValue(user).addOnSuccessListener(aVoid -> {
                            Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }


    private void openImgFolder() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQ);
    }

    private void uploadImage() {
        if (imageUri != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading");
            progressDialog.show();

            String fileName = System.currentTimeMillis() + "." + getFileExtension(imageUri);
            StorageReference imgRef = storageReference.child(fileName);

            uploadTask = imgRef.putFile(imageUri);
            uploadTask.continueWithTask(task -> {
                if (task.isSuccessful()) {
                    return imgRef.getDownloadUrl();
                } else {
                    throw task.getException();
                }
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = (Uri) task.getResult();
                    updateImageInDatabase(downloadUri.toString(), progressDialog);
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImageInDatabase(String imageUrl, ProgressDialog progressDialog) {
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(fUser.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("imageUrl", imageUrl);
        reference.updateChildren(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(this, "Profile update successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQ && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadTask == null || !uploadTask.isInProgress()) {
                uploadImage();
            } else {
                Toast.makeText(this, "Upload in progress", Toast.LENGTH_SHORT).show();
            }
        }
    }
}