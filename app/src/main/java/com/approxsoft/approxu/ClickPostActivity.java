package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ClickPostActivity extends AppCompatActivity {

    Toolbar mToolBar;
    private ImageView postImage,starImage, clickPostComments;
    private TextView postStatus, starCount;
    private Button deletePostBtn, editPostBtn;

    private String PostKey, currentUserId, databaseUserId, description, image, postRandomName,saveCurrentDate, saveCurrentTime,CurrentUserId;
    private DatabaseReference clickPostReff,StarReff;
    private FirebaseAuth mAuth;
    private DatabaseReference imageReff;
    int countStar;
    Boolean StarChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        PostKey = getIntent().getExtras().get("PostKey").toString();
        clickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey);

        StarReff = FirebaseDatabase.getInstance().getReference().child("Star");
        CurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        imageReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("PostImage").child(postRandomName);

        mToolBar = findViewById(R.id.click_post_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");



        postImage = findViewById(R.id.click_post_image);
        postStatus = findViewById(R.id.click_post_status_text);
        starImage = findViewById(R.id.click_post_star);
        starCount = findViewById(R.id.click_post_star_count);
        deletePostBtn = findViewById(R.id.delete_post_btn);
        editPostBtn = findViewById(R.id.edit_post_btn);
        clickPostComments = findViewById(R.id.click_post_comments);

        editPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                EditCurrentPost();
            }
        });
        deletePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteCurrentPost();
            }
        });






        clickPostReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postImage").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();
                    postStatus.setText(description);
                    Picasso.get().load(image).into(postImage);

                    if (currentUserId.equals(databaseUserId))
                    {
                        deletePostBtn.setVisibility(View.VISIBLE);
                        editPostBtn.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        starImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                StarChecker = true;
                StarReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (StarChecker.equals(true))
                        {
                            if (dataSnapshot.child(PostKey).hasChild(CurrentUserId))
                            {
                                StarReff.child(PostKey).child(CurrentUserId).removeValue();
                                StarChecker = false;
                            }
                            else
                            {
                                StarReff.child(PostKey).child(CurrentUserId).setValue(true);
                                StarChecker = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        StarReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(PostKey).hasChild(CurrentUserId))
                {
                    countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                    starImage.setImageResource(R.drawable.full_gold_star);
                    starCount.setText(Integer.toString(countStar)+(" Star"));
                }else {
                    countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                    starImage.setImageResource(R.drawable.gold_star);
                    starCount.setText(Integer.toString(countStar)+(" Star"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        clickPostComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentsIntent = new Intent(ClickPostActivity.this,CommentsActivity.class);
                commentsIntent.putExtra("PostKey",PostKey);
                startActivity(commentsIntent);
            }
        });


    }
    private void EditCurrentPost()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Edit Post");

        final EditText inputFiled = new EditText(ClickPostActivity.this);
        inputFiled.setText(description);
        builder.setView(inputFiled);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                clickPostReff.child("description").setValue(inputFiled.getText().toString());
                Toast.makeText(ClickPostActivity.this,"Post Update Successfully",Toast.LENGTH_SHORT).show();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_blue_dark);
    }
    private void DeleteCurrentPost() {
        clickPostReff.removeValue();
        imageReff.removeValue();
        SendUserToMainActivity();
        Toast.makeText(ClickPostActivity.this,"Delete Post Successfully",Toast.LENGTH_SHORT).show();
    }
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ClickPostActivity.this,HomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }









}
