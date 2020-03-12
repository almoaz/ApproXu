package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;

import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private TextView universityName, subjectName, semester_name,Gender, DateOfBirth, Religion, CurrentCity, HomeTown, emailId, phonNumber;;

    private FirebaseAuth mAuth;
    private DatabaseReference UserReff;
    String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);

        mToolbar = findViewById(R.id.about_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("About");


        universityName = findViewById(R.id.about_university_name);
        subjectName = findViewById(R.id.about_departments_name);
        semester_name = findViewById(R.id.about_semester_name);
        Gender = findViewById(R.id.about_gender_name);
        DateOfBirth = findViewById(R.id.about_date_of_birth);
        Religion = findViewById(R.id.about_religion);
        emailId = findViewById(R.id.about_email_id);
        phonNumber = findViewById(R.id.about_phone_number);
        CurrentCity = findViewById(R.id.about_current_city_name);
        HomeTown = findViewById(R.id.about_hometown_name);


        UserReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {

                    String university = dataSnapshot.child("university").getValue().toString();
                    String subject = dataSnapshot.child("departments").getValue().toString();
                    String semester = dataSnapshot.child("semester").getValue().toString();
                    String currentCity = dataSnapshot.child("currentCity").getValue().toString();
                    String homeTown = dataSnapshot.child("homeTown").getValue().toString();
                    String myDOB = dataSnapshot.child("dateOfBirth").getValue().toString();
                    String religion = dataSnapshot.child("religion").getValue().toString();
                    String myGender = dataSnapshot.child("gender").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String phone = dataSnapshot.child("phoneNo").getValue().toString();




                    universityName.setText(university);
                    subjectName.setText(subject);
                    semester_name.setText(semester);
                    DateOfBirth.setText(myDOB);
                    Gender.setText(myGender);
                    Religion.setText(religion);
                    emailId.setText(email);
                    phonNumber.setText(phone);
                    CurrentCity.setText(currentCity);
                    HomeTown.setText(homeTown);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
