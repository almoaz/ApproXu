package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateStudentSdActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText UpdateSdUniversity,UpdateSdDepartments, UpdateSdSemester, UpdateSdGroup,UpdateSdId, UpdateSdTotalPayment, UpdateSdPayablePayment, UpdateSdSemesterFee, UpdateSdPayment, UpdateSdDue;
    private CircleImageView SdUserProfileImage;
    private TextView sdUserFullName, sdEmail, sdPhoneNo , userName;
    private FirebaseAuth mAuth;
    private DatabaseReference studentReff,userReff,StudentReff;
    private String reciverUseId, currentUserId;
    private Button UpdateSdBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_student_sd);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reciverUseId = getIntent().getExtras().get("visit_user_id").toString();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId);

        mToolbar = findViewById(R.id.student_update_profile_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        userName = findViewById(R.id.sd_update_student_name);
        SdUserProfileImage = findViewById(R.id.update_sd_profile_image);
        sdUserFullName = findViewById(R.id.update_sd_full_name);
        UpdateSdUniversity = findViewById(R.id.update_sd_university_name);
        UpdateSdDepartments = findViewById(R.id.update_sd_departments_name);
        UpdateSdSemester = findViewById(R.id.update_sd_semester_name);
        UpdateSdGroup = findViewById(R.id.update_sd_group_name);
        UpdateSdId = findViewById(R.id.update_sd_id_name);
        sdEmail = findViewById(R.id.update_sd_email_id);
        sdPhoneNo = findViewById(R.id.update_sd_phone_number);
        UpdateSdTotalPayment = findViewById(R.id.update_sd_total_fee);
        UpdateSdPayablePayment = findViewById(R.id.update_sd_payable_fee);
        UpdateSdSemesterFee = findViewById(R.id.update_sd_semester_fee);
        UpdateSdPayment = findViewById(R.id.update_sd_semester_payments);
        UpdateSdDue = findViewById(R.id.update_sd_due);
        UpdateSdBtn = findViewById(R.id.Update_Sd_btn);

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
                                UpdateSdUniversity.setText(University);
                                UpdateSdSemester.setText(semester);
                                UpdateSdGroup.setText(group);
                                UpdateSdId.setText(id);
                                UpdateSdDepartments.setText(Departments);
                                sdPhoneNo.setText(phone);
                                UpdateSdTotalPayment.setText(totalPayments);
                                UpdateSdPayablePayment.setText(payablePayments);
                                UpdateSdSemesterFee.setText(semesterFee);
                                UpdateSdPayment.setText(payment);
                                UpdateSdDue.setText(due);

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

        UpdateSdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkDocuments();
            }
        });




    }

    private void checkDocuments()
    {
        String university = UpdateSdUniversity.getText().toString();
        String departments = UpdateSdDepartments.getText().toString();
        String semester = UpdateSdSemester.getText().toString();
        String group = UpdateSdGroup.getText().toString();
        String stdid = UpdateSdId.getText().toString();
        String totalFee = UpdateSdTotalPayment.getText().toString();
        String payablePayment = UpdateSdPayablePayment.getText().toString();
        String semesterFee = UpdateSdSemesterFee.getText().toString();
        String Payments = UpdateSdPayment.getText().toString();
        String paymentDue = UpdateSdDue.getText().toString();


        if (TextUtils.isEmpty(university))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student university name",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(departments))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student departments name",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(group))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student group name",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(stdid))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student id no",Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(totalFee))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student total fee",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(payablePayment))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student payable fee",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(semesterFee))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student semester fee",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(Payments))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student payments",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(paymentDue))
        {
            Toast.makeText(UpdateStudentSdActivity.this,"Please write student payments due",Toast.LENGTH_SHORT).show();
        }else
        {
            UpdateAccountInfo(university,departments,group,semester,stdid,totalFee,payablePayment,semesterFee,Payments,paymentDue);
        }
    }

    private void UpdateAccountInfo(final String university, final String departments, final String group, final String semester, String stdid, final String totalFee, final String payablePayment, final String semesterFee, final String payments, final String paymentDue) {

     userReff.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot)
         {
             if (dataSnapshot.exists()){
                 String University = dataSnapshot.child("university").getValue().toString();

                 Calendar calForDate = Calendar.getInstance();
                 SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                 final String saveCurrentDate = currentDate.format(calForDate.getTime());

                 Calendar calForTime = Calendar.getInstance();
                 SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                 final String saveCurrentTime = currentTime.format(calForDate.getTime());


                 HashMap rdsMap = new HashMap();
                 rdsMap.put("university",university);
                 rdsMap.put("Departments",departments);
                 rdsMap.put("Group",group);
                 rdsMap.put("TotalPayments",totalFee);
                 rdsMap.put("PayablePayments",payablePayment);
                 rdsMap.put("Semester", semester);
                 rdsMap.put("SemesterFee", semesterFee);
                 rdsMap.put("SemesterPayment", payments);
                 rdsMap.put("SemesterDue", paymentDue);
                 rdsMap.put("date",saveCurrentDate);
                 rdsMap.put("time",saveCurrentTime);

                 StudentReff = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child("All Students");

                 StudentReff.child(reciverUseId).updateChildren(rdsMap).addOnCompleteListener(new OnCompleteListener() {
                     @Override
                     public void onComplete(@NonNull Task task)
                     {
                         if (task.isSuccessful())
                         {
                             SendUserToFindStudentActivity();

                             Toast.makeText(UpdateStudentSdActivity.this,"Student SD Updated Successfully",Toast.LENGTH_SHORT).show();

                         }
                         else
                         {
                             Toast.makeText(UpdateStudentSdActivity.this,"Error , while updating student SD",Toast.LENGTH_SHORT).show();

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

    private void SendUserToFindStudentActivity() {
        Intent intent = new Intent(UpdateStudentSdActivity.this,FindStudentsActivity.class);
        startActivity(intent);
        Animatoo.animateSlideRight(UpdateStudentSdActivity.this);
    }
}
