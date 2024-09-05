package com.szniloycoder.mychat.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.szniloycoder.mychat.Adapters.UserAdapter;
import com.szniloycoder.mychat.Models.ChatList;
import com.szniloycoder.mychat.Models.User;
import com.szniloycoder.mychat.R;
import com.szniloycoder.mychat.databinding.FragmentChatsBinding;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    private UserAdapter adapter;
    private FragmentChatsBinding binding;
    private FirebaseUser fUser;
    private ArrayList<User> list;
    private ArrayList<ChatList> usersList;
    private DatabaseReference reference;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialize();
        setupRecyclerView();
        loadChatList();
    }

    private void initialize() {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        list = new ArrayList<>();
        usersList = new ArrayList<>();
    }

    private void setupRecyclerView() {
        binding.recChatFrag.setHasFixedSize(true);
        binding.recChatFrag.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadChatList() {
        reference = FirebaseDatabase.getInstance().getReference().child("ChatList").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    usersList.add(snapshot.getValue(ChatList.class));
                }
                updateUserList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors.
            }
        });
    }

    private void updateUserList() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && isUserInChatList(user)) {
                        list.add(user);
                    }
                }
                updateRecyclerView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors.
            }
        });
    }

    private boolean isUserInChatList(User user) {
        for (ChatList chat : usersList) {
            if (user.getId().equals(chat.getId())) {
                return true;
            }
        }
        return false;
    }

    private void updateRecyclerView() {
        adapter = new UserAdapter(getContext(), list);
        binding.recChatFrag.setAdapter(adapter);
    }
}