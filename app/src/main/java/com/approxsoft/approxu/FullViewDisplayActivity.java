package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FullViewDisplayActivity extends AppCompatActivity {

    Toolbar mToolbar;
    Button EnableFullDisplay, DisableFullDisplay;
    String currentUserId;
    FirebaseAuth mAuth;
    DatabaseReference userReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_view_display);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users");



        mToolbar = findViewById(R.id.full_view_display_ap_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Full View Display");
        EnableFullDisplay = findViewById(R.id.enable_full_view_display_btn);
        DisableFullDisplay = findViewById(R.id.disable_full_view_display_btn);

        userReference.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("fullViewDisplay").getValue().equals("disable"))
                {
                    DisableFullDisplay.setVisibility(View.GONE);
                    EnableFullDisplay.setVisibility(View.VISIBLE);
                    EnableFullDisplay.setEnabled(true);
                    EnableFullDisplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userReference.child(currentUserId).child("fullViewDisplay").setValue("enable").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        EnableFullDisplay.setVisibility(View.INVISIBLE);
                                        EnableFullDisplay.setEnabled(false);
                                        DisableFullDisplay.setVisibility(View.VISIBLE);
                                        DisableFullDisplay.setEnabled(true);
                                    }
                                }
                            });
                        }
                    });
                }
                else if (dataSnapshot.child("fullViewDisplay").getValue().equals("enable"))
                {
                    EnableFullDisplay.setVisibility(View.GONE);
                    DisableFullDisplay.setVisibility(View.VISIBLE);
                    DisableFullDisplay.setEnabled(true);
                    DisableFullDisplay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userReference.child(currentUserId).child("fullViewDisplay").setValue("disable").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        DisableFullDisplay.setVisibility(View.INVISIBLE);
                                        DisableFullDisplay.setEnabled(false);
                                        EnableFullDisplay.setVisibility(View.VISIBLE);
                                        EnableFullDisplay.setEnabled(true);
                                    }
                                }
                            });
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
