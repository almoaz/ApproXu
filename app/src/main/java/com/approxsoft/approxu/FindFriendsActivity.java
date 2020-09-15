package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView searchBtn;
    private EditText searchInputsText;

    private RecyclerView searchResultForFriend,findFriends;
    DatabaseReference userReff, notificationReff,NotificationReff, friendreff;
    private FirebaseAuth mAuth;
    String currentUserId,CurrentUserId;
    SwipeRefreshLayout refreshLayout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();

        friendreff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId).child("FriendRequest").child(CurrentUserId);
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        notificationReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        NotificationReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolbar = findViewById(R.id.find_friends_ap_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");


        findFriends = findViewById(R.id.find_friend_list);
        findFriends.setHasFixedSize(true);
        findFriends.setLayoutManager(new LinearLayoutManager(this));


        searchResultForFriend = findViewById(R.id.unknown_friend_list);
        searchResultForFriend.setHasFixedSize(true);
        searchResultForFriend.setLayoutManager(new LinearLayoutManager(this));




        searchBtn = findViewById(R.id.find_friends_search);
        refreshLayout = findViewById(R.id.find_friend_refresh_Layout);

        /***/



        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                //String searchBoxInputs = searchInputsText.getText().toString();
                /// SearchOthersFriends(searchBoxInputs);

                Intent intent = new Intent(FindFriendsActivity.this,SearchFriendsActivity.class);
                startActivity(intent);

            }
        });

        FriendRequestFriends();

        SearchOthersFriends();





        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               SearchOthersFriends();

            }
        });

    }

    private void FriendRequestFriends()
    {
        Query query = friendreff.orderByChild("request_type");


        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, FindFriendsActivity.FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendsActivity.FriendsViewHolder friendsViewHolder, final int position, @NonNull final Friends friends) {

                final String usersIDs = getRef(position).getKey();

                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                friendsViewHolder.username.setText(friends.getFullName());
                Picasso.get().load(friends.getProfileImage()).placeholder(R.drawable.profile_icon).into(friendsViewHolder.profileimage);

                userReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String userName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                        final String profileimage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                        friendsViewHolder.setFullName(userName);
                        friendsViewHolder.setProfileImage(profileimage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                friendsViewHolder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assert usersIDs != null;
                        userReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                    final String visit_user_id = userReff.child(Uid).getKey();

                                    if (Uid.equals(currentUserId))
                                    {
                                        Intent ProfileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
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

                friendsViewHolder.profileimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assert usersIDs != null;
                        userReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                    final String visit_user_id = userReff.child(Uid).getKey();

                                    if (Uid.equals(currentUserId))
                                    {
                                        Intent ProfileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
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

                friendsViewHolder.AcceptFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Calendar calForDate = Calendar.getInstance();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                        final String saveCurrentDate = currentDate.format(calForDate.getTime());


                        userReff.child(usersIDs).child("Friends").child(usersIDs).child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    userReff.child(currentUserId).child("Friends").child(currentUserId).child(usersIDs).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){


                                                userReff.child(usersIDs).child("FriendRequest").child(usersIDs).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            userReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(usersIDs).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        Calendar calForDate = Calendar.getInstance();
                                                                        @SuppressLint("SimpleDateFormat") final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                                        final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                                        Calendar calForTime = Calendar.getInstance();
                                                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
                                                                        final String saveCurrentTime = currentTime.format(calForTime.getTime());

                                                                        final HashMap notificationMap = new HashMap();
                                                                        notificationMap.put("date",saveCurrentDate);
                                                                        notificationMap.put("time",saveCurrentTime);
                                                                        notificationMap.put("text","has been friends request");
                                                                        notificationMap.put("type","user");
                                                                        notificationMap.put("condition","false");
                                                                        userReff.child(usersIDs).child("Notifications").child(usersIDs).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task task) {
                                                                                if(task.isSuccessful()){
                                                                                    userReff.child(usersIDs).child("Notification").child(usersIDs).child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                        {
                                                                                            friendsViewHolder.AcceptFriends.setText("Friends");

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
                                        }
                                    });
                                }
                            }
                        });
                    }
                });

                friendsViewHolder.DeleteFriends.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        userReff.child(usersIDs).child("FriendRequest").child(usersIDs).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                {
                                    userReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(usersIDs).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful()){

                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });



            }

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_friend_request_layout, parent ,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        findFriends.setAdapter(adapter);
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        TextView username, AcceptFriends, DeleteFriends;
        CircleImageView profileimage;

        FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username = itemView.findViewById(R.id.find_friend_profile_full_name);
            profileimage = itemView.findViewById(R.id.find_users_profile_image);
            AcceptFriends = itemView.findViewById(R.id.find_friends_accept_btn);
            DeleteFriends = itemView.findViewById(R.id.find_friends_delete_btn);
        }
        public void setProfileImage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.find_users_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.find_friend_profile_full_name);
            myName.setText(fullName);
        }

    }





    private void SearchOthersFriends()

    {
        Query searchPeopleAndFriendsQuery = userReff.orderByChild("uid");
                //.startAt(usersIDs).endAt(usersIDs + "\uf8ff");

        FirebaseRecyclerOptions<FindFriends> options = new FirebaseRecyclerOptions.Builder<FindFriends>().setQuery(searchPeopleAndFriendsQuery, FindFriends.class).build();
        FirebaseRecyclerAdapter<FindFriends, FindFriendsActivity.FindFriendViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<FindFriends, FindFriendsActivity.FindFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FindFriendViewHolder holder, final int position, @NonNull final FindFriends model)
            {

                refreshLayout.isRefreshing();
                refreshLayout.setRefreshing(false);
                final String FriendsKey = getRef(position).getKey();


                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                final String FindFriendKey = getRef(position).getKey();
                holder.username.setText(model.getFullName());
                //Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile_holder).into(holder.profileimage);
                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile_icon).into(holder.profileimage);

                userReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child(currentUserId).child("Friends").child(currentUserId).hasChild(FriendsKey))
                        {
                           holder.SearchFriendsLayout.setVisibility(View.GONE);

                        }else if (dataSnapshot.child(currentUserId).child("uid").getValue().equals(FriendsKey)){

                            holder.SearchFriendsLayout.setVisibility(View.GONE);

                        }else {
                            holder.SearchFriendsLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assert FindFriendKey != null;
                        userReff.child(FindFriendKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                    final String visit_user_id = userReff.child(Uid).getKey();

                                    if (Uid.equals(currentUserId))
                                    {
                                        Intent ProfileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
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

                holder.profileimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        assert FindFriendKey != null;
                        userReff.child(FindFriendKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                    final String visit_user_id = userReff.child(Uid).getKey();

                                    if (Uid.equals(currentUserId))
                                    {
                                        Intent ProfileIntent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
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

                holder.friendButtonControl(FindFriendKey);




                //////////////////////////////////
                /////////////////////////////////


                userReff.child(currentUserId).child("Following").child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.hasChild(FindFriendKey))
                        {
                            holder.friendFollowBtn.setText("Unfollow");
                            holder.AddFriendsBtn.setEnabled(false);
                            holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                            holder.CancelFriendsBtn.setEnabled(false);
                            holder.addFriendBtnLock.setVisibility(View.VISIBLE);
                            holder.friendFollowBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    userReff.child(currentUserId).child("Following").child(currentUserId).child(FindFriendKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                userReff.child(FindFriendKey).child("Following").child(FindFriendKey).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            holder.friendFollowBtn.setText("Follow");
                                                            holder.friendFollowBtn.setEnabled(true);
                                                            holder.AddFriendsBtn.setEnabled(true);
                                                            holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                                                            holder.CancelFriendsBtn.setEnabled(false);
                                                            holder.addFriendBtnLock.setVisibility(View.GONE);
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
                            holder.AddFriendsBtn.setEnabled(true);
                            holder.addFriendBtnLock.setVisibility(View.GONE);
                            holder.friendFollowBtn.setText("Follow");
                            holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                            holder.CancelFriendsBtn.setEnabled(false);
                            // holder.addFriendBtnLock.setVisibility(View.VISIBLE);
                            holder.friendFollowBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    userReff.child(currentUserId).child("Following").child(currentUserId).child(FindFriendKey).child("type").setValue("follow").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                userReff.child(FindFriendKey).child("Following").child(FindFriendKey).child(currentUserId).child("type").setValue("follower").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {


                                                            Calendar calForDate = Calendar.getInstance();
                                                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                            String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                            Calendar calForTime = Calendar.getInstance();
                                                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
                                                            String saveCurrentTime = currentTime.format(calForTime.getTime());

                                                            final HashMap notificationMap = new HashMap();
                                                            notificationMap.put("date",saveCurrentDate);
                                                            notificationMap.put("time",saveCurrentTime);
                                                            notificationMap.put("text","following");
                                                            notificationMap.put("type","user");
                                                            notificationMap.put("condition","false");


                                                            notificationReff.child(FindFriendKey).child("Notification").child(FindFriendKey).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                                @Override
                                                                public void onComplete(@NonNull Task task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        NotificationReff.child(FindFriendKey).child("Notifications").child(FindFriendKey).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task task) {
                                                                                if (task.isSuccessful()){

                                                                                    holder.friendFollowBtn.setText("Unfollow");
                                                                                    holder.AddFriendsBtn.setEnabled(false);
                                                                                    holder.addFriendBtnLock.setVisibility(View.VISIBLE);

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
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                holder.CancelFriendsBtn.setEnabled(false);



                //////////////////////////////////
                /////////////////////////////////


                userReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {


                        if (dataSnapshot.child("Friends").child(currentUserId).hasChild(FindFriendKey))
                        {

                            holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                            holder.CancelFriendsBtn.setEnabled(false);

                            holder.AddFriendAcceptBtn.setVisibility(View.INVISIBLE);
                            holder.AddFriendAcceptBtn.setEnabled(false);
                            holder.AddFriendDeleteBtn.setVisibility(View.INVISIBLE);
                            holder.AddFriendDeleteBtn.setEnabled(false);

                            holder.friendFollowBtn.setVisibility(View.INVISIBLE);
                            holder.friendFollowBtn.setEnabled(false);
                            holder.followBtnLock.setVisibility(View.GONE);
                            holder.AddFriendsBtn.setVisibility(View.VISIBLE);
                            holder.AddFriendsBtn.setText("Friend");

                            holder.FriendMessageBtn.setVisibility(View.VISIBLE);
                            holder.FriendMessageBtn.setEnabled(true);
                            holder.FriendMessageBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String visit_user_id = getRef(position).getKey();
                                    Intent chatIntent = new Intent(FindFriendsActivity.this, ChatActivity.class);
                                    chatIntent.putExtra("visit_user_id", visit_user_id);
                                    chatIntent.putExtra("userName",model.getFullName());
                                    startActivity(chatIntent);
                                }
                            });

                        }else {

                            holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                            holder.CancelFriendsBtn.setEnabled(false);
                            holder.followBtnLock.setVisibility(View.GONE);
                            holder.AddFriendsBtn.setVisibility(View.VISIBLE);
                           // holder.AddFriendsBtn.setEnabled(true);

                            if (dataSnapshot.child("FriendRequest").child(currentUserId).hasChild(FindFriendKey))
                            {

                                if (dataSnapshot.child("FriendRequest").child(currentUserId).child(FindFriendKey).child("request_type").getValue().equals("sent"))
                                {

                                    holder.AddFriendsBtn.setVisibility(View.INVISIBLE);
                                    holder.AddFriendsBtn.setEnabled(false);
                                    holder.CancelFriendsBtn.setVisibility(View.VISIBLE);
                                    holder.CancelFriendsBtn.setEnabled(true);
                                    holder.AddFriendAcceptBtn.setVisibility(View.INVISIBLE);
                                    holder.AddFriendAcceptBtn.setEnabled(false);
                                    holder.AddFriendDeleteBtn.setVisibility(View.INVISIBLE);
                                    holder.AddFriendDeleteBtn.setEnabled(false);
                                    holder.friendFollowBtn.setVisibility(View.VISIBLE);
                                    holder.friendFollowBtn.setEnabled(false);
                                    holder.followBtnLock.setVisibility(View.VISIBLE);
                                    holder.CancelFriendsBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            userReff.child(FindFriendKey).child("FriendRequest").child(FindFriendKey).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        userReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(FindFriendKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful())
                                                                {

                                                                    holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                                                                    holder.CancelFriendsBtn.setEnabled(false);
                                                                    holder.AddFriendAcceptBtn.setVisibility(View.INVISIBLE);
                                                                    holder.AddFriendAcceptBtn.setEnabled(false);
                                                                    holder.AddFriendDeleteBtn.setVisibility(View.INVISIBLE);
                                                                    holder.AddFriendDeleteBtn.setEnabled(false);

                                                                    holder.followBtnLock.setVisibility(View.GONE);
                                                                    holder.AddFriendsBtn.setVisibility(View.VISIBLE);
                                                                    holder.AddFriendsBtn.setEnabled(true);
                                                                    holder.friendFollowBtn.setVisibility(View.VISIBLE);
                                                                    holder.friendFollowBtn.setEnabled(true);

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });

                                } else if (dataSnapshot.child("FriendRequest").child(currentUserId).child(FindFriendKey).child("request_type").getValue().equals("received")) {
                                    holder.AddFriendsBtn.setVisibility(View.INVISIBLE);
                                    holder.AddFriendsBtn.setEnabled(false);
                                    holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                                    holder.CancelFriendsBtn.setEnabled(false);
                                    holder.AddFriendAcceptBtn.setVisibility(View.VISIBLE);
                                    holder.AddFriendAcceptBtn.setEnabled(true);
                                    holder.AddFriendDeleteBtn.setVisibility(View.VISIBLE);
                                    holder.AddFriendDeleteBtn.setEnabled(true);
                                    holder.friendFollowBtn.setVisibility(View.INVISIBLE);
                                    holder.friendFollowBtn.setEnabled(false);
                                    holder.AddFriendAcceptBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Calendar calForDate = Calendar.getInstance();
                                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                            final String saveCurrentDate = currentDate.format(calForDate.getTime());


                                            userReff.child(FindFriendKey).child("Friends").child(FindFriendKey).child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                    if (task.isSuccessful())
                                                    {
                                                        userReff.child(currentUserId).child("Friends").child(currentUserId).child(FindFriendKey).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){


                                                                    userReff.child(FindFriendKey).child("FriendRequest").child(FindFriendKey).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                userReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(FindFriendKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            Calendar calForDate = Calendar.getInstance();
                                                                                            @SuppressLint("SimpleDateFormat") final SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                                                            final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                                                            Calendar calForTime = Calendar.getInstance();
                                                                                            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
                                                                                            final String saveCurrentTime = currentTime.format(calForTime.getTime());

                                                                                            final HashMap notificationMap = new HashMap();
                                                                                            notificationMap.put("date",saveCurrentDate);
                                                                                            notificationMap.put("time",saveCurrentTime);
                                                                                            notificationMap.put("text","Accept friend request");
                                                                                            notificationMap.put("type","user");
                                                                                            notificationMap.put("condition","false");
                                                                                            userReff.child(FindFriendKey).child("Notifications").child(FindFriendKey).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task task) {
                                                                                                    if(task.isSuccessful()){
                                                                                                        userReff.child(FindFriendKey).child("Notification").child(FindFriendKey).child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                                                            {

                                                                                                                holder.AddFriendsBtn.setVisibility(View.VISIBLE);
                                                                                                                holder.AddFriendsBtn.setEnabled(true);
                                                                                                                holder.followBtnLock.setVisibility(View.GONE);
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
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });

                                    holder.AddFriendDeleteBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            userReff.child(FindFriendKey).child("FriendRequest").child(FindFriendKey).child(currentUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful())
                                                    {
                                                        userReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(FindFriendKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task)
                                                            {
                                                                if (task.isSuccessful()){

                                                                    holder.AddFriendAcceptBtn.setVisibility(View.INVISIBLE);
                                                                    holder.AddFriendAcceptBtn.setEnabled(false);
                                                                    holder.AddFriendDeleteBtn.setVisibility(View.INVISIBLE);
                                                                    holder.AddFriendDeleteBtn.setEnabled(false);
                                                                    holder.AddFriendsBtn.setVisibility(View.VISIBLE);
                                                                    holder.AddFriendsBtn.setEnabled(true);
                                                                    holder.friendFollowBtn.setVisibility(View.VISIBLE);
                                                                    holder.friendFollowBtn.setEnabled(true);
                                                                    holder.followBtnLock.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }


                            }else {

                                holder.CancelFriendsBtn.setVisibility(View.INVISIBLE);
                                holder.CancelFriendsBtn.setEnabled(false);
                                holder.AddFriendAcceptBtn.setVisibility(View.INVISIBLE);
                                holder.AddFriendAcceptBtn.setEnabled(false);
                                holder.AddFriendDeleteBtn.setVisibility(View.INVISIBLE);
                                holder.AddFriendDeleteBtn.setEnabled(false);
                                //holder.AddFriendsBtn.setVisibility(View.VISIBLE);
                                //holder.AddFriendsBtn.setEnabled(true);
                                holder.friendFollowBtn.setVisibility(View.VISIBLE);
                                holder.friendFollowBtn.setEnabled(true);
                                holder.AddFriendsBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userReff.child(FindFriendKey).child("FriendRequest").child(FindFriendKey).child(currentUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {

                                                    userReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(FindFriendKey).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            if (task.isSuccessful())
                                                            {


                                                                Calendar calForDate = Calendar.getInstance();
                                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                                final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                                Calendar calForTime = Calendar.getInstance();
                                                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
                                                                final String saveCurrentTime = currentTime.format(calForTime.getTime());

                                                                final HashMap notificationMap = new HashMap();
                                                                notificationMap.put("date",saveCurrentDate);
                                                                notificationMap.put("time",saveCurrentTime);
                                                                notificationMap.put("text","has been friends request");
                                                                notificationMap.put("type","user");
                                                                notificationMap.put("condition","false");

                                                                userReff.child(FindFriendKey).child("Notifications").child(FindFriendKey).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task task)
                                                                    {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            userReff.child(FindFriendKey).child("Notification").child(FindFriendKey).child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        //holder.AddFriendsBtn.setText("Cancel Request");
                                                                                        holder.AddFriendsBtn.setVisibility(View.INVISIBLE);
                                                                                        holder.AddFriendsBtn.setEnabled(false);
                                                                                        holder.AddFriendAcceptBtn.setVisibility(View.INVISIBLE);
                                                                                        holder.AddFriendAcceptBtn.setEnabled(false);
                                                                                        holder.AddFriendDeleteBtn.setVisibility(View.INVISIBLE);
                                                                                        holder.AddFriendDeleteBtn.setEnabled(false);
                                                                                        holder.CancelFriendsBtn.setVisibility(View.VISIBLE);
                                                                                        holder.CancelFriendsBtn.setEnabled(true);
                                                                                        holder.friendFollowBtn.setVisibility(View.VISIBLE);
                                                                                        holder.friendFollowBtn.setEnabled(false);
                                                                                        holder.followBtnLock.setVisibility(View.VISIBLE);
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
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {

                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_friends_display,viewGroup,false);

                FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                return viewHolder;
            }
        };

        searchResultForFriend.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }



    public class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, AddFriendsBtn,CancelFriendsBtn,AddFriendAcceptBtn,AddFriendDeleteBtn ,FriendMessageBtn, friendFollowBtn;
        CircleImageView profileimage;
        ImageView addFriendBtnLock, followBtnLock;
        String currentUserId;
        DatabaseReference UserReff;
        ConstraintLayout constraintLayout;
        View mView;
        LinearLayout SearchFriendsLayout;

        public FindFriendViewHolder(@NonNull View itemView)
        {

            super(itemView);
            mView = itemView;
            username = itemView.findViewById(R.id.all_friend_profile_full_name);
            profileimage = itemView.findViewById(R.id.all_users_profile_image);
            AddFriendsBtn = itemView.findViewById(R.id.add_friends_btn);
            CancelFriendsBtn = itemView.findViewById(R.id.cancel_friends_btn);
            AddFriendAcceptBtn = itemView.findViewById(R.id.add_friends_accept_btn);
            AddFriendDeleteBtn = itemView.findViewById(R.id.add_friends_delete_btn);
            constraintLayout = itemView.findViewById(R.id.add_friend_constraint_layout);
            FriendMessageBtn = itemView.findViewById(R.id.add_friends_message_btn);
            friendFollowBtn = itemView.findViewById(R.id.add_friends_follow_btn);
            addFriendBtnLock = itemView.findViewById(R.id.add_friend_lock);
            followBtnLock = itemView.findViewById(R.id.follow_lock);
            SearchFriendsLayout = itemView.findViewById(R.id.search_friend_linear_layout);


            UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        }

        void friendButtonControl(final String PostKey) {



        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}
