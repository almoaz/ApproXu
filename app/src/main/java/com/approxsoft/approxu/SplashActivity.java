package com.approxsoft.approxu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        firebaseAuth = FirebaseAuth.getInstance();

    }



    @Override
    protected void onStart() {
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (null!=activeNetwork)
        {
            SystemClock.sleep(3000);
           if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
               if (currentUser != null){
                   Intent loginIntent = new Intent(SplashActivity.this,HomeActivity.class);
                   startActivity(loginIntent);
                   finish();
               }
               else {
                   Intent loginIntent = new Intent(SplashActivity.this,SignInActivity.class);
                   startActivity(loginIntent);
                   finish();
               }

           }if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

            if (currentUser != null) {
                Intent loginIntent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(loginIntent);
                finish();
            } else {
                Intent loginIntent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }
        }else {
            ///Toast.makeText(SplashActivity.this,"Check Your Internet Connection",Toast.LENGTH_SHORT).show();
            Intent splashIntent = new Intent(SplashActivity.this, SplashActivity.class);
            startActivity(splashIntent);
        }
        super.onStart();
    }
}
