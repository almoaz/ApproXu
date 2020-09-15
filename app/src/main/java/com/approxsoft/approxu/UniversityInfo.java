package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class UniversityInfo extends AppCompatActivity {
    private Toolbar mToolBar;
    private Button ClassTest, ClassAssignment, ClassRoutine, ClassNotice, ClassTeacher, ClassGroupChat;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private DatabaseReference userReff, universityReff,mRef;
    private TextView classTestCount, classAssignmentCount, classRoutineCount, classNoticeCount;
    private int ClassTestCount = 0;
    private int ClassAssignmentCount = 0;
    private int ClassRoutineCount = 0;
    private int ClassNoticeCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_info);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);

        mToolBar = new Toolbar(this);
        mToolBar = findViewById(R.id.university_info_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Info");

        ClassTest = findViewById(R.id.student_Class_test_btn);
        ClassAssignment = findViewById(R.id.student_Class_assignment_btn);
        ClassRoutine = findViewById(R.id.student_class_routine_btn);
        ClassNotice = findViewById(R.id.student_class_notice_btn);
        ClassTeacher = findViewById(R.id.student_class_teacher_btn);
        ClassGroupChat  = findViewById(R.id.class_group_chat);
        classTestCount = findViewById(R.id.class_Test_count);
        classAssignmentCount = findViewById(R.id.class_assignment_count);
        classRoutineCount = findViewById(R.id.class_routine_count);
        classNoticeCount = findViewById(R.id.class_notice_count);

        ClassTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(UniversityInfo.this,ClassTestDataActivity.class);
                classTest.putExtra("condition","classTest");
                startActivity(classTest);
                Animatoo.animateSlideLeft(UniversityInfo.this);
            }
        });

        ClassAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(UniversityInfo.this,ClassTestDataActivity.class);
                startActivity(classTest);
                classTest.putExtra("condition","classAssignment");
                Animatoo.animateSlideLeft(UniversityInfo.this);
            }
        });
        ClassNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(UniversityInfo.this,ClassTestDataActivity.class);
                classTest.putExtra("condition","classNotice");
                startActivity(classTest);
                Animatoo.animateSlideLeft(UniversityInfo.this);
            }
        });
        ClassRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(UniversityInfo.this,ClassTestDataActivity.class);
                classTest.putExtra("condition","ClassRoutine");
                Animatoo.animateSlideLeft(UniversityInfo.this);
                startActivity(classTest);
            }
        });
        ClassGroupChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(UniversityInfo.this,ClassGroupChatActivity.class);
                classTest.putExtra("condition","ClassGroupChat");
                startActivity(classTest);
                Animatoo.animateSlideLeft(UniversityInfo.this);
            }
        });


    }

    @Override
    protected void onStart() {

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = dataSnapshot.child("university").getValue().toString();

                    universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(CurrentUserId);

                    universityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String University = dataSnapshot.child("university").getValue().toString();
                                String departments = dataSnapshot.child("Departments").getValue().toString();
                                String semester = dataSnapshot.child("Semester").getValue().toString();
                                String group = dataSnapshot.child("Group").getValue().toString();

                                mRef = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments).child(semester).child(group);

                                mRef.child("Class Test").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            classTestCount.setVisibility(View.VISIBLE);
                                            ClassTestCount = (int) dataSnapshot.getChildrenCount();
                                            classTestCount.setText(Integer.toString(ClassTestCount));
                                        } else {
                                            classTestCount.setText("");
                                        }
                                    }

                                    @Override
                                    public void onCancelled (@NonNull DatabaseError databaseError){

                                    }

                                });

                                mRef.child("Class Assignment").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            classAssignmentCount.setVisibility(View.VISIBLE);
                                            ClassAssignmentCount = (int) dataSnapshot.getChildrenCount();
                                            classAssignmentCount.setText(Integer.toString(ClassAssignmentCount));
                                        } else {
                                            classAssignmentCount.setText("");
                                        }
                                    }

                                    @Override
                                    public void onCancelled (@NonNull DatabaseError databaseError){

                                    }

                                });

                                mRef.child("Class Routine").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            classRoutineCount.setVisibility(View.VISIBLE);
                                            ClassRoutineCount = (int) dataSnapshot.getChildrenCount();
                                            classRoutineCount.setText(Integer.toString(ClassRoutineCount));
                                        } else {
                                            classRoutineCount.setText("");
                                        }
                                    }

                                    @Override
                                    public void onCancelled (@NonNull DatabaseError databaseError){

                                    }

                                });

                                mRef.child("Class Notice").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            classNoticeCount.setVisibility(View.VISIBLE);
                                            ClassNoticeCount = (int) dataSnapshot.getChildrenCount();
                                            classNoticeCount.setText(Integer.toString(ClassNoticeCount));
                                        } else {
                                            classNoticeCount.setText("");
                                        }
                                    }

                                    @Override
                                    public void onCancelled (@NonNull DatabaseError databaseError){

                                    }

                                });
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
        super.onStart();
    }
}
