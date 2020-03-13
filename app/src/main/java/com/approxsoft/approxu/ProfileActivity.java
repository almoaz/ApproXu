package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Toolbar mToolbar;
    private CircleImageView profileImage;
    ImageView addPostIcon, editProfileIcon, uploadCoverPic;
    TextView userName,addPost, editProfile, moreInformation, universityName, departmentsName, Semester, semesterID, date_Of_Birth, current_city_name, profileFullName;
    FirebaseAuth mAuth;
    DatabaseReference userReff, profileUserReff, FriendsReff, PostsReff, PostsRef, userRef, StarRef;
    String currentUserId;
    private RecyclerView myPostList;

    Boolean StarChecker = false;

    private TextView MyPosts, MyFriends;
    private int countFriends = 0, countPosts = 0;
    public static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private StorageReference ImageReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        profileUserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        ImageReff = FirebaseStorage.getInstance().getReference().child("Cover Image");

        FriendsReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Friends");
        PostsReff = FirebaseDatabase.getInstance().getReference().child("Post");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Post");
        userRef = FirebaseDatabase.getInstance().getReference().child("All Users");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Star");


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


        mToolbar = findViewById(R.id.profile_tool_bar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        myPostList = findViewById(R.id.my_all_post_view);
        myPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myPostList.setLayoutManager(linearLayoutManager);

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
                    Picasso.get().load(coverImage).into(uploadCoverPic);
                    Picasso.get().load(PrfileImage).placeholder(R.drawable.profile_icon).into(profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DisplayAllMyPosts();


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
                Intent editProfileIntent = new Intent(ProfileActivity.this, SettingsActivity.class);
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

        uploadCoverPic.setOnClickListener(new View.OnClickListener() {
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
                    MyFriends.setText("0 Friends");
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


        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            uploadCoverPic.setImageURI(ImageUri);
        }

        if (resultCode == RESULT_OK) {
            loadingBar.setTitle("Cover Image");
            loadingBar.setMessage("Your cover image uploading ...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();


            StorageReference filePath = ImageReff.child(currentUserId + ".jpg");                     //loadingBar.setTitle("Profile Image");

            filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
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

    }
    private void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }


    @Override
    protected void onRestart() {

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

                    Picasso.get().load(coverImage).into(uploadCoverPic);
                    Picasso.get().load(PrfileImage).placeholder(R.drawable.profile_icon).into(profileImage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        super.onRestart();
    }

    private void DisplayAllMyPosts() {
        Query ShortPostInDecendingOrder = PostsRef.orderByChild("uid")
                .startAt(currentUserId).endAt(currentUserId + " \uf8ff");


        FirebaseRecyclerOptions<Posts> options = new FirebaseRecyclerOptions.Builder<Posts>().setQuery(ShortPostInDecendingOrder, Posts.class).build();
        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts, ProfileActivity.PostsViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull ProfileActivity.PostsViewHolder holder, final int position, @NonNull Posts model) {

                final String PostKey = getRef(position).getKey();
                holder.username.setText(model.getFullName());
                holder.time.setText("    " + model.getTime());
                holder.date.setText("    " + model.getDate());
                holder.description.setText(model.getDescription());

                Picasso.get().load(model.getPostImage()).into(holder.postImage);
                Picasso.get().load(model.getProfileImage()).into(holder.user_profile_image);

                holder.setStarButtonStatus(PostKey);


                holder.postImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent clickPostIntent = new Intent(ProfileActivity.this,ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(clickPostIntent);
                    }
                });
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

                holder.StarPostBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StarChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (StarChecker.equals(true)) {
                                    if (dataSnapshot.child(Objects.requireNonNull(PostKey)).hasChild(currentUserId)) {
                                        StarRef.child(PostKey).child(currentUserId).removeValue();
                                        StarChecker = false;
                                    } else {
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
        TextView DisplayNoOfStar;
        int countStar;
        String currentUserId;
        DatabaseReference StarReff;
        TextView username, date, time, description;
        CircleImageView user_profile_image;
        ImageView postImage;

        PostsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            username = itemView.findViewById(R.id.post_user_name);
            date = itemView.findViewById(R.id.post_date);
            time = itemView.findViewById(R.id.post_time);
            description = itemView.findViewById(R.id.post_description);
            user_profile_image = itemView.findViewById(R.id.post_profile_image);
            postImage = itemView.findViewById(R.id.post_image);

            StarPostBtn = itemView.findViewById(R.id.display_star_btn);
            CommentsPostBtn = itemView.findViewById(R.id.comment_button);
            DisplayNoOfStar = itemView.findViewById(R.id.display_no_of_star);

            StarReff = FirebaseDatabase.getInstance().getReference().child("Star");
            currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        }

        void setStarButtonStatus(final String PostKey) {
            StarReff.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(PostKey).hasChild(currentUserId)) {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        DisplayNoOfStar.setText(countStar + (" Star"));
                    } else {
                        countStar = (int) dataSnapshot.child(PostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        DisplayNoOfStar.setText(countStar + (" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

}
