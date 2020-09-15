package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MoreInformationActivity extends AppCompatActivity {


    private Toolbar mToolbar;

    private TextView universityName, subjectName, semester_name,Gender, DateOfBirth, Religion, CurrentCity, HomeTown, emailId, phonNumber;;

    private FirebaseAuth mAuth;
    private DatabaseReference UserReff;
    String currentUserId,reciverUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_information);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reciverUserId = getIntent().getExtras().get("visit_user_info").toString();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolbar = findViewById(R.id.moreInfo_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("About");


        universityName = findViewById(R.id.moreInfo_university_name);
        subjectName = findViewById(R.id.moreInfo_department_name);
        semester_name = findViewById(R.id.moreInfo_semester_name);
        Gender = findViewById(R.id.moreInfo_gender_name);
        DateOfBirth = findViewById(R.id.moreInfo_date_of_birth);
        Religion = findViewById(R.id.moreInfo_religion);
        emailId = findViewById(R.id.moreInfo_email_id);
        phonNumber = findViewById(R.id.moreInfo_phone_number);
        CurrentCity = findViewById(R.id.moreInfo_city_name);
        HomeTown = findViewById(R.id.moreInfo_town_name);

        UserReff.child(reciverUserId).addValueEventListener(new ValueEventListener() {
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
