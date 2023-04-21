package ru.baryshnikov.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.baryshnikov.chatapp.fragment.ChatsFragment;
import ru.baryshnikov.chatapp.fragment.ProfileFragment;
import ru.baryshnikov.chatapp.fragment.UsersFragment;
import ru.baryshnikov.chatapp.model.Chat;
import ru.baryshnikov.chatapp.model.User;

public class MainActivity extends AppCompatActivity {

    CircleImageView civProfileImage;
    TextView tvUsername;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        civProfileImage = findViewById(R.id.civ_profile_image);
        tvUsername = findViewById(R.id.tv_username);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvUsername.setText(user.getUsername());
                if (user.getImageURL().equals("default")) {
                    civProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .into(civProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        final TabLayout tabLayout = findViewById(R.id.tab_layout);
        final ViewPager viewPager = findViewById(R.id.view_pager);

        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

                int unread = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(firebaseUser.getUid()) &&
                            !chat.isIsseen()) {
                        unread++;
                    }
                }

                if (unread == 0) {
                    viewPagerAdapter.addFragment(new ChatsFragment(), getString(R.string.chats));
                } else {
                    viewPagerAdapter.addFragment(new ChatsFragment(), getString(R.string.chats) +
                            " (" + unread + ")");
                }

                viewPagerAdapter.addFragment(new UsersFragment(), getString(R.string.users));
                viewPagerAdapter.addFragment(new ProfileFragment(), getString(R.string.profile));
                viewPager.setAdapter(viewPagerAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, StartActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            fragments = new ArrayList<>();
            titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int i) {
            return titles.get(i);
        }
    }

    private void status(String status) {
        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        databaseReference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
