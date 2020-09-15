package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
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
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private CircleImageView profileImage;
    ImageView addPostIcon, editProfileIcon, uploadCoverPic;
    TextView userName,addPost,AddCoverImageIcon, editProfile, moreInformation, universityName, departmentsName, Semester, semesterID, date_Of_Birth, current_city_name, profileFullName;
    FirebaseAuth mAuth;
    DatabaseReference userReff, profileUserReff, FriendsReff, PostsReff, PostsRef, userRef, StarRef;
    String currentUserId;
    private RecyclerView myPostList, myImageList;

    Boolean StarChecker = false;

    private TextView MyPosts, MyFriends;
    private int countFriends = 0, countPosts = 0;
    public static final int Gallery_pick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private StorageReference ImageReff;
    Bitmap thumb_bitmaps = null;
    SwipeRefreshLayout refreshLayout;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        profileUserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        ImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

        FriendsReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Friends");
        PostsReff = FirebaseDatabase.getInstance().getReference().child("Post");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
        userRef = FirebaseDatabase.getInstance().getReference().child("All Users");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Post");

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = Objects.requireNonNull(manager).getActiveNetworkInfo();

        if (null!=activeNetwork)
        {

        }else
        {

            final AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
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

        myPostList = findViewById(R.id.my_all_post_view);
        myPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostList.setLayoutManager(linearLayoutManager);

        DisplayAllMyPosts();



        addPostIcon = findViewById(R.id.profile_add_new_post_icon);
        addPost = findViewById(R.id.profile_add_new_post);
        editProfileIcon = findViewById(R.id.profile_edit_icon);
        editProfile = findViewById(R.id.profile_edit);
        moreInformation = findViewById(R.id.profile_more_information);
        universityName = findViewById(R.id.profile_university_name);
        profileImage = findViewById(R.id.profile_image);
        uploadCoverPic = findViewById(R.id.profile_cover_pic);
        departmentsName = findViewById(R.id.profile_department_name);
        Semester = findViewById(R.id.profile_semester);
        semesterID = findViewById(R.id.profile_id);
        date_Of_Birth = findViewById(R.id.profile_date_of_birth);
        current_city_name = findViewById(R.id.profile_current_city_name);
        MyFriends = findViewById(R.id.profile_friend_count);
        MyPosts = findViewById(R.id.profile_post_count);
        loadingBar = new ProgressDialog(this);
        userName = findViewById(R.id.profile_user_name);
        profileFullName = findViewById(R.id.profile_full_name);
        AddCoverImageIcon = findViewById(R.id.add_cover_image_icon);
        refreshLayout = findViewById(R.id.profile_refresh_Layout);
        relativeLayout = findViewById(R.id.profile_relative_layout);

        relativeLayout.setVisibility(View.VISIBLE);

        mToolbar = findViewById(R.id.profile_tool_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");





        userReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String fullname = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                    String departments = Objects.requireNonNull(dataSnapshot.child("departments").getValue()).toString();
                    String semester = Objects.requireNonNull(dataSnapshot.child("semester").getValue()).toString();
                    String semesterid = Objects.requireNonNull(dataSnapshot.child("stdId").getValue()).toString();
                    String dateOfBirth = Objects.requireNonNull(dataSnapshot.child("dateOfBirth").getValue()).toString();
                    String currentCity = Objects.requireNonNull(dataSnapshot.child("currentCity").getValue()).toString();
                    String PrfileImage = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    String coverImage = Objects.requireNonNull(dataSnapshot.child("coverImage").getValue()).toString();

                    profileFullName.setText(fullname);
                    userName.setText(fullname);
                    universityName.setText(university);
                    departmentsName.setText(departments);
                    Semester.setText(semester);
                    semesterID.setText(semesterid);
                    date_Of_Birth.setText(dateOfBirth);
                    current_city_name.setText(currentCity);
                    Picasso.get().load(coverImage).placeholder(R.drawable.blank_cover_image).into(uploadCoverPic);
                    Picasso.get().load(PrfileImage).placeholder(R.drawable.profile_icon).into(profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                startActivity(intent);
                Animatoo.animateFade(ProfileActivity.this);
                finish();
                refreshLayout.isRefreshing();
                refreshLayout.setRefreshing(false);
                DisplayAllMyPosts();



            }
        });


        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(ProfileActivity.this, AddPostActivity.class);
                startActivity(postIntent);
            }
        });

        addPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent postIntent = new Intent(ProfileActivity.this, AddPostActivity.class);
                startActivity(postIntent);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(editProfileIntent);
            }
        });

        editProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editProfileIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(editProfileIntent);
            }
        });

        moreInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent moreInformationIntent = new Intent(ProfileActivity.this, AboutActivity.class);
                startActivity(moreInformationIntent);
            }
        });

        AddCoverImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });



        FriendsReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countFriends = (int) dataSnapshot.getChildrenCount();
                    MyFriends.setText(countFriends + "  Friends");
                }
                else
                {
                    MyFriends.setText("Friends");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        MyFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendIntent = new Intent(ProfileActivity.this,FriendsActivity.class);
                startActivity(friendIntent);
                Animatoo.animateFade(ProfileActivity.this);
            }
        });

        PostsReff.orderByChild("uid").startAt(currentUserId).endAt(currentUserId + "\uf8ff")
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            countPosts = (int) dataSnapshot.getChildrenCount();
                            MyPosts.setText(countPosts + "  Posts");
                        }
                        else
                        {
                            MyPosts.setText("0 Posts");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode == Gallery_pick && resultCode == RESULT_OK && data!=null) {
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

        final  String postRandomName = saveCurrentDate + saveCurrentTime;


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                loadingBar.setTitle("Cover Image");
                loadingBar.setMessage("Your cover image uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());

                try {
                    thumb_bitmaps = new Compressor(ProfileActivity.this)
                            .setMaxWidth(400)
                            .setMaxHeight(200)
                            .setQuality(80)
                            .compressToBitmap(thumb_filePathUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmaps.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();


                StorageReference filePath = ImageReff.child(currentUserId).child("CoverImage").child(resultUri.getLastPathSegment() + postRandomName + ".jpg");                     //loadingBar.setTitle("Profile Image");

                filePath.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            //Toast.makeText(AddPostActivity.this, "Image upload successfully firebase storage...", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getMetadata()).getReference()).getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    profileUserReff.child("coverImage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {


                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = Objects.requireNonNull(task.getException()).getMessage();
                                                        Toast.makeText(ProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                loadingBar.dismiss();
            }
        } super.onActivityResult(requestCode, resultCode, data);

    }
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_pick);
    }



    private void DisplayAllMyPosts() {
        Query myPost = PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + " \uf8ff");


        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(myPost, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, ProfileActivity.PostsViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull final ProfileActivity.PostsViewHolder holder, final int position, @NonNull Posts model) {

                final String PostKey = getRef(position).getKey();
                final String PostKeys = getRef(position).getKey();

                relativeLayout.setVisibility(View.VISIBLE);
                holder.username.setText(model.getFullName());
                holder.time.setText("    " + model.getTime());
                holder.date.setText("    " + model.getDate());
                holder.description.setText(model.getDescription());

               /// Picasso.get().load(model.getProfileImage()).into(holder.user_profile_image);

                holder.setStarButtonStatus(PostKey);
                holder.setCommentsCount(PostKey);


                holder.description.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(ProfileActivity.this,ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(clickPostIntent);
                    }
                });


                holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsIntent = new Intent(ProfileActivity.this, CommentsActivity.class);
                        commentsIntent.putExtra("PostKey", PostKey);
                        startActivity(commentsIntent);
                    }
                });

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
                                        Intent commentsIntent = new Intent(ProfileActivity.this,StarFriendActivity.class);
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


                holder.StarPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StarChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (StarChecker.equals(true))
                                {
                                    assert PostKey != null;
                                    if (dataSnapshot.child(PostKey).child("Star").child(PostKey).hasChild(currentUserId))
                                    {
                                        StarRef.child(PostKey).child("Star").child(PostKey).child(currentUserId).removeValue();
                                        StarChecker = false;
                                    }
                                    else
                                    {
                                        StarRef.child(PostKey).child("Star").child(PostKey).child(currentUserId).setValue(true);
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
                PostsRef.child(PostKeys).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String Uid = dataSnapshot.child("uid").getValue().toString();

                            userRef.child(Uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                    String name = dataSnapshot.child("fullName").getValue().toString();

                                    holder.username.setText(name);
                                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(holder.user_profile_image);

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

            @NonNull
            @Override
            public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout, parent, false);
                PostsViewHolder viewHolder;
                viewHolder = new PostsViewHolder(view);
                return viewHolder;
            }
        };
        myPostList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView StarPostBtn, CommentsPostBtn;
        TextView DisplayNoOfStar,commentsCount;
        int countStar = 0 ;
        int CommentsCount = 0 ;
        String currentUserId;
        DatabaseReference StarReff,CommentReff;
        TextView username, date, time, description;
        CircleImageView user_profile_image;

        PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username = itemView.findViewById(R.id.post_user_name);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            user_profile_image = itemView.findViewById(R.id.post_profile_image);
            StarPostBtn = itemView.findViewById(R.id.display_star_btn);
            CommentsPostBtn = itemView.findViewById(R.id.comment_button);
            DisplayNoOfStar = itemView.findViewById(R.id.display_no_of_star);
            commentsCount = itemView.findViewById(R.id.comments_count_btn);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Post");
            CommentReff = FirebaseDatabase.getInstance().getReference().child("Post");
            currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        }

        void setStarButtonStatus(final String PostKey) {
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
