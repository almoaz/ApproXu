package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClickPostActivity extends AppCompatActivity {

    Toolbar mToolBar;
    ImageView postImage,starImage, clickPostComments, Image1,Image2,Image3,Image4;
    private TextView postStatus, starCount;
    private Button deletePostBtn, editPostBtn;

    String ClickPostKey, currentUserId, databaseUserId, description, image, postRandomName,saveCurrentDate, saveCurrentTime,CurrentUserId;
    DatabaseReference clickPostReff,StarReff,userRff, PageReff;
    FirebaseAuth mAuth;
    int countStar = 0 ;
    Boolean StarChecker = false;
    private RecyclerView PhotoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        ClickPostKey = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("PostKey")).toString();
        clickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(ClickPostKey);
        userRff = FirebaseDatabase.getInstance().getReference().child("All Users");
        PageReff = FirebaseDatabase.getInstance().getReference().child("All Pages");


        StarReff = FirebaseDatabase.getInstance().getReference().child("Post");
        CurrentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;



        mToolBar = findViewById(R.id.click_post_bar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Post");


        postStatus = findViewById(R.id.click_post_status_text);
        starImage = findViewById(R.id.click_post_star);
        starCount = findViewById(R.id.click_post_star_count);
        deletePostBtn = findViewById(R.id.delete_post_btn);
        editPostBtn = findViewById(R.id.edit_post_btn);
        clickPostComments = findViewById(R.id.click_post_comments);
        Image1 = findViewById(R.id.click_post_Image1);
        Image2 = findViewById(R.id.click_post_Image2);
        Image3 = findViewById(R.id.click_post_Image3);
        Image4 = findViewById(R.id.click_post_Image4);



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

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(manager).getActiveNetworkInfo();

        if (null!=activeNetwork)
        {

        }else
        {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
            builder.setTitle("No internet");
            builder.setMessage("Please check your internet connection, mobile data or wifi");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.dismiss();

                }
            });

            Dialog dialog = builder.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);
        }






        clickPostReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    description = Objects.requireNonNull(dataSnapshot.child("description").getValue()).toString();
                    databaseUserId = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                    final String type = dataSnapshot.child("type").getValue().toString();
                    postStatus.setText(description);

                    if (type.equals("User"))
                    {
                        if (currentUserId.equals(databaseUserId))
                        {
                            deletePostBtn.setVisibility(View.VISIBLE);
                            editPostBtn.setVisibility(View.VISIBLE);
                        }

                        userRff.child(currentUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.child("Friends").child(currentUserId).hasChild(databaseUserId))
                                {
                                    clickPostComments.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent commentsIntent = new Intent(ClickPostActivity.this,CommentsActivity.class);
                                            commentsIntent.putExtra("PostKey",ClickPostKey);
                                            startActivity(commentsIntent);
                                        }
                                    });
                                }else {
                                    clickPostComments.setVisibility(View.INVISIBLE);
                                    clickPostComments.setEnabled(false);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    else if (type.equals("Page"))
                    {
                        PageReff.child(databaseUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String uid = dataSnapshot.child("AdminUid").getValue().toString();

                                    if (uid.equals(currentUserId))
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

                        clickPostReff.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String image1 = Objects.requireNonNull(dataSnapshot.child("Image1").getValue()).toString();
                                    String image2 = Objects.requireNonNull(dataSnapshot.child("Image2").getValue()).toString();
                                    String image3 = Objects.requireNonNull(dataSnapshot.child("Image3").getValue()).toString();
                                    String image4 = Objects.requireNonNull(dataSnapshot.child("Image4").getValue()).toString();

                                    if (image1.equals("None"))
                                    {
                                        Image1.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Image1.setVisibility(View.VISIBLE);
                                        Picasso.get().load(image1).into(Image1);
                                    }
                                    if (image2.equals("None"))
                                    {
                                        Image2.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Image2.setVisibility(View.VISIBLE);
                                        Picasso.get().load(image2).into(Image2);
                                    }
                                    if (image3.equals("None"))
                                    {
                                        Image3.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Image3.setVisibility(View.VISIBLE);
                                        Picasso.get().load(image3).into(Image3);
                                    }
                                    if (image4.equals("None"))
                                    {
                                        Image4.setVisibility(View.GONE);
                                    }
                                    else
                                    {
                                        Image4.setVisibility(View.VISIBLE);
                                        Picasso.get().load(image4).into(Image4);
                                    }
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
                                commentsIntent.putExtra("PostKey",ClickPostKey);
                                startActivity(commentsIntent);
                            }
                        });
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
                            if (dataSnapshot.child(ClickPostKey).child("Star").child(ClickPostKey).hasChild(CurrentUserId))
                            {
                                StarReff.child(ClickPostKey).child("Star").child(ClickPostKey).child(CurrentUserId).child("condition").removeValue();
                                StarChecker = false;
                            }
                            else
                            {
                                StarReff.child(ClickPostKey).child("Star").child(ClickPostKey).child(CurrentUserId).child("condition").setValue(true);
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(ClickPostKey).child("Star").child(ClickPostKey).hasChild(currentUserId))
                {
                    countStar = (int) dataSnapshot.child(ClickPostKey).child("Star").child(ClickPostKey).getChildrenCount();
                    starImage.setImageResource(R.drawable.full_gold_star);
                    starCount.setText(Integer.toString(countStar)+(" Star"));
                }else {
                    countStar = (int) dataSnapshot.child(ClickPostKey).child("Star").child(ClickPostKey).getChildrenCount();
                    starImage.setImageResource(R.drawable.gold_star);
                    starCount.setText(Integer.toString(countStar)+(" Star"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);
    }
    private void DeleteCurrentPost() {
        CurrentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        /**clickPostReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String ImageUri = Objects.requireNonNull(dataSnapshot.child("imageUri").getValue()).toString();
                    //String Date = dataSnapshot.child("date").getValue().toString();
                   // String Time = dataSnapshot.child("time").getValue().toString();
                    StorageReference ImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

                    ImageReff.child(CurrentUserId).child("PostImage").child(ImageUri).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                        }
                    });
                    //DatabaseReference imageReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId).child("PostImage");
                    //imageReff.child(Date+Time).removeValue();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/
        clickPostReff.removeValue();



        Toast.makeText(ClickPostActivity.this,"Delete Post Successfully",Toast.LENGTH_SHORT).show();
        SendUserToMainActivity();
    }
    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(ClickPostActivity.this,HomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }









}
