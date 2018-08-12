package com.example.gaayathri.logindemo;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.content.ContentValues.TAG;

public abstract class AbstactClass extends Activity
{

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;

    String userNameL;
    String userPhoneL;
    String userDegreeL;
    String userSpecialL;
    String userCityL;
    String userProfilePicL;
/*
    firestoreDB = FirebaseFirestore.getInstance();
    firebaseAuth = FirebaseAuth.getInstance();

    DocumentReference docRef = firestoreDB.collection("users").document(mail);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                userNameL = document.getString("UserName");
                userPhoneL = document.getString("UserPhoneNo");
                userDegreeL = document.getString("UserDegree");
                userSpecialL = document.getString("UserSpecial");
                userCityL = document.getString("UserCity");
                userProfilePicL = document.getString("downloadUri");

                TextView userCity = findViewById(R.id.tvCity);
                userCity.setText(userCityL);

                //dummy(userCityL);

                //loadNotesList(userCityL);

            } else {
                Log.d(TAG, "No such document");
            }
        } else {
            Log.d(TAG, "get failed with ", task.getException());
        }
    }
    */

}
