package com.szniloycoder.mychat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.szniloycoder.mychat.ChatsActivity;
import com.szniloycoder.mychat.Models.ChatsModel;
import com.szniloycoder.mychat.Models.EncryptionUtils;
import com.szniloycoder.mychat.Models.User;
import com.szniloycoder.mychat.R;
import com.szniloycoder.mychat.databinding.UserItemBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.viewHolder>{

    private final Context context;
    private final ArrayList<User> userList;
    private String lastMessage;

    public UserAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        User user = userList.get(position);
        holder.binding.userName.setText(user.getUserName());

        if ("default".equals(user.getImageUrl())) {
            holder.binding.profileImg.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageUrl()).into(holder.binding.profileImg);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatsActivity.class);
            intent.putExtra("userid", user.getId());
            context.startActivity(intent);
        });

        loadLastMessage(user.getId(), holder.binding.lastMsg, holder.binding.unseenMsgCount, holder.binding.txtTime);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        UserItemBinding binding;

        public viewHolder(View view) {
            super(view);
            this.binding = UserItemBinding.bind(view);
        }
    }

    private void loadLastMessage(String userId, TextView lastMessageTextView,
                                 TextView unseenMsgCountTextView, TextView timeTextView) {
        lastMessage = "default";
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("Chats").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int unseenMessagesCount = 0;
                long lastMessageTime = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatsModel chat = snapshot.getValue(ChatsModel.class);

                    boolean isChatRelevant = (chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userId)) ||
                            (chat.getReceiver().equals(userId) && chat.getSender().equals(currentUser.getUid()));

                    if (isChatRelevant && chat.getTime() != null && chat.getTime().longValue() > lastMessageTime) {
                        lastMessageTime = chat.getTime().longValue();
                        try {
                            lastMessage = EncryptionUtils.decrypt(chat.getMessage(), EncryptionUtils.getKeyFromString(chat.getKey()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (!chat.isIsseen() && chat.getReceiver().equals(currentUser.getUid())) {
                            unseenMessagesCount++;
                        }

                        int color = ContextCompat.getColor(context, R.color.Green);
                        lastMessageTextView.setTextColor(chat.isIsseen() || !chat.getReceiver().equals(currentUser.getUid()) ?
                                Color.GRAY : color);
                    }
                }

                if (lastMessageTime != 0) {
                    String timeFormatted = new SimpleDateFormat("hh:mm a").format(new Date(lastMessageTime));
                    lastMessageTextView.setText(lastMessage);
                    timeTextView.setText(timeFormatted);
                } else {
                    timeTextView.setVisibility(View.GONE);
                }

                if (unseenMessagesCount > 0) {
                    unseenMsgCountTextView.setVisibility(View.VISIBLE);
                    unseenMsgCountTextView.setText(String.valueOf(unseenMessagesCount));
                } else {
                    unseenMsgCountTextView.setVisibility(View.GONE);
                }

                lastMessage = "default";
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }
}