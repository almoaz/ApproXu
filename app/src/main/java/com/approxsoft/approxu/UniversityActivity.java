package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class UniversityActivity extends AppCompatActivity {

    private ImageView UniversityCoverImage;
    private ImageView  slideImage1, slideImage2, slideImage3, slideImage4,slideImage5,slideImage6;
    private TextView universitySdBtn, universityEditBtn, universityInfoBtn, universityCrBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, UniversityReff;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);

        universitySdBtn = findViewById(R.id.university_sd_btn);
        universityEditBtn = findViewById(R.id.university_edit_btn);
        universityInfoBtn = findViewById(R.id.university_info_btn);
        universityCrBtn = findViewById(R.id.university_cr_btn);
        UniversityCoverImage = findViewById(R.id.university_cover_image);
        slideImage1 = findViewById(R.id.university_banner1);
        slideImage2 = findViewById(R.id.university_banner2);
        slideImage3 = findViewById(R.id.university_banner3);
        slideImage4 = findViewById(R.id.university_banner4);
        slideImage5 = findViewById(R.id.university_banner5);
        slideImage6 = findViewById(R.id.university_banner6);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();

                    UniversityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("UniversityGallery");

                    UniversityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                            {
                                String coverImage = dataSnapshot.child("coverImage").getValue().toString();
                                String slide1 = dataSnapshot.child("slideImage1").getValue().toString();
                                String slide2 = dataSnapshot.child("slideImage2").getValue().toString();
                                String slide3 = dataSnapshot.child("slideImage3").getValue().toString();
                                String slide4 = dataSnapshot.child("slideImage4").getValue().toString();
                                String slide5 = dataSnapshot.child("slideImage5").getValue().toString();
                                String slide6 = dataSnapshot.child("slideImage6").getValue().toString();
                                Picasso.get().load(coverImage).into(UniversityCoverImage);
                                Picasso.get().load(slide1).into(slideImage1);
                                Picasso.get().load(slide2).into(slideImage2);
                                Picasso.get().load(slide3).into(slideImage3);
                                Picasso.get().load(slide4).into(slideImage4);
                                Picasso.get().load(slide5).into(slideImage5);
                                Picasso.get().load(slide6).into(slideImage6);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        universitySdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sdIntent = new Intent(UniversityActivity.this,StudentSdActivity.class);
                startActivity(sdIntent);
                Animatoo.animateSlideLeft(UniversityActivity.this);
            }
        });

        universityEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(UniversityActivity.this,StudentEditProfileActivity.class);
                startActivity(editIntent);
                Animatoo.animateSlideLeft(UniversityActivity.this);
            }
        });

        universityCrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent crIntent = new Intent(UniversityActivity.this,CRActivity.class);
                startActivity(crIntent);
                Animatoo.animateSlideLeft(UniversityActivity.this);
            }
        });

        universityInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent infoIntent = new Intent(UniversityActivity.this,UniversityInfo.class);
                startActivity(infoIntent);
                Animatoo.animateSlideLeft(UniversityActivity.this);
            }
        });

    }


    @Override
    protected void onStart() {
        userRef.child("university").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().equals("None")){
                    LogUserToSetupActivity();
                }else if (dataSnapshot.getValue().equals("none")){
                    LogUserToUniversityForm();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onStart();
    }




    private void LogUserToUniversityForm() {
        Intent setupIntent = new Intent(UniversityActivity.this,MainUniversity.class);
        startActivity(setupIntent);
        Animatoo.animateFade(this);
    }

    private void LogUserToSetupActivity() {
        Intent setupIntent = new Intent(UniversityActivity.this,UniversityOptionSelectionActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        Animatoo.animateFade(this);
    }
}
