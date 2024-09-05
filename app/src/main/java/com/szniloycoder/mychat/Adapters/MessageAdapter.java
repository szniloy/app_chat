package com.szniloycoder.mychat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.szniloycoder.mychat.Models.ChatsModel;
import com.szniloycoder.mychat.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    private final Context context;
    private final ArrayList<ChatsModel> list;
    private final String imageUrl;
    private FirebaseUser fUser;

    public MessageAdapter(Context context, ArrayList<ChatsModel> list, String imageUrl) {
        this.context = context;
        this.list = list;
        this.imageUrl = imageUrl;
    }


    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {

        ChatsModel chatsModel = list.get(position);

        // Set message and visibility
        holder.showMessage.setText(chatsModel.getMessage());
        if ("image".equals(chatsModel.getType())) {
            holder.showMessage.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            Glide.with(context).load(chatsModel.getMessage()).into(holder.imageView);
        } else {
//            holder.imageView.setVisibility(View.GONE);
            holder.showMessage.setVisibility(View.VISIBLE);
        }


        if (holder.profile_img != null) {
            if ("default".equals(imageUrl)) {
                holder.profile_img.setImageResource(R.mipmap.ic_launcher);
            } else {
                Glide.with(context).load(imageUrl).into(holder.profile_img);
            }
        } else {
            Log.e("MessageAdapter", "profile_img is null at position " + position);
        }

        // Set profile image
//        if ("default".equals(imageUrl)) {
//            holder.profile_img.setImageResource(R.mipmap.ic_launcher);
//        } else {
//            Glide.with(context).load(imageUrl).into(holder.profile_img);
//        }


//        // Set message seen status
//        if (position != list.size() - 1) {
//            holder.txt_seen.setVisibility(View.GONE);
//        } else {
//            holder.txt_seen.setVisibility(View.VISIBLE);
//            holder.txt_seen.setText(chatsModel.isIsseen() ? "✔✔" : "✔");
//        }


        // Debugging: Log to check if txt_seen is null
        if (holder.txt_seen == null) {
            Log.e("MessageAdapter", "txt_seen is null at position " + position);
        }

        // Existing logic
        if (holder.txt_seen != null) {
            if (position != list.size() - 1) {
                holder.txt_seen.setVisibility(View.GONE);
            } else {
                holder.txt_seen.setVisibility(View.VISIBLE);
                holder.txt_seen.setText(chatsModel.isIsseen() ? "✔✔" : "✔");
            }
        }



        // Set message time and date
        if (chatsModel.getTime() != null) {
            String date = getDate(chatsModel.getTime());
            String time = getTime(chatsModel.getTime());
            holder.dateShowTxt.setText(date);
            holder.txtTime.setText(time);
        } else {
            holder.dateShowTxt.setText("");
            holder.txtTime.setText("");
        }

        // Show or hide date based on message position
        if (position == 0 || !getDate(chatsModel.getTime()).equals(getDate(list.get(position - 1).getTime()))) {
            holder.dateShowTxt.setVisibility(View.VISIBLE);
        } else {
            holder.dateShowTxt.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return  list.size();
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        return list.get(position).getSender().equals(fUser.getUid()) ? MSG_TYPE_RIGHT : MSG_TYPE_LEFT;
    }

    public static String getDate(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static String getTime(Long timestamp) {
        if (timestamp == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }


    public  class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView dateShowTxt;
        public final ImageView imageView;
        public final CircleImageView profile_img;
        public final TextView showMessage;
        public final TextView txtTime;
        public final TextView txt_seen;

        public ViewHolder(View view) {
            super(view);
            showMessage = view.findViewById(R.id.showMessage);
            txt_seen = view.findViewById(R.id.txt_seen);
            dateShowTxt = view.findViewById(R.id.dateShowTxt);
            txtTime = view.findViewById(R.id.txtTime);
            imageView = view.findViewById(R.id.textImg);
            profile_img = view.findViewById(R.id.profileImg);
        }
    }
}
