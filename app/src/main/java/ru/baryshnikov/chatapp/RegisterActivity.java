package ru.baryshnikov.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etUsername;
    EditText etPassword;
    Button btnRegister;

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initUI();

        auth = FirebaseAuth.getInstance();
    }

    private void initUI() {
        etEmail = findViewById(R.id.et_email);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = etEmail.getText().toString();
                String strUsername = etUsername.getText().toString();
                String strPassword = etPassword.getText().toString();
                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strUsername) ||
                        TextUtils.isEmpty(strPassword)) {
                    Toast.makeText(getApplicationContext(), R.string.all_fields_are_required, Toast.LENGTH_LONG).show();
                } else if (!checkPassword(strPassword)) {
                    Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_LONG).show();
                } else {
                    register(strEmail, strUsername, strPassword);
                }
            }
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void register(String strEmail, final String strUsername, String strPassword) {
        auth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            assert firebaseUser != null;
                            String userId = firebaseUser.getUid();
                            databaseReference = FirebaseDatabase.getInstance()
                                    .getReference("Users")
                                    .child(userId);
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userId);
                            hashMap.put("username", strUsername);
                            hashMap.put("imageURL", "default");
                            hashMap.put("status", "offline");
                            hashMap.put("about", "default");

                            databaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.you_cant_register, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private boolean checkPassword(String strPassword) {
        int digits = 0;
        int letters = 0;
        for (int i = 0; i < strPassword.length(); i++) {
            char c = strPassword.charAt(i);
            if (Character.isDigit(c)) {
                digits++;
            }
            if (Character.isLetter(c)) {
                letters++;
            }
        }
        return strPassword.length() >= 5 && digits > 0 && letters > 0;
    }
}
