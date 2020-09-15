package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserNameChangeActivity extends AppCompatActivity {

    Toolbar mToolBar;
    EditText firstName, lastName;
    TextView subFirstName, subLastName;
    ConstraintLayout messageConstraintLayout;
    Button NameChangeBtn;
    DatabaseReference userReff;
    String currentUserId;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name_change);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);

        mToolBar = findViewById(R.id.user_name_change_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Personal Information");

        firstName = findViewById(R.id.user_first_name);
        lastName = findViewById(R.id.user_last_name);
        subFirstName = findViewById(R.id.sub_first_name);
        subLastName = findViewById(R.id.sub_last_name);
        messageConstraintLayout = findViewById(R.id.constraintLayout);
        NameChangeBtn = findViewById(R.id.name_change_btn);

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String firstname = dataSnapshot.child("firstName").getValue().toString();
                    String lastname = dataSnapshot.child("lastName").getValue().toString();


                    firstName.setText(firstname);
                    lastName.setText(lastname);





                }
                if (dataSnapshot.hasChild("date"))
                {
                    String date = dataSnapshot.child("date").getValue().toString();

                    Calendar calForDate = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd");
                    String SaveCurrentDate = currentDate.format(calForDate.getTime());

                    if (date.equals(SaveCurrentDate))
                    {
                        NameChangeBtn.setVisibility(View.VISIBLE);

                        NameChangeBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                String FirstName = firstName.getText().toString();
                                String LastName = lastName.getText().toString();

                                Calendar calForDate = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd");
                                String saveCurrentDate = currentDate.format(calForDate.getTime());

                                Calendar calForTime = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                                String saveCurrentTime = currentTime.format(calForDate.getTime());

                                HashMap userMap = new HashMap();
                                userMap.put("firstName",FirstName);
                                userMap.put("lastName",LastName);
                                userMap.put("date",saveCurrentDate);
                                userMap.put("time",saveCurrentTime);

                                userReff.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(UserNameChangeActivity.this,"Name Change Successfully",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                    }else
                    {
                        NameChangeBtn.setVisibility(View.GONE);


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        NameChangeBtn.setVisibility(View.VISIBLE);

        NameChangeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String FirstName = firstName.getText().toString();
                String LastName = lastName.getText().toString();

                Calendar calForDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd");
                String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                String saveCurrentTime = currentTime.format(calForDate.getTime());

                HashMap userMap = new HashMap();
                userMap.put("firstName",FirstName);
                userMap.put("lastName",LastName);
                userMap.put("date",saveCurrentDate);
                userMap.put("time",saveCurrentTime);

                userReff.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(UserNameChangeActivity.this,"Name Change Successfully",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



        firstName.addTextChangedListener(new TextWatcher() {
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

        lastName.addTextChangedListener(new TextWatcher() {
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

    private void checkInputs()
    {
        if (!TextUtils.isEmpty(firstName.getText().toString()))
        {
            if (!TextUtils.isEmpty(lastName.getText().toString()))
            {

                NameChangeBtn.setEnabled(true);
            }else {


                NameChangeBtn.setEnabled(false);
            }

        }else {


            NameChangeBtn.setEnabled(false);
        }
    }
}
