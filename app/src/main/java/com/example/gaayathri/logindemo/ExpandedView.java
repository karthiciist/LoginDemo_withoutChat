package com.example.gaayathri.logindemo;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.like.LikeButton;
import com.like.OnAnimationEndListener;
import com.like.OnLikeListener;

import java.util.HashMap;
import java.util.Map;


public class ExpandedView extends AppCompatActivity implements OnLikeListener, OnAnimationEndListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private TextView bookName, authName, degreeName, specialName, price, location, timestamp, postedUser;
    private Toolbar toolbar;
    private DocumentReference docRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expanded_view);

        LikeButton likeButton = findViewById(R.id.heart_button);
        likeButton.setOnLikeListener(this);
        likeButton.setOnAnimationEndListener(this);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.main_color));
        }

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        bookName = findViewById(R.id.bookName);
        authName = findViewById(R.id.authorName);
        degreeName = findViewById(R.id.degreeName);
        specialName = findViewById(R.id.specialName);
        price = findViewById(R.id.price);
        location = findViewById(R.id.locationName);
        postedUser = findViewById(R.id.postedUser);


        firestoreDB = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        String entryName = bundle.getString("arg");
        String email = firebaseAuth.getCurrentUser().getEmail();



        DocumentReference docRef = firestoreDB.collection("books").document(entryName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String lbookName = (String) document.get ("title");
                        String lauthName = (String) document.get ("author");
                        String ldegreeName = (String) document.get ("degree");
                        String lspecialName = (String) document.get ("specialization");
                        String lprice = (String) document.get ("price");
                        String llocation = (String) document.get ("location");
                        String ltimestamp = (String) document.get ("timestamp");
                        String luserName = (String) document.get ("postedUser");
                        String lentryName = (String) document.get("entryName");

                        bookName.setText(lbookName);
                        authName.setText(lauthName);
                        degreeName.setText(ldegreeName);
                        specialName.setText(lspecialName);
                        price.setText(lprice);
                        location.setText(llocation);
                        postedUser.setText(luserName);



                    } else {
                        Toast.makeText(ExpandedView.this, "Book info is unavailable", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ExpandedView.this, "Cannot retrieve data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        checkFavorited();

    }

    @Override
    public void liked(LikeButton likeButton) {

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        String entryName = bundle.getString("arg");
        String email = firebaseAuth.getCurrentUser().getEmail();
        String uuid = firebaseAuth.getCurrentUser().getUid();

        final Map<String, String> bookMap = new HashMap<>();

        bookMap.put("title", bookName.getText().toString());
        bookMap.put("author", authName.getText().toString());
        bookMap.put("degree", degreeName.getText().toString());
        bookMap.put("specialization", specialName.getText().toString());
        bookMap.put("location", location.getText().toString());
        bookMap.put("price", price.getText().toString());
        bookMap.put("postedUser",postedUser.getText().toString());
        bookMap.put("entryName", entryName);

        firestoreDB.collection("users").document(email).collection("favorites").document(entryName).set(bookMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ExpandedView.this, "Added to favorites!!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExpandedView.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
            }
        });

        Map<String, Object> data = new HashMap<>();
        data.put(uuid, true);

        firestoreDB.collection("books").document(entryName)
                .set(data, SetOptions.merge());
    }

    @Override
    public void unLiked(LikeButton likeButton) {

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        String entryName = bundle.getString("arg");
        String email = firebaseAuth.getCurrentUser().getEmail();
        String uuid = firebaseAuth.getCurrentUser().getUid();

        firestoreDB.collection("users").document(email).collection("favorites").document(entryName)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ExpandedView.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ExpandedView.this, "Check your internet connection!!!", Toast.LENGTH_SHORT).show();
                    }
                });


        Map<String, Object> data = new HashMap<>();
        data.put(uuid, false);

        firestoreDB.collection("books").document(entryName)
                .set(data, SetOptions.merge());
    }

    @Override
    public void onAnimationEnd(LikeButton likeButton) {
        Log.d("ExpandedView", "Animation End for %s" + likeButton);
    }

    private void checkFavorited() {

        Intent intentExtras = getIntent();
        Bundle bundle = intentExtras.getExtras();
        String entryName = bundle.getString("arg");
        String email = firebaseAuth.getCurrentUser().getEmail();

        final LikeButton likeButton = findViewById(R.id.heart_button);
        likeButton.setOnLikeListener(this);
        likeButton.setOnAnimationEndListener(this);

        DocumentReference docRef = firestoreDB.collection("users").document(email).collection("favorites").document(entryName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    likeButton.setLiked(true);

                } else {
                    likeButton.setLiked(false);
                }
            }
        });
    }
}
