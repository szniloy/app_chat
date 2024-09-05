package com.szniloycoder.mychat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.szniloycoder.mychat.Adapters.MessageAdapter;
import com.szniloycoder.mychat.Models.ChatsModel;
import com.szniloycoder.mychat.Models.EncryptionUtils;
import com.szniloycoder.mychat.Models.User;
import com.szniloycoder.mychat.databinding.ActivityChatsBinding;

import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.SecretKey;

public class ChatsActivity extends AppCompatActivity {


    // Views and Data
    private MessageAdapter adapter;
    private FirebaseAuth auth;
    private ActivityChatsBinding binding;
    private ArrayList<ChatsModel> chatsList;
    private FirebaseDatabase database;
    private FirebaseUser fUser;
    private Intent intent;
    private DatabaseReference reference;
    private ValueEventListener seenListener;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // statusBar color:
        getWindow().setStatusBarColor(getResources().getColor(R.color.Green));

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        fUser = auth.getCurrentUser();
        chatsList = new ArrayList<>();

        if (fUser == null) {
            // Redirect to login if the user is not authenticated
            startActivity(new Intent(ChatsActivity.this, LoginActivity.class));
            finish();
            return;
        }


        binding.recChatsView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        binding.recChatsView.setLayoutManager(layoutManager);


        //getDataFromAdapter:
        intent = getIntent();

        userid = intent.getStringExtra("userid");


        binding.btnBack.setOnClickListener(view -> {
            finish();
        });

        //callActivity:
//        binding.btnVideo.setOnClickListener(view -> {
//            initiateVideoCall();
//        });

        binding.btnSend.setOnClickListener(view -> {
            String msg = binding.TxtSend.getText().toString();
            if (!msg.equals("")) {
                senMessage(fUser.getUid(),userid,msg);
            }else {
                Toast.makeText(this, "Type some txt", Toast.LENGTH_SHORT).show();
            }
            binding.TxtSend.setText("");
        });


        //setUserData:
        reference = database.getReference().child("users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User user = snapshot.getValue(User.class);
                if (user != null) {
                    binding.nameTxt.setText(user.getUserName());

//                    String onlineStatus = ""+ snapshot.child("OnlineStatus").getValue();
//                    if (onlineStatus.equals("online")){
//                        binding.statusTxt.setText(onlineStatus);
//                    }else {
//                        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//                        cal.setTimeInMillis(Long.parseLong(onlineStatus));
//                        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();
//                        binding.statusTxt.setText("Last seen at: "+dateTime);
//                    }

                    if ("default".equals(user.getImageUrl())) {
                        binding.profileImg.setImageResource(R.mipmap.ic_launcher);
                    } else {
                        Glide.with(getApplicationContext()).load(user.getImageUrl()).into(binding.profileImg);
                    }

                    //getReadMessageData:
                    readMessage(fUser.getUid(), userid, user.getImageUrl());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //
        seenMessage(userid);
    }


    //code for user txt seen status:  //**
    private void seenMessage(final String userid){
        reference =FirebaseDatabase.getInstance().getReference().child("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    ChatsModel model = snapshot1.getValue(ChatsModel.class);
                    if (model.getReceiver().equals(fUser.getUid()) && model.getSender().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot1.getRef().updateChildren(hashMap);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //send encrypte style txt:
    private void senMessage(String sender, String receiver, String message) {
        try {
            // Generate a new encryption key
            SecretKey secretKey = EncryptionUtils.generateKey();
            String encryptedMessage = EncryptionUtils.encrypt(message, secretKey);
            String keyString = EncryptionUtils.keyToString(secretKey);

            DatabaseReference reference1 = database.getReference();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("sender", sender);
            hashMap.put("receiver", receiver);
            hashMap.put("message", encryptedMessage);
            hashMap.put("isseen", false);
            hashMap.put("time", System.currentTimeMillis());
            hashMap.put("key", keyString); // Store the key

            reference1.child("Chats").push().setValue(hashMap);

            // Create chatIdList:
            DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("ChatList")
                    .child(fUser.getUid())
                    .child(userid);
            chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        chatRef.child("id").setValue(userid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //decrypt readMessage:
    private void readMessage(String myid, String userid, String imageurl) {
        reference = database.getReference().child("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                chatsList.clear();

                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    ChatsModel model = snapshot1.getValue(ChatsModel.class);

                    assert model != null;
                    if (model.getReceiver().equals(myid) && model.getSender().equals(userid) ||
                            model.getReceiver().equals(userid) && model.getSender().equals(myid)) {

                        //get dedecryptedMessage:
                        try {
                            // Get the encrypted message and key
                            String encryptedMessage = model.getMessage();
                            String secretKeyString = model.getKey();
                            SecretKey secretKey = EncryptionUtils.getKeyFromString(secretKeyString);

                            // Decrypt the message
                            String decryptedMessage = EncryptionUtils.decrypt(encryptedMessage, secretKey);
                            model.setMessage(decryptedMessage);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        chatsList.add(model);
                    }
                }
                adapter = new MessageAdapter(ChatsActivity.this, chatsList, imageurl);
                binding.recChatsView.setAdapter(adapter);

                // Scroll to the last message
                binding.recChatsView.scrollToPosition(chatsList.size() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }



//    private synchronized void status(String status){
//        if (fUser != null) {
//            reference = database.getReference().child("users").child(fUser.getUid());
//
//            HashMap<String, Object> hashMap = new HashMap<>();
//            hashMap.put("status", status);
//
//            reference.updateChildren(hashMap);
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        status("online");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        status("offline");
//    }

//    private void checkOnlineStatus(String status){
//        DatabaseReference dRef = database.getReference().child("users").child(fUser.getUid());
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("OnlineStatus",status);
//        dRef.updateChildren(hashMap);
//    }
//
//    @Override
//    protected void onStart() {
//        checkOnlineStatus("online");
//        super.onStart();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        //get Time:
//        String timestamp = String.valueOf(System.currentTimeMillis());
//        checkOnlineStatus(timestamp);
//    }
//
//    @Override
//    protected void onResume() {
//        checkOnlineStatus("online");
//        super.onResume();
//
//    }




    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener); //**
    }
}