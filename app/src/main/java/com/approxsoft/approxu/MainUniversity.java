package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

public class MainUniversity extends AppCompatActivity {

    private TextView AddInfoBtn;
    private TextView checkSdBtn;
    private TextView upDateSdBtn;
    private TextView EditBtn;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private ImageView UniversityCoverImage;
    private ImageView CoverImage, slideImage1, slideImage2, slideImage3, slideImage4,slideImage5,slideImage6;
    private DatabaseReference universityReff;
    public static final int Gallery_Pick = 1;
    private Uri ImageUri;
    private ProgressDialog loadingBar;
    private StorageReference ImageReff;
    private DatabaseReference userReff, universityImageUserReff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_university);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        universityReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId);
        ImageReff = FirebaseStorage.getInstance().getReference().child("University CoverImage");

        AddInfoBtn = findViewById(R.id.main_university_add_info_btn);
        checkSdBtn = findViewById(R.id.main_university_check_sd_btn);
        upDateSdBtn = findViewById(R.id.main_university_update_sd_btn);
        EditBtn = findViewById(R.id.main_university_edit_btn);
        UniversityCoverImage = findViewById(R.id.main_university_cover_image);
        slideImage1 = findViewById(R.id.banner1);
        slideImage2 = findViewById(R.id.banner2);
        slideImage3 = findViewById(R.id.banner3);
        slideImage4 = findViewById(R.id.banner4);
        slideImage5 = findViewById(R.id.banner5);
        slideImage6 = findViewById(R.id.banner6);
        loadingBar = new ProgressDialog(this);;

        AddInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setupIntent = new Intent(MainUniversity.this,UniversityDucumentsForm.class);
                startActivity(setupIntent);
                Animatoo.animateSlideLeft(MainUniversity.this);
            }
        });

        checkSdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent checkIntent = new Intent(MainUniversity.this,FindStudentsActivity.class);
                startActivity(checkIntent);
                Animatoo.animateSlideLeft(MainUniversity.this);
            }
        });

        EditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(MainUniversity.this,EditUniversityActivity.class);
                startActivity(editIntent);
                Animatoo.animateSlideLeft(MainUniversity.this);
            }
        });
        UniversityCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_Pick);
            }
        });

        universityReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                {
                    String university = dataSnapshot.child("CreateUniversityName").getValue().toString();

                    universityImageUserReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("UniversityGallery");

                    universityImageUserReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                String coverImage = dataSnapshot.child("coverImage").getValue().toString();
                                String slide1 = dataSnapshot.child("slideImage1").getValue().toString();
                                String slide2 = dataSnapshot.child("slideImage2").getValue().toString();
                                String slide3 = dataSnapshot.child("slideImage3").getValue().toString();
                                String slide4 = dataSnapshot.child("slideImage4").getValue().toString();
                                String slide5 = dataSnapshot.child("slideImage5").getValue().toString();
                                String slide6 = dataSnapshot.child("slideImage6").getValue().toString();
                                Picasso.get().load(coverImage).into(UniversityCoverImage);
                                Picasso.get().load(slide1).into(slideImage1);
                                Picasso.get().load(slide2).into(slideImage2);
                                Picasso.get().load(slide3).into(slideImage3);
                                Picasso.get().load(slide4).into(slideImage4);
                                Picasso.get().load(slide5).into(slideImage5);
                                Picasso.get().load(slide6).into(slideImage6);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            UniversityCoverImage.setImageURI(ImageUri);
        }


        StorageReference filePath = ImageReff.child(CurrentUserId + ".jpg");
        loadingBar.setTitle("University Cover Image");
        loadingBar.setMessage("Your university cover picture uploading ...");
        loadingBar.setCanceledOnTouchOutside(true);
        loadingBar.show();

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {

                    //Toast.makeText(AddPostActivity.this, "Image upload successfully firebase storage...", Toast.LENGTH_SHORT).show();

                    Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();

                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            final String downloadUrl = uri.toString();
                            universityImageUserReff.child("coverImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Intent selfIntent = new Intent(MainUniversity.this, MainUniversity.class);
                                                startActivity(selfIntent);
                                                Animatoo.animateFade(MainUniversity.this);
                                                loadingBar.dismiss();
                                            } else {
                                                String message = task.getException().getMessage();
                                                Toast.makeText(MainUniversity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                loadingBar.dismiss();
                                            }
                                        }
                                    });
                        }
                    });
                }
            }
        });

    }



    @Override
    public void onBackPressed() {
        Intent setupIntent = new Intent(MainUniversity.this,HomeActivity.class);
        startActivity(setupIntent);
        Animatoo.animateSlideRight(this);
        super.onBackPressed();
    }
}
