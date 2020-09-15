package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentSdActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView sdUserFullName,sdUniversity,sdDepartments, sdSemester, sdGroup,sdId, sdEmail, sdPhoneNo, sdTotalPayment, sdPayablePayment, sdSemesterFee, sdPayment, sdDue, profileUserName;
    private CircleImageView SdUserProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference studentReff,userReff;
    private String  currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sd);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);

        mToolbar = findViewById(R.id.student_profile_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        profileUserName = findViewById(R.id.profile_student_name);
        SdUserProfileImage = findViewById(R.id.student_sd_profile_image);
        sdUserFullName = findViewById(R.id.student_sd_full_name);
        sdUniversity = findViewById(R.id.student_sd_university_name);
        sdDepartments = findViewById(R.id.student_sd_departments_name);
        sdSemester = findViewById(R.id.student_sd_semester_name);
        sdGroup = findViewById(R.id.student_sd_group_name);
        sdId = findViewById(R.id.student_sd_id_name);
        sdEmail = findViewById(R.id.student_sd_email_id);
        sdPhoneNo = findViewById(R.id.student_sd_phone_number);
        sdTotalPayment = findViewById(R.id.student_sd_total_fee);
        sdPayablePayment = findViewById(R.id.student_sd_payable_fee);
        sdSemesterFee = findViewById(R.id.student_sd_semester_fee);
        sdPayment = findViewById(R.id.student_sd_semester_payments);
        sdDue = findViewById(R.id.student_sd_due);

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();

                    studentReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students");


                    studentReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
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
                                profileUserName.setText(name);
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

                                Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(SdUserProfileImage);

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
