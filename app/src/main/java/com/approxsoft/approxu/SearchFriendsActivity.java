package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

public class SearchFriendsActivity extends AppCompatActivity {

    EditText searchInputsText;
    ImageView searchBtn;
    private RecyclerView searchResultForFriend;
    DatabaseReference userReff,notificationReff,NotificationReff;
    String currentUserId;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        mAuth = FirebaseAuth.getInstance();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        notificationReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        NotificationReff = FirebaseDatabase.getInstance().getReference().child("All Users");


        searchResultForFriend = findViewById(R.id.search_friend_list);
        searchResultForFriend.setHasFixedSize(true);
        searchResultForFriend.setLayoutManager(new LinearLayoutManager(this));

        searchInputsText = findViewById(R.id.find_friends_search);
        searchBtn = findViewById(R.id.find_friends_btn);


        searchInputsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(searchInputsText.getText().toString()))
                {
                    searchBtn.setEnabled(false);

                }
                else {
                    searchBtn.setEnabled(true);
                    searchBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {

                            String searchBoxInputs = searchInputsText.getText().toString();
                            DisplaySearchFriends(searchBoxInputs);

                        }
                    });

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    private void DisplaySearchFriends(String searchBoxInputs) {

        Query query = userReff.orderByChild("fullName")
                .startAt(searchBoxInputs).endAt(searchBoxInputs + "\uf8ff");// haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, SearchFriendsActivity.FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SearchFriendsActivity.FriendsViewHolder friendsViewHolder, final int position, @NonNull final Friends friends) {

                final String usersIDs = getRef(position).getKey();

                currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                friendsViewHolder.username.setText(friends.getFullName());
                Picasso.get().load(friends.getProfileImage()).placeholder(R.drawable.profile_icon).into(friendsViewHolder.profileimage);

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
                                        Intent ProfileIntent = new Intent(SearchFriendsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(SearchFriendsActivity.this, PersonProfileActivity.class);
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
                                        Intent ProfileIntent = new Intent(SearchFriendsActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(SearchFriendsActivity.this, PersonProfileActivity.class);
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


                userReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child("Friends").child(currentUserId).hasChild(usersIDs))
                        {
                            friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.INVISIBLE);
                            friendsViewHolder.SearchFriendsCancelRequest.setEnabled(false);
                            friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.INVISIBLE);
                            friendsViewHolder.SearchFriendsAddRequest.setEnabled(false);
                            friendsViewHolder.SearchFriendAcceptRequest.setVisibility(View.INVISIBLE);
                            friendsViewHolder.SearchFriendAcceptRequest.setEnabled(false);
                            friendsViewHolder.FriendIcon.setVisibility(View.VISIBLE);
                            friendsViewHolder.FriendIcon.setEnabled(true);
                        }
                        else {
                            friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.INVISIBLE);
                            friendsViewHolder.SearchFriendsCancelRequest.setEnabled(false);
                            friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.VISIBLE);
                            friendsViewHolder.SearchFriendsAddRequest.setEnabled(true);
                            friendsViewHolder.FriendIcon.setVisibility(View.INVISIBLE);
                            friendsViewHolder.FriendIcon.setEnabled(false);

                            if (dataSnapshot.child("FriendRequest").child(currentUserId).hasChild(usersIDs))
                            {
                                if (dataSnapshot.child("FriendRequest").child(currentUserId).child(usersIDs).child("request_type").getValue().equals("sent"))
                                {
                                    friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.VISIBLE);
                                    friendsViewHolder.SearchFriendsCancelRequest.setEnabled(true);
                                    friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.INVISIBLE);
                                    friendsViewHolder.SearchFriendsAddRequest.setEnabled(false);
                                    friendsViewHolder.FriendIcon.setVisibility(View.INVISIBLE);
                                    friendsViewHolder.FriendIcon.setEnabled(false);

                                    friendsViewHolder.SearchFriendsCancelRequest.setOnClickListener(new View.OnClickListener() {
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
                                                                if (task.isSuccessful())
                                                                {

                                                                    friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.INVISIBLE);
                                                                    friendsViewHolder.SearchFriendsCancelRequest.setEnabled(false);
                                                                    friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.VISIBLE);
                                                                    friendsViewHolder.SearchFriendsAddRequest.setEnabled(true);
                                                                    friendsViewHolder.FriendIcon.setVisibility(View.INVISIBLE);
                                                                    friendsViewHolder.FriendIcon.setEnabled(false);

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                                else if (dataSnapshot.child("FriendRequest").child(currentUserId).child(usersIDs).child("request_type").getValue().equals("received"))
                                {
                                    friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.INVISIBLE);
                                    friendsViewHolder.SearchFriendsCancelRequest.setEnabled(false);
                                    friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.INVISIBLE);
                                    friendsViewHolder.SearchFriendsAddRequest.setEnabled(false);
                                    friendsViewHolder.SearchFriendAcceptRequest.setVisibility(View.VISIBLE);
                                    friendsViewHolder.SearchFriendAcceptRequest.setEnabled(true);
                                    friendsViewHolder.FriendIcon.setVisibility(View.INVISIBLE);
                                    friendsViewHolder.FriendIcon.setEnabled(false);

                                    friendsViewHolder.SearchFriendAcceptRequest.setOnClickListener(new View.OnClickListener() {
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
                                                                                            notificationMap.put("text","Accepted friend request");
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

                                                                                                                friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.INVISIBLE);
                                                                                                                friendsViewHolder.SearchFriendsCancelRequest.setEnabled(false);
                                                                                                                friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.INVISIBLE);
                                                                                                                friendsViewHolder.SearchFriendsAddRequest.setEnabled(false);
                                                                                                                friendsViewHolder.SearchFriendAcceptRequest.setVisibility(View.INVISIBLE);
                                                                                                                friendsViewHolder.SearchFriendAcceptRequest.setEnabled(false);
                                                                                                                friendsViewHolder.FriendIcon.setVisibility(View.VISIBLE);
                                                                                                                friendsViewHolder.FriendIcon.setEnabled(true);


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

                                }
                            }else
                            {
                                friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.INVISIBLE);
                                friendsViewHolder.SearchFriendsCancelRequest.setEnabled(false);
                                friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.VISIBLE);
                                friendsViewHolder.SearchFriendsAddRequest.setEnabled(true);
                                friendsViewHolder.SearchFriendAcceptRequest.setVisibility(View.INVISIBLE);
                                friendsViewHolder.SearchFriendAcceptRequest.setEnabled(false);
                                friendsViewHolder.FriendIcon.setVisibility(View.INVISIBLE);
                                friendsViewHolder.FriendIcon.setEnabled(false);


                                friendsViewHolder.SearchFriendsAddRequest.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        userReff.child(usersIDs).child("FriendRequest").child(usersIDs).child(currentUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task)
                                            {
                                                if (task.isSuccessful())
                                                {

                                                    userReff.child(currentUserId).child("FriendRequest").child(currentUserId).child(usersIDs).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                                                userReff.child(usersIDs).child("Notifications").child(usersIDs).child(currentUserId).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task task)
                                                                    {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            userReff.child(usersIDs).child("Notification").child(usersIDs).child(currentUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        //holder.AddFriendsBtn.setText("Cancel Request");
                                                                                        friendsViewHolder.SearchFriendsCancelRequest.setVisibility(View.VISIBLE);
                                                                                        friendsViewHolder.SearchFriendsCancelRequest.setEnabled(true);
                                                                                        friendsViewHolder.SearchFriendsAddRequest.setVisibility(View.INVISIBLE);
                                                                                        friendsViewHolder.SearchFriendsAddRequest.setEnabled(false);
                                                                                        friendsViewHolder.SearchFriendAcceptRequest.setVisibility(View.INVISIBLE);
                                                                                        friendsViewHolder.SearchFriendAcceptRequest.setEnabled(false);
                                                                                        friendsViewHolder.FriendIcon.setVisibility(View.INVISIBLE);
                                                                                        friendsViewHolder.FriendIcon.setEnabled(false);

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

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_friend_request_layout, parent ,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        searchResultForFriend.setAdapter(adapter);
    }
    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        ImageView SearchFriendsAddRequest,SearchFriendsCancelRequest, SearchFriendAcceptRequest,FriendIcon;
        TextView username;
        CircleImageView profileimage;

        FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username = itemView.findViewById(R.id.search_friend_profile_full_name);
            profileimage = itemView.findViewById(R.id.search_friends_profile_image);
            SearchFriendsAddRequest = itemView.findViewById(R.id.search_friends_add_friend);
            SearchFriendsCancelRequest = itemView.findViewById(R.id.search_friends_cancel_request);
            SearchFriendAcceptRequest = itemView.findViewById(R.id.search_friends_accept_request);
            FriendIcon = itemView.findViewById(R.id.search_friend_friends_icon);
        }

    }
}
