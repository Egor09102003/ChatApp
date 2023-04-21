package ru.baryshnikov.chatapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.List;

import ru.baryshnikov.chatapp.R;
import ru.baryshnikov.chatapp.adapter.UserAdapter;
import ru.baryshnikov.chatapp.model.Chatlist;
import ru.baryshnikov.chatapp.model.User;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> users;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    private List<Chatlist> usersList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        usersList = new ArrayList<>();

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Chatlist")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersList.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void chatList(){
        users = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    for(Chatlist chatlist:usersList){
                        if(user.getId().equals(chatlist.getId())){
                            users.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), users, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
