package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUniversityActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView FullName, emailId, addUniversityNext;
    private FirebaseAuth mAuth;
    DatabaseReference userReference,spinnerReference,universityReference, UserReference;
    String CurrentUserId, currentUser;
    Toolbar mToolBar;
    private Spinner UniversitySpinner;
    String Code1Pattern ="[j-tA-M5-9]+[._*+/]+[f-nM-O0-5]+@[A-Z]+.[a-z]";
    String IdPattern ="[0-9]+";


    private EditText codeBox1, studentId;



    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_university);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users");
        spinnerReference = FirebaseDatabase.getInstance().getReference().child("all University");



        mToolBar = findViewById(R.id.add_university_ap_bar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add University");

        UniversitySpinner = findViewById(R.id.add_university_name_spinner);

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<>(AddUniversityActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerDataList);
        UniversitySpinner.setAdapter(adapter);
        retrieveData();

        codeBox1 = findViewById(R.id.std_verification);
        studentId = findViewById(R.id.add_your_id_no);
        addUniversityNext = findViewById(R.id.add_university_next);

        profileImage = findViewById(R.id.add_university_user_profile);
        FullName = findViewById(R.id.add_university_user_full_name);
        emailId = findViewById(R.id.add_university_email);









        userReference.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    String name = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                    String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();

                    FullName.setText(name);
                    emailId.setText(email);

                    Picasso.get().load(image).placeholder(R.drawable.white_profile_holder).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        codeBox1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        studentId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addUniversityNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDocuments();
            }
        });




    }
    public void retrieveData()
    {
        listener = spinnerReference.child("university").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spinnerDataList.add(Objects.requireNonNull(item.getValue()).toString());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void checkInputs() {
        if (!TextUtils.isEmpty(codeBox1.getText().toString())){
            if (!TextUtils.isEmpty(studentId.getText().toString())){

                addUniversityNext.setEnabled(true);
                addUniversityNext.setTextColor(Color.rgb(36,55,91));
            }else {
                addUniversityNext.setEnabled(false);
                addUniversityNext.setTextColor(Color.argb(50,36,55,91));
            }
        }else {
            addUniversityNext.setEnabled(false);
            addUniversityNext.setTextColor(Color.argb(50,36,55,91));
        }

    }

    private void checkDocuments() {
        if (codeBox1.getText().toString().matches(Code1Pattern)){
            if (studentId.getText().toString().matches(IdPattern)){

                UniversitySpinner.getSelectedItem().toString();
                currentUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                UserReference = FirebaseDatabase.getInstance().getReference().child("All Users");
                universityReference = FirebaseDatabase.getInstance().getReference().child("All University");

                HashMap userMap = new HashMap();
                userMap.put("university",UniversitySpinner.getSelectedItem().toString());
                userMap.put("stdId",studentId.getText().toString());


                UserReference.child(currentUser).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            UserReference.child(currentUser).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.exists()){
                                        String name = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                                        String stdid = Objects.requireNonNull(dataSnapshot.child("stdId").getValue()).toString();
                                        String gender = Objects.requireNonNull(dataSnapshot.child("gender").getValue()).toString();
                                        String email = Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString();
                                        String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                                        String University = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                                        String phoneNo = Objects.requireNonNull(dataSnapshot.child("phoneNo").getValue()).toString();

                                        HashMap universityMap = new HashMap();
                                        universityMap.put("profileImage",image);
                                        universityMap.put("fullName",name);
                                        universityMap.put("stdId",stdid);
                                        universityMap.put("Gender",gender);
                                        universityMap.put("Email",email);
                                        universityMap.put("university",University);
                                        universityMap.put("TotalPayments","None");
                                        universityMap.put("Phone",phoneNo);
                                        universityMap.put("PayablePayments","None");
                                        universityMap.put("SemesterFee","None");
                                        universityMap.put("SemesterPayment","None");
                                        universityMap.put("SemesterDue","None");


                                        universityReference.child(University).child("All Students").child(currentUser).updateChildren(universityMap).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if (task.isSuccessful()){
                                                    Intent intent = new Intent(AddUniversityActivity.this,AddUniversity2Activity.class);
                                                    startActivity(intent);
                                                    Animatoo.animateSlideLeft(AddUniversityActivity.this);
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
                    }
                });

            }else {
                Toast.makeText(this,"Invalid Student Id",Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(this,"Invalid Code",Toast.LENGTH_SHORT).show();
        }

    }




}
