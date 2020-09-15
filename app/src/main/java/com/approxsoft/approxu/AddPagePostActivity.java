package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddPagePostActivity extends AppCompatActivity {

    Toolbar mToolBar;
    EditText PagePostText;
    TextView AddPostKbCount;
    ProgressBar Image1ProgressBar,Image2ProgressBar,Image3ProgressBar,Image4ProgressBar;
    private String Image1DownloadUrl,DownloadUrl1;
    private String Image2DownloadUrl,DownloadUrl2;
    private String Image3DownloadUrl,DownloadUrl3;
    private String Image4DownloadUrl,DownloadUrl4;
    Uri uri1,Image1Url;
    Uri uri2,Image2Url;
    Uri uri3,Image3Url;
    Uri uri4,Image4Url;
    Button PagePostBtn;
    DatabaseReference PostReference,profileUserReff, PageReference;
    Spinner AddPostPageSpinner;
    FirebaseAuth mAuth;
    StorageReference postImagesReff;
    private int countPosts = 0;
    private ProgressDialog loadingBar;
    String PageUri, ImageName;
    StorageReference ImageReff;
    CircleImageView AddPagePostImage;
    ImageView Image1, Image2, Image3, Image4, ImageView1DeleteBtn,ImageView2DeleteBtn,ImageView3DeleteBtn,ImageView4DeleteBtn;
    private final int Gallery_pick1 = 1;
    public final int Gallery_pick2 = 2;
    //public final int Gallery_pick3 = 3;
    //public final int Gallery_pick4 = 4;
    String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_page_post);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        PageUri = getIntent().getExtras().get("PageUid").toString();
        ImageName = getIntent().getExtras().get("imageName").toString();
        PostReference = FirebaseDatabase.getInstance().getReference().child("Post");
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");
        postImagesReff = FirebaseStorage.getInstance().getReference();
        profileUserReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);
        ImageReff = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mToolBar = findViewById(R.id.add_page_post_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("Post");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PagePostText = findViewById(R.id.page_post_text);
        PagePostBtn = findViewById(R.id.page_post_upload_status_Btn);
        AddPostPageSpinner = findViewById(R.id.add_post_page_condition);
        AddPostKbCount = findViewById(R.id.add_post_kb_count);
        AddPagePostImage = findViewById(R.id.add_page_post_profile_image);

        init();

        loadingBar = new ProgressDialog(this);

        PagePostText.addTextChangedListener(new TextWatcher() {
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

        PostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countPosts = (int) dataSnapshot.getChildrenCount();
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


        switch (ImageName) {
            case "Image1":
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick1);

                break;
            case "Image2": {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selection Image 2"), Gallery_pick2);

                break;
            }
            case "Image3": {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selection Image 3"), Gallery_pick2);

                break;
            }
            case "Image4": {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Selection Image 4"), Gallery_pick2);

                break;
            }
        }

        PageReference.child(PageUri).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("pageProfileImage").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(AddPagePostImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick1);
            }
        });

        Image2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Selection Image 2"),Gallery_pick2);
        }
        });

        Image3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Selection Image 3"),Gallery_pick2);
            }
        });

        Image4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Selection Image 4"),Gallery_pick2);
            }
        });

        PagePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               UploadToFirebaseDatabase();
            }
        });

    }


    private void init() {
        Image1 = findViewById(R.id.add_page_post_image_1);
        Image2 = findViewById(R.id.add_page_post_image_2);
        Image3 = findViewById(R.id.add_page_post_image_3);
        Image4 = findViewById(R.id.add_page_post_image_4);
        ImageView1DeleteBtn = findViewById(R.id.add_image_delete);
        ImageView2DeleteBtn = findViewById(R.id.add_image2_delete);
        ImageView3DeleteBtn = findViewById(R.id.add_image3_delete);
        ImageView4DeleteBtn = findViewById(R.id.add_image4_delete);
        Image1ProgressBar = findViewById(R.id.Image1_progressBar);
        Image2ProgressBar = findViewById(R.id.Image2_progressBar);
        Image3ProgressBar = findViewById(R.id.Image3_progressBar);
        Image4ProgressBar = findViewById(R.id.Image4_progressBar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Gallery_pick1 && resultCode == RESULT_OK)
        {
            final Uri imageUri = data.getData();
            if (imageUri!=null) {


                loadingBar.setTitle("Image Load");
                loadingBar.setMessage("Load your selected image..");
                loadingBar.show();


                PagePostBtn.setVisibility(View.VISIBLE);
                PagePostBtn.setEnabled(true);

                final File file1 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,imageUri),new File(this.getCacheDir(),"temp1")));
                final Uri uri11 = Uri.fromFile(file1);



                //StorageReference filePath = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(imageUri.getLastPathSegment()+ ".jpg");                         //loadingBar.setTitle("Profile Image");
                StorageReference filePath1 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(imageUri.getLastPathSegment()+ ".jpg");
                filePath1.putFile(uri11).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image1DownloadUrl = uri.toString();
                                        uri1 = imageUri;

                                        UploadToFirebaseDatabase();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                    }


                                }
                            });

                        }
                        file1.delete();
                    }
                });

            }



        }/*else if (requestCode == Gallery_pick2 && resultCode == RESULT_OK )
        {
            ClipData clipData = data.getClipData();
            if (clipData!=null)
            {
               // Image1.setImageURI(clipData.getItemAt(0).getUri());
               // Image2.setImageURI(clipData.getItemAt(1).getUri());
                Image1Url = clipData.getItemAt(0).getUri();
                Image2Url = clipData.getItemAt(1).getUri();
                PagePostBtn.setVisibility(View.VISIBLE);
                PagePostBtn.setEnabled(true);

                loadingBar.setTitle("Image Load");
                loadingBar.setMessage("Load your selected image..");
                loadingBar.show();


                for (int i = 0; i < clipData.getItemCount(); ++i)
                {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    Log.e("MAS IMGS: ",uri.toString());
                }


                final File file1 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image1Url),new File(this.getCacheDir(),"temp1")));
                final Uri uri11 = Uri.fromFile(file1);



                //StorageReference filePath = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(imageUri.getLastPathSegment()+ ".jpg");                         //loadingBar.setTitle("Profile Image");
                StorageReference filePath1 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image1Url.getLastPathSegment()+ ".jpg");
                filePath1.putFile(uri11).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image1DownloadUrl = uri.toString();
                                        uri1 = Image1Url;

                                        UploadToFirebaseDatabase();
                                    }


                                }
                            });

                        }
                        file1.delete();
                    }
                });

                final File file2 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image2Url),new File(this.getCacheDir(),"temp2")));
                final Uri uri22 = Uri.fromFile(file2);
                StorageReference filePath2 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image2Url.getLastPathSegment()+ ".jpg");
                filePath2.putFile(uri22).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image2DownloadUrl = uri.toString();
                                        uri2 = Image2Url;

                                        UploadToFirebaseDatabase();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                    }

                                }
                            });



                        }
                        file2.delete();


                    }
                });
            }

        }else if (requestCode == Gallery_pick3 && resultCode == RESULT_OK )
        {
            ClipData clipData = data.getClipData();
            if (clipData!=null)
            {
                Image1Url = clipData.getItemAt(0).getUri();
                Image2Url = clipData.getItemAt(1).getUri();
                Image3Url = clipData.getItemAt(2).getUri();
                PagePostBtn.setVisibility(View.VISIBLE);
                PagePostBtn.setEnabled(true);

                loadingBar.setTitle("Image Load");
                loadingBar.setMessage("Load your selected image..");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);

                for (int i = 0; i < clipData.getItemCount(); ++i)
                {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    Log.e("MAS IMGS: ",uri.toString());


                }

                final File file1 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image1Url),new File(this.getCacheDir(),"temp1")));
                final Uri uri11 = Uri.fromFile(file1);

                //StorageReference filePath = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(imageUri.getLastPathSegment()+ ".jpg");                         //loadingBar.setTitle("Profile Image");
                StorageReference filePath1 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image1Url.getLastPathSegment()+ ".jpg");
                filePath1.putFile(uri11).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image1DownloadUrl = uri.toString();
                                        uri1 = Image1Url;

                                        UploadToFirebaseDatabase();
                                    }


                                }
                            });

                        }
                        file1.delete();
                    }
                });

                final File file2 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image2Url),new File(this.getCacheDir(),"temp2")));
                final Uri uri22 = Uri.fromFile(file2);
                StorageReference filePath2 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image2Url.getLastPathSegment()+ ".jpg");
                filePath2.putFile(uri22).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image2DownloadUrl = uri.toString();
                                        uri2 = Image2Url;

                                        UploadToFirebaseDatabase();
                                    }


                                }
                            });



                        }
                        file2.delete();


                    }
                });


                final File file3 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image3Url),new File(this.getCacheDir(),"temp3")));
                final Uri uri33 = Uri.fromFile(file3);

                StorageReference filePath3 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image3Url.getLastPathSegment()+ ".jpg");
                filePath3.putFile(uri33).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image3DownloadUrl = uri.toString();
                                        uri3 = Image3Url;

                                        UploadToFirebaseDatabase();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                    }

                                }
                            });


                        }
                        file3.delete();
                    }
                });
            }

        }*/else if (requestCode == Gallery_pick2 && resultCode == RESULT_OK )
        {
            ClipData clipData = data.getClipData();
            if (clipData!=null)
            {
                Toast.makeText(AddPagePostActivity.this,"MultipleImage",Toast.LENGTH_SHORT).show();
                Image1Url = clipData.getItemAt(0).getUri();
                Image2Url = clipData.getItemAt(1).getUri();
                Image3Url = clipData.getItemAt(2).getUri();
                Image4Url = clipData.getItemAt(3).getUri();
                PagePostBtn.setVisibility(View.VISIBLE);
                PagePostBtn.setEnabled(true);

                loadingBar.setTitle("Image Load");
                loadingBar.setMessage("Load your selected image..");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(false);

                for (int i = 0; i < clipData.getItemCount(); ++i)
                {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    Log.e("MAS IMGS: ",uri.toString());
                }

                final File file1 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image1Url),new File(this.getCacheDir(),"temp1")));
                final Uri uri11 = Uri.fromFile(file1);


                StorageReference filePath1 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image1Url.getLastPathSegment()+ ".jpg");
                filePath1.putFile(uri11).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image1DownloadUrl = uri.toString();
                                        uri1 = Image1Url;

                                        UploadToFirebaseDatabase();
                                    }


                                }
                            });

                        }
                        file1.delete();
                    }
                });


                final File file2 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image2Url),new File(this.getCacheDir(),"temp2")));
                final Uri uri22 = Uri.fromFile(file2);
                StorageReference filePath2 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image2Url.getLastPathSegment()+ ".jpg");
                filePath2.putFile(uri22).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image2DownloadUrl = uri.toString();
                                        uri2 = Image2Url;

                                        UploadToFirebaseDatabase();
                                    }


                                }
                            });



                        }
                        file2.delete();


                    }
                });

                final File file3 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image3Url),new File(this.getCacheDir(),"temp3")));
                final Uri uri33 = Uri.fromFile(file3);

                StorageReference filePath3 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image3Url.getLastPathSegment()+ ".jpg");
                filePath3.putFile(uri33).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image3DownloadUrl = uri.toString();
                                        uri3 = Image3Url;

                                        UploadToFirebaseDatabase();
                                    }


                                }
                            });


                        }
                        file3.delete();
                    }
                });

                final File file4 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,Image4Url),new File(this.getCacheDir(),"temp4")));
                final Uri uri44 = Uri.fromFile(file4);

                StorageReference filePath4 = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(Image4Url.getLastPathSegment()+ ".jpg");
                filePath4.putFile(uri44).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {


                            Task<Uri> result = task.getResult().getMetadata().getReference().getDownloadUrl();


                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    if (task.isSuccessful())
                                    {
                                        Image4DownloadUrl = uri.toString();
                                        uri4 = Image4Url;

                                        UploadToFirebaseDatabase();
                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                    }

                                }
                            });

                        }
                        file4.delete();
                    }
                });
            }

        }



    }

    private void UploadToFirebaseDatabase() {

        if (Image1DownloadUrl!=null)
        {
            final Uri ImageUrl1 = uri1;
            //Image1.setImageURI(uri1);
            ImageView1DeleteBtn.setVisibility(View.VISIBLE);
            ImageView1DeleteBtn.setEnabled(true);
            DownloadUrl1 = Image1DownloadUrl;
            Picasso.get().load(DownloadUrl1).into(Image1);
            loadingBar.dismiss();
            ImageView1DeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(ImageUrl1.getLastPathSegment()+ ".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                ImageView1DeleteBtn.setVisibility(View.GONE);
                                ImageView1DeleteBtn.setEnabled(false);
                                DownloadUrl1 = "None";
                                Picasso.get().load(R.drawable.blank_cover_image).placeholder(R.drawable.blank_cover_image).into(Image1);
                            }
                        }
                    });
                }
            });
        }
        else
        {
            DownloadUrl1 = "None";
        }

        if (Image2DownloadUrl!=null)
        {
            final Uri ImageUrl2 = uri2;
            //Image2.setImageURI(uri2);
            ImageView2DeleteBtn.setVisibility(View.VISIBLE);
            ImageView2DeleteBtn.setEnabled(true);
            DownloadUrl2 = Image2DownloadUrl;
            Picasso.get().load(DownloadUrl2).into(Image2);
            ImageView2DeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(ImageUrl2.getLastPathSegment()+ ".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                ImageView2DeleteBtn.setVisibility(View.GONE);
                                ImageView2DeleteBtn.setEnabled(false);
                                DownloadUrl2 = "None";
                                Picasso.get().load(R.drawable.blank_cover_image).placeholder(R.drawable.blank_cover_image).into(Image2);
                            }
                        }
                    });
                }
            });
        }
        else
        {
            DownloadUrl2 = "None";
        }


        if (Image3DownloadUrl!=null)
        {
            final Uri ImageUrl3 = uri3;
            //Image3.setImageURI(uri3);
            ImageView3DeleteBtn.setVisibility(View.VISIBLE);
            ImageView3DeleteBtn.setEnabled(true);
            DownloadUrl3 = Image3DownloadUrl;
            Picasso.get().load(DownloadUrl3).into(Image3);
            ImageView3DeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(ImageUrl3.getLastPathSegment()+ ".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                ImageView3DeleteBtn.setVisibility(View.GONE);
                                ImageView3DeleteBtn.setEnabled(false);
                                DownloadUrl3 = "None";
                                Picasso.get().load(R.drawable.blank_cover_image).placeholder(R.drawable.blank_cover_image).into(Image3);
                            }
                        }
                    });
                }
            });

        }
        else
        {
            DownloadUrl3 = "None";
        }


        if (Image4DownloadUrl!=null)
        {
            final Uri ImageUrl4 = uri4;
            //Image4.setImageURI(uri4);
            ImageView4DeleteBtn.setVisibility(View.VISIBLE);
            ImageView4DeleteBtn.setEnabled(true);
            DownloadUrl4 = Image4DownloadUrl;
            Picasso.get().load(DownloadUrl4).into(Image4);
            ImageView4DeleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(ImageUrl4.getLastPathSegment()+ ".jpg").delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                ImageView4DeleteBtn.setVisibility(View.GONE);
                                ImageView4DeleteBtn.setEnabled(false);
                                DownloadUrl4 = "None";
                                Picasso.get().load(R.drawable.blank_cover_image).placeholder(R.drawable.blank_cover_image).into(Image4);
                            }
                        }
                    });
                }
            });
        }
        else
        {
            DownloadUrl4 = "None";
        }




        PagePostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                loadingBar.setTitle("New Post");
                loadingBar.setMessage("uploading your new post..");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);


                final String Description = PagePostText.getText().toString();
                final String StatusCondition = AddPostPageSpinner.getSelectedItem().toString();

                Calendar calForDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                String saveCurrentTime = currentTime.format(calForTime.getTime());

                String postRandomName = saveCurrentDate + saveCurrentTime;

                HashMap postsMap = new HashMap();
                postsMap.put("Image1",DownloadUrl1);
                postsMap.put("Image2",DownloadUrl2);
                postsMap.put("Image3",DownloadUrl3);
                postsMap.put("Image4",DownloadUrl4);
                postsMap.put("uid",PageUri);
                postsMap.put("date",saveCurrentDate);
                postsMap.put("time",saveCurrentTime);
                postsMap.put("description", Description );
                postsMap.put("counter",countPosts);
                postsMap.put("type","Page");
                postsMap.put("Condition",StatusCondition);
                postsMap.put("uiid",PageUri+postRandomName);

                PostReference.child(postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())

                        {
                            SendUserToHomeActivity();
                            loadingBar.dismiss();
                            Toast.makeText(AddPagePostActivity.this,"New Post Upload",Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(AddPagePostActivity.this,"Error occoured while updating your post" ,Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void checkInputs()
    {
        if (!TextUtils.isEmpty(PagePostText.getText().toString()))
        {
            PagePostBtn.setVisibility(View.VISIBLE);
            PagePostBtn.setEnabled(true);
        }
        else
        {
            PagePostBtn.setVisibility(View.INVISIBLE);
            PagePostBtn.setEnabled(false);
        }
    }

    /*private void UploadStatusToFirebaseDatabase()
    {
        loadingBar.setTitle("New Post");
        loadingBar.setMessage("uploading your new post..");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(true);

        PostReference.addValueEventListener(new ValueEventListener() {
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

        final String Description = PagePostText.getText().toString();
        final String StatusCondition = AddPostPageSpinner.getSelectedItem().toString();

        Calendar calForDate = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        String saveCurrentTime = currentTime.format(calForDate.getTime());

        String postRandomName = saveCurrentDate + saveCurrentTime;

        HashMap postsMap = new HashMap();
        postsMap.put("uid",PageUri);
        postsMap.put("date",saveCurrentDate);
        postsMap.put("time",saveCurrentTime);
        postsMap.put("description", Description );
        postsMap.put("counter",countPosts);
        postsMap.put("type","Page");
        postsMap.put("Condition",StatusCondition);
        postsMap.put("uiid",PageUri+postRandomName);

        PostReference.child(postRandomName).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful())

                {
                    SendUserToHomeActivity();
                    loadingBar.dismiss();
                    Toast.makeText(AddPagePostActivity.this,"New Post Upload",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(AddPagePostActivity.this,"Error occoured while updating your post" ,Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                }
            }
        });
    }*/

    private void SendUserToHomeActivity() {
        Intent intent = new Intent(AddPagePostActivity.this,PageHomeActivity.class);
        intent.putExtra("visit_user_id",PageUri);
        startActivity(intent);
        finish();
    }
}
