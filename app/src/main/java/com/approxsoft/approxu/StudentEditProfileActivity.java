package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class StudentEditProfileActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText fullName, emailId, phoneNo;
    private TextView userName;
    private CircleImageView UserProfileImage;
    private FirebaseAuth mAuth;
    private DatabaseReference studentReff,userReff,StudentReff;
    private String  currentUserId;
    public static final int Gallery_pick = 1;
    private Button UpdateProfileBtn;
    private StorageReference ImageReff;
    private ProgressDialog loadingBar;
    Bitmap thumb_bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        ImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = findViewById(R.id.student_edit_profile_tool_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        fullName = findViewById(R.id.student_edit_full_name);
        userName = findViewById(R.id.profile_edit_student_name);
        emailId = findViewById(R.id.student_edit_email_id);
        phoneNo = findViewById(R.id.student_edit_phone_number);
        UserProfileImage = findViewById(R.id.student_edit_profile_image);
        UpdateProfileBtn = findViewById(R.id.update_student_profile);
        loadingBar = new ProgressDialog(this);

        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("university").getValue().toString();

                    studentReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students");


                    studentReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String name = dataSnapshot.child("fullName").getValue().toString();
                                String email = dataSnapshot.child("Email").getValue().toString();
                                String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                String phone = dataSnapshot.child("Phone").getValue().toString();

                                fullName.setText(name);
                                userName.setText(name);
                                emailId.setText(email);
                                phoneNo.setText(phone);

                                Picasso.get().load(profileImage).into(UserProfileImage);



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

        UserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);
            }
        });

        UpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckDocuments();
            }
        });
    }

    private void CheckDocuments() {
        String name = fullName.getText().toString();
        String email = emailId.getText().toString();
        String number = phoneNo.getText().toString();

        if (TextUtils.isEmpty(name))
        {
            Toast.makeText(StudentEditProfileActivity.this,"Please write student university name",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(email))
        {
            Toast.makeText(StudentEditProfileActivity.this,"Please write student departments name",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(number))
        {
            Toast.makeText(StudentEditProfileActivity.this,"Please write student group name",Toast.LENGTH_SHORT).show();
        }
        else
        {
            UpdateAccountInfo(name,email,number);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

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

                Uri resultUri = result.getUri();

                File thumb_filePathUri = new File(resultUri.getPath());


                try
                {
                    thumb_bitmap = new Compressor(StudentEditProfileActivity.this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(80)
                            .compressToBitmap(thumb_filePathUri);

                }catch (IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,80, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();

                StorageReference filePath = ImageReff.child(currentUserId).child("ProfileImage").child(resultUri.getLastPathSegment()+postRandomName+".jpg");
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Your profile picture uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                filePath.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            // Toast.makeText(SettingsActivity.this, "Image upload successfully firebase storage...", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    studentReff.child(currentUserId).child("profileImage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Intent selfIntent = new Intent(StudentEditProfileActivity.this, StudentEditProfileActivity.class);
                                                        startActivity(selfIntent);

                                                        Toast.makeText(StudentEditProfileActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(StudentEditProfileActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(StudentEditProfileActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void UpdateAccountInfo(final String name, final String email, final String number) {
        userReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists()){
                    String University = dataSnapshot.child("university").getValue().toString();


                    HashMap rdsMap = new HashMap();
                    rdsMap.put("fullName",name);
                    rdsMap.put("Email",email);
                    rdsMap.put("Phone",number);

                    StudentReff = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child("All Students");

                    StudentReff.child(currentUserId).updateChildren(rdsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task)
                        {
                            if (task.isSuccessful())
                            {
                                SendUserToFindStudentActivity();

                                Toast.makeText(StudentEditProfileActivity.this,"Student SD Updated Successfully",Toast.LENGTH_SHORT).show();

                            }
                            else
                            {
                                Toast.makeText(StudentEditProfileActivity.this,"Error , while updating student SD",Toast.LENGTH_SHORT).show();

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
    private void SendUserToFindStudentActivity() {
        Intent intent = new Intent(StudentEditProfileActivity.this,UniversityActivity.class);
        startActivity(intent);
        Animatoo.animateSlideRight(StudentEditProfileActivity.this);
    }

}
