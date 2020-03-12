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
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class CreateUniversityActivity extends AppCompatActivity {

    private EditText universityName;

    private EditText Code1, Code2, Code3, Code4, Code5;
    private TextView CodeNext;
    private Toolbar mToolBar;
    private String Code1Pattern ="[a-zA-Z0-9._-]+";
    private String Code2Pattern ="[a-zA-Z0-9._-]+";
    private String Code3Pattern ="[a-zA-Z0-9._-]+";
    private String Code4Pattern ="[a-zA-Z0-9._-]+";
    private String Code5Pattern ="[a-zA-Z0-9._-]+";

    private DatabaseReference universityReff, userReff;
    private FirebaseAuth mAuth;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_university);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUser);

        mToolBar = findViewById(R.id.create_university_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Create University");


        universityName = findViewById(R.id.create_university_name);
        Code1 = findViewById(R.id.code1);
        Code2 = findViewById(R.id.code2);
        Code3 = findViewById(R.id.code3);
        Code4 = findViewById(R.id.code4);
        Code5 = findViewById(R.id.code5);
        CodeNext = findViewById(R.id.code_next);

        CodeNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxInputs();
            }
        });

        universityName.addTextChangedListener(new TextWatcher() {
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


        Code1.addTextChangedListener(new TextWatcher() {
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

        Code2.addTextChangedListener(new TextWatcher() {
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

        Code3.addTextChangedListener(new TextWatcher() {
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

        Code4.addTextChangedListener(new TextWatcher() {
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

        Code5.addTextChangedListener(new TextWatcher() {
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
    }

    private void checkBoxInputs() {
            if (Code1.getText().toString().matches(Code1Pattern)) {
                if (Code2.getText().toString().matches(Code2Pattern)) {
                    if (Code3.getText().toString().matches(Code3Pattern)) {
                        if (Code4.getText().toString().matches(Code4Pattern)) {
                            if (Code5.getText().toString().matches(Code5Pattern)) {

                                final String university = universityName.getText().toString();

                                HashMap userMap = new HashMap();
                                userMap.put("CreateUniversityName",university);
                                userMap.put("university","none");

                                userReff.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task)
                                    {
                                        if (task.isSuccessful()){
                                            userReff.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                {
                                                    if (dataSnapshot.exists()){
                                                        String UniversityName = dataSnapshot.child("CreateUniversityName").getValue().toString();

                                                        universityReff = FirebaseDatabase.getInstance().getReference("all University").child(universityName.getText().toString());

                                                        HashMap universityMap = new HashMap();
                                                        universityMap.put("UniversityName",UniversityName);
                                                        universityMap.put("Departments","Select your departments");
                                                        universityMap.put("Semester","Select your semester");
                                                        universityMap.put("Group","Select your group");

                                                        Intent intent = new Intent(CreateUniversityActivity.this, MainUniversity.class);
                                                        startActivity(intent);
                                                        Animatoo.animateSlideLeft(CreateUniversityActivity.this);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                });


                            } else {
                                Toast.makeText(this, "Code 5 don't match", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(this, "Code 4 don't match", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "Code 3 don't match", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(this, "Code 2 don't match", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Code 1 don't match", Toast.LENGTH_SHORT).show();
            }
       }

    private void checkInputs() {
        if (!TextUtils.isEmpty(universityName.getText().toString())) {
            if (!TextUtils.isEmpty(Code1.getText().toString())) {
                if (!TextUtils.isEmpty(Code2.getText().toString())) {
                    if (!TextUtils.isEmpty(Code3.getText().toString())) {
                        if (!TextUtils.isEmpty(Code4.getText().toString())) {
                            if (!TextUtils.isEmpty(Code5.getText().toString())) {
                                CodeNext.setEnabled(true);
                                CodeNext.setTextColor(Color.rgb(36,55,91));
                            } else {
                                CodeNext.setEnabled(false);
                                CodeNext.setTextColor(Color.argb(50,36,55,91));
                            }

                        } else {
                            CodeNext.setEnabled(false);
                            CodeNext.setTextColor(Color.argb(50,36,55,91));
                        }

                    } else {
                        CodeNext.setEnabled(false);
                        CodeNext.setTextColor(Color.argb(50,36,55,91));
                    }

                } else {
                    CodeNext.setEnabled(false);
                    CodeNext.setTextColor(Color.argb(50,36,55,91));
                }

            } else {
                CodeNext.setEnabled(false);
                CodeNext.setTextColor(Color.argb(50,36,55,91));
            }
        }else {
            CodeNext.setEnabled(false);
            CodeNext.setTextColor(Color.argb(50,36,55,91));
        }
    }
}
