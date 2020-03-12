package com.approxsoft.approxu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;

public class CrUpdateInfoActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    private Button ClassTest, ClassAssignment, ClassRoutine, ClassNotice, ClassTeacher, ClassCr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cr_update_info);

        mToolBar = new Toolbar(this);
        mToolBar = findViewById(R.id.cr_update_info_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Update Info");

        ClassTest = findViewById(R.id.cr_Class_test_btn);
        ClassAssignment = findViewById(R.id.cr_Class_assignment_btn);
        ClassRoutine = findViewById(R.id.cr_class_routine_btn);
        ClassNotice = findViewById(R.id.cr_class_notice_btn);
        ClassTeacher = findViewById(R.id.cr_class_teacher_btn);
        ClassCr  = findViewById(R.id.cr_cr_info_btn);

        ClassTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(CrUpdateInfoActivity.this,ClassTestActivity.class);
                startActivity(classTest);
                Animatoo.animateSlideLeft(CrUpdateInfoActivity.this);
            }
        });

        ClassRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(CrUpdateInfoActivity.this,ClassRoutineActivity.class);
                startActivity(classTest);
                Animatoo.animateSlideLeft(CrUpdateInfoActivity.this);
            }
        });

        ClassAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(CrUpdateInfoActivity.this,AssignmentActivity.class);
                startActivity(classTest);
                Animatoo.animateSlideLeft(CrUpdateInfoActivity.this);
            }
        });

        ClassNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent classTest = new Intent(CrUpdateInfoActivity.this,ClassNoticeActivity.class);
                startActivity(classTest);
                Animatoo.animateSlideLeft(CrUpdateInfoActivity.this);
            }
        });
    }
}
