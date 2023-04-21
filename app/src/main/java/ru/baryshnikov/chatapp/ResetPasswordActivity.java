package ru.baryshnikov.chatapp;

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
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordActivity extends AppCompatActivity {

    EditText etSendEmail;
    Button btnReset;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.reset);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etSendEmail = findViewById(R.id.et_send_email);
        btnReset = findViewById(R.id.btn_reset);

        firebaseAuth = FirebaseAuth.getInstance();
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etSendEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(),
                            R.string.all_fields_are_required,
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),
                                                R.string.check_your_email,
                                                Toast.LENGTH_LONG)
                                                .show();
                                        finish();
                                    } else {
                                        String error = task.getException().getMessage();
                                        Toast.makeText(getApplicationContext(),
                                                error,
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
