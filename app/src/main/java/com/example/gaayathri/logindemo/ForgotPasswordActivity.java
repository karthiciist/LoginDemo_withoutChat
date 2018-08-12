package com.example.gaayathri.logindemo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText passwordEmail;
    private Button resetButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        passwordEmail = (EditText) findViewById(R.id.etForgotEmail);
        resetButton = (Button) findViewById(R.id.btnResetPassword);
        firebaseAuth = FirebaseAuth.getInstance();

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userPassword = passwordEmail.getText().toString().trim();

                if (userPassword.equals("")) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your registered email!", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.sendPasswordResetEmail(userPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Password reset mail has ben sent!", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent (ForgotPasswordActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this, "Could not send password reset mail!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
