package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.approxsoft.approxu.Model.Data;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Repo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ReportSubmitActivity extends AppCompatActivity {

    String reportTopicName;
    TextView ReportTopicsTextView;
    EditText ReportInputs;
    Button ReportSubmitBtn;
    DatabaseReference userReference, reportReference;
    FirebaseAuth mAuth;
    String CurrentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_submit);

        reportTopicName = getIntent().getExtras().get("Name").toString();
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users");

        ReportTopicsTextView = findViewById(R.id.report_topics_name);
        ReportTopicsTextView.setText(reportTopicName);
        ReportInputs = findViewById(R.id.report_box);
        ReportSubmitBtn = findViewById(R.id.report_submit_btn);
        ReportSubmitBtn.setEnabled(false);

        ReportInputs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        userReference.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String uid = dataSnapshot.child("uid").getValue().toString();
                    reportReference = FirebaseDatabase.getInstance().getReference().child("Approxu").child("Report");


                    checkInputs();
                    ReportSubmitBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Calendar calForDate = Calendar.getInstance();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                            final String saveCurrentDate = currentDate.format(calForDate.getTime());

                            Calendar calForTime = Calendar.getInstance();
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                            final String saveCurrentTime = currentTime.format(calForDate.getTime());


                            HashMap reportMap = new HashMap();
                            reportMap.put("Text",ReportInputs.getText().toString());
                            reportMap.put("ReporterUid",uid);
                            reportMap.put("date",saveCurrentDate);
                            reportMap.put("time",saveCurrentTime);
                            reportMap.put("topics",reportTopicName);

                            reportReference.push().updateChildren(reportMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        ReportInputs.setText("");
                                        Toast.makeText(ReportSubmitActivity.this,"Report submitted",Toast.LENGTH_SHORT).show();
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

    private void checkInputs() {
        if (!TextUtils.isEmpty(ReportInputs.getText().toString().trim()))
        {

            ReportSubmitBtn.setEnabled(true);
        }
        else
        {

            ReportSubmitBtn.setEnabled(false);
        }
    }


}
