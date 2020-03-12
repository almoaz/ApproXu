package com.approxsoft.approxu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class UniversityOptionSelectionActivity extends AppCompatActivity {

    Toolbar mToolBar;

    private Button addUniversity;
    private Button createUniversity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_option_selection);


        mToolBar = findViewById(R.id.university_selection);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Option");

        addUniversity = findViewById(R.id.add_university);
        createUniversity = findViewById(R.id.create_university);


        addUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UniversityOptionSelectionActivity.this,AddUniversityActivity.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(UniversityOptionSelectionActivity.this);
            }
        });

        createUniversity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UniversityOptionSelectionActivity.this,CreateUniversityActivity.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(UniversityOptionSelectionActivity.this);
            }
        });



    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UniversityOptionSelectionActivity.this,HomeActivity.class);
        startActivity(intent);
        Animatoo.animateSlideRight(this);

        super.onBackPressed();
    }
}
