package com.example.gaayathri.logindemo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.chat21.android.ui.login.activities.ChatLoginActivity;
import org.chat21.android.ui.login.activities.ChatSplashActivity;

import static org.chat21.android.utils.DebugConstants.DEBUG_LOGIN;

/**
 * Created by stefanodp91 on 21/12/17.
 */

public class SplashActivity extends ChatSplashActivity {


    @Override
    protected Class<?> getTargetClass() {
        Log.d(DEBUG_LOGIN, "SplashActivity.getTargetClass");
        return SplashScreenActivity.class;
    }

//    @Override
//    protected Intent getLoginIntent() {
//        return new Intent(this, ChatLoginActivity.class);
//    }

}