package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
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

public class HomeActivity extends AppCompatActivity {


    private DrawerLayout MainLayout;
    private NavigationView navigationView;
    private RecyclerView postList;
    private Toolbar mtoolber;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView navProfileName,notificationCount, messageCount;
    private ImageView notificationBtn ,homeMessage;
    private EditText profileEditText;
    private FirebaseAuth mAuth;
    String currentUserId,reciverUseId;
    private int notifiCount = 0;
    private int messagecount = 0;
    private DatabaseReference userRef;
    private DatabaseReference PostsRef, FriendPosts;
    private DatabaseReference StarRef;
    Boolean StarChecker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mtoolber = findViewById(R.id.main_page_toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("All Users");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Star");

        notificationCount = findViewById(R.id.notification_count);
        messageCount = findViewById(R.id.message_count);
        homeMessage = findViewById(R.id.home_message_icon);


        postList = (RecyclerView) findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);


        //setSupportActionBar(mtoolber);
        //setTitle("ApproXu");

        MainLayout = findViewById(R.id.main_layout);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView = findViewById(R.id.main_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this,MainLayout,mtoolber,R.string.navigation_open,R.string.navigation_close);
        MainLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileName = navView.findViewById(R.id.nav_full_name);
        NavProfileImage = navView.findViewById(R.id.nav_profile_image);
        notificationBtn = findViewById(R.id.notification_Btn);

