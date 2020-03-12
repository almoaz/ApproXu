package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddGroup extends AppCompatActivity {

    Spinner spinner;
    private EditText spinnerText;
    private DatabaseReference  userReff,SpinnerReff;
    String TextData = "";
    private TextView GroupNext;

    String CurrentUserId;
    FirebaseAuth mAuth;

    ValueEventListener listener;
    ArrayAdapter<String> adapter;
    ArrayList<String> spinnerDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        spinner = findViewById(R.id.semester_group_spinner);
        spinnerText = findViewById(R.id.semester_group_name);
        GroupNext = findViewById(R.id.group_next);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);
        spinnerDataList = new ArrayList<>();
        adapter = new ArrayAdapter<String>(AddGroup.this,
                android.R.layout.simple_spinner_dropdown_item,spinnerDataList);
        spinner.setAdapter(adapter);

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String university = dataSnapshot.child("CreateUniversityName").getValue().toString();

                    SpinnerReff = FirebaseDatabase.getInstance().getReference().child("all University").child(university);
                    retrieveData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void Group_btn(View view) {
        TextData = spinnerText.getText().toString().trim();
        SpinnerReff.child("Group").push().setValue(TextData).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                spinnerText.setText("");
                spinnerDataList.clear();
                retrieveData();
                adapter.notifyDataSetChanged();
                Toast.makeText(AddGroup.this,"Data Inserted",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void retrieveData()
    {
        listener = SpinnerReff.child("Group").addValueEventListener(new ValueEventListener() {
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
}
