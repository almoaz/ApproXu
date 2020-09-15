package com.approxsoft.approxu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SettingsActivity extends AppCompatActivity {

    Toolbar mToolBar;
    ConstraintLayout personalInformationLayout, securityConstraintLayout, fullViewDisplayConstraintLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        mToolBar = findViewById(R.id.settings_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");

        personalInformationLayout = findViewById(R.id.personal_constraint_layout);
        securityConstraintLayout = findViewById(R.id.security_constraint_layout);
        fullViewDisplayConstraintLayout = findViewById(R.id.full_view_display_constraint_layout);

        personalInformationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,UserNameChangeActivity.class);
                startActivity(intent);
            }
        });

        securityConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,SecurityAndLoginActivity.class);
                startActivity(intent);
            }
        });

        fullViewDisplayConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this,FullViewDisplayActivity.class);
                startActivity(intent);
            }
        });


    }

}
