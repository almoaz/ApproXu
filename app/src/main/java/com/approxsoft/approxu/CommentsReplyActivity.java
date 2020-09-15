package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.approxsoft.approxu.Model.CommentsReply;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsReplyActivity extends AppCompatActivity {

    Toolbar mToolBar;
    private ImageView postImage,starImage;
    private TextView postStatus, starCount, postDate,postTime,userFullName,UserName;
    private CircleImageView postProfileImage;

    String CommentPostKey, Current_user_id, databaseUserId, description, image, postRandomName,saveCurrentDate, saveCurrentTime,CurrentUserId;
    DatabaseReference clickPostReff,StarReff;
    FirebaseAuth mAuth;
    DatabaseReference imageReff,StarRef;
    int countStar;
    Boolean StarChecker = false;
    Boolean starChecker = false;

    private RecyclerView CommentsReplyList;
    private ImageView postCommentButton;
    private EditText CommentsInputsText;

    DatabaseReference Postreff,starReff,PostReference,ReplyReff,ClickPostReff;
    DatabaseReference usersReff;
    String Post_Key, current_user_id ;
    String founderNamePattern ="[*]+[<]+[#]+[>]+[/]+[2]+";
    private int countComments = 0;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_reply);

        Post_Key = getIntent().getExtras().get("PostKey").toString();
        CommentPostKey = getIntent().getExtras().get("visit_user_id").toString();
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        Current_user_id = mAuth.getCurrentUser().getUid();

        usersReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        Postreff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments").child(CommentPostKey);
        ReplyReff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments").child(CommentPostKey).child("replies");
        StarReff = FirebaseDatabase.getInstance().getReference().child("Post");



        ClickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments").child(CommentPostKey);
        clickPostReff = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments").child(CommentPostKey).child("replies");
        StarRef = FirebaseDatabase.getInstance().getReference().child("Post").child(Post_Key).child("Comments").child(CommentPostKey).child("replies");

        current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;
        imageReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(current_user_id).child("PostImage").child(postRandomName);

        mToolBar = findViewById(R.id.comments_reply_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Replies");


        postStatus = findViewById(R.id.post_comments_description);
        starImage = findViewById(R.id.display_star_btn);
        starCount = findViewById(R.id.display_no_of_star);
        postProfileImage = findViewById(R.id.post_comments_profile_image);
        postDate = findViewById(R.id.post_comment_date);
        postTime = findViewById(R.id.post_comment_time);
        userFullName = findViewById(R.id.post_comments_user_name);
        UserName = findViewById(R.id.post_comment_user_tool_bar_name);
        linearLayout = findViewById(R.id.post_reply_linear_layout);
        linearLayout.setVisibility(View.VISIBLE);


        Postreff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    description = dataSnapshot.child("comment").getValue().toString();
                    String date = dataSnapshot.child("date").getValue().toString();
                    String time = dataSnapshot.child("time").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();
                    postStatus.setText(description);

                    postDate.setText(date);
                    postTime.setText(time);


                    usersReff.child(databaseUserId).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                            String name = dataSnapshot.child("fullName").getValue().toString();

                            userFullName.setText(name);
                            Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(postProfileImage);
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

        userFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Postreff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String Uid = dataSnapshot.child("uid").getValue().toString();
                            final String visit_user_id = usersReff.child(Uid).getKey();


                            if (Uid.equals(Current_user_id))
                            {
                                Intent ProfileIntent = new Intent(CommentsReplyActivity.this, ProfileActivity.class);
                                startActivity(ProfileIntent);
                            }
                            else
                            {
                                Intent findProfileIntent = new Intent(CommentsReplyActivity.this, PersonProfileActivity.class);
                                findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(findProfileIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        postProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Postreff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            String Uid = dataSnapshot.child("uid").getValue().toString();
                            final String visit_user_id = usersReff.child(Uid).getKey();

                            if (Uid.equals(Current_user_id))
                            {
                                Intent ProfileIntent = new Intent(CommentsReplyActivity.this, ProfileActivity.class);
                                startActivity(ProfileIntent);
                            }
                            else
                            {
                                Intent findProfileIntent = new Intent(CommentsReplyActivity.this, PersonProfileActivity.class);
                                findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                startActivity(findProfileIntent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        CommentsReplyList = findViewById(R.id.comment_reply_list);
        CommentsReplyList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsReplyList.setLayoutManager(linearLayoutManager);



        CommentsInputsText =findViewById(R.id.comment_reply_text);
        postCommentButton = findViewById(R.id.comment_reply_btn);

        Postreff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    countComments = (int) dataSnapshot.child("replies").getChildrenCount();

                }
                else
                {
                    countComments = 0;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        postCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {


                if (!TextUtils.isEmpty(CommentsInputsText.getText().toString()))
                {
                    if (CommentsInputsText.getText().toString().matches(founderNamePattern)){
                        founderNameComments();
                    }
                    else {
                        Comments();
                    }
                }else {
                    Toast.makeText(CommentsReplyActivity.this,"Write somethings",Toast.LENGTH_SHORT).show();
                }
            }
        });

        CommentsInputsText.addTextChangedListener(new TextWatcher() {
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



        displayAllCommentsReply();


    }


    private void checkInputs() {
        if (!TextUtils.isEmpty(CommentsInputsText.getText().toString())) {
            postCommentButton.setEnabled(true);
        }
        else {
            postCommentButton.setEnabled(false);
        }
    }
    private void founderNameComments() {

        CommentsInputsText.setText("Mahfuj Salehin Moaz");


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final String RandomKeys = CurrentUserId + saveCurrentDate + saveCurrentTime;
        //postCommentButton.setEnabled(true);



        HashMap CommentsMap = new HashMap();
        CommentsMap.put("uid", CurrentUserId);
        CommentsMap.put("count", countComments);
        CommentsMap.put("reply", CommentsInputsText.getText().toString());
        CommentsMap.put("date", saveCurrentDate);
        CommentsMap.put("time", saveCurrentTime);

        Postreff.child("replies").child(RandomKeys).updateChildren(CommentsMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {

                            //Toast.makeText(CommentsActivity.this,"You have commented successfully...",Toast.LENGTH_SHORT).show();
                            postCommentButton.setEnabled(false);
                            CommentsInputsText.setText("");


                        }else
                        {
                            Toast.makeText(CommentsReplyActivity.this,"Error ocoured , Try again",Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


    private void Comments()
    {



        String commentText = CommentsInputsText.getText().toString();


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final String RandomKey = CurrentUserId + saveCurrentDate + saveCurrentTime;
        //postCommentButton.setEnabled(true);



        HashMap commentsMap = new HashMap();
        commentsMap.put("uid", CurrentUserId);
        commentsMap.put("count", countComments);
        commentsMap.put("reply", commentText);
        commentsMap.put("date", saveCurrentDate);
        commentsMap.put("time", saveCurrentTime);

        Postreff.child("replies").child(RandomKey).updateChildren(commentsMap)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful())
                        {

                            //Toast.makeText(CommentsActivity.this,"You have commented successfully...",Toast.LENGTH_SHORT).show();
                            postCommentButton.setEnabled(false);
                            CommentsInputsText.setText("");


                        }else
                        {
                            Toast.makeText(CommentsReplyActivity.this,"Error ocoured , Try again",Toast.LENGTH_SHORT).show();
                        }

                    }
                });

        Postreff.child("lastReply").child("commentReplyUId").setValue(RandomKey);
    }


    private void displayAllCommentsReply() {
        Query commentsQuerry = ReplyReff.orderByChild("count");
        FirebaseRecyclerOptions<CommentsReply> options= new FirebaseRecyclerOptions.Builder<CommentsReply>().setQuery(commentsQuerry,CommentsReply.class).build();

        FirebaseRecyclerAdapter<CommentsReply, CommentsReplyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<CommentsReply,CommentsReplyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentsReplyActivity.CommentsReplyViewHolder holder, int position, @NonNull CommentsReply model) {


                final String commentsReplyPostKey = getRef(position).getKey();

                Calendar calForDate = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                String saveCurrentDate = currentDate.format(calForDate.getTime());

                Calendar calForTime = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
                String saveCurrentTime = currentTime.format(calForTime.getTime());

                if (model.getDate().equals(saveCurrentDate))
                {
                    holder.date.setText("Today at "+ model.getTime());

                    if (model.getTime().equals(saveCurrentTime))
                    {
                        holder.date.setText("Just now");
                    }
                }
                else
                {
                    holder.date.setText(model.getDate());
                }

                //holder.time.setText(model.getTime());
                holder.reply.setText(model.getReply());


                holder.setStarButtonStatus(commentsReplyPostKey);

                final String redText =  holder.reply.getText().toString();

                if (redText.equals("Mahfuj Salehin Moaz"))
                {
                    holder.reply.setTextColor(Color.RED);

                }

                else if (redText.contains("Congratulation"))
                {
                    holder.reply.setTextColor(Color.BLUE);
                }
                else if (redText.contains("http") && redText.length() >= 13)
                {
                    holder.reply.setTextColor(Color.GRAY);
                    holder.reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(redText));
                            startActivity(intent);
                        }
                    });
                    holder.UrlImage.setVisibility(View.VISIBLE);
                    holder.UrlImage.getSettings().setJavaScriptEnabled(true);
                    //holder.UrlImage.setWebViewClient(new WebViewClient());
                    //holder.UrlImage.setWebChromeClient(new WebChromeClient());
                    holder.UrlImage.loadUrl(redText);

                }
                else if ((redText.contains("www.") || redText.contains("WWW.")) && redText.length() >= 6)
                {
                    holder.reply.setTextColor(Color.GRAY);
                    holder.reply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(redText));
                            startActivity(intent);
                        }
                    });

                    holder.UrlImage.setVisibility(View.VISIBLE);
                      holder.UrlImage.getSettings().setJavaScriptEnabled(true);
                      holder.UrlImage.setWebViewClient(new WebViewClient());
                      holder.UrlImage.setWebChromeClient(new WebChromeClient());
                      holder.UrlImage.loadUrl("http://"+redText);


                }



                ReplyReff.child(commentsReplyPostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            final String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                            usersReff.child(Uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                {
                                    String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                    final String name = dataSnapshot.child("fullName").getValue().toString();

                                    holder.username.setText(name);
                                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(holder.profileImage);
                                    if (Uid.equals(current_user_id))
                                    {
                                        holder.profileImage.setBorderWidth(2);
                                        holder.profileImage.setBorderColor(Color.BLUE);
                                    }

                                    holder.replyBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            CommentsInputsText.setText("@"+name);
                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            if (Uid.equals(current_user_id))
                            {
                                holder.replyBtn.setVisibility(View.INVISIBLE);
                            }




                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Toast.makeText(CommentsReplyActivity.this,"Data load",Toast.LENGTH_SHORT).show();

                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReplyReff.child(commentsReplyPostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = dataSnapshot.child("uid").getValue().toString();
                                    final String visit_user_id = usersReff.child(Uid).getKey();

                                    if (Uid.equals(Current_user_id))
                                    {
                                        Intent ProfileIntent = new Intent(CommentsReplyActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(CommentsReplyActivity.this, PersonProfileActivity.class);
                                        findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                        startActivity(findProfileIntent);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });



                holder.profileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReplyReff.child(commentsReplyPostKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    String Uid = dataSnapshot.child("uid").getValue().toString();
                                    final String visit_user_id = usersReff.child(Uid).getKey();

                                    if (Uid.equals(Current_user_id))
                                    {
                                        Intent ProfileIntent = new Intent(CommentsReplyActivity.this, ProfileActivity.class);
                                        startActivity(ProfileIntent);
                                    }
                                    else
                                    {
                                        Intent findProfileIntent = new Intent(CommentsReplyActivity.this, PersonProfileActivity.class);
                                        findProfileIntent.putExtra("visit_user_id", visit_user_id);
                                        startActivity(findProfileIntent);
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
                    public void onClick(View v)
                    {
                        starChecker = true;

                        StarRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (starChecker.equals(true))
                                {
                                    if (dataSnapshot.child(Objects.requireNonNull(commentsReplyPostKey)).child("ReplyStar").child(commentsReplyPostKey).hasChild(CurrentUserId))
                                    {
                                        StarRef.child(commentsReplyPostKey).child("ReplyStar").child(commentsReplyPostKey).child(CurrentUserId).removeValue();
                                        starChecker = false;
                                    }
                                    else
                                    {
                                        StarRef.child(commentsReplyPostKey).child("ReplyStar").child(commentsReplyPostKey).child(CurrentUserId).setValue(true);
                                        starChecker = false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });

                ClickPostReff.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {

                        if (dataSnapshot.exists()){
                            String userUid = dataSnapshot.child("uid").getValue().toString();

                            if (userUid.equals(Current_user_id))
                            {
                                holder.deleteCommentsBtn.setVisibility(View.VISIBLE);
                                holder.editCommentsBtn.setVisibility(View.INVISIBLE);
                                holder.deleteCommentsBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        StarRef.child(Objects.requireNonNull(commentsReplyPostKey)).child("ReplyStar").child(commentsReplyPostKey).removeValue();
                                        ClickPostReff.child("replies").child(commentsReplyPostKey).removeValue();

                                    }
                                });
                            }
                            else
                            {
                                ClickPostReff.child("replies").child(commentsReplyPostKey).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.exists())
                                        {


                                            String uid = dataSnapshot.child("uid").getValue().toString();

                                            if ((Current_user_id).equals(uid))
                                            {
                                                holder.deleteCommentsBtn.setVisibility(View.INVISIBLE);
                                                holder.editCommentsBtn.setVisibility(View.VISIBLE);
                                                holder.editCommentsBtn.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        AlertDialog.Builder builder = new AlertDialog.Builder(CommentsReplyActivity.this);

                                                        final EditText inputFiled = new EditText(CommentsReplyActivity.this);
                                                        inputFiled.setText(holder.reply.getText().toString());
                                                        inputFiled.setPadding(16,10,16,10);
                                                        builder.setView(inputFiled);


                                                        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which)
                                                            {
                                                                Toast.makeText(CommentsReplyActivity.this,"Reply Update Successfully",Toast.LENGTH_SHORT).show();
                                                                ClickPostReff.child("replies").child(commentsReplyPostKey).child("reply").setValue(inputFiled.getText().toString());
                                                                Toast.makeText(CommentsReplyActivity.this,"Reply Update Successfully",Toast.LENGTH_SHORT).show();

                                                            }
                                                        });

                                                        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which)
                                                            {
                                                                StarRef.child(Objects.requireNonNull(Post_Key)).child("ReplyStar").child(commentsReplyPostKey).removeValue();
                                                                ClickPostReff.child("replies").child(commentsReplyPostKey).removeValue();
                                                            }
                                                        });

                                                        Dialog dialog = builder.create();
                                                        dialog.show();
                                                        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.background_light);
                                                    }
                                                });
                                            }else
                                            {
                                                holder.deleteCommentsBtn.setVisibility(View.INVISIBLE);
                                                holder.editCommentsBtn.setVisibility(View.INVISIBLE);
                                            }




                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }


                        }




                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }




            @NonNull
            @Override
            public CommentsReplyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.comments_reply_layout, viewGroup, false);
                CommentsReplyViewHolder commentsReplyViewHolder = new CommentsReplyViewHolder(view);
                return commentsReplyViewHolder;
            }
        };

        CommentsReplyList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        firebaseRecyclerAdapter.notifyDataSetChanged();
    }


    public  class CommentsReplyViewHolder extends RecyclerView.ViewHolder
    {
        View mView;
        TextView username,date,time,reply,DisplayNoOfStar,deleteCommentsBtn, editCommentsBtn, replyBtn;
        ImageView StarPostBtn, CommentsPostBtn;
        WebView UrlImage;
        CircleImageView profileImage;

        public CommentsReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.comment_reply_user_name);
            date = itemView.findViewById(R.id.comment_reply_date);
            reply = itemView.findViewById(R.id.comment_reply_text);
            profileImage = itemView.findViewById(R.id.comment_reply_profile_image);
            StarPostBtn = itemView.findViewById(R.id.comments_reply_star_btn);
            DisplayNoOfStar = itemView.findViewById(R.id.comments_reply_no_of_star);
            deleteCommentsBtn = itemView.findViewById(R.id.delete_reply_comments_button);
            editCommentsBtn = itemView.findViewById(R.id.edit_reply_comments_button);
            replyBtn = itemView.findViewById(R.id.reply_reply_button);
            UrlImage = itemView.findViewById(R.id.load_url_image);




        }


        void setStarButtonStatus(final String commentsReplyPostKey)
        {
            StarRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.child(commentsReplyPostKey).child("ReplyStar").child(commentsReplyPostKey).hasChild(current_user_id))
                    {
                        countStar = (int) dataSnapshot.child(commentsReplyPostKey).child("ReplyStar").child(commentsReplyPostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.full_gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar)+(" Star"));
                    }else {
                        countStar = (int) dataSnapshot.child(commentsReplyPostKey).child("ReplyStar").child(commentsReplyPostKey).getChildrenCount();
                        StarPostBtn.setImageResource(R.drawable.gold_star);
                        DisplayNoOfStar.setText(Integer.toString(countStar)+(" Star"));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }




    }
}
