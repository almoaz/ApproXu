package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.approxsoft.approxu.Model.Data;
import com.approxsoft.approxu.Model.DataList;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClassTestDataActivity extends AppCompatActivity {

    Toolbar mToolBar;
    AdView mAdView;

    ListView testViewData;
    List<Data> dataList;
    DatabaseReference mRef;
    DatabaseReference userReff, universityReff;
    String CurrentUserId, Condition;
    FirebaseAuth mAuth;
    TextView universityName, departmentName, semsterName, groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_test_data);

        Condition = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("condition")).toString();

        switch (Condition) {
            case "classTest":
                mToolBar = new Toolbar(this);
                mToolBar = findViewById(R.id.class_test_ap_bar);
                setSupportActionBar(mToolBar);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Class Test");

                mAuth = FirebaseAuth.getInstance();
                CurrentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);

                MobileAds.initialize(this, new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {
                    }
                });
                mAdView = findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);


                universityName = findViewById(R.id.university_Name);
                departmentName = findViewById(R.id.department_Name);
                semsterName = findViewById(R.id.semester_Name);
                groupName = findViewById(R.id.group_Name);


                testViewData = findViewById(R.id.class_test_Data);
                dataList = new ArrayList<>();

                DisplayInformation();

                break;
            case "classAssignment":

                mToolBar = new Toolbar(this);
                mToolBar = findViewById(R.id.class_test_ap_bar);
                setSupportActionBar(mToolBar);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Assignment");

                mAuth = FirebaseAuth.getInstance();
                CurrentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);


                universityName = findViewById(R.id.assignment_university_Name);
                departmentName = findViewById(R.id.assignment_department_Name);
                semsterName = findViewById(R.id.assignment_semester_Name);
                groupName = findViewById(R.id.assignment_group_Name);


                testViewData = findViewById(R.id.assignment_Data);
                dataList = new ArrayList<>();

                DisplayInformation2();
                break;
            case "classNotice":


                mToolBar = new Toolbar(this);
                mToolBar = findViewById(R.id.class_test_ap_bar);
                setSupportActionBar(mToolBar);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Assignment");

                mAuth = FirebaseAuth.getInstance();
                CurrentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);


                universityName = findViewById(R.id.notice_university_Name);
                departmentName = findViewById(R.id.notice_department_Name);
                semsterName = findViewById(R.id.notice_semester_Name);
                groupName = findViewById(R.id.notice_group_Name);


                testViewData = findViewById(R.id.notice_Data);
                dataList = new ArrayList<>();

                DisplayInformation3();

                break;
            case "ClassRoutine":
                mToolBar = new Toolbar(this);
                mToolBar = findViewById(R.id.class_test_ap_bar);
                setSupportActionBar(mToolBar);
                Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Assignment");

                mAuth = FirebaseAuth.getInstance();
                CurrentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);


                universityName = findViewById(R.id.routine_university_Name);
                departmentName = findViewById(R.id.routine_department_Name);
                semsterName = findViewById(R.id.routine_semester_Name);
                groupName = findViewById(R.id.routine_group_Name);


                testViewData = findViewById(R.id.routine_Data);
                dataList = new ArrayList<>();

                DisplayInformation4();
                break;
            case "ClassGroupChat":

                break;
        }



    }

    private void DisplayInformation4() {
        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();

                    universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(CurrentUserId);

                    universityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String University = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                                String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();

                                universityName.setText(University);
                                departmentName.setText(departments);
                                semsterName.setText(semester);
                                groupName.setText(group);

                                mRef = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments).child(semester).child(group).child("Class Routine");

                                mRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        dataList.clear();

                                        for (DataSnapshot uploadSnapshot : dataSnapshot.getChildren()) {
                                            Data data = uploadSnapshot.getValue(Data.class);

                                            dataList.add(data);

                                        }
                                        DataList adapter = new DataList(ClassTestDataActivity.this, dataList);
                                        testViewData.setAdapter(adapter);

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayInformation3() {
        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();

                    universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(CurrentUserId);

                    universityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String University = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                                String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();

                                universityName.setText(University);
                                departmentName.setText(departments);
                                semsterName.setText(semester);
                                groupName.setText(group);

                                mRef = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments).child(semester).child(group).child("Class Notice");

                                mRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        dataList.clear();

                                        for (DataSnapshot uploadSnapshot : dataSnapshot.getChildren()) {
                                            Data data = uploadSnapshot.getValue(Data.class);

                                            dataList.add(data);

                                        }
                                        DataList adapter = new DataList(ClassTestDataActivity.this, dataList);
                                        testViewData.setAdapter(adapter);

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayInformation2() {
        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();

                    universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(CurrentUserId);

                    universityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String University = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                                String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();

                                universityName.setText(University);
                                departmentName.setText(departments);
                                semsterName.setText(semester);
                                groupName.setText(group);

                                mRef = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments).child(semester).child(group).child("Class Assignment");

                                mRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        dataList.clear();

                                        for (DataSnapshot uploadSnapshot : dataSnapshot.getChildren()) {
                                            Data data = uploadSnapshot.getValue(Data.class);

                                            dataList.add(data);

                                        }
                                        DataList adapter = new DataList(ClassTestDataActivity.this, dataList);
                                        testViewData.setAdapter(adapter);

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayInformation()
    {
        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();

                    universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(CurrentUserId);

                    universityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String University = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                                String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();

                                universityName.setText(University);
                                departmentName.setText(departments);
                                semsterName.setText(semester);
                                groupName.setText(group);

                                mRef = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments).child(semester).child(group).child("Class Test");

                                mRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        dataList.clear();

                                        for (DataSnapshot uploadSnapshot : dataSnapshot.getChildren()) {
                                            Data data = uploadSnapshot.getValue(Data.class);

                                            dataList.add(data);

                                        }
                                        DataList adapter = new DataList(ClassTestDataActivity.this, dataList);
                                        testViewData.setAdapter(adapter);

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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }

}
