package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private TextView searchBtn;
    private EditText searchInputsText;

    private RecyclerView searchResultForFriend;
    private DatabaseReference userReff;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        mToolbar = findViewById(R.id.find_friends_ap_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");


        searchResultForFriend = findViewById(R.id.search_friend_list);
        searchResultForFriend.setHasFixedSize(true);
        searchResultForFriend.setLayoutManager(new LinearLayoutManager(this));


        searchBtn = findViewById(R.id.find_friends_btn);
        searchInputsText = findViewById(R.id.find_friends_search);

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
                            SearchOthersFriends(searchBoxInputs);

                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void SearchOthersFriends(String searchBoxInputs)

    {
        Query searchPeopleAndFriendsQuery = userReff.orderByChild("fullName")
                .startAt(searchBoxInputs).endAt(searchBoxInputs + "\uf8ff");

        FirebaseRecyclerOptions.Builder<FindFriends> findFriendsBuilder = new FirebaseRecyclerOptions.Builder<FindFriends>();
        findFriendsBuilder.setQuery(searchPeopleAndFriendsQuery,FindFriends.class);
        FirebaseRecyclerOptions<FindFriends> options= findFriendsBuilder.build(); //query build past the query to FirebaseRecyclerAdapter
        FirebaseRecyclerAdapter<FindFriends, FindFriendViewHolder> adapter
                =new FirebaseRecyclerAdapter<FindFriends, FindFriendViewHolder>(options)
        {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendViewHolder holder, final int position, @NonNull FindFriends model)
            {

                final String PostKey = getRef(position).getKey();
                holder.username.setText(model.getFullName());
                holder.University.setText(model.getUniversity());
                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile_holder).into(holder.profileimage);

                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        String visit_user_id = getRef(position).getKey();
                        Intent findProfileIntent = new Intent(FindFriendsActivity.this, PersonProfileActivity.class);
                        findProfileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(findProfileIntent);

                    }
                });
            }
            @NonNull
            @Override
            public FindFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {

                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_friends_display,viewGroup,false);

                FindFriendViewHolder viewHolder=new FindFriendViewHolder(view);
                return viewHolder;
            }
        };

        searchResultForFriend.setAdapter(adapter);
        adapter.startListening();
    }

    public class FindFriendViewHolder extends RecyclerView.ViewHolder
    {
        TextView username, University;
        CircleImageView profileimage;

        public FindFriendViewHolder(@NonNull View itemView)
        {
            super(itemView);
            username = itemView.findViewById(R.id.all_friend_profile_full_name);
            University = itemView.findViewById(R.id.all_friend_university_name);
            profileimage = itemView.findViewById(R.id.all_users_profile_image);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Animatoo.animateSlideRight(this);
    }
}
