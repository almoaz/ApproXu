package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.approxsoft.approxu.Model.Data;
import com.approxsoft.approxu.Model.DataList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassRoutineDataActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    ListView testViewData;
    List<Data> dataList;
    DatabaseReference mRef;
    DatabaseReference userReff, universityReff;
    String CurrentUserId;
    FirebaseAuth mAuth;
    TextView universityName, departmentName, semsterName, groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_routine_data);

        mToolBar = new Toolbar(this);
        mToolBar = findViewById(R.id.class_routine_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assignment");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);


        universityName = findViewById(R.id.routine_university_Name);
        departmentName = findViewById(R.id.routine_department_Name);
        semsterName = findViewById(R.id.routine_semester_Name);
        groupName = findViewById(R.id.routine_group_Name);


        testViewData = findViewById(R.id.routine_Data);
        dataList = new ArrayList<>();
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
                                        DataList adapter = new DataList(ClassRoutineDataActivity.this, dataList);
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

