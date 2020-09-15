package com.approxsoft.approxu;

import androidx.annotation.NonNull;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
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

public class CreatePageActivity extends AppCompatActivity {

    Toolbar mToolBar;
    RelativeLayout FirstCreatePageLayout, SecondCreatePageLayout;
    CheckBox CheckBox;
    Button CreatePageAgreeBtn, PageCreateBtn;
    TextView pageType, pageCategory;
    CircleImageView CreatePageImage;
    EditText CreatePageName, CreatePageUserName, CreatePagePhoneNumber, CreatePageWebsite, CreatePageAddress;
    Spinner pageTypeSpinner, pageCategorySpinner;
    public static final int Gallery_pick = 1;
    private ProgressDialog loadingBar;
    Bitmap thumb_bitmap = null;
    FirebaseAuth mAuth;
    String currentUserId ;
    DatabaseReference UserReff, PageReff;
    StorageReference thumbImageReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        PageReff = FirebaseDatabase.getInstance().getReference().child("All Pages");
        thumbImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolBar = findViewById(R.id.create_page_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Create Page");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirstCreatePageLayout = findViewById(R.id.create_page_permission_relative_layout);
        SecondCreatePageLayout = findViewById(R.id.create_account_relative_layout);
        CheckBox = findViewById(R.id.create_page_checkBox);
        CreatePageAgreeBtn = findViewById(R.id.create_page_get_started_btn);
        PageCreateBtn = findViewById(R.id.page_create_btn);
        pageType = findViewById(R.id.create_page_type);
        pageCategory = findViewById(R.id.create_page_category);
        pageTypeSpinner = findViewById(R.id.create_page_type_spinner);
        pageCategorySpinner = findViewById(R.id.create_page_category_spinner);
        CreatePageName = findViewById(R.id.create_page_name);
        CreatePageUserName = findViewById(R.id.create_page_username);
        CreatePagePhoneNumber = findViewById(R.id.create_page_phone_number);
        CreatePageWebsite = findViewById(R.id.create_page_website_link);
        CreatePageAddress = findViewById(R.id.create_page_address);
        CreatePageImage = findViewById(R.id.create_page_profile_image);
        loadingBar = new ProgressDialog(this);



        CheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CheckBox.isChecked())
                {
                    CreatePageAgreeBtn.setEnabled(true);
                    CreatePageAgreeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            FirstCreatePageLayout.setVisibility(View.GONE);
                            FirstCreatePageLayout.setEnabled(false);
                            SecondCreatePageLayout.setVisibility(View.VISIBLE);
                        }
                    });
                }
                else
                {

                    CreatePageAgreeBtn.setEnabled(false);
                    FirstCreatePageLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        CreatePageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });





        CreatePageName.addTextChangedListener(new TextWatcher() {
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
        CreatePageUserName.addTextChangedListener(new TextWatcher() {
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

        CreatePageAddress.addTextChangedListener(new TextWatcher() {
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
        PageCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SavingInformationToFirebaseDatabase();

            }
        });




    }


    private void checkInputs()
    {
        if (!TextUtils.isEmpty(CreatePageName.getText().toString()))
        {

            if (!TextUtils.isEmpty(CreatePageUserName.getText().toString()))
            {
                if (!TextUtils.isEmpty(CreatePageAddress.getText().toString()))
                {

                    if (pageTypeSpinner.getSelectedItem().toString().equals("Select Page Type"))
                    {
                        if (pageCategorySpinner.getSelectedItem().toString().equals("Select Page Category"))
                        {
                            PageCreateBtn.setEnabled(true);
                        }
                        else
                        {
                            PageCreateBtn.setEnabled(false);
                        }
                    }
                    else
                    {
                        PageCreateBtn.setEnabled(false);
                    }
                }
                else
                {
                    PageCreateBtn.setEnabled(false);
                }
            }
            else
            {
                PageCreateBtn.setEnabled(false);
            }
        }
        else
        {
            PageCreateBtn.setEnabled(false);
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

            CreatePageImage.setImageURI(imageUri);



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
                    thumb_bitmap = new Compressor(CreatePageActivity.this)
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

                StorageReference thumb_path = thumbImageReff.child(currentUserId).child("PageProfileImage").child(resultUri.getLastPathSegment()+postRandomName+".jpg");
                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Your profile picture uploading ...");
                loadingBar.setCanceledOnTouchOutside(true);
                loadingBar.show();


                thumb_path.putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                        Task<Uri> thumbDownloadUrl = thumb_task.getResult().getMetadata().getReference().getDownloadUrl();
                        if (thumb_task.isSuccessful())
                        {
                            thumbDownloadUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();


                                }
                            });
                        }

                    }
                });




            } else {
                Toast.makeText(CreatePageActivity.this, "Error: Image not crop.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);


    }
    private void SavingInformationToFirebaseDatabase() {

        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final  String PostRandomName = saveCurrentDate + saveCurrentTime;

        HashMap pageMap = new HashMap();
        pageMap.put("pageName",CreatePageName.getText().toString());
        pageMap.put("pageUserName",CreatePageUserName.getText().toString());
        pageMap.put("pageProfileImage","None");
        pageMap.put("pageCoverImage","None");
        pageMap.put("address",CreatePageAddress.getText().toString());
        pageMap.put("pageType",pageTypeSpinner.getSelectedItem().toString());
        pageMap.put("pageCategory",pageCategorySpinner.getSelectedItem().toString());
        pageMap.put("phoneNumber",CreatePagePhoneNumber.getText().toString());
        pageMap.put("websiteLink",CreatePageWebsite.getText().toString());
        pageMap.put("date",saveCurrentDate);
        pageMap.put("time",saveCurrentTime);
        pageMap.put("AdminUid",currentUserId);
        pageMap.put("pageUid",currentUserId+PostRandomName);

        PageReff.child(currentUserId+PostRandomName).updateChildren(pageMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())
                {
                    Intent intent = new Intent(CreatePageActivity.this,PageActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });



    }

}
