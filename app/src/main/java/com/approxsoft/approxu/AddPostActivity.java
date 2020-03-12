package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private EditText postText;
    private ImageView postImage;
    private Button postUploadBtn;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference userReff, postReff, UserReff;
    private StorageReference postImagesReff;
    private String CurrentUserId;
    private String saveCurrentDate;
    private String saveCurrentTime;
    private String postRandomName;
    private Task<Uri> downloadUrl;
    private long countPosts = 0;
    private String Description;
    public static final int Gallery_pick = 1;
    private Uri ImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        postImagesReff = FirebaseStorage.getInstance().getReference();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);
        postReff = FirebaseDatabase.getInstance().getReference().child("Post");


        postText = findViewById(R.id.new_post_text);
        postImage = findViewById(R.id.post_image);
        postUploadBtn = findViewById(R.id.upload_status);
        loadingBar = new ProgressDialog(this);

        mToolbar = findViewById(R.id.add_post_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Upload Post");


        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();
            }
        });

        postText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
               checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        postUploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidatePostInfo();
            }
        });


    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(postText.getText().toString()))
        {
            postUploadBtn.setEnabled(true);
        }else {
            postUploadBtn.setEnabled(false);
        }
    }

    private void ValidatePostInfo()
    {
        Description = postText.getText().toString();

        if (ImageUri == null )
        {
            loadingBar.setTitle("New Post");
            loadingBar.setMessage("uploading your new post..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            savingPostInformationToDatabase();
        }
        else if (!TextUtils.isEmpty(Description))
        {
            loadingBar.setTitle("New Post");
            loadingBar.setMessage("uploading your new post..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImageToFireBaseStorage();

        }else {
            loadingBar.setTitle("New Post");
            loadingBar.setMessage("uploading your new post..");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            StoringImageToFireBaseStorage();

        }
    }

    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,Gallery_pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==Gallery_pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            postImage.setImageURI(ImageUri);
            postUploadBtn.setEnabled(true);
        }
        else {
            postUploadBtn.setEnabled(false);
        }
    }

    private void StoringImageToFireBaseStorage()
    {
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;


        StorageReference filePath = postImagesReff.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");                         //loadingBar.setTitle("Profile Image");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                   // Toast.makeText(AddPostActivity.this, "Image upload successfully firebase storage...", Toast.LENGTH_SHORT).show();

                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                   result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl = uri.toString();



                            UserReff.child("PostImage").child(postRandomName).setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                SavingPostInformationToDatabase();

                                                //Toast.makeText(AddPostActivity.this, "Image upload firebase database...", Toast.LENGTH_SHORT).show();
                                                //loadingBar.dismiss();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(AddPostActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                //loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    });

                }
            }
        });


    }


    private void SavingPostInformationToDatabase()
    {
        postReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countPosts = dataSnapshot.getChildrenCount();
                }
                else
                {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        userReff.child(CurrentUserId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String userFullName = dataSnapshot.child("fullName").getValue().toString();
                    final String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    final String userPostImage = dataSnapshot.child("PostImage").child(postRandomName).getValue().toString();



                    HashMap postsMap = new HashMap();
                    postsMap.put("uid",CurrentUserId);
                    postsMap.put("date",saveCurrentDate);
                    postsMap.put("time",saveCurrentTime);
                    postsMap.put("description", Description );
                    postsMap.put("postImage",userPostImage);
                    postsMap.put("profileImage",userProfileImage);
                    postsMap.put("counter",countPosts);
                    postsMap.put("fullName",userFullName);
                    postsMap.put("uiid",CurrentUserId + postRandomName);


                    postReff.child(CurrentUserId + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())

                                    {
                                        SendUserToHomeActivity();
                                        loadingBar.dismiss();
                                        Toast.makeText(AddPostActivity.this,"New Post Upload",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(AddPostActivity.this,"Error occoured while updating your post" ,Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void savingPostInformationToDatabase()
    {

        postReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countPosts = dataSnapshot.getChildrenCount();
                }
                else
                {
                    countPosts = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        userReff.child(CurrentUserId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String userFullName = dataSnapshot.child("fullName").getValue().toString();
                    final String userProfileImage = dataSnapshot.child("profileImage").getValue().toString();



                    HashMap postsMap = new HashMap();
                    postsMap.put("uid",CurrentUserId);
                    postsMap.put("date",saveCurrentDate);
                    postsMap.put("time",saveCurrentTime);
                    postsMap.put("description", Description );
                    postsMap.put("postImage","None");
                    postsMap.put("profileImage",userProfileImage);
                    postsMap.put("fullName",userFullName);
                    postsMap.put("counter",countPosts);
                    postsMap.put("uiid",CurrentUserId + postRandomName);


                    postReff.child(CurrentUserId + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())

                                    {
                                        SendUserToHomeActivity();
                                        loadingBar.dismiss();
                                        Toast.makeText(AddPostActivity.this,"New Post Upload",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(AddPostActivity.this,"Error occoured while updating your post" ,Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void SendUserToHomeActivity() {
        Intent postIntent = new Intent(AddPostActivity.this,HomeActivity.class);
        startActivity(postIntent);
        finish();
    }




    /*@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            SendUserToMainActivity();

        }
        return super.onOptionsItemSelected(item);
    }*/





}
