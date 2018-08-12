package com.example.gaayathri.logindemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText userName, userPassword, userEmail, userCity;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private FirebaseFirestore firestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        setupUIViews();

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()){

                    final String user_email = userEmail.getText().toString().trim();
                    String user_password = userPassword.getText().toString().trim();
                    final String user_name = userName.getText().toString().trim();
                    final String user_city = userCity.getText().toString().trim();

                    firebaseAuth.createUserWithEmailAndPassword(user_email, user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            progressDialog.setMessage("Registering...");
                            progressDialog.show();

                            if (task.isSuccessful()){
                                //Toast.makeText(RegistrationActivity.this, "Registration Sucessful !", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                sendEmailVerification();


                                Map<String, String> userMap = new HashMap<>();

                                userMap.put("name", user_name);
                                userMap.put("email", user_email );
                                userMap.put("city", user_city);

                                firestore.collection("users").document(user_email).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //Toast.makeText(RegistrationActivity.this, "User profile created successfully!!!", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(RegistrationActivity.this, EntryActivity.class);
                                        startActivity(intent);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        String error = e.getMessage();

                                        Toast.makeText(RegistrationActivity.this, "Cannot create user profile. Error:" + error, Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }else{
                                Toast.makeText(RegistrationActivity.this, "Registration Failed !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }
        });

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this, EntryActivity.class));
            }
        });
    }

    private void setupUIViews (){

        userName = findViewById(R.id.etUserName);
        userPassword = findViewById(R.id.etUserPassword);
        userEmail = findViewById(R.id.etUserEmail);
        regButton = findViewById(R.id.btnRegister);
        userLogin = findViewById(R.id.tvUserLogin);
        userCity = findViewById(R.id.etUserCity);

    }

    private boolean validate (){

        Boolean result = false;

        String name = userName.getText().toString();
        String password = userPassword.getText().toString();
        String email = userEmail.getText().toString();
        String city = userCity.getText().toString();

        if (name.isEmpty() || password.isEmpty() || email.isEmpty() || city.isEmpty()){
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show();
        }else{
            result = true;
        }

        return result;
    }

    private void sendEmailVerification(){
        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered. Verification mail has been sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        //startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                    }else{
                        Toast.makeText(RegistrationActivity.this, "Verification mail can't be sent!", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
    }

}
