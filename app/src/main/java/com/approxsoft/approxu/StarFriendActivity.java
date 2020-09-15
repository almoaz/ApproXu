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

public class StarFriendActivity extends AppCompatActivity {

    Toolbar mToolbar;

    private RecyclerView myFriendList;
    DatabaseReference FriendReff, UserReff;
    FirebaseAuth mAuth;
    String online_user_id,StarPostKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_star_friend);

        StarPostKey = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("PostKey")).toString();
        mAuth = FirebaseAuth.getInstance();
        online_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        FriendReff = FirebaseDatabase.getInstance().getReference().child("Post").child(StarPostKey).child("Star").child(StarPostKey);
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolbar = findViewById(R.id.star_friend_list_ap_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Star Friends");


        myFriendList = findViewById(R.id.star_friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);

        DisplayAllFriends();
    }

    private void DisplayAllFriends() {

        Query query = FriendReff.orderByChild("condition"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends,StarViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final StarViewHolder starViewHolder, final int position, @NonNull Friends friends) {

                final String usersIDs = getRef(position).getKey();

                UserReff.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            final String profileimage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                            starViewHolder.setFullName(userName);
                            starViewHolder.setProfileImage(profileimage);

                            starViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    Intent profileIntent = new Intent(StarFriendActivity.this,PersonProfileActivity.class);
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

            public StarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.star_friends_layout, parent ,false);
                return new StarViewHolder(view);
            }
        };
        adapter.startListening();
        myFriendList.setAdapter(adapter);
    }
    public static class StarViewHolder extends RecyclerView.ViewHolder {

        View mView;

        StarViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.star_friend_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = mView.findViewById(R.id.star_friend_profile_full_name);
            myName.setText(fullName);
        }

    }
}
