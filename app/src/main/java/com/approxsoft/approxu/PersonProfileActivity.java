package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;

    Toolbar mToolbar;
    private CircleImageView profileImage;
    ImageView  personCoverPic, addFriendBtnLock, followBtnLock;
    TextView sendFriendRequestBtn,followingFriends, cancelFriendRequestBtn, message, moreInformation,personProfileName, universityName, departmentsName, Semester , date_Of_Birth, current_city_name,useName;
    FirebaseAuth mAuth;
    DatabaseReference userReff, profileUserReff,NotificationReff, FriendsReff, PostsReff, PostsRef, userRef, StarRef,FriendRequestReff,FriendsRef,notificationReff;
    String currentUserId,reciverUseId,CURRENT_STATE,saveCurrentDate,saveCurrentTime,CurrentUserId;
    private RecyclerView personPostList;
    ConstraintLayout LockMessage;
    RelativeLayout relativeLayout;
    Boolean StarChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        reciverUseId = getIntent().getExtras().get("visit_user_id").toString();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        CurrentUserId = mAuth.getCurrentUser().getUid();


        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        FriendRequestReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
        ///FriendRequestReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        FriendsReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Friends");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId).child("Friends");
        profileUserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        notificationReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId).child("Notification");
        NotificationReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId).child("Notifications");
        PostsReff = FirebaseDatabase.getInstance().getReference().child("Post");

        userRef = FirebaseDatabase.getInstance().getReference().child("All Users");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Post");


        mToolbar = findViewById(R.id.person_tool_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        personProfileName = findViewById(R.id.person_profile_full_name);
        useName = findViewById(R.id.person_user_name);
        moreInformation = findViewById(R.id.person_profile_more_information);
        universityName = findViewById(R.id.person_profile_university_name);
        profileImage = findViewById(R.id.person_profile_image);
        departmentsName = findViewById(R.id.person_profile_department_name);
        Semester = findViewById(R.id.person_profile_semester);
        date_Of_Birth = findViewById(R.id.person_profile_date_of_birth);
        current_city_name = findViewById(R.id.person_profile_current_city_name);
        constraintLayout = findViewById(R.id.person_constraint_layout);
        ///constraintLayout2 = findViewById(R.id.person_constraint_layout2);
        sendFriendRequestBtn = findViewById(R.id.person_profile_add_friend_request);
        cancelFriendRequestBtn = findViewById(R.id.person_profile_cancel_friend_request);
        personCoverPic = findViewById(R.id.person_profile_cover_pic);
        followingFriends = findViewById(R.id.person_profile_following_request);
        //acceptFriendsRequest = findViewById(R.id.person_profile_confirm_friend_request);
        //cancelFriendRequest = findViewById(R.id.person_profile_delete_friend_request);
        message = findViewById(R.id.person_profile_message_friend);
        addFriendBtnLock = findViewById(R.id.add_friend_lock);
        followBtnLock = findViewById(R.id.follow_lock);
        LockMessage = findViewById(R.id.lock_profile_layout);
        relativeLayout = findViewById(R.id.person_profile_relative_layout);

        CURRENT_STATE = "not_friends";

        personPostList = findViewById(R.id.person_all_post_view);
        personPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        personPostList.setLayoutManager(linearLayoutManager);
        DisplayAllMyPosts();
        relativeLayout.setVisibility(View.VISIBLE);
        /**userRef.child(reciverUseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child("profile").getValue().equals("lock"))
                {
                    if (dataSnapshot.child("Friends").child(reciverUseId).hasChild(currentUserId))
                    {
                        LockMessage.setVisibility(View.GONE);
                        PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
                        DisplayAllMyPosts();
                    }
                    else {
                        LockMessage.setVisibility(View.VISIBLE);
                        personPostList.setVisibility(View.GONE);
                    }

                }else if (dataSnapshot.child("profile").getValue().equals("unlock"))
                {
                    LockMessage.setVisibility(View.GONE);
                    PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
                    DisplayAllMyPosts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        userReff.child(reciverUseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String fullName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                    String departments = Objects.requireNonNull(dataSnapshot.child("departments").getValue()).toString();
                    String semester = Objects.requireNonNull(dataSnapshot.child("semester").getValue()).toString();
                    String dateOfBirth = Objects.requireNonNull(dataSnapshot.child("dateOfBirth").getValue()).toString();
                    String currentCity = Objects.requireNonNull(dataSnapshot.child("currentCity").getValue()).toString();
                    String PrfileImage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    String coverPic = Objects.requireNonNull(dataSnapshot.child("coverImage").getValue()).toString();



                    personProfileName.setText(fullName);
                    useName.setText(fullName);
                    universityName.setText(university);
                    departmentsName.setText(departments);
                    Semester.setText(semester);
                    date_Of_Birth.setText(dateOfBirth);
                    current_city_name.setText(currentCity);

                    Picasso.get().load(PrfileImage).placeholder(R.drawable.profile_icon).into(profileImage);
                    Picasso.get().load(coverPic).into(personCoverPic);

                    message.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String visit_user_id = userReff.child(reciverUseId).getKey();
                            Intent chatIntent = new Intent(PersonProfileActivity.this, ChatActivity.class);
                            chatIntent.putExtra("visit_user_id", visit_user_id);
                            chatIntent.putExtra("userName", fullName);
                            chatIntent.putExtra("type","user");
                            startActivity(chatIntent);
                        }
                    });

                    MaintananceOfButtons();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visit_user_id = userReff.child(reciverUseId).getKey();
                Intent findProfileIntent = new Intent(PersonProfileActivity.this, MoreInformationActivity.class);
                findProfileIntent.putExtra("visit_user_info", visit_user_id);
                startActivity(findProfileIntent);
            }
        });
        userReff.child(currentUserId).child("Following").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
              if (dataSnapshot.hasChild(reciverUseId))
              {
                  followingFriends.setText("Unfollow");
                  sendFriendRequestBtn.setEnabled(false);
                  addFriendBtnLock.setVisibility(View.VISIBLE);
                  followingFriends.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          userReff.child(currentUserId).child("Following").child(currentUserId).child(reciverUseId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task)
                              {
                                  if (task.isSuccessful())
                                  {
                                      userReff.child(reciverUseId).child("Following").child(reciverUseId).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task)
                                          {
                                              if (task.isSuccessful())
                                              {
                                                  followingFriends.setText("Follow");
                                                  followingFriends.setEnabled(true);
                                                  sendFriendRequestBtn.setEnabled(true);
                                                  addFriendBtnLock.setVisibility(View.GONE);
                                              }
                                          }
                                      });
                                  }
                              }
                          });
                      }
                  });
              }
              else
              {
                  followingFriends.setOnClickListener(new View.OnClickListener() {
                      @Override
                      public void onClick(View v) {
                          userReff.child(currentUserId).child("Following").child(currentUserId).child(reciverUseId).child("type").setValue("follow").addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task)
                              {
                                  if (task.isSuccessful())
                                  {
                                      userReff.child(reciverUseId).child("Following").child(reciverUseId).child(currentUserId).child("type").setValue("follower").addOnCompleteListener(new OnCompleteListener<Void>() {
                                          @Override
                                          public void onComplete(@NonNull Task<Void> task)
                                          {
                                              if (task.isSuccessful())
                                              {

                                                  followFriendsNotification();

                                              }
                                          }
                                      });
                                  }
                              }
                          });
                      }
                  });
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        //cancelFriendRequest.setVisibility(View.INVISIBLE);
        cancelFriendRequestBtn.setVisibility(View.GONE);
        cancelFriendRequestBtn.setEnabled(false);

        if (!currentUserId.equals(reciverUseId))
        {
            sendFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    sendFriendRequestBtn.setEnabled(false);
                    if (CURRENT_STATE.equals("not_friends"))
                    {
                        sendFriendRequestToPerson();
                    }
                    if (CURRENT_STATE.equals("request_sent"))
                    {
                        CancelFriendRequest();
                    }
                    if (CURRENT_STATE.equals("request_received"))
                    {
                        AcceptFriendRequest();
                    }
                    if (CURRENT_STATE.equals("friends"))
                    {
                        UnFriendAnExistingFriend();
                    }
                }
            });
        }else
        {

            cancelFriendRequestBtn.setVisibility(View.INVISIBLE);
            sendFriendRequestBtn.setVisibility(View.INVISIBLE);
            constraintLayout.setVisibility(View.INVISIBLE);
            message.setVisibility(View.INVISIBLE);
            followingFriends.setVisibility(View.INVISIBLE);


        }




    }


    private void DisplayAllMyPosts() {
        Query ShortPostInDecendingOrder = PostsRef.orderByChild("uid")
                .startAt(reciverUseId).endAt(reciverUseId + " \uf8ff");


        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(ShortPostInDecendingOrder, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PersonProfileActivity.PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PersonProfileActivity.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PersonProfileActivity.PostsViewHolder holder, final int position, @NonNull Posts model) {


                final String PostKeys = getRef(position).getKey();
                holder.username.setText(model.getFullName());
                holder.time.setText(String.format("    %s", model.getTime()));
                holder.date.setText(String.format("    %s", model.getDate()));
                holder.description.setText(model.getDescription());
                Picasso.get().load(model.getProfileImage()).into(holder.user_profile_image);
                relativeLayout.setVisibility(View.VISIBLE);

                holder.setStarButtonStatus(PostKeys);
                holder.setCommentsCount(PostKeys);


                holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent = new Intent(PersonProfileActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("PostKey", PostKeys);
                        startActivity(commentsIntent);
                    }
                });

                PostsRef.child(PostKeys).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String Uid = dataSnapshot.child("uid").getValue().toString();

                            userRef.child(Uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                    String name = dataSnapshot.child("fullName").getValue().toString();

                                    holder.username.setText(name);
                                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(holder.user_profile_image);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });




                            userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    if (dataSnapshot.child("Friends").child(currentUserId).hasChild(Uid))
                                    {
                                        holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Intent commentsIntent = new Intent(PersonProfileActivity.this,CommentsActivity.class);
                                                commentsIntent.putExtra("PostKey",PostKeys);
                                                startActivity(commentsIntent);
                                            }
                                        });
                                    }else {
                                        holder.CommentsPostBtn.setVisibility(View.INVISIBLE);
                                        holder.CommentsPostBtn.setEnabled(false);
                                        holder.commentsCount.setVisibility(View.INVISIBLE);
                                        holder.commentsCount.setEnabled(false);
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



                holder.user_profile_image.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        assert PostKeys != null;
                        PostsRef.child(PostKeys).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                    final String visit_user_id = userRef.child(Uid).getKey();

                                    Intent findProfileIntent = new Intent(PersonProfileActivity.this, PersonProfileActivity.class);
                                    findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                    startActivity(findProfileIntent);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }
                });



                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PostsRef.child(PostKeys).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = dataSnapshot.child("uid").getValue().toString();
                                    final String visit_user_id = userRef.child(Uid).getKey();

                                    Intent findProfileIntent = new Intent(PersonProfileActivity.this, PersonProfileActivity.class);
                                    findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                    Animatoo.animateFade(PersonProfileActivity.this);
                                    startActivity(findProfileIntent);
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
                    public void onClick(View v) {
                        StarChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (StarChecker.equals(true))
                                {
                                    if (dataSnapshot.child(PostKeys).child("Star").child(PostKeys).hasChild(currentUserId))
                                    {
                                        StarRef.child(PostKeys).child("Star").child(PostKeys).child(currentUserId).child("condition").removeValue();
                                        StarChecker = false;
                                        Animatoo.animateFade(PersonProfileActivity.this);
                                    }
                                    else
                                    {
                                        StarRef.child(PostKeys).child("Star").child(PostKeys).child(currentUserId).child("condition").setValue(true);
                                        StarChecker = false;
                                        Animatoo.animateFade(PersonProfileActivity.this);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });


                holder.DisplayNoOfStar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent = new Intent(PersonProfileActivity.this,StarFriendActivity.class);
                        commentsIntent.putExtra("PostKey",PostKeys);
                        startActivity(commentsIntent);
                    }
                });

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout, parent, false);
                PostsViewHolder viewHolder;
                viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };
        personPostList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView StarPostBtn, CommentsPostBtn;
        TextView DisplayNoOfStar,commentsCount;
        int countStar = 0 ;
        int CommentsCount = 0 ;
        String CurrentUserId;
        DatabaseReference StarReff,CommentReff;
        TextView username, date, time, description;
        CircleImageView user_profile_image;

        PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username = itemView.findViewById(R.id.post_user_name);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            user_profile_image = itemView.findViewById(R.id.post_profile_image);
            commentsCount = itemView.findViewById(R.id.comments_count_btn);

            StarPostBtn = itemView.findViewById(R.id.display_star_btn);
            CommentsPostBtn = itemView.findViewById(R.id.comment_button);
            DisplayNoOfStar = itemView.findViewById(R.id.display_no_of_star);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Post");
            CommentReff = FirebaseDatabase.getInstance().getReference().child("Post");
            CurrentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        }

        void setStarButtonStatus(final String PostKeys) {
            StarReff.child(PostKeys).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child("Star").child(PostKeys).hasChild(CurrentUserId))
                    {
                        countStar = (int) dataSnapshot.child("Star").child(PostKeys).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        DisplayNoOfStar.setText(countStar +(" Star"));
                    }else {
                        countStar = (int) dataSnapshot.child("Star").child(PostKeys).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        DisplayNoOfStar.setText(countStar +(" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        void setCommentsCount(final String PostKeys)
        {
            CommentReff.child(PostKeys).child("Comments").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        commentsCount.setVisibility(View.VISIBLE);
                        CommentsCount = (int) dataSnapshot.getChildrenCount();
                        commentsCount.setText(Integer.toString(CommentsCount));
                    } else {
                        commentsCount.setText("");
                        commentsCount.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }

            });

        }

    }


    private void followFriendsNotification() {

        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        final HashMap notificationMap = new HashMap();
        notificationMap.put("date",saveCurrentDate);
        notificationMap.put("time",saveCurrentTime);
        notificationMap.put("text","following");
        notificationMap.put("type","user");
        notificationMap.put("condition","false");
        notificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful())
                {
                    NotificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){

                                followingFriends.setText("Unfollow");
                                sendFriendRequestBtn.setEnabled(false);
                                addFriendBtnLock.setVisibility(View.VISIBLE);

                            }

                        }
                    });
                }
            }
        });
    }

    private void UnFriendAnExistingFriend()
    {
        FriendsReff.child(currentUserId).child(reciverUseId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendsRef.child(reciverUseId).child(currentUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestBtn.setText("Add Friend");
                                                sendFriendRequestBtn.setTextColor(Color.rgb(14,39,99));
                                                cancelFriendRequestBtn.setVisibility(View.GONE);
                                                cancelFriendRequestBtn.setEnabled(false);
                                                message.setVisibility(View.GONE);
                                                message.setEnabled(false);

                                                followingFriends.setVisibility(View.VISIBLE);
                                                followingFriends.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void AcceptFriendRequest()
    {
        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        FriendsReff.child(currentUserId).child(reciverUseId).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendsRef.child(reciverUseId).child(currentUserId).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                                acceptFriendRequestNotification();

                                            }
                                        }
                                    });



                        }
                    }
                });

    }

    private void cancelFriendRequest()
    {
        FriendRequestReff.child(reciverUseId).child("FriendRequest").child(reciverUseId).child(currentUserId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendRequestReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(reciverUseId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestBtn.setText("Add Friend");
                                                sendFriendRequestBtn.setTextColor(Color.rgb(14,39,99));

                                                followingFriends.setVisibility(View.VISIBLE);
                                                followingFriends.setEnabled(true);
                                                followBtnLock.setVisibility(View.GONE);

                                                cancelFriendRequestBtn.setVisibility(View.GONE);
                                                cancelFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void CancelFriendRequest()
    {
        FriendRequestReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(reciverUseId)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendRequestReff.child(reciverUseId).child("FriendRequest").child(reciverUseId).child(currentUserId)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestBtn.setText("Add Friend");
                                                sendFriendRequestBtn.setTextColor(Color.rgb(14,39,99));

                                                followingFriends.setVisibility(View.VISIBLE);
                                                followingFriends.setEnabled(true);
                                                followBtnLock.setVisibility(View.GONE);
                                                cancelFriendRequestBtn.setVisibility(View.GONE);
                                                cancelFriendRequestBtn.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void MaintananceOfButtons()
    {
        FriendRequestReff.child(currentUserId).child("FriendRequest").child(currentUserId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(reciverUseId))
                        {
                            String request_type = Objects.requireNonNull(dataSnapshot.child(reciverUseId).child("request_type").getValue()).toString();

                            if (request_type.equals("sent"))
                            {
                                CURRENT_STATE = "request_sent";
                                sendFriendRequestBtn.setText("Cancel Request");

                                cancelFriendRequestBtn.setVisibility(View.GONE);
                                cancelFriendRequestBtn.setEnabled(false);
                                followingFriends.setVisibility(View.VISIBLE);
                                followingFriends.setEnabled(false);
                                followBtnLock.setVisibility(View.VISIBLE);
                            }
                            else if (request_type.equals("received"))
                            {
                                CURRENT_STATE = "request_received";
                                sendFriendRequestBtn.setText("Accept Request");

                                message.setVisibility(View.GONE);
                                cancelFriendRequestBtn.setVisibility(View.VISIBLE);
                                cancelFriendRequestBtn.setEnabled(true);
                                followingFriends.setVisibility(View.GONE);
                                followingFriends.setEnabled(false);

                                cancelFriendRequestBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        cancelFriendRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            FriendsReff.child(currentUserId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.hasChild(reciverUseId))
                                            {
                                                CURRENT_STATE = "friends";
                                                sendFriendRequestBtn.setText("Unfriend");

                                                cancelFriendRequestBtn.setVisibility(View.GONE);
                                                cancelFriendRequestBtn.setEnabled(false);
                                                followingFriends.setVisibility(View.GONE);
                                                followingFriends.setEnabled(false);
                                                message.setVisibility(View.VISIBLE);
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


    private void sendFriendRequestToPerson()
    {
        FriendRequestReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(reciverUseId)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            FriendRequestReff.child(reciverUseId).child("FriendRequest").child(reciverUseId).child(currentUserId)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequestNotification();

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void acceptFriendRequestNotification()
    {
        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        final HashMap notificationMap = new HashMap();
        notificationMap.put("date",saveCurrentDate);
        notificationMap.put("time",saveCurrentTime);
        notificationMap.put("text","accept friend request");
        notificationMap.put("type","user");
        notificationMap.put("condition","false");


        notificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
               if (task.isSuccessful())
               {
                   NotificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {
                           if (task.isSuccessful()){
                               FriendRequestReff.child(reciverUseId).child("FriendRequest").child(reciverUseId).child(currentUserId)
                                       .removeValue()
                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task)
                                           {
                                               if (task.isSuccessful())
                                               {
                                                   FriendRequestReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(reciverUseId)
                                                           .removeValue()
                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @SuppressLint("SetTextI18n")
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task)
                                                               {
                                                                   if (task.isSuccessful())
                                                                   {
                                                                       sendFriendRequestBtn.setEnabled(true);
                                                                       CURRENT_STATE = "friends";
                                                                       sendFriendRequestBtn.setText("Unfriend");

                                                                       cancelFriendRequestBtn.setVisibility(View.GONE);
                                                                       cancelFriendRequestBtn.setEnabled(false);
                                                                       followingFriends.setVisibility(View.GONE);
                                                                       followingFriends.setEnabled(false);
                                                                       message.setVisibility(View.VISIBLE);
                                                                       followBtnLock.setVisibility(View.INVISIBLE);
                                                                   }
                                                               }
                                                           });
                                               }
                                           }
                                       });
                           }
                       }
                   });
               }
            }
        });
    }

    private void sendFriendRequestNotification()
    {
        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        final HashMap notificationMap = new HashMap();
        notificationMap.put("date",saveCurrentDate);
        notificationMap.put("time",saveCurrentTime);
        notificationMap.put("text","has been friends request");
        notificationMap.put("type","user");
        notificationMap.put("condition","false");


        notificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    NotificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onComplete(@NonNull Task task) {
                            sendFriendRequestBtn.setEnabled(true);
                            CURRENT_STATE = "request_sent";
                            sendFriendRequestBtn.setText("Cancel Request");

                            followingFriends.setVisibility(View.VISIBLE);
                            followingFriends.setEnabled(false);
                            followBtnLock.setVisibility(View.VISIBLE);
                            cancelFriendRequestBtn.setVisibility(View.GONE);
                            cancelFriendRequestBtn.setEnabled(false);
                        }
                    });
                }
            }
        });
    }


    private void updateUserStatus(String state)
    {
        String SaveCurrentDate, SaveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        SaveCurrentDate =currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime =currentTime.format(callForTime.getTime());


        Map CurrentStatemap = new HashMap();
        CurrentStatemap.put("time", SaveCurrentTime);
        CurrentStatemap.put("date", SaveCurrentDate);
        CurrentStatemap.put("type", state);

        userRef.child(reciverUseId).child("userState")
                .updateChildren(CurrentStatemap);
    }

    /**@Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");

    }

    @Override
    protected void onDestroy() {
        updateUserStatus("offline");
        super.onDestroy();
    }*/

}
