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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail;
    EditText etPassword;
    Button btnLogin;

    FirebaseAuth auth;
    TextView tvForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initUI();
    }

    private void initUI() {
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById((R.id.et_password));

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strEmail = etEmail.getText().toString();
                String strPassword = etPassword.getText().toString();
                if (TextUtils.isEmpty(strEmail) || TextUtils.isEmpty(strPassword)) {
                    Toast.makeText(getApplicationContext(), R.string.all_fields_are_required, Toast.LENGTH_LONG).show();
                } else {
                    login(strEmail, strPassword);
                }
            }
        });

        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void login(String strEmail, String strPassword) {
        auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.you_cant_login, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
