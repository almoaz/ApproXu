package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    Toolbar mToolBar;
    private RecyclerView CommentsList;
    private ImageView postImage,starImage, StarBtn;
    private TextView postStatus, starCount, postDate,postTime,userFullName,UserName;
    private CircleImageView postProfileImage;

    String CommentPostKey, Current_user_id, databaseUserId, description, image, postRandomName,saveCurrentDate, saveCurrentTime,CurrentUserId;
    DatabaseReference clickPostReff,StarReff;
    FirebaseAuth mAuth;
    DatabaseReference imageReff,StarRef;
    int countStar;
    private int replyCount = 0;
    Boolean StarChecker = false;
    Boolean starChecker = false;


    private ImageView postCommentButton;
    private EditText CommentsInputsText,CommentsText;
    DatabaseReference usersReff,ClickPostReff;
    DatabaseReference Postreff,starReff,PageReference, CommentsReff;
    String Post_Key, current_user_id ;
    String founderNamePattern ="[*]+[<]+[#]+[>]+[/]+[2]+";
    private int countComments = 0;
    NestedScrollView nestedScrollView;
    LinearLayout linearLayout,linearLayout1,linearLayout2;
    RelativeLayout relativeLayout1, relativeLayout2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);


        mToolBar = findViewById(R.id.comments_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        Postreff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments");
        CommentsReff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Current_user_id = mAuth.getCurrentUser().getUid();

        usersReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");

        StarReff = FirebaseDatabase.getInstance().getReference().child("Post");


        CommentPostKey = getIntent().getExtras().get("PostKey").toString();
        clickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(CommentPostKey);
        ClickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(CommentPostKey);
        StarRef = FirebaseDatabase.getInstance().getReference().child("Post");

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        imageReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(current_user_id).child("PostImage").child(postRandomName);


        postStatus = findViewById(R.id.post_description);
        starImage = findViewById(R.id.display_star_btn);
        starCount = findViewById(R.id.display_no_of_star);
        postProfileImage = findViewById(R.id.post_profile_image);
        postDate = findViewById(R.id.post_date);
        postTime = findViewById(R.id.post_time);
        userFullName = findViewById(R.id.post_user_name);
        UserName = findViewById(R.id.post_user_tool_bar_name);
        nestedScrollView = findViewById(R.id.post_nested_layout);
        linearLayout = findViewById(R.id.post_linear_layout);
        CommentsText = findViewById(R.id.comment_text2);
        relativeLayout1 = findViewById(R.id.comments_relative_layout);
        relativeLayout2 = findViewById(R.id.comments_relative_layout2);
        linearLayout1 = findViewById(R.id.linerLayout1);
        linearLayout2 = findViewById(R.id.linerLayout2);
        StarBtn = findViewById(R.id.comment_btn2);

        CommentsList = findViewById(R.id.comment_list);
        /////////

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommentsActivity.this);
        CommentsList.setLayoutManager(linearLayoutManager);
        CommentsList.setHasFixedSize(true);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);


        displayAllComments();

        linearLayout.setVisibility(View.VISIBLE);
        clickPostReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    description = dataSnapshot.child("description").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();
                    final String type = dataSnapshot.child("type").getValue().toString();
                    postStatus.setText(description);



                    Calendar calForDate = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    String saveCurrentDate = currentDate.format(calForDate.getTime());

                    Calendar calForTime = Calendar.getInstance();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                    String saveCurrentTime = currentTime.format(calForTime.getTime());

                    if (date.equals(saveCurrentDate))
                    {
                        postDate.setText("Today at "+ date);

                        if (time.equals(saveCurrentTime))
                        {
                            postDate.setText("Just now");
                        }
                    }
                    else
                    {
                        postDate.setText(date);
                    }

                    if (type.equals("User")) {
                        usersReff.child(databaseUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                String name = dataSnapshot.child("fullName").getValue().toString();

                                userFullName.setText(name);
                                UserName.setText(name + "'s Post");
                                Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(postProfileImage);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        ///////// btn click

                        userFullName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickPostReff.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String Uid = dataSnapshot.child("uid").getValue().toString();
                                            final String visit_user_id = usersReff.child(Uid).getKey();


                                            if (Uid.equals(Current_user_id)) {
                                                Intent ProfileIntent = new Intent(CommentsActivity.this, ProfileActivity.class);
                                                startActivity(ProfileIntent);
                                            } else {
                                                Intent findProfileIntent = new Intent(CommentsActivity.this, PersonProfileActivity.class);
                                                findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                                startActivity(findProfileIntent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        postProfileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickPostReff.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            String Uid = dataSnapshot.child("uid").getValue().toString();
                                            final String visit_user_id = usersReff.child(Uid).getKey();

                                            if (Uid.equals(Current_user_id)) {
                                                Intent ProfileIntent = new Intent(CommentsActivity.this, ProfileActivity.class);
                                                startActivity(ProfileIntent);
                                            } else {
                                                Intent findProfileIntent = new Intent(CommentsActivity.this, PersonProfileActivity.class);
                                                findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                                startActivity(findProfileIntent);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        ////////////////////
                    } else if (type.equals("Page")) {
                        PageReference.child(databaseUserId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String profileImage = dataSnapshot.child("pageProfileImage").getValue().toString();
                                String name = dataSnapshot.child("pageName").getValue().toString();

                                userFullName.setText(name);
                                UserName.setText(name + "'s Post");
                                Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(postProfileImage);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }



                }

                ///////////////////////




                ///////////////////////
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Postreff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countComments = (int) dataSnapshot.getChildrenCount();
                }
                else
                {
                    countComments = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        starImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StarChecker = true;
                StarReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (StarChecker.equals(true)) {
                            if (dataSnapshot.child(Post_Key).child("Star").child(Post_Key).hasChild(CurrentUserId)) {
                                StarRef.child(Post_Key).child("Star").child(Post_Key).child(CurrentUserId).child("condition").removeValue();
                                StarChecker = false;
                            } else {
                                StarRef.child(Post_Key).child("Star").child(Post_Key).child(CurrentUserId).child("condition").setValue(true);
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
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Post_Key).child("Star").child(Post_Key).hasChild(current_user_id)) {
                    countStar = (int) dataSnapshot.child(Post_Key).child("Star").child(Post_Key).getChildrenCount();
                    starImage.setImageResource(R.drawable.full_gold_star);
                    starCount.setText(countStar + (" Star"));
                } else {
                    countStar = (int) dataSnapshot.child(Post_Key).child("Star").child(Post_Key).getChildrenCount();
                    starImage.setImageResource(R.drawable.gold_star);
                    starCount.setText(countStar + (" Star"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        CommentsInputsText = findViewById(R.id.comment_text2);
        postCommentButton = findViewById(R.id.comment_btn3);

        CommentsInputsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(CommentsInputsText.getText().toString())) {
                    if (CommentsInputsText.getText().toString().matches(founderNamePattern)) {
                        founderNameComments();
                    } else {
                        Comments();
                    }
                } else {
                    Toast.makeText(CommentsActivity.this, "Write somethings", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(CommentsInputsText.getText().toString())) {
            postCommentButton.setEnabled(true);
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            StarBtn.setVisibility(View.GONE);
            postCommentButton.setVisibility(View.VISIBLE);
           // relativeLayout2.setBackground(R.drawable.registration_input_background);
            relativeLayout2.setBackgroundResource(R.drawable.registration_input_background);
        }
        else {
            postCommentButton.setEnabled(false);
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            StarBtn.setVisibility(View.VISIBLE);
            postCommentButton.setVisibility(View.GONE);
            //relativeLayout2.setBackground(Drawable.createFromPath(String.valueOf(R.drawable.post_btn_background)));
            relativeLayout2.setBackgroundResource(R.drawable.post_btn_background);
        }
    }
    private void founderNameComments() {

        CommentsInputsText.setText("Mahfuj Salehin Moaz");


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm ");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final String RandomKeys = CurrentUserId + saveCurrentDate + saveCurrentTime;
        //postCommentButton.setEnabled(true);



        HashMap CommentsMap = new HashMap();
        CommentsMap.put("uid", CurrentUserId);
        CommentsMap.put("count", countComments);
        CommentsMap.put("comment", CommentsInputsText.getText().toString());
        CommentsMap.put("date", saveCurrentDate);
        CommentsMap.put("time", saveCurrentTime);

        Postreff.child(RandomKeys).updateChildren(CommentsMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {

                            //Toast.makeText(CommentsActivity.this,"You have commented successfully...",Toast.LENGTH_SHORT).show();
                            postCommentButton.setEnabled(false);
                            CommentsInputsText.setText("");


                        }else
                        {
                            Toast.makeText(CommentsActivity.this,"Error ocoured , Try again",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void Comments()
    {

        String commentText = CommentsInputsText.getText().toString();


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final String RandomKey = CurrentUserId + saveCurrentDate + saveCurrentTime;
        //postCommentButton.setEnabled(true);



        HashMap commentsMap = new HashMap();
        commentsMap.put("uid", CurrentUserId);
        commentsMap.put("count", countComments);
        commentsMap.put("comment", commentText);
        commentsMap.put("date", saveCurrentDate);
        commentsMap.put("time", saveCurrentTime);



        Postreff.child(RandomKey).updateChildren(commentsMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {

                            //Toast.makeText(CommentsActivity.this,"You have commented successfully...",Toast.LENGTH_SHORT).show();
                            postCommentButton.setEnabled(false);
                            CommentsInputsText.setText("");


                        }else
                        {
                            Toast.makeText(CommentsActivity.this,"Error ocoured , Try again",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        CommentsReff.child("LastComments").child("commentUId").setValue(RandomKey);
    }

    private void displayAllComments() {


        Query commentsQuerry = Postreff.orderByChild("count");
        FirebaseRecyclerOptions<Comments> options= new FirebaseRecyclerOptions.Builder<Comments>().setQuery(commentsQuerry,Comments.class).build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder holder, int position, @NonNull final Comments model) {


                final String commentPostKey = getRef(position).getKey();

                Calendar calForDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                String saveCurrentTime = currentTime.format(calForTime.getTime());

                if (model.getDate().equals(saveCurrentDate))
                {
                    holder.date.setText("Today at "+ model.getTime());

                    if (model.getTime().equals(saveCurrentTime))
                    {
                        holder.date.setText("Just now");
                    }
                }
                else
                {
                    holder.date.setText(model.getDate());
                }
                holder.comments.setText(model.getComment());



                holder.setStarButtonStatus(commentPostKey);
                linearLayout.setVisibility(View.VISIBLE);

                Postreff.child(commentPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                            usersReff.child(Uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                    String name = dataSnapshot.child("fullName").getValue().toString();

                                    holder.username.setText(name);
                                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(holder.profileImage);

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

                //Toast.makeText(CommentsActivity.this,"Data load",Toast.LENGTH_SHORT).show();



                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Postreff.child(commentPostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = dataSnapshot.child("uid").getValue().toString();
                                    final String visit_user_id = usersReff.child(Uid).getKey();

                                    if (Uid.equals(Current_user_id))
                                    {
                                        Intent ProfileIntent = new Intent(CommentsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(CommentsActivity.this, PersonProfileActivity.class);
                                        findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                        startActivity(findProfileIntent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });



                Postreff.child(commentPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            replyCount = (int) dataSnapshot.child("replies").getChildrenCount();
                            holder.replyBtn.setText("Reply"+"  "+replyCount+"  "+"other");

                            if (holder.replyBtn.getText().toString().equals("Reply"+"  "+0+"  "+"other"))
                            {
                                holder.replyBtn.setText("Reply");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.replyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String PostKey = StarRef.child(Post_Key).getKey();
                        Intent replyIntent = new Intent(CommentsActivity.this, CommentsReplyActivity.class);
                        replyIntent.putExtra("PostKey", PostKey);
                        replyIntent.putExtra("visit_user_id", commentPostKey);
                        startActivity(replyIntent);
                    }
                });

                holder.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Postreff.child(commentPostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = dataSnapshot.child("uid").getValue().toString();
                                    final String visit_user_id = usersReff.child(Uid).getKey();

                                    if (Uid.equals(Current_user_id))
                                    {
                                        Intent ProfileIntent = new Intent(CommentsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(CommentsActivity.this, PersonProfileActivity.class);
                                        findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                        startActivity(findProfileIntent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });


                holder.StarPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        starChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (starChecker.equals(true))
                                {
                                    if (dataSnapshot.child(Objects.requireNonNull(Post_Key)).child("CommentsStar").child(commentPostKey).hasChild(CurrentUserId))
                                    {
                                        StarRef.child(Post_Key).child("CommentsStar").child(commentPostKey).child(CurrentUserId).removeValue();
                                        starChecker = false;
                                    }
                                    else
                                    {
                                        StarRef.child(Post_Key).child("CommentsStar").child(commentPostKey).child(CurrentUserId).setValue(true);
                                        starChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                clickPostReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.exists()){
                            String userUid = dataSnapshot.child("uid").getValue().toString();

                            if (userUid.equals(Current_user_id))
                            {
                                holder.deleteCommentsBtn.setVisibility(View.VISIBLE);
                                holder.editCommentsBtn.setVisibility(View.INVISIBLE);
                                holder.deleteCommentsBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        StarRef.child(Objects.requireNonNull(Post_Key)).child("CommentsStar").child(commentPostKey).removeValue();
                                        clickPostReff.child("Comments").child(commentPostKey).removeValue();

                                    }
                                });
                            }
                            else
                            {
                                clickPostReff.child("Comments").child(commentPostKey).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.exists())
                                        {


                                            String uid = dataSnapshot.child("uid").getValue().toString();

                                            if ((Current_user_id).equals(uid))
                                            {
                                                holder.deleteCommentsBtn.setVisibility(View.INVISIBLE);
                                                holder.editCommentsBtn.setVisibility(View.VISIBLE);
                                                holder.editCommentsBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);

                                                        final EditText inputFiled = new EditText(CommentsActivity.this);
                                                        inputFiled.setText(holder.comments.getText().toString());
                                                        inputFiled.setPadding(16,10,16,10);
                                                        builder.setView(inputFiled);


                                                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which)
                                                            {
                                                                clickPostReff.child("Comments").child(commentPostKey).child("comment").setValue(inputFiled.getText().toString());
                                                                Toast.makeText(CommentsActivity.this,"Comment Update Successfully",Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                                        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which)
                                                            {
                                                                StarRef.child(Objects.requireNonNull(Post_Key)).child("CommentsStar").child(commentPostKey).removeValue();
                                                                clickPostReff.child("Comments").child(commentPostKey).removeValue();
                                                            }
                                                        });

                                                        Dialog dialog = builder.create();
                                                        dialog.show();
                                                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);
                                                    }
                                                });
                                            }else
                                            {
                                                holder.deleteCommentsBtn.setVisibility(View.INVISIBLE);
                                                holder.editCommentsBtn.setVisibility(View.INVISIBLE);
                                            }




                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }


                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });







            }




            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_comment_layout, viewGroup, false);
                CommentsViewHolder commentsViewHolder = new CommentsViewHolder(view);
                return commentsViewHolder;
            }
        };

        CommentsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();



    }

    public  class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        TextView username,date,comments,DisplayNoOfStar,deleteCommentsBtn, editCommentsBtn, replyBtn;
        ImageView StarPostBtn, CommentsPostBtn;
        CircleImageView profileImage;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.comment_user_name);
            date = itemView.findViewById(R.id.comment_date);
            comments = itemView.findViewById(R.id.comment_text);
            profileImage = itemView.findViewById(R.id.comment_profile_image);
            StarPostBtn = itemView.findViewById(R.id.comments_star_btn);
            DisplayNoOfStar = itemView.findViewById(R.id.comments_no_of_star);
            deleteCommentsBtn = itemView.findViewById(R.id.delete_comments_button);
            editCommentsBtn = itemView.findViewById(R.id.edit_comments_button);
            replyBtn = itemView.findViewById(R.id.reply_button);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments").child("Star");



        }


        void setStarButtonStatus(final String commentPostKey)
        {
            StarRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(Post_Key).child("CommentsStar").child(commentPostKey).hasChild(current_user_id))
                    {
                        countStar = (int) dataSnapshot.child(Post_Key).child("CommentsStar").child(commentPostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar)+(" Star"));
                    }else {
                        countStar = (int) dataSnapshot.child(Post_Key).child("CommentsStar").child(commentPostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar)+(" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }




    }


}
