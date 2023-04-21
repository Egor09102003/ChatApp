package ru.baryshnikov.chatapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import ru.baryshnikov.chatapp.R;
import ru.baryshnikov.chatapp.model.Chat;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private static final int MESSAGE_TYPE_LEFT = 0;
    private static final int MESSAGE_TYPE_RIGHT = 1;

    private Context context;
    private List<Chat> chats;
    private String imageurl;

    private FirebaseUser firebaseUser;

    public MessageAdapter(Context context, List<Chat> chats, String imageurl) {
        this.context = context;
        this.chats = chats;
        this.imageurl = imageurl;
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_item_right, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.chat_item_left, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chat chat = chats.get(position);
        holder.tv_show_message.setText(chat.getMessage());

        if (imageurl.equals("default")) {
            holder.civ_profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(imageurl).into(holder.civ_profile_image);
        }

        if (position == chats.size() - 1) {
            if (chat.isIsseen()) {
                holder.tv_msg_seen.setText(R.string.seen);
            } else {
                holder.tv_msg_seen.setText(R.string.delivered);
            }
        } else {
            holder.tv_msg_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chats.get(position).getSender().equals(firebaseUser.getUid())) {
            return MESSAGE_TYPE_RIGHT;
        } else {
            return MESSAGE_TYPE_LEFT;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView civ_profile_image;
        TextView tv_show_message;
        TextView tv_msg_seen;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            civ_profile_image = itemView.findViewById(R.id.civ_profile_image);
            tv_show_message = itemView.findViewById(R.id.tv_show_message);
            tv_msg_seen = itemView.findViewById(R.id.tv_msg_seen);
        }
    }
}
