package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private EditText postText;
    private Button postUploadBtn;
    private ProgressDialog loadingBar;
    FirebaseAuth mAuth;
    DatabaseReference userReference, postReference, UserReference;
    private String CurrentUserId;
    private int countPosts = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users");
        UserReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);
        postReference = FirebaseDatabase.getInstance().getReference().child("Post");


        postText = findViewById(R.id.new_post_text);
        postUploadBtn = findViewById(R.id.upload_status);
        loadingBar = new ProgressDialog(this);

        mToolbar = findViewById(R.id.add_post_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Post");


        postText.addTextChangedListener(new TextWatcher() {
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

        postReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countPosts = (int) dataSnapshot.getChildrenCount();

                }
                else
                {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savingPostInformationToDatabase();
            }
        });




    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(postText.getText().toString()))
        {
            postUploadBtn.setEnabled(true);
            postUploadBtn.setVisibility(View.VISIBLE);
        }else {
            postUploadBtn.setEnabled(false);
            postUploadBtn.setVisibility(View.INVISIBLE);
        }
    }




    private void savingPostInformationToDatabase()
    {
        loadingBar.setTitle("New Post");
        loadingBar.setMessage("uploading your new post..");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        final String Description = postText.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        String saveCurrentTime = currentTime.format(calForTime.getTime());

        String postRandomName = saveCurrentDate + saveCurrentTime;

        HashMap postsMap = new HashMap();
        postsMap.put("uid",CurrentUserId);
        postsMap.put("date", saveCurrentDate);
        postsMap.put("time", saveCurrentTime);
        postsMap.put("description", Description );
        postsMap.put("counter",countPosts);
        postsMap.put("type","User");
        postsMap.put("uiid",CurrentUserId + postRandomName);


        postReference.child(CurrentUserId + postRandomName).updateChildren(postsMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful())

                        {
                            SendUserToHomeActivity();
                            loadingBar.dismiss();
                            Toast.makeText(AddPostActivity.this,"New Post Upload",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddPostActivity.this,"Error occoured while updating your post" ,Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }


    private void SendUserToHomeActivity() {
        Intent postIntent = new Intent(AddPostActivity.this,HomeActivity.class);
        startActivity(postIntent);
        finish();
    }




    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            SendUserToMainActivity();

        }
        return super.onOptionsItemSelected(item);
    }*/





}
