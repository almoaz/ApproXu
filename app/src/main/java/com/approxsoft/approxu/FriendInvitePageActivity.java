package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendInvitePageActivity extends AppCompatActivity {

    Toolbar mToolbar;

    private RecyclerView myFriendList;
    DatabaseReference FriendReff, UserReff;
    FirebaseAuth mAuth;
    String online_user_id,PageAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_invite_page);
        mAuth = FirebaseAuth.getInstance();
        PageAddress =  getIntent().getExtras().get("PageId").toString();
        online_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        FriendReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(online_user_id).child("Friends").child(online_user_id);
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolbar = findViewById(R.id.invite_friends_app_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invite Page");


        myFriendList = findViewById(R.id.invite_friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();
    }

    private void DisplayAllFriends() {

        Query query = FriendReff.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, FriendInvitePageActivity.FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendInvitePageActivity.FriendsViewHolder friendsViewHolder, final int position, @NonNull final Friends friends) {


                final String usersIDs = getRef(position).getKey();

                UserReff.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("StarPage").hasChild(PageAddress))
                        {
                            friendsViewHolder.linearLayout.setVisibility(View.GONE);
                        }else
                        {
                            friendsViewHolder.linearLayout.setVisibility(View.VISIBLE);

                            if (dataSnapshot.child("Invitation").hasChild(PageAddress))
                            {
                                friendsViewHolder.InviteBtn.setText("Cancel");
                                friendsViewHolder.InviteBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        UserReff.child(usersIDs).child("Notifications").child(usersIDs).child(PageAddress).removeValue();
                                        friendsViewHolder.InviteBtn.setText("Invite");
                                    }
                                });
                            }else
                            {
                                friendsViewHolder.InviteBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        String SaveCurrentDate, SaveCurrentTime;

                                        Calendar callForDate = Calendar.getInstance();
                                        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
                                        SaveCurrentDate =currentDate.format(callForDate.getTime());

                                        Calendar callForTime = Calendar.getInstance();
                                        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                                        SaveCurrentTime =currentTime.format(callForTime.getTime());


                                        HashMap notificationMap = new HashMap();
                                        notificationMap.put("date",SaveCurrentDate);
                                        notificationMap.put("time",SaveCurrentTime);
                                        notificationMap.put("type","page");
                                        notificationMap.put("condition","false");
                                        notificationMap.put("text","Invite a new page");

                                        HashMap notificationMap2 = new HashMap();
                                        notificationMap2.put("text","Invite a new page");

                                        HashMap notificationMap3 = new HashMap();
                                        notificationMap3.put("date",SaveCurrentDate);
                                        notificationMap3.put("time",SaveCurrentTime);
                                        notificationMap3.put("type","page");
                                        notificationMap3.put("condition","false");
                                        notificationMap3.put("text","Invite a new page");


                                        UserReff.child(usersIDs).child("Notifications").child(usersIDs).child(PageAddress).updateChildren(notificationMap);
                                        UserReff.child(usersIDs).child("Notification").child(usersIDs).child(PageAddress).updateChildren(notificationMap2);
                                        UserReff.child(usersIDs).child("Invitation").child(PageAddress).updateChildren(notificationMap3);

                                        friendsViewHolder.InviteBtn.setText("Cancel");
                                    }
                                });
                            }
                        }

                        if (dataSnapshot.exists()) {
                            final String userName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            final String profileimage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                            friendsViewHolder.setFullName(userName);
                            friendsViewHolder.setProfileImage(profileimage);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.invite_friends_layout, parent ,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        myFriendList.setAdapter(adapter);
    }
    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        Button InviteBtn;
        LinearLayout linearLayout;

        FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            InviteBtn = mView.findViewById(R.id.friend_invite_btn);
            linearLayout = mView.findViewById(R.id.invite_friends_linear_layout);
        }

        public void setProfileImage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.invite_friend_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.invite_friend_profile_full_name);
            myName.setText(fullName);
        }
    }
}
