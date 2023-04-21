package ru.baryshnikov.chatapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.baryshnikov.chatapp.MessageActivity;
import ru.baryshnikov.chatapp.R;
import ru.baryshnikov.chatapp.model.Chat;
import ru.baryshnikov.chatapp.model.User;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private boolean isChat;

    String message;

    public UserAdapter(Context context, List<User> users, boolean isChat) {
        this.context = context;
        this.users = users;
        this.isChat = isChat;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final User user = users.get(i);
        viewHolder.username.setText(user.getUsername());
        if (user.getImageURL().equals("default")) {
            viewHolder.profileImage.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getImageURL()).into(viewHolder.profileImage);
        }

        if(isChat){
            lastMessage(user.getId(), viewHolder.tvLastMessage, viewHolder.tvUnread);
        } else {
            viewHolder.tvUnread.setVisibility(View.GONE);
        }

        if (isChat) {
            if (user.getStatus().equals("online")) {
                viewHolder.civStatusOnline.setVisibility(View.VISIBLE);
                viewHolder.civStatusOffline.setVisibility(View.GONE);
            } else {
                viewHolder.civStatusOnline.setVisibility(View.GONE);
                viewHolder.civStatusOffline.setVisibility(View.VISIBLE);
            }
        } else {
            viewHolder.civStatusOnline.setVisibility(View.GONE);
            viewHolder.civStatusOffline.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra("userid", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView username;
        CircleImageView civStatusOnline;
        CircleImageView civStatusOffline;
        TextView tvLastMessage;
        TextView tvUnread;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.civ_profile_image);
            username = itemView.findViewById(R.id.tv_username);
            civStatusOnline = itemView.findViewById(R.id.civ_status_online);
            civStatusOffline = itemView.findViewById(R.id.civ_status_offline);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvUnread = itemView.findViewById(R.id.tv_unread);
        }
    }

    private void lastMessage(final String userId, final TextView textView, final TextView tvUnread) {
        message = "";

        final FirebaseUser firebaseUser = FirebaseAuth
                .getInstance()
                .getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("Chats");

        final AtomicInteger unreadCounter = new AtomicInteger(0);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) ||
                            chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(userId)) {
                        message = chat.getMessage();
                    }

                    if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userId) &&
                    !chat.isIsseen()){
                        unreadCounter.incrementAndGet();
                    };
                }

                textView.setText(message);
                if (unreadCounter.get() > 0) {
                    tvUnread.setVisibility(View.VISIBLE);
                    tvUnread.setText(unreadCounter.toString());
                } else {
                    tvUnread.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
