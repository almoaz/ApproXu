package com.approxsoft.approxu;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

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

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationActivity extends AppCompatActivity {

    private RecyclerView notificationList,notificationList2;
    private Toolbar mToolbar;
    DatabaseReference FriendReff,UserReff,FriendRef;
    private FirebaseAuth mAuth;
    private String currentUser,reciverUseId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser().getUid();
        //reciverUseId = getIntent().getExtras().get("visit_user_id").toString();
        FriendReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUser).child("Notification").child(currentUser);
        FriendRef = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUser).child("Notifications").child(currentUser);
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolbar = findViewById(R.id.notification_ap_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification");

        notificationList = findViewById(R.id.notification_list);
        notificationList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        notificationList.setLayoutManager(linearLayoutManager);

        notificationList2 = findViewById(R.id.notification_seen_list);
        notificationList2.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        notificationList2.setLayoutManager(linearLayoutManager2);



        DisplaySeenNotification();





    }

    private void DisplaySeenNotification() {
        Query query = FriendRef.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>().setQuery(query, Notification.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Notification, NotificationActivity.NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationActivity.NotificationViewHolder notificationViewHolder, final int position, @NonNull Notification notification) {

                notificationViewHolder.setDate(notification.getDate());
                notificationViewHolder.setTime(notification.getTime());
                notificationViewHolder.setText(notification.getText());
                final String usersIDs = getRef(position).getKey();

                UserReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("fullName").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileImage").getValue().toString();

                            notificationViewHolder.setFullName(userName);
                            notificationViewHolder.setProfileImage(getApplicationContext(), profileimage);

                            notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
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

            }

            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent ,false);
                return new NotificationViewHolder(view);
            }
        };
        adapter.startListening();
        notificationList2.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        Query query = FriendReff.orderByChild("time"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>().setQuery(query, Notification.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Notification, NotificationActivity.NotificationViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationActivity.NotificationViewHolder notificationViewHolder, final int position, @NonNull Notification notification) {

                notificationViewHolder.setDate(notification.getDate());
                notificationViewHolder.setTime(notification.getTime());
                notificationViewHolder.setText(notification.getText());
                final String usersIDs = getRef(position).getKey();

                UserReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("fullName").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileImage").getValue().toString();

                            notificationViewHolder.setFullName(userName);
                            notificationViewHolder.setProfileImage(getApplicationContext(), profileimage);

                            notificationViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
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

            }

            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_layout, parent ,false);
                return new NotificationViewHolder(view);
            }
        };
        adapter.startListening();
        notificationList.setAdapter(adapter);
        super.onStart();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public NotificationViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.friend_notification_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_holder).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.friend_notification_profile_full_name);
            myName.setText(fullName);
        }

        public void setText(String data){
            TextView friendsDate = (TextView) mView.findViewById(R.id.friend_notification_text);
            friendsDate.setText(data);
        }
        public void setDate(String date){
            TextView dates = (TextView) mView.findViewById(R.id.friend_notification_date);
            dates.setText(date);
        }

        public void setTime(String time){
            TextView times = (TextView) mView.findViewById(R.id.friend_notification_time);
            times.setText(time);
        }
    }

    @Override
    protected void onStop() {

        FriendReff.removeValue();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}

