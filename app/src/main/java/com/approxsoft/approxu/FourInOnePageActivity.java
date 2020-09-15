package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.approxsoft.approxu.Model.Page;
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

public class FourInOnePageActivity extends AppCompatActivity {

    Toolbar mToolBar;
    private RecyclerView invitationPageList, newPageList, starPageList;
    DatabaseReference InvitationReference, PageReference;
    FirebaseAuth mAuth;
    String online_user_id, PageType;
    RelativeLayout InvitationLayout, NewPageLayout, StarPageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four_in_one_page);

        PageType = getIntent().getExtras().get("pageType").toString();
        InvitationLayout = findViewById(R.id.invitation_relative_layout);
        NewPageLayout = findViewById(R.id.new_page_relative_layout);
        StarPageLayout = findViewById(R.id.page_star_relative_layout);

        mAuth = FirebaseAuth.getInstance();
        online_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();


        mToolBar = findViewById(R.id.four_in_one_page_app_bar);
        setSupportActionBar(mToolBar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        switch (PageType) {
            case "Invitation": {
                InvitationLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Invitation Page");

                InvitationReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(online_user_id).child("Invitation");
                PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");

                invitationPageList = findViewById(R.id.invitation_page_list);
                invitationPageList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                invitationPageList.setLayoutManager(linearLayoutManager);

                DisplayAllInvitationPage();

                break;
            }
            case "NewPage": {
                NewPageLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("New Page");

                InvitationReference = FirebaseDatabase.getInstance().getReference().child("All Pages");
                PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");

                newPageList = findViewById(R.id.new_page_list);
                newPageList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                newPageList.setLayoutManager(linearLayoutManager);

                DisplayAllNewPage();

                break;
            }
            case "StarPage": {
                StarPageLayout.setVisibility(View.VISIBLE);
                getSupportActionBar().setTitle("Page Star");

                InvitationReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(online_user_id).child("StarPage");
                PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");

                starPageList = findViewById(R.id.star_page_list);
                starPageList.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                linearLayoutManager.setReverseLayout(true);
                linearLayoutManager.setStackFromEnd(true);
                starPageList.setLayoutManager(linearLayoutManager);

                DisplayStarPage();
                break;
            }
        }
    }

    private void DisplayStarPage()
    {
        Query query = InvitationReference.orderByChild("condition"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Page> options = new FirebaseRecyclerOptions.Builder<Page>().setQuery(query, Page.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Page, FourInOnePageActivity.PageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FourInOnePageActivity.PageViewHolder pageViewHolder, final int position, @NonNull Page page) {

                pageViewHolder.setDate(page.getDate());
                final String usersIDs = getRef(position).getKey();
                pageViewHolder.setDate(page.getDate());


                PageReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String pageName = dataSnapshot.child("pageName").getValue().toString();
                            String pageProfileImage = dataSnapshot.child("pageProfileImage").getValue().toString();

                            pageViewHolder.setPageName(pageName);
                            pageViewHolder.setPageProfileImage(pageProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                pageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FourInOnePageActivity.this,UserPageHomeActivity.class);
                        intent.putExtra("visit_user_id",usersIDs);
                        startActivity(intent);
                    }
                });

            }

            public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friends_display, parent ,false);
                return new PageViewHolder(view);
            }
        };
        adapter.startListening();
        starPageList.setAdapter(adapter);
    }

    private void DisplayAllNewPage()
    {
        Query query = InvitationReference.orderByChild("pageName"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Page> options = new FirebaseRecyclerOptions.Builder<Page>().setQuery(query, Page.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Page, FourInOnePageActivity.PageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FourInOnePageActivity.PageViewHolder pageViewHolder, final int position, @NonNull Page page) {

                pageViewHolder.setDate(page.getDate());
                final String usersIDs = getRef(position).getKey();
                pageViewHolder.setDate(page.getDate());


                PageReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String pageName = dataSnapshot.child("pageName").getValue().toString();
                            String pageProfileImage = dataSnapshot.child("pageProfileImage").getValue().toString();

                            pageViewHolder.setPageName(pageName);
                            pageViewHolder.setPageProfileImage(pageProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                pageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FourInOnePageActivity.this,UserPageHomeActivity.class);
                        intent.putExtra("visit_user_id",usersIDs);
                        startActivity(intent);
                    }
                });

            }

            public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friends_display, parent ,false);
                return new PageViewHolder(view);
            }
        };
        adapter.startListening();
        newPageList.setAdapter(adapter);
    }

    private void DisplayAllInvitationPage()
    {
        Query query = InvitationReference.orderByChild("type").equalTo("page"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Page> options = new FirebaseRecyclerOptions.Builder<Page>().setQuery(query, Page.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Page, FourInOnePageActivity.PageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FourInOnePageActivity.PageViewHolder pageViewHolder, final int position, @NonNull Page page) {

                pageViewHolder.setDate(page.getDate());
                final String usersIDs = getRef(position).getKey();
                pageViewHolder.setDate(page.getDate());


                PageReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String pageName = dataSnapshot.child("pageName").getValue().toString();
                            String pageProfileImage = dataSnapshot.child("pageProfileImage").getValue().toString();

                            pageViewHolder.setPageName(pageName);
                            pageViewHolder.setPageProfileImage(pageProfileImage);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                pageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(FourInOnePageActivity.this,UserPageHomeActivity.class);
                        intent.putExtra("visit_user_id",usersIDs);
                        startActivity(intent);
                    }
                });

            }

            public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_friends_display, parent ,false);
                return new PageViewHolder(view);
            }
        };
        adapter.startListening();
        invitationPageList.setAdapter(adapter);
    }

    public static class PageViewHolder extends RecyclerView.ViewHolder {

        View mView;

        PageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setPageProfileImage(String pageProfileImage){
            CircleImageView image = mView.findViewById(R.id.all_users_profile_image);
            Picasso.get().load(pageProfileImage).placeholder(R.drawable.profile_icon).into(image);
        }


        void setPageName(String pageName){
            TextView myName = mView.findViewById(R.id.all_friend_profile_full_name);
            myName.setText(pageName);
        }

        @SuppressLint("SetTextI18n")
        public void setDate(String date){
            TextView friendsDate = mView.findViewById(R.id.all_friend_university_name);
            friendsDate.setText("Page science: " + date);
        }
    }
}
