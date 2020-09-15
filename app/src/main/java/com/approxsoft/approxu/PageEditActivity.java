package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

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
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class PageEditActivity extends AppCompatActivity {

    Toolbar mToolBar;
    CircleImageView PageProfileImage, addPageProfileImage, addPageProfileCoverImage;
    ImageView PageCoverImage;
    TextView PageName, HideText;
    EditText PageUserName, PageNumber, PageLocation, PageWebsiteLink;
    Button UpdateBtn;
    Bitmap thumb_bitmap = null;
    DatabaseReference PageReference;
    String PageUid;
    StorageReference postImagesReff;
    private ProgressDialog loadingBar;
    private final int Gallery_pick1 = 1;
    public final int Gallery_pick2 = 1;
    String currentUserId;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_edit);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        PageUid = getIntent().getExtras().get("PageUid").toString();
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");
        postImagesReff = FirebaseStorage.getInstance().getReference();

        mToolBar = findViewById(R.id.page_edit_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Page");


        PageProfileImage = findViewById(R.id.edit_page_profile_Image);
        addPageProfileImage = findViewById(R.id.edit_page_add_profile_image_btn);
        addPageProfileCoverImage = findViewById(R.id.edit_page_add_cover_image_btn);
        PageCoverImage = findViewById(R.id.edit_page_cover_image);
        PageName = findViewById(R.id.edit_page_page_name);
        PageUserName = findViewById(R.id.edit_page_page_user_name);
        PageNumber = findViewById(R.id.edit_page_page_phone_number);
        PageLocation = findViewById(R.id.edit_page_page_location);
        PageWebsiteLink = findViewById(R.id.edit_page_page_website_link);
        UpdateBtn = findViewById(R.id.edit_page_data_change_btn);
        loadingBar = new ProgressDialog(this);
        HideText = findViewById(R.id.edit_page_hide_text);
        HideText.setText("");
        HideText.setVisibility(View.INVISIBLE);

        PageReference.child(PageUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String profileImage = dataSnapshot.child("pageProfileImage").getValue().toString();
                    String coverImage = dataSnapshot.child("pageCoverImage").getValue().toString();
                    String pageName = dataSnapshot.child("pageName").getValue().toString();
                    String pageNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                    String pageUserName = dataSnapshot.child("pageUserName").getValue().toString();
                    String pageWebLink = dataSnapshot.child("websiteLink").getValue().toString();
                    String address = dataSnapshot.child("address").getValue().toString();

                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(PageProfileImage);
                    Picasso.get().load(coverImage).placeholder(R.drawable.blank_cover_image).into(PageCoverImage);

                    PageName.setText(pageName);
                    PageNumber.setText(pageNumber);
                    PageUserName.setText(pageUserName);
                    PageWebsiteLink.setText(pageWebLink);
                    PageLocation.setText(address);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addPageProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick1);
                HideText.setText("ProfilePicture");
                HideText.setVisibility(View.INVISIBLE);
            }
        });

        addPageProfileCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick2);
                HideText.setText("CoverPicture");
                HideText.setVisibility(View.INVISIBLE);
            }
        });

        PageUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        PageLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        UpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
               StoringDatabase();
            }
        });
    }

    private void StoringDatabase()
    {
        HashMap uploadMap = new HashMap();
        uploadMap.put("pageUserName",PageUserName.getText().toString());
        uploadMap.put("address",PageLocation.getText().toString());
        uploadMap.put("phoneNumber",PageNumber.getText().toString());
        uploadMap.put("websiteLink",PageWebsiteLink.getText().toString());

        PageReference.child(PageUid).updateChildren(uploadMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    UpdateBtn.setText("Change Successfully");
                }
            }
        });
    }

    private void checkInputs()
    {
        if (!TextUtils.isEmpty(PageUserName.getText().toString()))
        {
            if (!TextUtils.isEmpty(PageLocation.getText().toString()))
            {
                UpdateBtn.setEnabled(true);
            }else {
                UpdateBtn.setEnabled(false);
            }
        }else
        {
            UpdateBtn.setEnabled(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (HideText.getText().toString().equals("ProfilePicture")) {
            if (requestCode == Gallery_pick1 && resultCode == RESULT_OK && data != null) {
                Uri imageUri = data.getData();

                CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);


            }



            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);

                if (resultCode == RESULT_OK) {

                    Uri resultUri = result.getUri();

                    File thumb_filePathUri = new File(resultUri.getPath());


                    try {
                        thumb_bitmap = new Compressor(PageEditActivity.this)
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

                    StorageReference thumb_path = postImagesReff.child("Profile Images").child(currentUserId).child("PageProfileImage").child(resultUri.getLastPathSegment()+ ".jpg");
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

                                        PageReference.child(PageUid).child("pageProfileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    Toast.makeText(PageEditActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                    loadingBar.dismiss();
                                                    HideText.setText("");
                                                    HideText.setVisibility(View.INVISIBLE);
                                                } else {
                                                    String message = Objects.requireNonNull(task.getException()).getMessage();
                                                    Toast.makeText(PageEditActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(PageEditActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
        else if (HideText.getText().toString().equals("CoverPicture"))
        {
            if (requestCode == Gallery_pick2 && resultCode == RESULT_OK && data != null)
            {
                final Uri imageUri = data.getData();



                loadingBar.setTitle("Cover Image");
                loadingBar.setMessage("Page cover picture uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();
                final File file1 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,imageUri),new File(this.getCacheDir(),"temp1")));
                final Uri uri11 = Uri.fromFile(file1);



                StorageReference filePath1 = postImagesReff.child("Profile Images").child(currentUserId).child("PageProfileImage").child(imageUri.getLastPathSegment()+ ".jpg");
                filePath1.putFile(uri11).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();
                                    PageReference.child(PageUid).child("pageCoverImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(PageEditActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                                HideText.setText("");
                                                HideText.setVisibility(View.INVISIBLE);
                                            } else {
                                                String message = Objects.requireNonNull(task.getException()).getMessage();
                                                Toast.makeText(PageEditActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });


                                }
                            });

                        }
                        file1.delete();
                    }
                });
                super.onActivityResult(requestCode, resultCode, data);
            }


        }
    }
}
