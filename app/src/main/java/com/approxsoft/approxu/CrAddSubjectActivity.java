package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CrAddSubjectActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private Spinner subjectSpinner;
    private EditText spinnerText;
    private DatabaseReference SpinnerReff, userReff,SubjectReff;
    String TextData = "";

    String CurrentUserId,currentUserId;
    FirebaseAuth mAuth;

    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cr_add_subject);

        subjectSpinner = findViewById(R.id.add_subject_spinner);
        spinnerText = findViewById(R.id.add_subject_edit_text);
        mToolBar = new Toolbar(this);

        mToolBar = findViewById(R.id.cr_add_subject_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Subject");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);


        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();

                    currentUserId = mAuth.getCurrentUser().getUid();



                    SpinnerReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(currentUserId);

                    SpinnerReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                            {
                                String University = dataSnapshot.child("university").getValue().toString();
                                String Departments = dataSnapshot.child("Departments").getValue().toString();
                                String Semester = dataSnapshot.child("Semester").getValue().toString();
                                String Group = dataSnapshot.child("Group").getValue().toString();

                                SubjectReff = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(Departments).child(Semester).child(Group);
                                retrieveData();

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

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(CrAddSubjectActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList);

        subjectSpinner.setAdapter(adapter);

    }

    public void UploadSubject(View view) {
        TextData = spinnerText.getText().toString().trim();
        SubjectReff.child(currentUserId).child("subject").push().setValue(TextData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                spinnerText.setText("");
                spinnerDataList.clear();
                retrieveData();
                adapter.notifyDataSetChanged();
                Toast.makeText(CrAddSubjectActivity.this,"Data Inserted",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void retrieveData()
    {
        listener = SubjectReff.child(currentUserId).child("subject").addValueEventListener(new ValueEventListener() {
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
}
