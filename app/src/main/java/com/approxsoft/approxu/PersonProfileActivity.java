package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileActivity extends AppCompatActivity {

    private ConstraintLayout constraintLayout;
    private ConstraintLayout constraintLayout2;

    private Toolbar mToolbar;
    private CircleImageView profileImage;
    private ImageView addPostIcon, editProfileIcon, personCoverPic;
    private TextView sendFriendRequestBtn, cancelFriendRequestBtn, acceptFriendsRequest, cancelFriendRequest, message, moreInformation,personProfileName, universityName, departmentsName, Semester , date_Of_Birth, current_city_name,useName;
    private FirebaseAuth mAuth;
    private DatabaseReference userReff, profileUserReff,NotificationReff, FriendsReff, PostsReff, PostsRef, userRef, StarRef,FriendRequestReff,FriendsRef,notificationReff;
    private String currentUserId,reciverUseId,CURRENT_STATE,saveCurrentDate,Post_Key,saveCurrentTime;
    private RecyclerView personPostList;

    Boolean StarChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        reciverUseId = getIntent().getExtras().get("visit_user_id").toString();

        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        FriendRequestReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        ///FriendRequestReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        FriendsReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Friends");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId).child("Friends");
        profileUserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        notificationReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId).child("Notification");
        NotificationReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(reciverUseId).child("Notifications");
        PostsReff = FirebaseDatabase.getInstance().getReference().child("Post");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
        userRef = FirebaseDatabase.getInstance().getReference().child("All Users");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Star");



        IntializedFields();


        personPostList = findViewById(R.id.person_all_post_view);
        personPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        personPostList.setLayoutManager(linearLayoutManager);


        DisplayAllMyPosts();



        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String visit_user_id = userReff.child(reciverUseId).getKey();
                Intent findProfileIntent = new Intent(PersonProfileActivity.this, MoreInformationActivity.class);
                findProfileIntent.putExtra("visit_user_info", visit_user_id);
                startActivity(findProfileIntent);
            }
        });





        userReff.child(reciverUseId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullName = dataSnapshot.child("fullName").getValue().toString();
                    String university = dataSnapshot.child("university").getValue().toString();
                    String departments = dataSnapshot.child("departments").getValue().toString();
                    String semester = dataSnapshot.child("semester").getValue().toString();
                    String dateOfBirth = dataSnapshot.child("dateOfBirth").getValue().toString();
                    String currentCity = dataSnapshot.child("currentCity").getValue().toString();
                    String PrfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String coverPic = dataSnapshot.child("coverImage").getValue().toString();

                    personProfileName.setText(fullName);
                    useName.setText(fullName);
                    universityName.setText(university);
                    departmentsName.setText(departments);
                    Semester.setText(semester);
                    date_Of_Birth.setText(dateOfBirth);
                    current_city_name.setText(currentCity);

                    Picasso.get().load(PrfileImage).placeholder(R.drawable.profile_holder).into(profileImage);
                    Picasso.get().load(coverPic).into(personCoverPic);

                    MaintananceOfButtons();
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


        }


    }

    private void UnFriendAnExistingFriend()
    {
        FriendsRef.child(currentUserId).child(reciverUseId)
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
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                FriendsReff.child(reciverUseId).child(currentUserId)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {
                                                                    FriendsReff.child(currentUserId).child(reciverUseId)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        sendFriendRequestBtn.setEnabled(true);
                                                                                        CURRENT_STATE = "not_friends";
                                                                                        sendFriendRequestBtn.setText("Add Friend");
                                                                                        sendFriendRequestBtn.setTextColor(Color.rgb(14,39,99));

                                                                                        cancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                                                                                        cancelFriendRequestBtn.setEnabled(false);
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

    private void AcceptFriendRequest()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
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
        FriendRequestReff.child(currentUserId).child("FriendRequest").child(reciverUseId).child(currentUserId)
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
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestBtn.setText("Add Friend");
                                                sendFriendRequestBtn.setTextColor(Color.rgb(14,39,99));

                                                cancelFriendRequestBtn.setVisibility(View.INVISIBLE);
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
        FriendRequestReff.child(reciverUseId).child("FriendRequest").child(currentUserId).child(reciverUseId)
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
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendFriendRequestBtn.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                sendFriendRequestBtn.setText("Add Friend");
                                                sendFriendRequestBtn.setTextColor(Color.rgb(14,39,99));

                                                cancelFriendRequestBtn.setVisibility(View.INVISIBLE);
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
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(reciverUseId))
                        {
                            String request_type = dataSnapshot.child(reciverUseId).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                CURRENT_STATE = "request_sent";
                                sendFriendRequestBtn.setText("Cancel Friend Request");

                                cancelFriendRequestBtn.setVisibility(View.GONE);
                                cancelFriendRequestBtn.setEnabled(false);
                            }
                            else if (request_type.equals("received"))
                            {
                                CURRENT_STATE = "request_received";
                                sendFriendRequestBtn.setText("Accept Friend Request");

                                message.setVisibility(View.GONE);
                                cancelFriendRequestBtn.setVisibility(View.VISIBLE);
                                cancelFriendRequestBtn.setEnabled(true);

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
        FriendRequestReff.child(reciverUseId).child("FriendRequest").child(currentUserId).child(reciverUseId)
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
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap notificationMap = new HashMap();
        notificationMap.put("date",saveCurrentDate);
        notificationMap.put("time",saveCurrentTime);
        notificationMap.put("text","accept friend request");


        notificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
               if (task.isSuccessful())
               {
                   NotificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                       @Override
                       public void onComplete(@NonNull Task task) {
                           if (task.isSuccessful()){
                               FriendRequestReff.child(currentUserId).child("FriendRequest").child(reciverUseId).child(currentUserId)
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
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final HashMap notificationMap = new HashMap();
        notificationMap.put("date",saveCurrentDate);
        notificationMap.put("time",saveCurrentTime);
        notificationMap.put("text","has been friends request");


        notificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    NotificationReff.child(reciverUseId).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            sendFriendRequestBtn.setEnabled(true);
                            CURRENT_STATE = "request_sent";
                            sendFriendRequestBtn.setText("Cancel friend Request");

                            cancelFriendRequestBtn.setVisibility(View.INVISIBLE);
                            cancelFriendRequestBtn.setEnabled(false);
                        }
                    });
                }
            }
        });
    }

    private void DisplayAllMyPosts() {
        Query ShortPostInDecendingOrder = PostsRef.orderByChild("uid")
                .startAt(reciverUseId).endAt(reciverUseId + " \uf8ff");


        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(ShortPostInDecendingOrder, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PersonProfileActivity.PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, PersonProfileActivity.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PersonProfileActivity.PostsViewHolder holder, final int position, @NonNull Posts model) {

                final String PostKey = getRef(position).getKey();
                holder.username.setText(model.getFullName());
                holder.time.setText("    " + model.getTime());
                holder.date.setText("    " + model.getDate());
                holder.description.setText(model.getDescription());

                Picasso.get().load(model.getPostImage()).into(holder.postImage);
                Picasso.get().load(model.getProfileImage()).into(holder.user_profile_image);

                holder.setStarButtonStatus(PostKey);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(PersonProfileActivity.this, ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey", PostKey);
                        startActivity(clickPostIntent);
                    }
                });


                holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent = new Intent(PersonProfileActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("PostKey", PostKey);
                        startActivity(commentsIntent);
                    }
                });

                holder.StarPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StarChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (StarChecker.equals(true)) {
                                    if (dataSnapshot.child(PostKey).hasChild(reciverUseId)) {
                                        StarRef.child(PostKey).child(reciverUseId).removeValue();
                                        StarChecker = false;
                                    } else {
                                        StarRef.child(PostKey).child(reciverUseId).setValue(true);
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

            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout, parent, false);
                PostsViewHolder viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };
        personPostList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView StarPostBtn, CommentsPostBtn;
        TextView DisplayNoOfStar;
        int countStar;
        String currentUserId;
        DatabaseReference StarReff;
        TextView username, date, time, description;
        CircleImageView user_profile_image;
        ImageView postImage;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username = itemView.findViewById(R.id.post_user_name);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            user_profile_image = itemView.findViewById(R.id.post_profile_image);
            postImage = itemView.findViewById(R.id.post_image);

            StarPostBtn = itemView.findViewById(R.id.display_star_btn);
            CommentsPostBtn = itemView.findViewById(R.id.comment_button);
            DisplayNoOfStar = itemView.findViewById(R.id.display_no_of_star);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Star");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setStarButtonStatus(final String PostKey) {
            StarReff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId)) {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar) + (" Star"));
                    } else {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar) + (" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void updateUserStatus(String state)
    {
        String SaveCurrentDate, SaveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        SaveCurrentDate =currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        SaveCurrentTime =currentTime.format(callForTime.getTime());


        Map CurrentStatemap = new HashMap();
        CurrentStatemap.put("time", SaveCurrentTime);
        CurrentStatemap.put("date", SaveCurrentDate);
        CurrentStatemap.put("type", state);

        userRef.child(currentUserId).child("userState")
                .updateChildren(CurrentStatemap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");

    }

    @Override
    protected void onDestroy() {
        updateUserStatus("offline");
        super.onDestroy();
    }

    private void IntializedFields() {


        mToolbar = findViewById(R.id.person_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        //acceptFriendsRequest = findViewById(R.id.person_profile_confirm_friend_request);
        //cancelFriendRequest = findViewById(R.id.person_profile_delete_friend_request);
        message = findViewById(R.id.person_profile_message_friend);

        CURRENT_STATE = "not_friends";



    }


}
