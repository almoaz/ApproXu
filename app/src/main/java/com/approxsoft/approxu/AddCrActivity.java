package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCrActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView fullname, addCrNext, codeBox;
    FirebaseAuth mAuth;
    DatabaseReference userReff,UserReff;
    String CurrentUserId,currentUserId;
    Toolbar mToolBar;
    String Code1Pattern ="[j-tA-M5-9]+[._*+/]+[f-nM-O0-5]+@[A-Z]+.[cr]";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cr);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        profileImage = findViewById(R.id.cr_profileImage);
        fullname = findViewById(R.id.cr_full_name);
        addCrNext = findViewById(R.id.add_cr_next);
        codeBox = findViewById(R.id.Cr_verification);


        mToolBar = findViewById(R.id.add_cr_ap_bar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add CR");

        UserReff.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    String name = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();

                    fullname.setText(name);


                    Picasso.get().load(image).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        codeBox.addTextChangedListener(new TextWatcher() {
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

        addCrNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDocuments();
            }
        });
    }

    private void checkDocuments() {
        if (codeBox.getText().toString().matches(Code1Pattern)){

                currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                userReff = FirebaseDatabase.getInstance().getReference().child("All Users");


                HashMap userMap = new HashMap();
                userMap.put("CrId","true");


                userReff.child(CurrentUserId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            Intent intent = new Intent(AddCrActivity.this,CRActivity.class);
                            startActivity(intent);
                            Animatoo.animateSlideLeft(AddCrActivity.this);
                        }
                    }
                });

            }else {
                Toast.makeText(this,"Invalid CR's Id",Toast.LENGTH_SHORT).show();
            }
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(codeBox.getText().toString())){

                addCrNext.setEnabled(true);
                addCrNext.setTextColor(Color.rgb(36,55,91));
            }else {
                addCrNext.setEnabled(false);
                addCrNext.setTextColor(Color.argb(50,36,55,91));
            }
        }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AddCrActivity.this,UniversityActivity.class);
        startActivity(intent);
        Animatoo.animateSlideRight(this);
        super.onBackPressed();
    }
}
