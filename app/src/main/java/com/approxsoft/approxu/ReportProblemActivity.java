package com.approxsoft.approxu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

public class ReportProblemActivity extends AppCompatActivity {

    Toolbar mToolBar;

    TextView messageReport, statusReport, wideDisplayReport, signInReport, flagReport, profileLockReport, otherReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_problem);

        mToolBar = findViewById(R.id.report_a_problem_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Report a problem");

        messageReport = findViewById(R.id.message_report);
        statusReport = findViewById(R.id.status_report);
        wideDisplayReport = findViewById(R.id.wide_display_report);
        signInReport = findViewById(R.id.sign_in_report);
        flagReport = findViewById(R.id.flag_report);
        profileLockReport = findViewById(R.id.profile_lock_report);
        otherReport = findViewById(R.id.other_report);

        messageReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblemActivity.this,ReportSubmitActivity.class);
                intent.putExtra("Name","Message and chat");
                startActivity(intent);
            }
        });


        statusReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblemActivity.this,ReportSubmitActivity.class);
                intent.putExtra("Name","Status or post");
                startActivity(intent);
            }
        });


        wideDisplayReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblemActivity.this,ReportSubmitActivity.class);
                intent.putExtra("Name","Wide display");
                startActivity(intent);
            }
        });


        signInReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblemActivity.this,ReportSubmitActivity.class);
                intent.putExtra("Name","Sign in or sign out");
                startActivity(intent);
            }
        });


        flagReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblemActivity.this,ReportSubmitActivity.class);
                intent.putExtra("Name","Flag");
                startActivity(intent);
            }
        });


        profileLockReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblemActivity.this,ReportSubmitActivity.class);
                intent.putExtra("Name","Profile lock");
                startActivity(intent);
            }
        });


        otherReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportProblemActivity.this,ReportSubmitActivity.class);
                intent.putExtra("Name","Other");
                startActivity(intent);
            }
        });





    }
}
