package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.approxsoft.approxu.Model.Data;
import com.approxsoft.approxu.Model.DataList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassNoticeDataActivity extends AppCompatActivity {

    private Toolbar mToolBar;

    ListView testViewData;
    List<Data> dataList;
    DatabaseReference mRef;
    DatabaseReference userReff, universityReff;
    String CurrentUserId;
    FirebaseAuth mAuth;
    TextView universityName, departmentName, semsterName, groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_notice_data);

    }


    @Override
    protected void onStart() {


        super.onStart();
    }
}

