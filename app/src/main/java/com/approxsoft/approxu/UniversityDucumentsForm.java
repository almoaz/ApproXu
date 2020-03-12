package com.approxsoft.approxu;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class UniversityDucumentsForm extends AppCompatActivity {

    private Button DepartmentsBtn, semesterBtn, groupBtn, createGalleryBtn;
    private TextView universityName;

    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private DatabaseReference universityReff,UniversityReff;
    private Toolbar mToolBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_ducuments_form);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        universityReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);

        universityName = findViewById(R.id.University_name);
        DepartmentsBtn = findViewById(R.id.departments_btn);
        semesterBtn = findViewById(R.id.semester_btn);
        groupBtn = findViewById(R.id.group_btn);
        createGalleryBtn = findViewById(R.id.Create_gallery_btn);

        mToolBar = findViewById(R.id.university_add_info_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Info");


        universityReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String university = dataSnapshot.child("CreateUniversityName").getValue().toString();
                    universityName.setText(university);

                    UniversityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("UniversityGallery");


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        createGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap galleryMap = new HashMap();
                galleryMap.put("coverImage","None");
                galleryMap.put("slideImage1","None");
                galleryMap.put("slideImage2","None");
                galleryMap.put("slideImage3","None");
                galleryMap.put("slideImage4","None");
                galleryMap.put("slideImage5","None");
                galleryMap.put("slideImage6","None");

                UniversityReff.updateChildren(galleryMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                    }
                });
            }
        });

        DepartmentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UniversityDucumentsForm.this,AddDepartments.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(UniversityDucumentsForm.this);
            }
        });

        semesterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UniversityDucumentsForm.this,AddSemester.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(UniversityDucumentsForm.this);
            }
        });

        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UniversityDucumentsForm.this,AddGroup.class);
                startActivity(intent);
                Animatoo.animateSlideLeft(UniversityDucumentsForm.this);
            }
        });








    }



    @Override
    public void onBackPressed() {
        Intent setupIntent = new Intent(UniversityDucumentsForm.this,MainUniversity.class);
        startActivity(setupIntent);
        Animatoo.animateSlideRight(this);
        super.onBackPressed();
    }
}
