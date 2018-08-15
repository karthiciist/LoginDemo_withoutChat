package com.example.gaayathri.logindemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.chat21.android.core.ChatManager;
import org.chat21.android.core.authentication.ChatAuthentication;
import org.chat21.android.ui.login.activities.ChatLoginActivity;

import static org.chat21.android.utils.DebugConstants.DEBUG_LOGIN;

public class SplashScreenActivity extends AppCompatActivity {



    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestoreDB;
    private static final String TAG = "SplashscreenActivity";

    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";

    public static final String City = "cityKey";
    public static final String Degree = "degreeKey";

    String userNameL;
    String userPhoneL;
    String userDegreeL;
    String userSpecialL;
    String userCityL;
    String userProfilePicL;

    String email;

    //protected abstract Class<?> getTargetClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);

        ChatAuthentication.getInstance().setTenant(ChatManager.Configuration.appId);
        ChatAuthentication.getInstance().createAuthListener(onAuthStateChangeListener);


        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();

    }

    private class LogoLauncher extends Thread{

        public void run(){
            try{
                sleep(100);
            }catch (InterruptedException e){
                e.printStackTrace();
            }

            sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

            firestoreDB = FirebaseFirestore.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();

            FirebaseUser currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {

                final String mail = firebaseAuth.getCurrentUser().getEmail();

                DocumentReference docRef = firestoreDB.collection("users").document(mail);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            //String userCityL;
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                                userNameL = document.getString("UserName");
                                userPhoneL = document.getString("UserPhoneNo");
                                userDegreeL = document.getString("UserDegree");
                                userSpecialL = document.getString("UserSpecial");
                                userCityL = document.getString("UserCity");
                                userProfilePicL = document.getString("downloadUri");

                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(City, userCityL);
                                editor.putString(Degree, userDegreeL);
                                editor.commit();

                                Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
                                startActivity(intent);

                                SplashScreenActivity.this.finish();

                            } else {
                                Log.d(TAG, "No such document");
                            }

                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            } else {

                Intent intent = new Intent(SplashScreenActivity.this, EntryActivity.class);
                startActivity(intent);

                SplashScreenActivity.this.finish();

            }

            //Intent intent = new Intent(SplashScreenActivity.this, EntryActivity.class);
            //startActivity(intent);

        }

    }

    private static final int LOGIN_REQUEST = 0;
    private static final int TARGET_REQUEST = 1;

    private ChatAuthentication.OnAuthStateChangeListener onAuthStateChangeListener =
            new ChatAuthentication.OnAuthStateChangeListener() {
                @Override
                public void onAuthStateChanged(FirebaseUser user) {
                    runDispatch();
                }
            };

    private void runDispatch() {
        Log.d(DEBUG_LOGIN, "ChatSplashActivity.runDispatch");
        // If current user has already logged in launch the target activity,
        // else launch the login activity
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            //Log.d(DEBUG_LOGIN, "ChatSplashActivity.runDispatch: user is logged in. Goto : " + getTargetClass().getName());
            Intent targetIntent = new Intent(this, HomeActivity.class);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                targetIntent.putExtras(extras);
            }
            startActivityForResult(targetIntent, TARGET_REQUEST);
        } else {
            Log.d(DEBUG_LOGIN, "ChatSplashActivity.runDispatch: user is not logged in. Goto  ChatLoginActivity");
            // Send user to login activity
//            startActivityForResult(getLoginIntent(), LOGIN_REQUEST);
            startActivityForResult(new Intent(this, MainActivity.class), LOGIN_REQUEST);
        }
    }

}
