package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserPageHomeActivity extends AppCompatActivity {


    Toolbar mToolBar;
    TextView pageName, pageUserName, appBarUsername, pageType, pageNumber, pageWebsiteLink, pageLocation, PageStarCount, PageVisitorCount;
    String PageAddress;
    FirebaseAuth mAuth;
    Button pageHomeAddBtn, pageHomeMessageBtn, pageHomeCallBtn, pageHomeWebsiteVisitBtn, InviteFriends;
    DatabaseReference PageReference;
    String currentUserId ;
    DatabaseReference UserReff, PageReff,PostsReff,StarRef,FriendReff;
    StorageReference thumbImageReff;
    CircleImageView pageProfileImage,pageProfileImage2;
    ImageView pageCoverImage, PageStarIcons;
    Boolean StarChecker = false;
    private int count = 0;
    RecyclerView PageAllPostList, PageInvitePageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page_home);


        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        PageAddress =  getIntent().getExtras().get("visit_user_id").toString();
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages").child(PageAddress);
        PageReff = FirebaseDatabase.getInstance().getReference().child("All Pages");
        PostsReff = FirebaseDatabase.getInstance().getReference().child("Post");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Post");
        FriendReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Friends").child(currentUserId);
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        thumbImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolBar = findViewById(R.id.user_page_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        pageName = findViewById(R.id.user_page_name);
        pageUserName = findViewById(R.id.user_page_username);
        pageProfileImage = findViewById(R.id.user_page_profile_image);
        pageCoverImage = findViewById(R.id.user_page_cover_image);
        pageType = findViewById(R.id.user_page_type);
        pageWebsiteLink = findViewById(R.id.user_page_website_link);
        pageNumber = findViewById(R.id.user_page_number);
        pageLocation = findViewById(R.id.user_page_location);
        appBarUsername = findViewById(R.id.user_page_user_name);
        PageStarIcons = findViewById(R.id.user_page_star_icon);
        pageHomeAddBtn = findViewById(R.id.user_page_add_btn);
        pageHomeMessageBtn = findViewById(R.id.user_page_add_message_btn);
        pageHomeCallBtn = findViewById(R.id.user_page_add_call_btn);
        pageHomeWebsiteVisitBtn = findViewById(R.id.user_page_add_webLink_btn);
        PageStarCount = findViewById(R.id.user_page_star_count);
        PageVisitorCount = findViewById(R.id.user_page_visitor_count);
        InviteFriends = findViewById(R.id.user_invite_btn);


        PageAllPostList = findViewById(R.id.user_id_page_all_post);
        PageAllPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        PageAllPostList.setLayoutManager(linearLayoutManager);

        PageInvitePageList = findViewById(R.id.user_invite_friends_list);
        PageInvitePageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        PageInvitePageList.setLayoutManager(linearLayoutManager2);

        PageReference.child("PageVisitor").child(currentUserId).child("condition").setValue(true);

        PageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String PageName = dataSnapshot.child("pageName").getValue().toString();
                    String PageUserName = dataSnapshot.child("pageUserName").getValue().toString();
                    String PageType = dataSnapshot.child("pageType").getValue().toString();
                    String PagePhone = dataSnapshot.child("phoneNumber").getValue().toString();
                    final String PageWebLink = dataSnapshot.child("websiteLink").getValue().toString();
                    String PageLocation = dataSnapshot.child("address").getValue().toString();
                    String PageProfileImage = dataSnapshot.child("pageProfileImage").getValue().toString();
                    String PageCoverImage = dataSnapshot.child("pageCoverImage").getValue().toString();

                    pageName.setText(PageName);
                    pageUserName.setText(PageUserName);
                    pageType.setText(PageType);
                    pageLocation.setText(PageLocation);
                    appBarUsername.setText(PageName);
                    Picasso.get().load(PageProfileImage).placeholder(R.drawable.profile_icon).into(pageProfileImage);
                    Picasso.get().load(PageCoverImage).placeholder(R.drawable.blank_cover_image).into(pageCoverImage);

                    if (pageNumber.getText().toString().equals(PagePhone))
                    {
                        pageNumber.setVisibility(View.GONE);
                    }
                    if (pageWebsiteLink.getText().toString().equals(PageWebLink))
                    {
                        pageWebsiteLink.setVisibility(View.GONE);
                    }
                    else
                    {
                        pageNumber.setText(PagePhone);
                        pageWebsiteLink.setText(PageWebLink);
                    }

                    pageWebsiteLink.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(PageWebLink));
                            startActivity(intent);
                        }
                    });

                    pageHomeWebsiteVisitBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(PageWebLink));
                            startActivity(intent);
                        }
                    });



                }

                if (dataSnapshot.child("PageStarUser").hasChild(currentUserId))
                {

                    PageStarIcons.setImageResource(R.drawable.full_gold_star);

                }else {
                    PageStarIcons.setImageResource(R.drawable.gold_star);

                }


                if (dataSnapshot.hasChild("PageButton"))
                {
                    String btnType = dataSnapshot.child("PageButton").getValue().toString();

                    if (btnType.equals("MessageBtn"))
                    {
                        pageHomeMessageBtn.setVisibility(View.VISIBLE);
                        pageHomeAddBtn.setVisibility(View.GONE);
                        pageHomeCallBtn.setVisibility(View.GONE);
                        pageHomeWebsiteVisitBtn.setVisibility(View.GONE);

                        pageHomeMessageBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(UserPageHomeActivity.this, PageChatActivity.class);
                                chatIntent.putExtra("visit_user_id", PageAddress);
                                chatIntent.putExtra("userId",currentUserId);
                                chatIntent.putExtra("type","page");
                                chatIntent.putExtra("from","user");
                                startActivity(chatIntent);
                            }
                        });
                    }else
                    if (btnType.equals("CallBtn"))
                    {
                        pageHomeCallBtn.setVisibility(View.VISIBLE);
                        pageHomeMessageBtn.setVisibility(View.GONE);
                        pageHomeAddBtn.setVisibility(View.GONE);
                        pageHomeWebsiteVisitBtn.setVisibility(View.GONE);

                    }else
                    if (btnType.equals("websiteLink"))
                    {
                        pageHomeWebsiteVisitBtn.setVisibility(View.VISIBLE);
                        pageHomeMessageBtn.setVisibility(View.GONE);
                        pageHomeAddBtn.setVisibility(View.GONE);
                        pageHomeCallBtn.setVisibility(View.GONE);
                    }
                    else
                    {
                        pageHomeAddBtn.setVisibility(View.VISIBLE);
                        pageHomeMessageBtn.setVisibility(View.GONE);
                        pageHomeCallBtn.setVisibility(View.GONE);
                        pageHomeWebsiteVisitBtn.setVisibility(View.GONE);
                    }


                }

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        InviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserPageHomeActivity.this,FriendInvitePageActivity.class);
                intent.putExtra("PageId",PageAddress);
                startActivity(intent);
            }
        });

        PageReference.child("PageStarUser").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    count = (int) dataSnapshot.getChildrenCount();
                    PageStarCount.setText(Integer.toString(count) + " people star this page");
                } else {
                    PageStarCount.setText("Nobody star this page");

                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        });

        PageReference.child("PageVisitor").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    count = (int) dataSnapshot.getChildrenCount();
                    PageVisitorCount.setText(Integer.toString(count) + " people visit this page");
                } else {
                    PageVisitorCount.setText("Nobody visit this page");

                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        });





        PageStarIcons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                StarChecker = true;

                PageReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (StarChecker.equals(true))
                        {
                            if (dataSnapshot.child("PageStarUser").hasChild(currentUserId))
                            {
                                PageReference.child("PageStarUser").child(currentUserId).child("condition").removeValue();
                                UserReff.child(currentUserId).child("StarPage").child(PageAddress).child("condition").removeValue();
                                PageReference.child("Notifications").child(currentUserId).removeValue();
                                StarChecker = false;
                            }
                            else
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
                                notificationMap.put("type","user");
                                notificationMap.put("condition","false");
                                notificationMap.put("text","Invite a new page");

                                PageReference.child("PageStarUser").child(currentUserId).child("condition").setValue(true);
                                UserReff.child(currentUserId).child("StarPage").child(PageAddress).child("condition").setValue(true);
                                PageReference.child("Notifications").child(currentUserId).updateChildren(notificationMap);
                                UserReff.child(currentUserId).child("Invitation").child(PageAddress).removeValue();
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



        DisplayInviteFriends();

        DisplyAllPagePost();
    }


    private void DisplayInviteFriends() {
        Query query = FriendReff.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, UserPageHomeActivity.FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UserPageHomeActivity.FriendsViewHolder friendsViewHolder, final int position, @NonNull Friends friends) {


                final String usersIDs = getRef(position).getKey();

                UserReff.child(Objects.requireNonNull(usersIDs)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // final String userName = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                            final String profileimage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                            friendsViewHolder.setProfileImage(profileimage);


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.circle_layout, parent ,false);
                return new FriendsViewHolder(view);
            }
        };
        adapter.startListening();
        PageInvitePageList.setAdapter(adapter);
    }

    public class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setProfileImage(String profileimage) {
            CircleImageView image = mView.findViewById(R.id.friends_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }
    }

    private void DisplyAllPagePost() {
        Query query = PostsReff.orderByChild("uid")
                .startAt(PageAddress).endAt(PageAddress + " \uf8ff");

        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(query, Posts.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, UserPageHomeActivity.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final UserPageHomeActivity.PostsViewHolder holder, final int position, @NonNull final Posts model) {



                final String PostKey = getRef(position).getKey();
                final String Post_Key = getRef(position).getKey();
                // final String visit_user_id = userRef.child(currentUserId).getKey();
                //holder.time.setText(String.format("    %s", model.getTime()));
                holder.date.setText(model.getDate());
                holder.description.setText(model.getDescription());



                holder.setStarButtonStatus(PostKey);
                holder.setCommentsCount(PostKey);


                holder.AllPostLayout.setVisibility(View.GONE);
                holder.PageLayout.setVisibility(View.VISIBLE);

                PostsReff.child(Post_Key).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                            final String type = dataSnapshot.child("type").getValue().toString();

                            if (type.equals("Page"))
                            {
                                PageReff.child(Uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.exists())
                                        {
                                            String profileImages = dataSnapshot.child("pageProfileImage").getValue().toString();
                                            String names = dataSnapshot.child("pageName").getValue().toString();

                                            holder.username.setText(names);
                                            Picasso.get().load(profileImages).placeholder(R.drawable.profile_icon).into(holder.user_profile_image);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                PostsReff.child(Post_Key).addValueEventListener(new ValueEventListener() {
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
                                                        Intent commentsIntent = new Intent(UserPageHomeActivity.this,ClickPostActivity.class);
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
                                                        Intent commentsIntent = new Intent(UserPageHomeActivity.this,ClickPostActivity.class);
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
                                                        Intent commentsIntent = new Intent(UserPageHomeActivity.this,ClickPostActivity.class);
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
                                                        Intent commentsIntent = new Intent(UserPageHomeActivity.this,ClickPostActivity.class);
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

                                holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Intent commentsIntent = new Intent(UserPageHomeActivity.this,CommentsActivity.class);
                                        commentsIntent.putExtra("PostKey",PostKey);
                                        startActivity(commentsIntent);
                                    }
                                });



                                ///////
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.DisplayNoOfStar.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        assert PostKey != null;
                        PostsReff.child(PostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();

                                    if (Uid.equals(currentUserId))
                                    {
                                        Intent commentsIntent = new Intent(UserPageHomeActivity.this,StarFriendActivity.class);
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
                                Intent clickPostIntent = new Intent(UserPageHomeActivity.this,ClickPostActivity.class);
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

            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout, parent ,false);
                return new PostsViewHolder(view);
            }
        };
        adapter.startListening();
        PageAllPostList.setAdapter(adapter);
    }


    public static class PostsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView StarPostBtn, CommentsPostBtn, Image1,Image2,Image3,Image4;
        TextView DisplayNoOfStar, commentsCount;
        int countStar = 0;
        int CommentsCount = 0 ;
        String currentUserId;
        DatabaseReference StarReff,CommentReff;
        TextView username,date,time,description;
        CircleImageView user_profile_image;
        LinearLayout AllPostLayout,PageLayout;

        PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username=itemView.findViewById(R.id.post_user_name2);
            date=itemView.findViewById(R.id.post_date2);
            time=itemView.findViewById(R.id.post_time2);
            description=itemView.findViewById(R.id.post_description2);
            user_profile_image =itemView.findViewById(R.id.post_profile_image2);

            StarPostBtn = itemView.findViewById(R.id.display_star_btn2);
            CommentsPostBtn = itemView.findViewById(R.id.comment_button2);
            commentsCount = itemView.findViewById(R.id.comments_count_btn2);
            DisplayNoOfStar = itemView.findViewById(R.id.display_no_of_star2);
            AllPostLayout = itemView.findViewById(R.id.all_post_linear_layout);
            PageLayout = itemView.findViewById(R.id.all_post_linear_layout2);
            Image1 = itemView.findViewById(R.id.post_image1);
            Image2 = itemView.findViewById(R.id.post_image2);
            Image3 = itemView.findViewById(R.id.post_image3);
            Image4 = itemView.findViewById(R.id.post_image4);

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
                        DisplayNoOfStar.setText(countStar +(" Star"));
                    }else {
                        countStar = (int) dataSnapshot.child("Star").child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        DisplayNoOfStar.setText(countStar +(" Star"));
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

}
