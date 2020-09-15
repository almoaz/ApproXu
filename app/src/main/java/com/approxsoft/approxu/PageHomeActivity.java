package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class PageHomeActivity extends AppCompatActivity {

    Toolbar mToolBar;
    TextView pageName, pageUserName, appBarUsername, pageType, pageNumber, pageWebsiteLink, pageLocation, HideText, AddPagePost,PageMessageCount, PageStarCount, PageVisitorCount;
    String PageAddress;
    CircleImageView PageHomeAddBtn;
    FirebaseAuth mAuth;
    Button pageHomeAddBtn, pageHomeMessageBtn, pageHomeCallBtn, pageHomeWebsiteVisitBtn, InviteFriendsBtn;
    DatabaseReference PageReference;
    ImageView PageProfileImageBtn, PageCoverImageBtn, PageHomeAddPhoto, PageMessageIcon , NotificationIcon;
    public static final int Gallery_pick = 1;
    public static final int Gallery_Pick = 1;
    private ProgressDialog loadingBar;
    Bitmap thumb_bitmap = null;
    String currentUserId ;
    DatabaseReference UserReff, PageReff,PostsReff,StarRef,FriendReff;
    StorageReference thumbImageReff;
    CircleImageView pageProfileImage,pageProfileImage2;
    ImageView pageCoverImage;
    private int messagecount = 0;
    private int count = 0;
    Boolean StarChecker = false;
    LinearLayout AddPagePostLayout, AddPagePhotoLayout, PageEditLayout;
    RecyclerView PageAllPostList, PageInvitePageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_home);

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

        mToolBar = findViewById(R.id.page_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");

        pageName = findViewById(R.id.page_name);
        pageUserName = findViewById(R.id.page_username);
        pageProfileImage = findViewById(R.id.page_profile_image);
        pageCoverImage = findViewById(R.id.page_cover_image);
        pageType = findViewById(R.id.page_type);
        pageWebsiteLink = findViewById(R.id.page_website_link);
        pageNumber = findViewById(R.id.page_number);
        pageLocation = findViewById(R.id.page_location);
        appBarUsername = findViewById(R.id.page_user_name);
        PageProfileImageBtn = findViewById(R.id.page_profile_image_change);
        PageCoverImageBtn = findViewById(R.id.page_cover_Image_change);
        pageProfileImage2 = findViewById(R.id.page_profile_Image2);
        AddPagePost = findViewById(R.id.add_page_post);
        AddPagePostLayout = findViewById(R.id.add_post_layout);
        AddPagePhotoLayout = findViewById(R.id.add_photo_layout);
        PageHomeAddPhoto = findViewById(R.id.page_home_add_photo);
        PageEditLayout = findViewById(R.id.page_edit_layout);
        pageHomeAddBtn = findViewById(R.id.page_add_btn);
        pageHomeMessageBtn = findViewById(R.id.page_add_message_btn);
        pageHomeCallBtn = findViewById(R.id.page_add_call_btn);
        pageHomeWebsiteVisitBtn = findViewById(R.id.page_add_webLink_btn);
        PageMessageIcon = findViewById(R.id.page_message_icon);
        PageMessageCount = findViewById(R.id.page_message_count);
        PageStarCount = findViewById(R.id.page_star_count);
        PageVisitorCount = findViewById(R.id.page_visitor_count);
        loadingBar = new ProgressDialog(this);
        HideText = findViewById(R.id.hide_text);
        HideText.setText("");
        InviteFriendsBtn = findViewById(R.id.invite_friendsBtn);
        NotificationIcon = findViewById(R.id.page_notification_icon);


        PageAllPostList = findViewById(R.id.id_page_all_post);
        PageAllPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        PageAllPostList.setLayoutManager(linearLayoutManager);

        PageInvitePageList = findViewById(R.id.invite_friends_list);
        PageInvitePageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        PageInvitePageList.setLayoutManager(linearLayoutManager2);



        DisplayInviteFriends();

        DisplyAllPagePost();


        PageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String PageName = dataSnapshot.child("pageName").getValue().toString();
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
                    Picasso.get().load(PageProfileImage).placeholder(R.drawable.profile_icon).into(pageProfileImage2);
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

        PageReference.child("MessageNotification").child(PageAddress).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    PageMessageCount.setVisibility(View.VISIBLE);
                    messagecount = (int) dataSnapshot.getChildrenCount();
                    PageMessageCount.setText(Integer.toString(messagecount));
                } else {
                    PageMessageCount.setText("");
                    PageMessageCount.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError databaseError){

            }

        });

        InviteFriendsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PageHomeActivity.this,FriendInvitePageActivity.class);
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


        NotificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PageHomeActivity.this,NotificationActivity.class);
                intent.putExtra("type","page");
                intent.putExtra("userId",PageAddress);
                startActivity(intent);
            }
        });



        pageHomeAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SetupBtn();
            }
        });

        pageHomeMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SetupBtn();
            }
        });

        pageHomeCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SetupBtn();
            }
        });

        pageHomeWebsiteVisitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SetupBtn();
            }
        });



        PageProfileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);
                HideText.setText("ProfilePicture");

            }
        });

        PageCoverImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_Pick);
                HideText.setText("CoverPicture");

            }
        });

        AddPagePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                intent.putExtra("PageUid",PageAddress);
                intent.putExtra("imageName","null");
                startActivity(intent);
            }
        });

        AddPagePostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                intent.putExtra("PageUid",PageAddress);
                intent.putExtra("imageName","null");
                startActivity(intent);
            }
        });

        PageMessageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(PageHomeActivity.this,PageMessageActivity.class);
                messageIntent.putExtra("userId",PageAddress);
                messageIntent.putExtra("type","page");
                startActivity(messageIntent);
            }
        });

        AddPagePhotoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CharSequence[] options = new CharSequence[]
                        {
                                "Select Image 1",
                                "Select Image 2",
                                "Select Image 3",
                                "Select Image 4",
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(PageHomeActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0)
                        {
                            String image1 = "Image1";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image1);
                            startActivity(addPostIntent);
                        }
                        if (which == 1)
                        {
                            String image2 = "Image2";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image2);
                            startActivity(addPostIntent);
                        }

                        if (which == 2)
                        {
                            String image3 = "Image3";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image3);
                            startActivity(addPostIntent);
                        }
                        if (which == 3)
                        {
                            String image4 = "Image4";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image4);
                            startActivity(addPostIntent);
                        }
                    }
                });
                builder.show();
            }
        });

        PageHomeAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CharSequence[] options = new CharSequence[]
                        {
                                "Select Image 1",
                                "Select Image 2",
                                "Select Image 3",
                                "Select Image 4",
                        };

                AlertDialog.Builder builder = new AlertDialog.Builder(PageHomeActivity.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        if (which == 0)
                        {
                            String image1 = "Image1";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image1);
                            startActivity(addPostIntent);
                        }
                        if (which == 1)
                        {
                            String image2 = "Image2";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image2);
                            startActivity(addPostIntent);
                        }

                        if (which == 2)
                        {
                            String image3 = "Image3";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image3);
                            startActivity(addPostIntent);
                        }
                        if (which == 3)
                        {
                            String image4 = "Image4";
                            Intent addPostIntent = new Intent(PageHomeActivity.this,AddPagePostActivity.class);
                            addPostIntent.putExtra("PageUid",PageAddress);
                            addPostIntent.putExtra("imageName",image4);
                            startActivity(addPostIntent);
                        }
                    }
                });
                builder.show();
            }
        });

        PageEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(PageHomeActivity.this,PageEditActivity.class);
                intent.putExtra("PageUid",PageAddress);
                startActivity(intent);
            }
        });

        updateUserStatus("online");


    }


    private void updateUserStatus(String state)
    {
        String SaveCurrentDate, SaveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        SaveCurrentDate =currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        SaveCurrentTime =currentTime.format(callForTime.getTime());


        Map CurrentStatemap = new HashMap();
        CurrentStatemap.put("time", SaveCurrentTime);
        CurrentStatemap.put("date", SaveCurrentDate);
        CurrentStatemap.put("type", state);

        PageReference.child("userState")
                .updateChildren(CurrentStatemap);
    }

    private void SetupBtn()
    {
        CharSequence[] options = new CharSequence[]
                {
                        "Set Message Button",
                        "Set Call Now Button",
                        "Set Go to Website Button",
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(PageHomeActivity.this);
        builder.setTitle("Set Button");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which == 0)
                {
                    PageReference.child("PageButton").setValue("MessageBtn");
                    pageHomeMessageBtn.setVisibility(View.VISIBLE);
                    pageHomeAddBtn.setVisibility(View.GONE);
                    pageHomeCallBtn.setVisibility(View.GONE);
                    pageHomeWebsiteVisitBtn.setVisibility(View.GONE);
                }
                if (which == 1)
                {
                    if (pageNumber.getText().toString().equals(""))
                    {
                        Intent addPostIntent = new Intent(PageHomeActivity.this,PageEditActivity.class);
                        addPostIntent.putExtra("PageUid",PageAddress);
                        startActivity(addPostIntent);
                        Toast.makeText(PageHomeActivity.this,"Please save phone number",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        PageReference.child("PageButton").setValue("CallBtn");
                        pageHomeCallBtn.setVisibility(View.VISIBLE);
                        pageHomeMessageBtn.setVisibility(View.GONE);
                        pageHomeAddBtn.setVisibility(View.GONE);
                        pageHomeWebsiteVisitBtn.setVisibility(View.GONE);
                    }
                }

                if (which == 2)
                {
                    if (pageWebsiteLink.getText().toString().equals(""))
                    {
                        Intent addPostIntent = new Intent(PageHomeActivity.this,PageEditActivity.class);
                        addPostIntent.putExtra("PageUid",PageAddress);
                        startActivity(addPostIntent);
                        Toast.makeText(PageHomeActivity.this,"Please save website link",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        PageReference.child("PageButton").setValue("websiteLink");
                        pageHomeWebsiteVisitBtn.setVisibility(View.VISIBLE);
                        pageHomeMessageBtn.setVisibility(View.GONE);
                        pageHomeAddBtn.setVisibility(View.GONE);
                        pageHomeCallBtn.setVisibility(View.GONE);

                    }
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onStop() {
        updateUserStatus("offline");
        super.onStop();
    }

    private void DisplayInviteFriends() {
        Query query = FriendReff.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PageHomeActivity.FriendsViewHolder friendsViewHolder, final int position, @NonNull Friends friends) {


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

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Posts, PageHomeActivity.PostsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PageHomeActivity.PostsViewHolder holder, final int position, @NonNull final Posts model) {



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
                                                        Intent commentsIntent = new Intent(PageHomeActivity.this,ClickPostActivity.class);
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
                                                        Intent commentsIntent = new Intent(PageHomeActivity.this,ClickPostActivity.class);
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
                                                        Intent commentsIntent = new Intent(PageHomeActivity.this,ClickPostActivity.class);
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
                                                        Intent commentsIntent = new Intent(PageHomeActivity.this,ClickPostActivity.class);
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
                                        Intent commentsIntent = new Intent(PageHomeActivity.this,CommentsActivity.class);
                                        commentsIntent.putExtra("PostKey",PostKey);
                                        startActivity(commentsIntent);
                                    }
                                });
                                holder.user_profile_image.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent findPageIntent = new Intent(PageHomeActivity.this, PageHomeActivity.class);
                                        findPageIntent.putExtra("visit_user_id", Uid);
                                        startActivity(findPageIntent);
                                    }
                                });

                                holder.username.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent findPageIntent = new Intent(PageHomeActivity.this, PageHomeActivity.class);
                                        findPageIntent.putExtra("visit_user_id", Uid);
                                        startActivity(findPageIntent);
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
                                        Intent commentsIntent = new Intent(PageHomeActivity.this,StarFriendActivity.class);
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
                                Intent clickPostIntent = new Intent(PageHomeActivity.this,ClickPostActivity.class);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (HideText.getText().toString().equals("ProfilePicture")) {
            if (requestCode == Gallery_pick && resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);


            }


            Calendar calForDate = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calForTime.getTime());

            final String postRandomName = saveCurrentDate + saveCurrentTime;


            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    Uri resultUri = result.getUri();

                    File thumb_filePathUri = new File(resultUri.getPath());


                    try {
                        thumb_bitmap = new Compressor(PageHomeActivity.this)
                                .setMaxWidth(200)
                                .setMaxHeight(200)
                                .setQuality(80)
                                .compressToBitmap(thumb_filePathUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                    final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                    StorageReference thumb_path = thumbImageReff.child(currentUserId).child("PageProfileImage").child(resultUri.getLastPathSegment() + postRandomName + ".jpg");
                    loadingBar.setTitle("Profile Image");
                    loadingBar.setMessage("Page profile picture uploading ...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();


                    thumb_path.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                            Task<Uri> thumbDownloadUrl = thumb_task.getResult().getMetadata().getReference().getDownloadUrl();
                            if (thumb_task.isSuccessful()) {
                                thumbDownloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();

                                        PageReference.child("pageProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(PageHomeActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                    HideText.setText("");
                                                } else {
                                                    String message = Objects.requireNonNull(task.getException()).getMessage();
                                                    Toast.makeText(PageHomeActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });

                                    }
                                });
                            }

                        }
                    });


                } else {
                    Toast.makeText(PageHomeActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
        else if (HideText.getText().toString().equals("CoverPicture"))
        {

            if (requestCode == Gallery_Pick && resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);


            }


            Calendar calForDate = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            final String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calForTime.getTime());

            final String postRandomName = saveCurrentDate + saveCurrentTime;


            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    Uri resultUri = result.getUri();

                    File thumb_filePathUri = new File(resultUri.getPath());


                    try {
                        thumb_bitmap = new Compressor(PageHomeActivity.this)
                                .setMaxWidth(400)
                                .setMaxHeight(200)
                                .setQuality(80)
                                .compressToBitmap(thumb_filePathUri);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                    final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                    StorageReference thumb_path = thumbImageReff.child(currentUserId).child("PageProfileImage").child(resultUri.getLastPathSegment() + postRandomName + ".jpg");
                    loadingBar.setTitle("Cover Image");
                    loadingBar.setMessage("Page cover picture uploading ...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();


                    thumb_path.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                            Task<Uri> thumbDownloadUrl = thumb_task.getResult().getMetadata().getReference().getDownloadUrl();
                            if (thumb_task.isSuccessful()) {
                                thumbDownloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadUrl = uri.toString();

                                        PageReference.child("pageCoverImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(PageHomeActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                    HideText.setText("");
                                                } else {
                                                    String message = Objects.requireNonNull(task.getException()).getMessage();
                                                    Toast.makeText(PageHomeActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                }
                                            }
                                        });

                                    }
                                });
                            }

                        }
                    });


                } else {
                    Toast.makeText(PageHomeActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }





        }
    }