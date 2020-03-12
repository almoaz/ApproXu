package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class AddUniversity2Activity extends AppCompatActivity {

    private TextView UniversityName, StudentIdNo, nextBtn;

    private Spinner departmentsNameSpinner;
    private Spinner semesterSpinner;
    private Spinner groupSpinner;

    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    ValueEventListener listener2;
    ArrayAdapter<String> adapter2;
    ArrayList<String> spinnerDataList2;

    ValueEventListener listener3;
    ArrayAdapter<String> adapter3;
    ArrayList<String> spinnerDataList3;



    private FirebaseAuth mAuth;
    private DatabaseReference userReff,SpinnerReff,universiryReff;
    private String CurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_university2);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        universiryReff = FirebaseDatabase.getInstance().getReference().child("All University");

        UniversityName = findViewById(R.id.add_university_university_name);
        StudentIdNo = findViewById(R.id.add_university_id_no);


        departmentsNameSpinner = findViewById(R.id.add_departments_name_spinner);


        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(AddUniversity2Activity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList);
        departmentsNameSpinner.setAdapter(adapter);


        semesterSpinner = findViewById(R.id.add_semester_spinner);

        spinnerDataList2 = new ArrayList<>();
        adapter2 = new ArrayAdapter<String>(AddUniversity2Activity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList2);
        semesterSpinner.setAdapter(adapter2);


        groupSpinner = findViewById(R.id.add_semester_group_spinner);

        spinnerDataList3 = new ArrayList<>();
        adapter3 = new ArrayAdapter<String>(AddUniversity2Activity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList3);
        groupSpinner.setAdapter(adapter3);


        nextBtn = findViewById(R.id.add_university2_next);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDocuments();
            }
        });


    }

    @Override
    protected void onStart() {
        userReff.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();
                    String studentIdNo = dataSnapshot.child("stdId").getValue().toString();
                    UniversityName.setText(university);
                    StudentIdNo.setText(studentIdNo);

                    SpinnerReff = FirebaseDatabase.getInstance().getReference().child("all University").child(university);

                    retrieveData();
                    retrieveData2();
                    retrieveData3();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }

    private void checkDocuments() {

        userReff.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = dataSnapshot.child("university").getValue().toString();

                    HashMap userMap = new HashMap();
                    userMap.put("Departments",departmentsNameSpinner.getSelectedItem().toString());
                    userMap.put("Semester",semesterSpinner.getSelectedItem().toString());
                    userMap.put("Group",groupSpinner.getSelectedItem().toString());

                    universiryReff.child(university).child("All Students").child(CurrentUserId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(AddUniversity2Activity.this,UniversityActivity.class);
                                startActivity(intent);
                                Animatoo.animateSlideLeft(AddUniversity2Activity.this);
                            }
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void retrieveData()
    {
        listener = SpinnerReff.child("Departments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spinnerDataList.add(item.getValue().toString());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void retrieveData2()
    {
        listener2 = SpinnerReff.child("Semester").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spinnerDataList2.add(item.getValue().toString());
                }

                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void retrieveData3()
    {
        listener3 = SpinnerReff.child("Group").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spinnerDataList3.add(item.getValue().toString());
                }

                adapter3.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddUniversity2Activity.this,AddUniversity2Activity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(AddUniversity2Activity.this,AddUniversity2Activity.class);
        startActivity(intent);
        super.onDestroy();
    }
}
