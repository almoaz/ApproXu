package com.approxsoft.approxu;


import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationList,notificationList2;
    Toolbar mToolbar;
    DatabaseReference FriendReff,UserReff,FriendRef,NotificationReff, PageReference;
    FirebaseAuth mAuth;
    String currentUser, Type;
    RelativeLayout UserLayout, PageLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mAuth = FirebaseAuth.getInstance();
        currentUser = getIntent().getExtras().get("userId").toString();
        Type= getIntent().getExtras().get("type").toString();
        UserLayout = findViewById(R.id.user_notification_layout);
        PageLayout = findViewById(R.id.page_notification_layout);

        mToolbar = findViewById(R.id.notification_ap_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification");

        if (Type.equals("user"))
        {
            UserLayout.setVisibility(View.VISIBLE);
            //FriendReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUser).child("Notification").child(currentUser);
            NotificationReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUser).child("Notifications");
            FriendRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUser).child("Notifications").child(currentUser);
            UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");
            PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");


            notificationList2 = findViewById(R.id.notification_seen_list);
            notificationList2.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
            linearLayoutManager2.setReverseLayout(true);
            linearLayoutManager2.setStackFromEnd(true);
            notificationList2.setLayoutManager(linearLayoutManager2);

            DisplayUserNotification();
        }
        else if (Type.equals("page"))
        {
            PageLayout.setVisibility(View.VISIBLE);
            NotificationReff = FirebaseDatabase.getInstance().getReference().child("All Pages").child(currentUser).child("Notifications");
            FriendRef = FirebaseDatabase.getInstance().getReference().child("All Pages").child(currentUser).child("Notifications");
            UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");
            PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");

            notificationList = findViewById(R.id.page_notification_seen_list);
            notificationList.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setReverseLayout(true);
            linearLayoutManager.setStackFromEnd(true);
            notificationList.setLayoutManager(linearLayoutManager);

            DisplayPageNotification();
        }







    }

    private void DisplayPageNotification()
    {
        Query query = FriendRef.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>().setQuery(query, Notification.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Notification, NotificationActivity.NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationActivity.NotificationViewHolder notificationViewHolder, final int position, @NonNull final Notification notification) {

                notificationViewHolder.setDate(notification.getDate());
                notificationViewHolder.setTime(notification.getTime());
                notificationViewHolder.setText(notification.getText());
                final String usersIDs = getRef(position).getKey();

                FriendRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("type").getValue().toString().equals("user"))
                        {
                            UserReff.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final String userName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                                        final String profileimage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                                        notificationViewHolder.setFullName(userName);
                                        notificationViewHolder.setProfileImage(profileimage);

                                        notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                FriendRef.child(usersIDs).child("condition").setValue("true");
                                                Intent profileIntent = new Intent(NotificationActivity.this,PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
                                            }
                                        });


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else if (dataSnapshot.child("type").getValue().toString().equals("page"))
                        {
                            PageReference.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final String userName = Objects.requireNonNull(dataSnapshot.child("pageName").getValue()).toString();
                                        final String profileimage = Objects.requireNonNull(dataSnapshot.child("pageProfileImage").getValue()).toString();

                                        notificationViewHolder.setFullName(userName);
                                        notificationViewHolder.setProfileImage(profileimage);

                                        notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                FriendRef.child(usersIDs).child("condition").setValue("true");
                                                Intent profileIntent = new Intent(NotificationActivity.this,UserPageHomeActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
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


                notificationViewHolder.deleteNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendRef.child(usersIDs).removeValue();
                    }
                });



                FriendRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String condition = dataSnapshot.child("condition").getValue().toString();

                            if (condition.equals("true"))
                            {
                                notificationViewHolder.setDate(notification.getDate());
                                notificationViewHolder.setTime(notification.getTime());
                                notificationViewHolder.setText(notification.getText());
                            }
                            else if (condition.equals("false"))
                            {
                                notificationViewHolder.NotificationLayout.setBackgroundColor(Color.rgb(241,242,245));
                                notificationViewHolder.setDate(notification.getDate());
                                notificationViewHolder.setTime(notification.getTime());
                                notificationViewHolder.setText(notification.getText());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent ,false);
                return new NotificationViewHolder(view);
            }
        };
        adapter.startListening();
        notificationList.setAdapter(adapter);
    }

    private void DisplayUserNotification() {
        Query query = FriendRef.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>().setQuery(query, Notification.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Notification, NotificationActivity.NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationActivity.NotificationViewHolder notificationViewHolder, final int position, @NonNull final Notification notification) {

                notificationViewHolder.setDate(notification.getDate());
                notificationViewHolder.setTime(notification.getTime());
                notificationViewHolder.setText(notification.getText());
                final String usersIDs = getRef(position).getKey();

                FriendRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("type").getValue().toString().equals("user"))
                        {
                            UserReff.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final String userName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                                        final String profileimage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                                        notificationViewHolder.setFullName(userName);
                                        notificationViewHolder.setProfileImage(profileimage);

                                        notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                FriendRef.child(usersIDs).child("condition").setValue("true");
                                                Intent profileIntent = new Intent(NotificationActivity.this,PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
                                            }
                                        });


                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else if (dataSnapshot.child("type").getValue().toString().equals("page"))
                        {
                            PageReference.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        final String userName = Objects.requireNonNull(dataSnapshot.child("pageName").getValue()).toString();
                                        final String profileimage = Objects.requireNonNull(dataSnapshot.child("pageProfileImage").getValue()).toString();

                                        notificationViewHolder.setFullName(userName);
                                        notificationViewHolder.setProfileImage(profileimage);

                                        notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                FriendRef.child(usersIDs).child("condition").setValue("true");
                                                Intent profileIntent = new Intent(NotificationActivity.this,UserPageHomeActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
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


                notificationViewHolder.deleteNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FriendRef.child(usersIDs).removeValue();
                    }
                });



                FriendRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String condition = dataSnapshot.child("condition").getValue().toString();

                            if (condition.equals("true"))
                            {
                                notificationViewHolder.setDate(notification.getDate());
                                notificationViewHolder.setTime(notification.getTime());
                                notificationViewHolder.setText(notification.getText());
                            }
                            else if (condition.equals("false"))
                            {
                                notificationViewHolder.NotificationLayout.setBackgroundColor(Color.rgb(241,242,245));
                                notificationViewHolder.setDate(notification.getDate());
                                notificationViewHolder.setTime(notification.getTime());
                                notificationViewHolder.setText(notification.getText());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent ,false);
                return new NotificationViewHolder(view);
            }
        };
        adapter.startListening();
        notificationList2.setAdapter(adapter);
    }



    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        View mView;
        View notificationView;
        TextView deleteNotification;
        LinearLayout NotificationLayout;

        NotificationViewHolder(View itemView) {
            super(itemView);

            notificationView = itemView;

            deleteNotification = notificationView.findViewById(R.id.notification_delete);
            NotificationLayout = notificationView.findViewById(R.id.notification_layout);

            mView = itemView;

        }

        public void setProfileImage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.friend_notification_profile_image);
           Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.friend_notification_profile_full_name);
            myName.setText(fullName);
        }

        public void setText(String data){
            TextView friendsDate =  mView.findViewById(R.id.friend_notification_text);
            friendsDate.setText(data);
        }
        public void setDate(String date){
            TextView dates = mView.findViewById(R.id.friend_notification_date);
            dates.setText(date);
        }

        public void setTime(String time){
            TextView times = mView.findViewById(R.id.friend_notification_time);
            times.setText(time);
        }
    }

}

