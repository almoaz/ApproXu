package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private CircleImageView settingProfileImage;
    private TextView setting_full_name, universityName, subjectName, semester_name;
    private EditText  Gender, DateOfBirth, Religion, CurrentCity, HomeTown, emailId, phonNumber;
    private ImageView settingsCoverPic;

    private FirebaseAuth mAuth;
    private StorageReference ImageReff;
    private DatabaseReference UserReff;
    String currentUserId;
    private String dateOfBirthPattern ="[0-9._/-]+";
    private String phoneNoPattern ="[0-9+-]+";
    private Button updateSettingsBtn;
    public static final int Gallery_pick = 1;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        ImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolbar = findViewById(R.id.setting_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        settingProfileImage = findViewById(R.id.settings_profile_image);
        universityName = findViewById(R.id.settings_university_name);
        subjectName = findViewById(R.id.settings_departments_name);
        semester_name = findViewById(R.id.settings_semester_name);
        setting_full_name = findViewById(R.id.settings_full_name);
        Gender = findViewById(R.id.settings_gender_name);
        DateOfBirth = findViewById(R.id.settings_date_of_birth);
        Religion = findViewById(R.id.settings_religion);
        emailId = findViewById(R.id.settings_email_id);
        phonNumber = findViewById(R.id.settings_phone_number);
        CurrentCity = findViewById(R.id.settings_current_city_name);
        HomeTown = findViewById(R.id.settings_hometown_name);
        updateSettingsBtn = findViewById(R.id.settings_update_btn);
        settingsCoverPic = findViewById(R.id.settings_cover_pic);
        loadingBar = new ProgressDialog(this);


        UserReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
               if (dataSnapshot.exists())
               {
                   String Profileimage = dataSnapshot.child("profileImage").getValue().toString();
                   String coverPic = dataSnapshot.child("coverImage").getValue().toString();
                   String fullname = dataSnapshot.child("fullName").getValue().toString();
                   String university = dataSnapshot.child("university").getValue().toString();
                   String subject = dataSnapshot.child("departments").getValue().toString();
                   String semester = dataSnapshot.child("semester").getValue().toString();
                   String currentCity = dataSnapshot.child("currentCity").getValue().toString();
                   String homeTown = dataSnapshot.child("homeTown").getValue().toString();
                   String myDOB = dataSnapshot.child("dateOfBirth").getValue().toString();
                   String religion = dataSnapshot.child("religion").getValue().toString();
                   String myGender = dataSnapshot.child("gender").getValue().toString();
                   String email = dataSnapshot.child("email").getValue().toString();
                   String phone = dataSnapshot.child("phoneNo").getValue().toString();


                   Picasso.get().load(Profileimage).placeholder(R.drawable.profile_icon).into(settingProfileImage);
                   Picasso.get().load(coverPic).into(settingsCoverPic);
                   //Picasso.get().load(myCoverImage).into(updateCoverPicture);

                   setting_full_name.setText(fullname);
                   universityName.setText(university);
                   subjectName.setText(subject);
                   semester_name.setText(semester);
                   DateOfBirth.setText(myDOB);
                   Gender.setText(myGender);
                   Religion.setText(religion);
                   emailId.setText(email);
                   phonNumber.setText(phone);
                   CurrentCity.setText(currentCity);
                   HomeTown.setText(homeTown);



               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        updateSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidateAccountInfo();
            }
        });

        settingProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,Gallery_pick);
            }
        });
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


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                StorageReference filePath = ImageReff.child(currentUserId + ".jpg");
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Your profile picture uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                           // Toast.makeText(SettingsActivity.this, "Image upload successfully firebase storage...", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();

                                    UserReff.child("profileImage").setValue(downloadUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(SettingsActivity.this, "Image upload successfully", Toast.LENGTH_SHORT).show();
                                                        loadingBar.dismiss();
                                                    } else {
                                                        String message = task.getException().getMessage();
                                                        Toast.makeText(SettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SettingsActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }

    private void ValidateAccountInfo()
    {
        String city = CurrentCity.getText().toString();
        String town = HomeTown.getText().toString();
        String gender = Gender.getText().toString();
        String date_of_birth = DateOfBirth.getText().toString();
        String religion = Religion.getText().toString();
        String email = emailId.getText().toString();
        String phone = phonNumber.getText().toString();

        if (TextUtils.isEmpty(city))
        {
            Toast.makeText(SettingsActivity.this,"Please write your city name...",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(town))
        {
            Toast.makeText(SettingsActivity.this,"Please write your town name...",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(date_of_birth))
        {
            Toast.makeText(SettingsActivity.this,"Please write your date of birth...",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(religion))
        {
            Toast.makeText(SettingsActivity.this,"Please write your religion...",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(email))
        {
            Toast.makeText(SettingsActivity.this,"Please write your email...",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(gender))
        {
            Toast.makeText(SettingsActivity.this,"Please write your gender...",Toast.LENGTH_SHORT).show();
        }
        else  if (TextUtils.isEmpty(phone))
        {
            Toast.makeText(SettingsActivity.this,"Please write your phone number...",Toast.LENGTH_SHORT).show();
        }
        else {
            loadingBar.setTitle("Profile Settings");
            loadingBar.setMessage("Your profile settings uploading ...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();
            UpdateAccountInfo(city,town,gender,date_of_birth,religion, email,phone);
        }
    }



    private void UpdateAccountInfo(String city, String town, String gender, String date_of_birth, String religion, String email, String phone)
    {
        if (date_of_birth.matches(dateOfBirthPattern)){
            if (phone.matches(phoneNoPattern))
            {
                HashMap userMap = new HashMap();
                userMap.put("currentCity", city);
                userMap.put("homeTown", town);
                userMap.put("gender", gender);
                userMap.put("dateOfBirth", date_of_birth);
                userMap.put("religion", religion);
                userMap.put("email", email);
                userMap.put("phoneNo", phone);

                UserReff.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {
                            SendUserToMainActivity();

                            Toast.makeText(SettingsActivity.this,"Account Setting Updated Successfully",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        else
                        {
                            Toast.makeText(SettingsActivity.this,"Error Occoured , while updating account info",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }else {
                Toast.makeText(SettingsActivity.this,"Phone no incorrect format",Toast.LENGTH_SHORT).show();
            }

        }else {
            Toast.makeText(SettingsActivity.this,"Date of birth incorrect format",Toast.LENGTH_SHORT).show();
        }

    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(SettingsActivity.this,HomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}
