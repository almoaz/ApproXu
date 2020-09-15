package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {


    DrawerLayout MainLayout;
    NavigationView navigationView;
    private RecyclerView postList;
    Toolbar mToolBer;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private CircleImageView NavProfileImage;
    private TextView navProfileName,notificationCount, messageCount,HidenBtn, UpdateMessage, HomeApproxText;
    private ImageView notificationBtn ,homeMessage, profileIcon;
    FirebaseAuth mAuth;
    String currentUserId;
    private int notifiCount = 0;
    private int messagecount = 0;
    DatabaseReference userRef, VersionReff, PageReff;
    DatabaseReference PostsRef ;
    DatabaseReference StarRef;
    Boolean StarChecker = false;
    SwipeRefreshLayout refreshLayout;
    ConstraintLayout constraintLayout;
    MediaPlayer mediaPlayer, mediaPlayer2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolBer = findViewById(R.id.main_page_toolbar);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("All Users");
        PageReff = FirebaseDatabase.getInstance().getReference().child("All Pages");
        VersionReff = FirebaseDatabase.getInstance().getReference().child("Approxu");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Post");

        notificationCount = findViewById(R.id.notification_count);
        messageCount = findViewById(R.id.message_count);
        homeMessage = findViewById(R.id.home_message_icon);
        refreshLayout = findViewById(R.id.refresh_Layout);
        constraintLayout = findViewById(R.id.home_constraint_layout);
        HidenBtn = findViewById(R.id.home_hiden_btn);

        mediaPlayer = MediaPlayer.create(this,R.raw.intuition);
        mediaPlayer2 = MediaPlayer.create(this,R.raw.pristine);


        userRef.child(currentUserId).child("Version").child("version").setValue("1.0.2");



        postList = findViewById(R.id.all_user_post_list);
        postList.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);
        postList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {

                super.onScrollStateChanged(recyclerView, newState);
            }
        });




        DisplayAllUsersPosts();

        UpdateUserStatus("online");

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = Objects.requireNonNull(manager).getActiveNetworkInfo();

                if (null!=activeNetwork)
                {

                }else
                {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    builder.setTitle("No internet");
                    builder.setMessage("Please check your internet connection, mobile data or wifi");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            dialog.dismiss();

                        }
                    });

                    Dialog dialog = builder.create();
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);
                }

                DisplayAllUsersPosts();
                mToolBer.setVisibility(View.VISIBLE);
                constraintLayout.setVisibility(View.VISIBLE);


            }
        });

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(manager).getActiveNetworkInfo();

        if (null!=activeNetwork)
        {

        }else
        {

            final AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("No internet");
            builder.setMessage("Please check your internet connection, mobile data or wifi");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    dialog.dismiss();

                }
            });

            Dialog dialog = builder.create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);
        }



        //setSupportActionBar(mtoolber);
        //setTitle("ApproXu");

        MainLayout = findViewById(R.id.main_layout);

        navigationView = findViewById(R.id.main_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(HomeActivity.this,MainLayout,mToolBer,R.string.navigation_open,R.string.navigation_close);
        MainLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();



        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        navProfileName = navView.findViewById(R.id.nav_full_name);
        NavProfileImage = navView.findViewById(R.id.nav_profile_image);
        notificationBtn = findViewById(R.id.notification_Btn);
        profileIcon = findViewById(R.id.home_profile_icon);
        UpdateMessage = findViewById(R.id.version_update_message);
        HomeApproxText = findViewById(R.id.home_approx_text);







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
                messageIntent.putExtra("userId",currentUserId);
                startActivity(messageIntent);

            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileIntent = new Intent(HomeActivity.this,ProfileActivity.class);
                startActivity(profileIntent);
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
                            String fullName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            navProfileName.setText(fullName);
                        }
                        if (dataSnapshot.hasChild("profileImage")){
                            String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(NavProfileImage);
                            Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(profileIcon);

                        }else {
                            Toast.makeText(HomeActivity.this,"Profile image do not exists...", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(HomeActivity.this,"Profile name do not exists...", Toast.LENGTH_SHORT).show();
                    }

                    if (dataSnapshot.child("fullViewDisplay").getValue().equals("enable"))
                    {
                        HidenBtn.setVisibility(View.VISIBLE);
                        HidenBtn.setEnabled(true);
                        HidenBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mToolBer.setVisibility(View.GONE);
                                constraintLayout.setVisibility(View.GONE);

                            }
                        });
                    }else if (dataSnapshot.child("fullViewDisplay").getValue().equals("disable")){
                        HidenBtn.setVisibility(View.INVISIBLE);
                        HidenBtn.setEnabled(false);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        VersionReff.child("Version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                   final String versionMessage = dataSnapshot.child("version").getValue().toString();

                    userRef.child(currentUserId).child("Version").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.child("version").getValue().equals(versionMessage))
                            {
                                UpdateMessage.setVisibility(View.GONE);
                                HidenBtn.setVisibility(View.VISIBLE);
                                HomeApproxText.setVisibility(View.VISIBLE);
                                HomeApproxText.setEnabled(true);
                            }
                            else {

                                HidenBtn.setVisibility(View.GONE);
                                HomeApproxText.setEnabled(false);

                                UpdateMessage.setVisibility(View.VISIBLE);
                                UpdateMessage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent versionIntent = new Intent(HomeActivity.this,VersionUpdateActivity.class);
                                        startActivity(versionIntent);
                                    }
                                });
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

        HomeApproxText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this,SearchFriendsActivity.class);
                startActivity(intent);
            }
        });




        userRef.child(currentUserId).child("Notification").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
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
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    messageCount.setVisibility(View.VISIBLE);
                    messagecount = (int) dataSnapshot.getChildrenCount();
                    messageCount.setText(Integer.toString(messagecount));
                    homeMessage.setImageResource(R.drawable.message_bubble);
                    mediaPlayer2.start();
                } else {
                    messageCount.setText("");
                    messageCount.setVisibility(View.INVISIBLE);
                    homeMessage.setImageResource(R.drawable.message_bubble);
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        SaveCurrentDate =currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        SaveCurrentTime =currentTime.format(callForTime.getTime());


        Map CurrentStatemap = new HashMap();
        CurrentStatemap.put("time", SaveCurrentTime);
        CurrentStatemap.put("date", SaveCurrentDate);
        CurrentStatemap.put("type", state);

        userRef.child(currentUserId).child("userState")
                .updateChildren(CurrentStatemap);
    }

    @Override
    protected void onStart() {

        CheckForReceivingCall();

        super.onStart();
    }

    private void CheckForReceivingCall()
    {
        userRef.child(currentUserId).child("Ringing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild("ringing"))
                {
                    final String Uid = dataSnapshot.child("ringing").getValue().toString();

                    Intent intent = new Intent(HomeActivity.this,VideoCallActivity.class);
                    intent.putExtra("visit_user_id",Uid);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void DisplayAllUsersPosts() {
        Query query = PostsRef.orderByChild("counter");
        FirebaseRecyclerOptions<Posts> options=new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query,Posts.class).build();
        final FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PostsViewHolder holder, final int position, @NonNull final Posts model) {
                refreshLayout.isRefreshing();
                refreshLayout.setRefreshing(false);

                final String PostKey = getRef(position).getKey();
                final String Post_Key = getRef(position).getKey();
               // final String visit_user_id = userRef.child(currentUserId).getKey();
                //holder.time.setText(String.format("    %s", model.getTime()));


                PostsRef.child(Post_Key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                            final String type = dataSnapshot.child("type").getValue().toString();

                            if (type.equals("User"))
                            {
                                holder.PostCommentLayout.setVisibility(View.GONE);
                                holder.AllCommentReplyLayout.setVisibility(View.GONE);
                                PostsRef.child(Post_Key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {

                                        if (dataSnapshot.child("LastComments").exists())
                                        {
                                            holder.PostCommentLayout.setVisibility(View.VISIBLE);
                                            holder.AllCommentReplyLayout.setVisibility(View.GONE);
                                            final String commentId = (dataSnapshot.child("LastComments").child("commentUId").getValue()).toString();

                                            holder.PostCommentLayout.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    Intent commentsIntent = new Intent(HomeActivity.this,CommentsActivity.class);
                                                    commentsIntent.putExtra("PostKey",PostKey);
                                                    startActivity(commentsIntent);
                                                }
                                            });

                                            userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                {
                                                    if (dataSnapshot.exists())
                                                    {
                                                        String userImage  = dataSnapshot.child("profileImage").getValue().toString();

                                                        Picasso.get().load(userImage).into(holder.HomeCommentsUserProfileImage);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            PostsRef.child(Post_Key).child("Comments").child(commentId).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                {
                                                    if (dataSnapshot.exists())
                                                    {
                                                        final String comment = dataSnapshot.child("comment").getValue().toString();
                                                        String commentUserUid = dataSnapshot.child("uid").getValue().toString();

                                                        userRef.child(commentUserUid).addValueEventListener(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                            {
                                                                if (dataSnapshot.exists())
                                                                {
                                                                    String name = dataSnapshot.child("fullName").getValue().toString();
                                                                    String profileImages = dataSnapshot.child("profileImage").getValue().toString();
                                                                    holder.PostCommentText.setText(comment);
                                                                    holder.PostCommentUserName.setText(name);
                                                                    Picasso.get().load(profileImages).into(holder.PostCommentImageView);

                                                                    holder.AllCommentReplyLayout.setVisibility(View.GONE);

                                                                    PostsRef.child(Post_Key).child("Comments").child(commentId).addValueEventListener(new ValueEventListener() {
                                                                        @Override
                                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                                        {
                                                                            if (dataSnapshot.child("lastReply").exists())
                                                                            {
                                                                                holder.AllCommentReplyLayout.setVisibility(View.VISIBLE);
                                                                                final String commentReplyId = (dataSnapshot.child("lastReply").child("commentReplyUId").getValue()).toString();

                                                                                PostsRef.child(Post_Key).child("Comments").child(commentId).child("replies").child(commentReplyId).addValueEventListener(new ValueEventListener() {
                                                                                    @Override
                                                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                                                    {
                                                                                        if (dataSnapshot.exists())
                                                                                        {
                                                                                            final String commentReply = dataSnapshot.child("reply").getValue().toString();
                                                                                            String commentReplyUserUid = dataSnapshot.child("uid").getValue().toString();

                                                                                            userRef.child(commentReplyUserUid).addValueEventListener(new ValueEventListener() {
                                                                                                @Override
                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                                                                {
                                                                                                    if (dataSnapshot.exists())
                                                                                                    {
                                                                                                        String replyUserName = dataSnapshot.child("fullName").getValue().toString();
                                                                                                        String replyUserProfileImages = dataSnapshot.child("profileImage").getValue().toString();
                                                                                                        holder.PostCommentReplyText.setText(commentReply);
                                                                                                        holder.PostCommentReplyUserName.setText(replyUserName);
                                                                                                        Picasso.get().load(replyUserProfileImages).into(holder.PostCommentReplyImageView);
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
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            PostsRef.child(Post_Key).child("Comments").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                                {
                                                    if (dataSnapshot.exists())
                                                    {
                                                        holder.countComments = (int) dataSnapshot.getChildrenCount();
                                                    }
                                                    else
                                                    {
                                                        holder.countComments = 0;
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            holder.HomeCommentsImageBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view)
                                                {
                                                    if (holder.HomeCommentText.getText().toString().equals(""))
                                                    {
                                                        Toast.makeText(HomeActivity.this,"Please write a comments",Toast.LENGTH_SHORT).show();
                                                    }
                                                    else
                                                    {
                                                        String commentText = holder.HomeCommentText.getText().toString();
                                                        int countComment = holder.countComments;


                                                        Calendar calForDate = Calendar.getInstance();
                                                        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                                        final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                                        Calendar calForTime = Calendar.getInstance();
                                                        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                                                        final String saveCurrentTime = currentTime.format(calForTime.getTime());

                                                        final String RandomKey = currentUserId + saveCurrentDate + saveCurrentTime;
                                                        //postCommentButton.setEnabled(true);



                                                        HashMap commentsMap = new HashMap();
                                                        commentsMap.put("uid", currentUserId);
                                                        commentsMap.put("count", countComment);
                                                        commentsMap.put("comment", commentText);
                                                        commentsMap.put("date", saveCurrentDate);
                                                        commentsMap.put("time", saveCurrentTime);



                                                        PostsRef.child(Post_Key).child("Comments").child(RandomKey).updateChildren(commentsMap)
                                                                .addOnCompleteListener(new OnCompleteListener() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task task)
                                                                    {
                                                                        if (task.isSuccessful())
                                                                        {

                                                                            //Toast.makeText(CommentsActivity.this,"You have commented successfully...",Toast.LENGTH_SHORT).show();
                                                                            holder.HomeCommentText.setText("");


                                                                        }else
                                                                        {
                                                                            Toast.makeText(HomeActivity.this,"Error ocoured , Try again",Toast.LENGTH_SHORT).show();
                                                                        }

                                                                    }
                                                                });

                                                        PostsRef.child(Post_Key).child("LastComments").child("commentUId").setValue(RandomKey);
                                                    }
                                                }
                                            });



                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                holder.PostCommentLayout.setVisibility(View.GONE);
                                holder.AllCommentReplyLayout.setVisibility(View.GONE);

                                Calendar calForDate = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                String saveCurrentDate = currentDate.format(calForDate.getTime());

                                Calendar calForTime = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                                String saveCurrentTime = currentTime.format(calForTime.getTime());

                                if (model.getDate().equals(saveCurrentDate))
                                {
                                    holder.date.setText("Today at "+ model.getTime());

                                    if (model.getTime().equals(saveCurrentTime))
                                    {
                                        holder.date.setText("Just now");
                                    }
                                }
                                else
                                {
                                    holder.date.setText(model.getDate());
                                }
                                holder.AllPostLayout.setVisibility(View.VISIBLE);
                                holder.PagePostLayout.setVisibility(View.GONE);


                                holder.description.setText(model.getDescription());
                                holder.setStarButtonStatus(PostKey);
                                holder.setCommentsCount(PostKey);
                                userRef.child(Uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                       if (dataSnapshot.exists())
                                       {
                                           String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                           String name = dataSnapshot.child("fullName").getValue().toString();

                                           holder.username.setText(name);
                                           Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(holder.user_profile_image);
                                       }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.child("Friends").child(currentUserId).hasChild(Uid))
                                        {
                                            holder.CommentsPostBtn.setVisibility(View.VISIBLE);
                                            holder.CommentsPostBtn.setEnabled(true);
                                            holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    Intent commentsIntent = new Intent(HomeActivity.this,CommentsActivity.class);
                                                    commentsIntent.putExtra("PostKey",PostKey);
                                                    startActivity(commentsIntent);
                                                }
                                            });

                                        }else if (dataSnapshot.child("uid").getValue().equals(Uid)){

                                            holder.CommentsPostBtn.setVisibility(View.VISIBLE);
                                            holder.CommentsPostBtn.setEnabled(true);
                                            holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    Intent commentsIntent = new Intent(HomeActivity.this,CommentsActivity.class);
                                                    commentsIntent.putExtra("PostKey",PostKey);
                                                    startActivity(commentsIntent);
                                                }
                                            });

                                        }else {
                                            holder.AllPostLayout.setVisibility(View.GONE);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                //////// button clock

                                holder.user_profile_image.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {

                                        assert PostKey != null;
                                        PostsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.exists())
                                                {
                                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                                    final String visit_user_id = userRef.child(Uid).getKey();

                                                    if (Uid.equals(currentUserId))
                                                    {
                                                        Intent ProfileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                                                        startActivity(ProfileIntent);
                                                    }
                                                    else
                                                    {
                                                        Intent findProfileIntent = new Intent(HomeActivity.this, PersonProfileActivity.class);
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
                                holder.username.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        assert PostKey != null;
                                        PostsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.exists())
                                                {
                                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                                                    final String visit_user_id = userRef.child(Uid).getKey();

                                                    if (Uid.equals(currentUserId))
                                                    {
                                                        Intent ProfileIntent = new Intent(HomeActivity.this, ProfileActivity.class);
                                                        startActivity(ProfileIntent);
                                                    }
                                                    else
                                                    {
                                                        Intent findProfileIntent = new Intent(HomeActivity.this, PersonProfileActivity.class);
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


                                ///////

                                holder.DisplayNoOfStar.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View v)
                                    {

                                        assert PostKey != null;
                                        PostsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                            {
                                                if (dataSnapshot.exists())
                                                {
                                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();

                                                    if (Uid.equals(currentUserId))
                                                    {
                                                        Intent commentsIntent = new Intent(HomeActivity.this,StarFriendActivity.class);
                                                        commentsIntent.putExtra("PostKey",PostKey);
                                                        startActivity(commentsIntent);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });



                                    }
                                });




                                holder.description.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        holder.description.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent clickPostIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                                                clickPostIntent.putExtra("PostKey",PostKey);
                                                startActivity(clickPostIntent);
                                            }
                                        });
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
                                                    if (dataSnapshot.child(Objects.requireNonNull(Post_Key)).child("Star").child(Post_Key).hasChild(currentUserId))
                                                    {
                                                        StarRef.child(Post_Key).child("Star").child(Post_Key).child(currentUserId).child("condition").removeValue();
                                                        StarChecker = false;
                                                    }
                                                    else
                                                    {
                                                        StarRef.child(Post_Key).child("Star").child(Post_Key).child(currentUserId).child("condition").setValue(true);
                                                        StarChecker = false;
                                                        mediaPlayer.start();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                });

                                ///////
                            }else  if (type.equals("Page"))
                            {
                                holder.PostCommentLayout.setVisibility(View.GONE);

                                Calendar calForDate = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                String saveCurrentDate = currentDate.format(calForDate.getTime());

                                Calendar calForTime = Calendar.getInstance();
                                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                                String saveCurrentTime = currentTime.format(calForTime.getTime());

                                if (model.getDate().equals(saveCurrentDate))
                                {
                                    holder.date2.setText("Today at "+ model.getTime());

                                    if (model.getTime().equals(saveCurrentTime))
                                    {
                                        holder.date2.setText("Just now");
                                    }
                                }
                                else
                                {
                                    holder.date2.setText(model.getDate());
                                }

                                holder.PagePostLayout.setVisibility(View.VISIBLE);
                                holder.AllPostLayout.setVisibility(View.GONE);
                                holder.description2.setText(model.getDescription());
                                holder.setStarButtonStatus2(PostKey);
                                holder.setCommentsCount2(PostKey);
                                PageReff.child(Uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.exists())
                                        {
                                            String profileImages = dataSnapshot.child("pageProfileImage").getValue().toString();
                                            String names = dataSnapshot.child("pageName").getValue().toString();

                                            holder.username2.setText(names);
                                            Picasso.get().load(profileImages).placeholder(R.drawable.profile_icon).into(holder.user_profile_image2);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostsRef.child(Post_Key).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.exists())
                                        {
                                            final String image1 = dataSnapshot.child("Image1").getValue().toString();
                                            final String image2 = dataSnapshot.child("Image2").getValue().toString();
                                            final String image3 = dataSnapshot.child("Image3").getValue().toString();
                                            final String image4 = dataSnapshot.child("Image4").getValue().toString();

                                            if (image1.equals("None"))
                                            {
                                                holder.Image1.setVisibility(View.GONE);
                                            }
                                            else {
                                                holder.Image1.setVisibility(View.VISIBLE);
                                                Picasso.get().load(image1).placeholder(R.drawable.blank_cover_image).into(holder.Image1);
                                                holder.Image1.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        Intent commentsIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                                                        commentsIntent.putExtra("PostKey",PostKey);
                                                        startActivity(commentsIntent);
                                                    }
                                                });
                                            }
                                            if (image2.equals("None"))
                                            {
                                                holder.Image2.setVisibility(View.GONE);
                                            }
                                            else {
                                                holder.Image2.setVisibility(View.VISIBLE);
                                                Picasso.get().load(image2).placeholder(R.drawable.blank_cover_image).into(holder.Image2);
                                                holder.Image2.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        Intent commentsIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                                                        commentsIntent.putExtra("PostKey",PostKey);
                                                        startActivity(commentsIntent);
                                                    }
                                                });
                                            }
                                            if (image3.equals("None"))
                                            {
                                                holder.Image3.setVisibility(View.GONE);
                                            }
                                            else {
                                                holder.Image3.setVisibility(View.VISIBLE);
                                                Picasso.get().load(image3).placeholder(R.drawable.blank_cover_image).into(holder.Image3);
                                                holder.Image3.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        Intent commentsIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                                                        commentsIntent.putExtra("PostKey",PostKey);
                                                        startActivity(commentsIntent);
                                                    }
                                                });
                                            }
                                            if (image4.equals("None"))
                                            {
                                                holder.Image4.setVisibility(View.GONE);
                                            }
                                            else {
                                                holder.Image4.setVisibility(View.VISIBLE);
                                                Picasso.get().load(image4).placeholder(R.drawable.blank_cover_image).into(holder.Image4);

                                                holder.Image4.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        Intent commentsIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                                                        commentsIntent.putExtra("PostKey",PostKey);
                                                        startActivity(commentsIntent);
                                                    }
                                                });
                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                holder.CommentsPostBtn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Intent commentsIntent = new Intent(HomeActivity.this,CommentsActivity.class);
                                        commentsIntent.putExtra("PostKey",PostKey);
                                        startActivity(commentsIntent);
                                    }
                                });

                                PageReff.child(Uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                       if (dataSnapshot.child("AdminUid").getValue().toString().equals(currentUserId))
                                       {
                                           holder.user_profile_image2.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   Intent findPageIntent = new Intent(HomeActivity.this, PageHomeActivity.class);
                                                   findPageIntent.putExtra("visit_user_id", Uid);
                                                   startActivity(findPageIntent);
                                               }
                                           });

                                           holder.username2.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   Intent findPageIntent = new Intent(HomeActivity.this, PageHomeActivity.class);
                                                   findPageIntent.putExtra("visit_user_id", Uid);
                                                   startActivity(findPageIntent);
                                               }
                                           });
                                       }
                                       else
                                       {
                                           holder.user_profile_image2.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   Intent findPageIntent = new Intent(HomeActivity.this, UserPageHomeActivity.class);
                                                   findPageIntent.putExtra("visit_user_id", Uid);
                                                   startActivity(findPageIntent);
                                               }
                                           });

                                           holder.username2.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View view) {
                                                   Intent findPageIntent = new Intent(HomeActivity.this, UserPageHomeActivity.class);
                                                   findPageIntent.putExtra("visit_user_id", Uid);
                                                   startActivity(findPageIntent);
                                               }
                                           });
                                       }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }


                            holder.DisplayNoOfStar2.setOnClickListener(new View.OnClickListener()
                            {
                                @Override
                                public void onClick(View v)
                                {

                                    assert PostKey != null;
                                    PostsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                        {
                                            if (dataSnapshot.exists())
                                            {
                                                String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();

                                                if (Uid.equals(currentUserId))
                                                {
                                                    Intent commentsIntent = new Intent(HomeActivity.this,StarFriendActivity.class);
                                                    commentsIntent.putExtra("PostKey",PostKey);
                                                    startActivity(commentsIntent);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                }
                            });




                            holder.description2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.description2.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent clickPostIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
                                            clickPostIntent.putExtra("PostKey",PostKey);
                                            startActivity(clickPostIntent);
                                        }
                                    });
                                }
                            });



                            holder.StarPostBtn2.setOnClickListener(new View.OnClickListener() {
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
                                                if (dataSnapshot.child(Objects.requireNonNull(Post_Key)).child("Star").child(Post_Key).hasChild(currentUserId))
                                                {
                                                    StarRef.child(Post_Key).child("Star").child(Post_Key).child(currentUserId).child("condition").removeValue();
                                                    StarChecker = false;
                                                }
                                                else
                                                {
                                                    StarRef.child(Post_Key).child("Star").child(Post_Key).child(currentUserId).child("condition").setValue(true);
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

                        }else {

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                //////////////////////



                ////////////////////////




            }

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout,parent,false);
                PostsViewHolder viewHolder;
                viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };
        postList.setAdapter(firebaseRecyclerAdapter);


        firebaseRecyclerAdapter.startListening();

    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView StarPostBtn, CommentsPostBtn,StarPostBtn2, CommentsPostBtn2, Image1,Image2,Image3,Image4,HomeCommentsImageBtn;
        TextView DisplayNoOfStar;
        TextView commentsCount;
        TextView DisplayNoOfStar2;
        TextView commentsCount2;
        EditText HomeCommentText;
        int countStar = 0;
        int CommentsCount = 0 ;
        int countComments;
        String currentUserId;
        private AdView adView;
        DatabaseReference StarReff,CommentReff;
        TextView username,date,time,description,username2,date2,time2,description2, PostCommentUserName,PostCommentReplyUserName;
        private TextView PostCommentText,PostCommentReplyText;
        CircleImageView user_profile_image,user_profile_image2, PostCommentImageView,PostCommentReplyImageView, HomeCommentsUserProfileImage;
        LinearLayout AllPostLayout,PagePostLayout, AllCommentReplyLayout;
        RelativeLayout PostCommentLayout;

        PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username=itemView.findViewById(R.id.post_user_name);
            date=itemView.findViewById(R.id.post_date);
            time=itemView.findViewById(R.id.post_time);
            description=itemView.findViewById(R.id.post_description);
            user_profile_image =itemView.findViewById(R.id.post_profile_image);

            username2=itemView.findViewById(R.id.post_user_name2);
            date2=itemView.findViewById(R.id.post_date2);
            time2=itemView.findViewById(R.id.post_time2);
            description2=itemView.findViewById(R.id.post_description2);
            user_profile_image2 =itemView.findViewById(R.id.post_profile_image2);

            StarPostBtn = itemView.findViewById(R.id.display_star_btn);
            CommentsPostBtn = itemView.findViewById(R.id.comment_button);
            commentsCount = itemView.findViewById(R.id.comments_count_btn);
            DisplayNoOfStar = itemView.findViewById(R.id.display_no_of_star);
            AllPostLayout = itemView.findViewById(R.id.all_post_linear_layout);

            StarPostBtn2 = itemView.findViewById(R.id.display_star_btn2);
            CommentsPostBtn2 = itemView.findViewById(R.id.comment_button2);
            commentsCount2 = itemView.findViewById(R.id.comments_count_btn2);
            DisplayNoOfStar2 = itemView.findViewById(R.id.display_no_of_star2);
            PagePostLayout = itemView.findViewById(R.id.all_post_linear_layout2);

            Image1 = itemView.findViewById(R.id.post_image1);
            Image2 = itemView.findViewById(R.id.post_image2);
            Image3 = itemView.findViewById(R.id.post_image3);
            Image4 = itemView.findViewById(R.id.post_image4);

            PostCommentLayout = itemView.findViewById(R.id.post_comment_layout);
            PostCommentText = itemView.findViewById(R.id.home_comment_descriptions);
            PostCommentImageView = itemView.findViewById(R.id.comment_profile_image);
            PostCommentUserName = itemView.findViewById(R.id.comment_user_name);
            AllCommentReplyLayout = itemView.findViewById(R.id.all_comments_reply_linear_layout);
            PostCommentReplyText = itemView.findViewById(R.id.home_comment_reply_descriptions);
            PostCommentReplyImageView = itemView.findViewById(R.id.comment_reply_profile_image);
            PostCommentReplyUserName = itemView.findViewById(R.id.comment_reply_user_name);


            HomeCommentsUserProfileImage = itemView.findViewById(R.id.home_comments_user_profile_image);

            HomeCommentText = itemView.findViewById(R.id.home_comment_text);
            HomeCommentsImageBtn = itemView.findViewById(R.id.home_comment_btn);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Post");
            CommentReff = FirebaseDatabase.getInstance().getReference().child("Post");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();



        }

        void setStarButtonStatus(final String PostKey)
        {
            StarReff.child(PostKey).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child("Star").child(PostKey).hasChild(currentUserId))
                    {
                        countStar = (int) dataSnapshot.child("Star").child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        Number number = countStar;
                        DisplayNoOfStar.setText(prettyCount(number) +(" Star"));

                    }else {
                        countStar = (int) dataSnapshot.child("Star").child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        Number number = countStar;
                        DisplayNoOfStar.setText(prettyCount(number) +(" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        void setCommentsCount(final String PostKey)
        {
            CommentReff.child(PostKey).child("Comments").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        commentsCount.setVisibility(View.VISIBLE);
                        CommentsCount = (int) dataSnapshot.getChildrenCount();

                        Number number = CommentsCount;

                        commentsCount.setText(prettyCount(number));


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



        void setStarButtonStatus2(final String PostKey)
        {
            StarReff.child(PostKey).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child("Star").child(PostKey).hasChild(currentUserId))
                    {
                        countStar = (int) dataSnapshot.child("Star").child(PostKey).getChildrenCount();
                        StarPostBtn2.setImageResource(R.drawable.full_gold_star);
                        Number number = countStar;
                        DisplayNoOfStar2.setText(prettyCount(number)+(" Star"));
                    }else {
                        countStar = (int) dataSnapshot.child("Star").child(PostKey).getChildrenCount();
                        StarPostBtn2.setImageResource(R.drawable.gold_star);
                        Number number = countStar;
                        DisplayNoOfStar2.setText(prettyCount(number)+(" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        void setCommentsCount2(final String PostKey)
        {
            CommentReff.child(PostKey).child("Comments").addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        commentsCount2.setVisibility(View.VISIBLE);
                        CommentsCount = (int) dataSnapshot.getChildrenCount();
                        Number number = CommentsCount;
                        commentsCount2.setText(prettyCount(number));
                    } else {
                        commentsCount2.setText("");
                        commentsCount2.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){

                }

            });

        }

        public String prettyCount(Number number) {
            char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
            long numValue = number.longValue();
            int value = (int) Math.floor(Math.log10(numValue));
            int base = value / 3;
            if (value >= 3 && base < suffix.length) {
                return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
            } else {
                return new DecimalFormat("#,##0").format(numValue);
            }
        }



    }





    @Override
    protected void onDestroy() {
        UpdateUserStatus("offline");
        super.onDestroy();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        UpdateUserStatus("offline");
    }

    public void homeBtn(View view) {
        if (view == findViewById(R.id.home_home_icon)){

            startActivity(new Intent(HomeActivity.this, HomeActivity.class));
            Animatoo.animateFade(HomeActivity.this);
        }
        if (view == findViewById(R.id.home_add_post_icon)){

            startActivity(new Intent(HomeActivity.this, AddPostActivity.class));

        }
        if (view == findViewById(R.id.home_friends_icon)){

            startActivity(new Intent(HomeActivity.this, FindFriendsActivity.class));

        }
        if (view == findViewById(R.id.notification_Btn)){

            Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
            intent.putExtra("type","user");
            intent.putExtra("userId",currentUserId);
            startActivity(intent);


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

            case R.id.menu_Page:
                // Toast.makeText(this,"Find Friend",Toast.LENGTH_SHORT).show();
                SendToPageActivity();
                break;

            case R.id.menu_settings:
                SendToSettingsActivity();
                break;

            case R.id.menu_version_update:
                SendToVersionUpdateActivity();
                break;

            case R.id.menu_report_problem:
                SendToReportProblemActivity();
                break;

            case R.id.menu_sign_out:
                UpdateUserStatus("offline");
                UserToLoginActivity();
                mAuth.signOut();
                break;
        }

    }

    private void SendToPageActivity() {
        Intent pageIntent = new Intent(HomeActivity.this,PageActivity.class);
        startActivity(pageIntent);
    }

    private void SendToReportProblemActivity() {
        Intent reportIntent = new Intent(HomeActivity.this,ReportProblemActivity.class);
        startActivity(reportIntent);

    }

    private void SendToVersionUpdateActivity() {
        Intent versionIntent = new Intent(HomeActivity.this,VersionUpdateActivity.class);
        startActivity(versionIntent);
    }

    private void SendToUniversityActivity() {
        Intent universityIntent = new Intent(HomeActivity.this,UniversityActivity.class);
        startActivity(universityIntent);
        Animatoo.animateSlideLeft(this);
    }

    private void SendToNotificationActivity() {
        Intent intent = new Intent(HomeActivity.this, NotificationActivity.class);
        intent.putExtra("type","user");
        intent.putExtra("userId",currentUserId);
        startActivity(intent);
    }

    private void SendToMessageActivity() {
        Intent messageIntent = new Intent(HomeActivity.this,MessageActivity.class);
        startActivity(messageIntent);
        Animatoo.animateSlideLeft(this);
    }

    private void SendToFriendsActivity() {
        startActivity(new Intent(HomeActivity.this, FindFriendsActivity.class));
        Animatoo.animateSlideLeft(this);
    }

    private void SendToFindFriendsActivity() {
        startActivity(new Intent(HomeActivity.this, SearchFriendsActivity.class));
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
        finish();
    }


}

