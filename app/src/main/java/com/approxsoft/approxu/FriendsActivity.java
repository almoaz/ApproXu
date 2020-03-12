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

public class FriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView myFriendList;
    private DatabaseReference FriendReff, UserReff;
    private FirebaseAuth mAuth;
    private String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid();
        FriendReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(online_user_id).child("Friends").child(online_user_id);
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolbar = findViewById(R.id.friend_list_ap_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Friends");


        myFriendList = findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();
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

        UserReff.child(online_user_id).child("userState")
                .updateChildren(CurrentStatemap);
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUserStatus("online");
    }


    private void DisplayAllFriends() {

        Query query = FriendReff.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, final int position, @NonNull Friends friends) {

                friendsViewHolder.setDate(friends.getDate());
                final String usersIDs = getRef(position).getKey();

                UserReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("fullName").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileImage").getValue().toString();

                            friendsViewHolder.setFullName(userName);
                            friendsViewHolder.setProfileImage(getApplicationContext(), profileimage);

                            friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    userName + "'s Profile",
                                                    "Send Message"
                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(FriendsActivity.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            if (which == 0)
                                            {
                                                Intent profileIntent = new Intent(FriendsActivity.this,PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
                                            }
                                            if (which == 1)
                                            {
                                                Intent chatIntent = new Intent(FriendsActivity.this,ChatActivity.class);
                                                chatIntent.putExtra("visit_user_id",usersIDs);
                                                chatIntent.putExtra("userName",userName);
                                                startActivity(chatIntent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_friends_layout, parent ,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        myFriendList.setAdapter(adapter);
    }
    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.friend_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_holder).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.friend_profile_full_name);
            myName.setText(fullName);
        }

        public void setDate(String date){
            TextView friendsDate = (TextView) mView.findViewById(R.id.friend_request_accept_date);
            friendsDate.setText("Friends scince: " + date);
        }
    }
}