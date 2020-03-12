package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsActivity extends AppCompatActivity {

    Toolbar mToolBar;
    private ImageView postImage,starImage;
    private TextView postStatus, starCount, postDate,postTime,userFullName;
    private CircleImageView postProfileImage;

    private String PostKey, currentUserId, databaseUserId, description, image, postRandomName,saveCurrentDate, saveCurrentTime,CurrentUserId;
    private DatabaseReference clickPostReff,StarReff;
    private FirebaseAuth mAuth;
    private DatabaseReference imageReff,StarRef;
    int countStar;
    Boolean StarChecker = false;

    private RecyclerView CommentsList;
    private ImageView postCommentButton;
    private EditText CommentsInputsText;

    private DatabaseReference usersReff,ClickPostReff;
    private DatabaseReference Postreff,starReff;
    private String Post_Key, current_user_id;
    private long countComments = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        usersReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        Postreff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments");

        PostKey = getIntent().getExtras().get("PostKey").toString();

        clickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey);
        ClickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(PostKey);
        StarRef = FirebaseDatabase.getInstance().getReference().child("Comments").child("Star");

        starReff = FirebaseDatabase.getInstance().getReference().child("Star");
        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        imageReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(current_user_id).child("PostImage").child(postRandomName);

        mToolBar = findViewById(R.id.comments_ap_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Comments");


        postImage = findViewById(R.id.post_image);
        postStatus = findViewById(R.id.post_description);
        starImage = findViewById(R.id.display_star_btn);
        starCount = findViewById(R.id.display_no_of_star);
        postProfileImage = findViewById(R.id.post_profile_image);
        postDate = findViewById(R.id.post_date);
        postTime = findViewById(R.id.post_time);
        userFullName = findViewById(R.id.post_user_name);


        clickPostReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String userName = dataSnapshot.child("fullName").getValue().toString();
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postImage").getValue().toString();
                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();
                    userFullName.setText(userName);
                    postStatus.setText(description);
                    Picasso.get().load(image).into(postImage);
                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_holder).into(postProfileImage);
                    postDate.setText(date);
                    postTime.setText(time);

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
                            if (dataSnapshot.child(PostKey).hasChild(current_user_id))
                            {
                                StarReff.child(PostKey).child(current_user_id).removeValue();
                                StarChecker = false;
                            }
                            else
                            {
                                StarReff.child(PostKey).child(current_user_id).setValue(true);
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

        starReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(PostKey).hasChild(current_user_id))
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

        CommentsList = findViewById(R.id.comment_list);
        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);

        CommentsInputsText =findViewById(R.id.comment_text);
        postCommentButton = findViewById(R.id.comment_btn);

        CommentsInputsText.addTextChangedListener(new TextWatcher() {
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


        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Postreff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            countComments = dataSnapshot.getChildrenCount();
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

                usersReff.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String userName = dataSnapshot.child("fullName").getValue().toString();
                            String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();


                            ///ValidateComments(userName);

                             String commentText = CommentsInputsText.getText().toString();


                                Calendar calForDate = Calendar.getInstance();
                                SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                Calendar calForTime = Calendar.getInstance();
                                SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                                final String saveCurrentTime = currentTime.format(calForTime.getTime());

                                final String RandomKey = current_user_id + saveCurrentDate + saveCurrentTime;
                                //postCommentButton.setEnabled(true);



                                HashMap commentsMap = new HashMap();
                                commentsMap.put("uid", current_user_id);
                                commentsMap.put("comment", commentText);
                                commentsMap.put("date", saveCurrentDate);
                                commentsMap.put("time", saveCurrentTime);
                                commentsMap.put("fullName", userName);
                                commentsMap.put("profileImage",userProfileImage);

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

                            }

                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        displayAllComments();



    }
    private void checkInputs() {
        if (!TextUtils.isEmpty(CommentsInputsText.getText().toString()))
        {
            postCommentButton.setEnabled(true);
        }else {
            postCommentButton.setEnabled(false);
        }
    }



    private void displayAllComments() {
        Query commentsQuerry = Postreff.orderByChild("counter");
        FirebaseRecyclerOptions<Comments> options= new FirebaseRecyclerOptions.Builder<Comments>().setQuery(commentsQuerry,Comments.class).build();

        FirebaseRecyclerAdapter<Comments, CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments,CommentsActivity. CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsActivity.CommentsViewHolder holder, int position, @NonNull Comments model) {


                final String PostKey = getRef(position).getKey();

                holder.username.setText(model.getFullName());
                holder.date.setText(model.getDate());
                holder.time.setText(model.getTime());
                holder.comments.setText(model.getComment());
                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile_holder).into(holder.profileImage);

                holder.setStarButtonStatus(PostKey);




                holder.StarPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        StarChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (StarChecker.equals(true))
                                {
                                    if (dataSnapshot.child(PostKey).hasChild(current_user_id))
                                    {
                                        StarRef.child(PostKey).child(current_user_id).removeValue();
                                        StarChecker = false;
                                    }
                                    else
                                    {
                                        StarRef.child(PostKey).child(current_user_id).setValue(true);
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



                holder.deleteCommentsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        clickPostReff.child("Comments").child(PostKey).removeValue();
                    }
                });



            }




            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_comments_layout, viewGroup, false);
                CommentsActivity.CommentsViewHolder commentsViewHolder = new CommentsActivity.CommentsViewHolder(view);
                return commentsViewHolder;
            }
        };

        CommentsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        super.onStart();
    }


    public  class CommentsViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        TextView username,date,time,comments,DisplayNoOfStar,deleteCommentsBtn;
        ImageView StarPostBtn, CommentsPostBtn;
        CircleImageView profileImage;

        public CommentsViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.comment_user_name);
            date = itemView.findViewById(R.id.comment_date);
            time = itemView.findViewById(R.id.comment_time);
            comments = itemView.findViewById(R.id.comment_text);
            profileImage = itemView.findViewById(R.id.comment_profile_image);
            StarPostBtn = itemView.findViewById(R.id.comments_star_btn);
            DisplayNoOfStar = itemView.findViewById(R.id.comments_no_of_star);
            deleteCommentsBtn = itemView.findViewById(R.id.delete_comments_button);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Comments").child("Star");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            clickPostReff.child("Comments").child(PostKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        databaseUserId = dataSnapshot.child("uid").getValue().toString();
                    }if (current_user_id.equals(databaseUserId))
                     {
                         deleteCommentsBtn.setVisibility(View.VISIBLE);
                      }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }


        public void setStarButtonStatus(final String PostKey)
        {
            StarReff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(PostKey).hasChild(current_user_id))
                    {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar)+(" Star"));
                    }else {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
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
