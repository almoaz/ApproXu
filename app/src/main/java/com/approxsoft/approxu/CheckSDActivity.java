package com.approxsoft.approxu;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CheckSDActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView sdUserFullName,sdUniversity,sdDepartments, sdSemester, sdGroup,sdId, sdEmail, sdPhoneNo, sdTotalPayment, sdPayablePayment, sdSemesterFee, sdPayment, sdDue, userName;
    private CircleImageView SdUserProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference studentReff,userReff;
    private String reciverUseId, currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_s_d);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reciverUseId = getIntent().getExtras().get("visit_user_id").toString();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId);

        mToolbar = findViewById(R.id.student_sd_profile_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        userName = findViewById(R.id.student_sd_name);
        SdUserProfileImage = findViewById(R.id.sd_profile_image);
        sdUserFullName = findViewById(R.id.sd_full_name);
        sdUniversity = findViewById(R.id.sd_university_name);
        sdDepartments = findViewById(R.id.sd_departments_name);
        sdSemester = findViewById(R.id.sd_semester_name);
        sdGroup = findViewById(R.id.sd_group_name);
        sdId = findViewById(R.id.sd_id_name);
        sdEmail = findViewById(R.id.sd_email_id);
        sdPhoneNo = findViewById(R.id.sd_phone_number);
        sdTotalPayment = findViewById(R.id.sd_total_fee);
        sdPayablePayment = findViewById(R.id.sd_payable_fee);
        sdSemesterFee = findViewById(R.id.sd_semester_fee);
        sdPayment = findViewById(R.id.sd_semester_payments);
        sdDue = findViewById(R.id.sd_due);

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();

                    studentReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students");


                    studentReff.child(reciverUseId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String name = dataSnapshot.child("fullName").getValue().toString();
                                String email = dataSnapshot.child("Email").getValue().toString();
                                String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                String University = dataSnapshot.child("university").getValue().toString();
                                String semester = dataSnapshot.child("Semester").getValue().toString();
                                String group = dataSnapshot.child("Group").getValue().toString();
                                String id = dataSnapshot.child("stdId").getValue().toString();
                                String Departments = dataSnapshot.child("Departments").getValue().toString();
                                String phone = dataSnapshot.child("Phone").getValue().toString();
                                String totalPayments = dataSnapshot.child("TotalPayments").getValue().toString();
                                String payablePayments = dataSnapshot.child("PayablePayments").getValue().toString();
                                String semesterFee = dataSnapshot.child("SemesterFee").getValue().toString();
                                String payment = dataSnapshot.child("SemesterPayment").getValue().toString();
                                String due = dataSnapshot.child("SemesterDue").getValue().toString();

                                sdUserFullName.setText(name);
                                userName.setText(name);
                                sdEmail.setText(email);
                                sdUniversity.setText(University);
                                sdSemester.setText(semester);
                                sdGroup.setText(group);
                                sdId.setText(id);
                                sdDepartments.setText(Departments);
                                sdPhoneNo.setText(phone);
                                sdTotalPayment.setText(totalPayments);
                                sdPayablePayment.setText(payablePayments);
                                sdSemesterFee.setText(semesterFee);
                                sdPayment.setText(payment);
                                sdDue.setText(due);

                                Picasso.get().load(profileImage).into(SdUserProfileImage);

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


}