        DisplayAllUsersPosts();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        homeMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent messageIntent = new Intent(HomeActivity.this,MessageActivity.class);
                startActivity(messageIntent);
                Animatoo.animateFade(HomeActivity.this);
            }
        });

        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    if(dataSnapshot.hasChild("fullName"))
                    {
                        if (dataSnapshot.hasChild("fullName")){
                            String fullName =dataSnapshot.child("fullName").getValue().toString();
                            navProfileName.setText(fullName);
                        }
                        if (dataSnapshot.hasChild("profileImage")){
                            String image =dataSnapshot.child("profileImage").getValue().toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(NavProfileImage);
                        }else {
                            Toast.makeText(HomeActivity.this,"Profile image do not exists...", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(HomeActivity.this,"Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userRef.child(currentUserId).child("Friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        userRef.child(currentUserId).child("Notification").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    notificationCount.setVisibility(View.VISIBLE);
                    notifiCount = (int) dataSnapshot.getChildrenCount();
                    notificationCount.setText(Integer.toString(notifiCount));
                    notificationBtn.setImageResource(R.drawable.full_black_notification);
                } else {
                    notificationCount.setText("");
                    notificationCount.setVisibility(View.INVISIBLE);
                    notificationBtn.setImageResource(R.drawable.full_black_notification);
                }
            }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }

        });

        userRef.child(currentUserId).child("MessageNotification").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messageCount.setVisibility(View.VISIBLE);
                    messagecount = (int) dataSnapshot.getChildrenCount();
                    messageCount.setText(Integer.toString(messagecount));
                    homeMessage.setImageResource(R.drawable.chat_icon);
                } else {
                    messageCount.setText("");
                    messageCount.setVisibility(View.INVISIBLE);
                    homeMessage.setImageResource(R.drawable.chat_icon);
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        });





    }


    private void UpdateUserStatus(String state)
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

        userRef.child(currentUserId).child("userState")
                .updateChildren(CurrentStatemap);
    }

    private void DisplayAllUsersPosts() {
        Query ShortPostInDecendingOrder = PostsRef.orderByChild("counter");



        FirebaseRecyclerOptions<Posts> options=new FirebaseRecyclerOptions.Builder<Posts>().setQuery(ShortPostInDecendingOrder,Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull PostsViewHolder holder, final int position, @NonNull Posts model) {

                final String PostKey = getRef(position).getKey();
                final String visit_user_id = userRef.child(currentUserId).getKey();
                holder.username.setText(model.getFullName());
                holder.time.setText("    " +model.getTime());
                holder.date.setText("    " +model.getDate());
                holder.description.setText(model.getDescription());

                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile_icon).into(holder.user_profile_image);
                Picasso.get().load(model.getPostImage()).into(holder.postImage);

                holder.setStarButtonStatus(PostKey);
                holder.setCommentsCount(PostKey);

                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(clickPostIntent);
                    }
                });
                holder.user_profile_image.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        Intent findProfileIntent = new Intent(HomeActivity.this, PersonProfileActivity.class);
                        findProfileIntent.putExtra("visit_user_id", visit_user_id);
                        startActivity(findProfileIntent);

                    }
                });
                holder.description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(clickPostIntent);
                    }
                });



                holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        Intent commentsIntent = new Intent(HomeActivity.this,CommentsActivity.class);
                        commentsIntent.putExtra("PostKey",PostKey);
                        startActivity(commentsIntent);
                    }
                });

                holder.StarPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        StarChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (StarChecker.equals(true))
                                {
                                    if (dataSnapshot.child(PostKey).hasChild(currentUserId))
                                    {
                                        StarRef.child(PostKey).child(currentUserId).removeValue();
                                        StarChecker = false;
                                    }
                                    else
                                    {
                                        StarRef.child(PostKey).child(currentUserId).setValue(true);
                                        StarChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });



            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout,parent,false);
                PostsViewHolder viewHolder=new PostsViewHolder(view);
                return viewHolder;
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);

        //updateUserStatus("online");
        firebaseRecyclerAdapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView StarPostBtn, CommentsPostBtn;
        TextView DisplayNoOfStar, commentsCount;
        int countStar;
        int CommentsCount = 0 ;
        String currentUserId;
        DatabaseReference StarReff,CommentReff;
        TextView username,date,time,description;
        CircleImageView user_profile_image;
        ImageView postImage;
        public PostsViewHolder(View itemView ) {
            super(itemView);
            mView = itemView;

            username=itemView.findViewById(R.id.post_user_name);
            date=itemView.findViewById(R.id.post_date);
            time=itemView.findViewById(R.id.post_time);
            description=itemView.findViewById(R.id.post_description);
            user_profile_image =itemView.findViewById(R.id.post_profile_image);
            postImage=itemView.findViewById(R.id.post_image);

            StarPostBtn = itemView.findViewById(R.id.display_star_btn);
            CommentsPostBtn = itemView.findViewById(R.id.comment_button);
            commentsCount = itemView.findViewById(R.id.comments_count_btn);
            DisplayNoOfStar = itemView.findViewById(R.id.display_no_of_star);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Star");
            CommentReff = FirebaseDatabase.getInstance().getReference().child("Post");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        }

        public void setStarButtonStatus(final String PostKey)
        {
            StarReff.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId))
                    {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar)+(" Star"));
                    }else {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar)+(" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        public void setCommentsCount(final String PostKey)
        {
            CommentReff.child(PostKey).child("Comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        commentsCount.setVisibility(View.VISIBLE);
                        CommentsCount = (int) dataSnapshot.getChildrenCount();
                        commentsCount.setText(Integer.toString(CommentsCount));
                    } else {
                        commentsCount.setText("");
                        commentsCount.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }

            });

        }


    }

    public void homeBtn(View view) {
        if (view == findViewById(R.id.home_home_icon)){

            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            Animatoo.animateFade(this);
        }
        if (view == findViewById(R.id.home_add_post_icon)){

            startActivity(new Intent(HomeActivity.this, AddPostActivity.class));
            Animatoo.animateFade(this);
        }
        if (view == findViewById(R.id.home_friends_icon)){

            startActivity(new Intent(HomeActivity.this, FriendsActivity.class));
            Animatoo.animateSlideLeft(this);
        }
        if (view == findViewById(R.id.notification_Btn)){

            startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
            Animatoo.animateSlideLeft(this);
        }
        if (view == findViewById(R.id.home_profile_icon)){

            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            Animatoo.animateFade(this);
        }
        if (view == findViewById(R.id.home_approx_text)){

            startActivity(new Intent(HomeActivity.this, FindFriendsActivity.class));
            Animatoo.animateFade(this);
        }

    }




    private void UserMenuSelector(MenuItem item)
    {
        switch (item.getItemId()){
            case R.id.menu_profile:
                SendUserToProfileActivity();
                break;
            case R.id.menu_new_post:
               // Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                SendToPostActivity();
                break;
            case R.id.menu_message:
                SendToMessageActivity();
               // Toast.makeText(this,"University",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_notification:
                SendToNotificationActivity();
                // Toast.makeText(this,"University",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_friends:
                SendToFriendsActivity();
               // Toast.makeText(this,"Home",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_find_friends:
                SendToFindFriendsActivity();
               // Toast.makeText(this,"Friend",Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_university:
               // Toast.makeText(this,"Find Friend",Toast.LENGTH_SHORT).show();
                SendToUniversityActivity();
                break;

            case R.id.menu_settings:
                SendToSettingsActivity();
                break;

            case R.id.menu_sign_out:
                UpdateUserStatus("offline");
                mAuth.signOut();
                UserToLoginActivity();

                break;
        }

    }

    private void SendToUniversityActivity() {
        Intent universityIntent = new Intent(HomeActivity.this,UniversityActivity.class);
        startActivity(universityIntent);
        Animatoo.animateSlideLeft(this);
    }

    private void SendToNotificationActivity() {
        startActivity(new Intent(HomeActivity.this, NotificationActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    private void SendToMessageActivity() {
        Intent messageIntent = new Intent(HomeActivity.this,MessageActivity.class);
        startActivity(messageIntent);
        Animatoo.animateSlideLeft(this);
    }

    private void SendToFriendsActivity() {
        startActivity(new Intent(HomeActivity.this, FriendsActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    private void SendToFindFriendsActivity() {
        startActivity(new Intent(HomeActivity.this, FindFriendsActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    private void SendUserToProfileActivity() {
        startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    private void SendToPostActivity() {
        startActivity(new Intent(HomeActivity.this, AddPostActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    private void SendToSettingsActivity() {
        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    private void UserToLoginActivity() {
        Intent loginIntent = new Intent(HomeActivity.this,SignInActivity.class);
        startActivity(loginIntent);
        Animatoo.animateFade(this);
        finish();
    }


}

