package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CRActivity extends AppCompatActivity {

    private TextView CrUniversity, CrSemester, CrGroup, CrDepartments;
    private Button CrAddSubject, CrUpdateInfo;

    private FirebaseAuth mAuth;
    private DatabaseReference userReff, universityReff;
    private String CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_c_r);

        mAuth = FirebaseAuth.getInstance();
        CurrentUser = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUser);

        CrUniversity = findViewById(R.id.cr_university);
        CrDepartments = findViewById(R.id.cr_departments);
        CrSemester = findViewById(R.id.cr_semester);
        CrGroup = findViewById(R.id.cr_group);
        CrAddSubject = findViewById(R.id.cr_add_subject);
        CrUpdateInfo = findViewById(R.id.cr_update_info);


        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();

                    universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students");

                    universityReff.child(CurrentUser).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            String University = dataSnapshot.child("university").getValue().toString();
                            String departments = dataSnapshot.child("Departments").getValue().toString();
                            String semester = dataSnapshot.child("Semester").getValue().toString();
                            String group = dataSnapshot.child("Group").getValue().toString();

                            CrUniversity.setText(University);
                            CrDepartments.setText(departments);
                            CrSemester.setText(semester);
                            CrGroup.setText(group);
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

        CrAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent subjectIntent = new Intent(CRActivity.this,CrAddSubjectActivity.class);
                startActivity(subjectIntent);
                Animatoo.animateSlideLeft(CRActivity.this);
            }
        });

        CrUpdateInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent updateIntent = new Intent(CRActivity.this,CrUpdateInfoActivity.class);
                startActivity(updateIntent);
                Animatoo.animateSlideLeft(CRActivity.this);
            }
        });
    }

    @Override
    protected void onStart() {

        userReff.child("CrId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    LogUserToSetupActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onStart();
    }

    private void LogUserToSetupActivity() {
        Intent intent = new Intent(CRActivity.this,AddCrActivity.class);
        startActivity(intent);
        Animatoo.animateFade(CRActivity.this);
    }
}
