package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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

public class AssignmentActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    Spinner spinner;
    EditText topicsText;
    EditText timeText;
    Button uploadBtn;
    TextView departmentsname, semesterName, universityName, groupName;

    ListView listViewData;
    List<Data> dataList;

    DatabaseReference mRefere, userReff, UniversityReff, SubjectReff,universityReff,mRef;
    private DatabaseReference SpinnerReff;
    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;
    String CurrentUserId,currentUserId;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignment);

        mToolBar = new Toolbar(this);
        mToolBar = findViewById(R.id.class_assignment_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Assignment");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);



        spinner = findViewById(R.id.class_assignment_spinner);
        topicsText = findViewById(R.id.class_assignment_topics);
        timeText = findViewById(R.id.class_assignment_date);
        uploadBtn = findViewById(R.id.class_assignment_upload_btn);
        listViewData = findViewById(R.id.cr_assignment_list_view);
        universityName = findViewById(R.id.class_assignment_university);
        departmentsname = findViewById(R.id.class_assignment_departments);
        semesterName = findViewById(R.id.class_assignment_semester);
        groupName = findViewById(R.id.class_assignment_group);


        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(AssignmentActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList);

        spinner.setAdapter(adapter);



        dataList = new ArrayList<>();

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadStored();
            }
        });

        listViewData.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                Data data = dataList.get(i);
                showUpdateData(data.getSubjectId(), data.getSubjectName(), data.getSubjectTopics(), data.getSubjectDate());
                return false;
            }
        });



        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();

                    currentUserId = mAuth.getCurrentUser().getUid();

                    UniversityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(currentUserId);
                    UniversityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String University = dataSnapshot.child("university").getValue().toString();
                                String departments = dataSnapshot.child("Departments").getValue().toString();
                                String semester = dataSnapshot.child("Semester").getValue().toString();
                                String group = dataSnapshot.child("Group").getValue().toString();

                                universityName.setText(University);
                                departmentsname.setText(departments);
                                semesterName.setText(semester);
                                groupName.setText(group);

                                SubjectReff = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments).child(semester).child(group);
                                spinnerDataList.clear();
                                retrieveData();
                                adapter.notifyDataSetChanged();

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

    @Override
    protected void onStart() {

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = dataSnapshot.child("university").getValue().toString();

                    universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students").child(CurrentUserId);

                    universityReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String University = dataSnapshot.child("university").getValue().toString();
                                String departments = dataSnapshot.child("Departments").getValue().toString();
                                String semester = dataSnapshot.child("Semester").getValue().toString();
                                String group = dataSnapshot.child("Group").getValue().toString();

                                universityName.setText(University);
                                departmentsname.setText(departments);
                                semesterName.setText(semester);
                                groupName.setText(group);

                                mRef = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments).child(semester).child(group).child("Class Assignment");

                                mRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        dataList.clear();

                                        for (DataSnapshot uploadSnapshot : dataSnapshot.getChildren()) {
                                            Data data = uploadSnapshot.getValue(Data.class);

                                            dataList.add(data);

                                        }
                                        DataList adapter = new DataList(AssignmentActivity.this, dataList);
                                        listViewData.setAdapter(adapter);

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

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onStart();
    }
    private void showUpdateData(final String subjectId, String subjectName, String subjectTopics, String subjectDate) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_data, null);
        dialogBuilder.setView(dialogView);





        final Spinner editTextSubject = dialogView.findViewById(R.id.update_spinner_subject);
        final EditText editTextTopics = (EditText) dialogView.findViewById(R.id.update_data_topic);
        final EditText editTextDate = (EditText) dialogView.findViewById(R.id.update_data_date);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.update_btn);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.deleteBtn);

        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(AssignmentActivity.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList);

        editTextSubject.setAdapter(adapter);

        retrieveData();


        dialogBuilder.setTitle("Updating Data " + subjectName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = editTextSubject.getSelectedItem().toString();
                String topic = editTextTopics.getText().toString();
                String date = editTextDate.getText().toString();
                if (!TextUtils.isEmpty(topic)) {
                    editTextTopics.setError("Name required");
                }
                updateData(subjectId, subject, topic, date);

                alertDialog.dismiss();


            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData(subjectId);
            }
        });


    }

    public void deleteData(String subjectId) {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference().child("All University").child(universityName.getText().toString()).child(departmentsname.getText().toString()).child(semesterName.getText().toString()).child(groupName.getText().toString()).child("Class Assignment").child(subjectId);
        dataRef.removeValue();

        Toast.makeText(this, "Data delete ", Toast.LENGTH_SHORT).show();

    }
    private boolean updateData(String id, String subject, String topics, String date) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("All University").child(universityName.getText().toString()).child(departmentsname.getText().toString()).child(semesterName.getText().toString()).child(groupName.getText().toString()).child("Class Assignment").child(id);

        Data data = new Data(id, subject, topics, date);
        databaseReference.setValue(data);


        Toast.makeText(this, "New data update successfully", Toast.LENGTH_SHORT).show();
        return true;
    }




    public void retrieveData()
    {

        listener = SubjectReff.child(currentUserId).child("subject").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot item:dataSnapshot.getChildren())
                {
                    spinnerDataList.add(item.getValue().toString());
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void uploadStored() {


        String name = spinner.getSelectedItem().toString();
        String topics = topicsText.getText().toString();
        String time = timeText.getText().toString();
        if (!TextUtils.isEmpty(name)) {

            String id = SubjectReff.child("Class Assignment").push().getKey();
            Data data = new Data(id, name, topics, time);
            SubjectReff.child("Class Assignment").child(id).setValue(data);
            Toast.makeText(this, "Data Upload", Toast.LENGTH_SHORT).show();

            topicsText.setText("");
            timeText.setText("");

        } else {
            Toast.makeText(this, "Write an subject with code", Toast.LENGTH_SHORT).show();
        }
    }
}

