package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class AllGroupFriendsListActivity extends AppCompatActivity {

    Toolbar mToolbar;

    private RecyclerView myFriendList;
    DatabaseReference FriendReff, UserReff,FriendRef;
    FirebaseAuth mAuth;
    String online_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_group_friends_list);

        //StarPostKey = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("PostKey")).toString();

        //UserKey = getIntent().getExtras().get("visit_user_Id").toString();
        mAuth = FirebaseAuth.getInstance();
        online_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");


        mToolbar = findViewById(R.id.group_friend_list_ap_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Group Friends");


        myFriendList = findViewById(R.id.group_friend_list);

        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AllGroupFriendsListActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);


        UserReff.child(online_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                    FriendReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university);

                    FriendReff.child("All Students").child(online_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists()){
                                final String University = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                                final String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                final String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                final String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();

                                FriendRef = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(departments+semester).child(group).child("Message");
                                DisplayAllGroupFriends();
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



    private void DisplayAllGroupFriends() {

        Query query = FriendRef.orderByChild("Messages"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends,GroupChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final GroupChatViewHolder groupChatViewHolder, final int position, @NonNull Friends friends) {

                final String usersIDs = getRef(position).getKey();

                UserReff.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            final String profileimage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                            groupChatViewHolder.setFullName(userName);
                            groupChatViewHolder.setProfileImage(profileimage);

                            groupChatViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent profileIntent = new Intent(AllGroupFriendsListActivity.this,PersonProfileActivity.class);
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

            public GroupChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_friend_layout, parent ,false);
                return new GroupChatViewHolder(view);
            }
        };

        myFriendList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class GroupChatViewHolder extends RecyclerView.ViewHolder {

        View mView;

        GroupChatViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.group_friend_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.group_friend_profile_full_name);
            myName.setText(fullName);
        }

    }
}
