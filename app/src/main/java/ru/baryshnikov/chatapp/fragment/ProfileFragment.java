package ru.baryshnikov.chatapp.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import ru.baryshnikov.chatapp.R;
import ru.baryshnikov.chatapp.model.User;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    CircleImageView civProfileImage;
    TextView tvUsername;
    TextView tvAbout;

    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        civProfileImage = view.findViewById(R.id.civ_profile_image);
        tvUsername = view.findViewById(R.id.tv_username);
        tvAbout = view.findViewById(R.id.tv_about);

        storageReference = FirebaseStorage.getInstance().getReference("Uploads");

        firebaseUser = FirebaseAuth
                .getInstance()
                .getCurrentUser();

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(firebaseUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvUsername.setText(user.getUsername());

                if (user.getImageURL().equals("default")) {
                    civProfileImage.setImageResource(R.mipmap.ic_launcher);
                } else {
                    Glide.with(getContext()).load(user.getImageURL()).into(civProfileImage);
                }

                if(!user.getAbout().equals("default")){
                    tvAbout.setText(user.getAbout());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        civProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        return view;
    }

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.uploading));
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "");
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String uri = downloadUri.toString();
                        databaseReference = FirebaseDatabase
                                .getInstance()
                                .getReference("Users")
                                .child(firebaseUser.getUid());

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", uri);
                        databaseReference.updateChildren(map);
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),
                            getString(R.string.unknown_error),
                            Toast.LENGTH_LONG)
                            .show();
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(),
                    getString(R.string.no_image_selected),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK &&
                data != null && data.getData() != null) {
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getContext(),
                        getString(R.string.upload_in_progress),
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                uploadImage();
            }
        }
    }
}
