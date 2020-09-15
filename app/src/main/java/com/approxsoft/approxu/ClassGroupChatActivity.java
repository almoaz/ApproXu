package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.approxsoft.approxu.Adapter.GroupMessageAdapter;
import com.approxsoft.approxu.Model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClassGroupChatActivity extends AppCompatActivity {

    Toolbar mToolBar;
    DatabaseReference userReference,universityReference,RootReff;
    FirebaseAuth mAuth;
    String messageSenderID;
    CircleImageView groupChatUserProfileImage;
    ImageView sendMessagebtn;
    TextView userName, addOtherStudentCount;
    private GroupMessageAdapter groupMessageAdapter;
    private final List<Messages> messagesList = new ArrayList<>();
    private RecyclerView userMessageList;
    EditText userMessageInputs;
    private int AddOtherStudentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_group_chat);

        mAuth = FirebaseAuth.getInstance();
        messageSenderID = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(messageSenderID);
        RootReff = FirebaseDatabase.getInstance().getReference();



        mToolBar = findViewById(R.id.class_group_chat_bar);
        setSupportActionBar(mToolBar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("InflateParams") View action_bar_view = Objects.requireNonNull(layoutInflater).inflate(R.layout.class_group_chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);


        groupMessageAdapter = new GroupMessageAdapter(messagesList);
        userMessageList = findViewById(R.id.class_group_messages_list_of_users);
        userMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //linearLayoutManager.setStackFromEnd(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(groupMessageAdapter);

        groupChatUserProfileImage = findViewById(R.id.groupChat_user_profile_image);
        sendMessagebtn = findViewById(R.id.class_group_send_message_btn);
        userMessageInputs = findViewById(R.id.class_group_message_text);
        userName = findViewById(R.id.group_chat_custom_profile_name);
        addOtherStudentCount = findViewById(R.id.group_chat_custom_all_user);





        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                    universityReference = FirebaseDatabase.getInstance().getReference().child("All University").child(university);

                    universityReference.child("All Students").child(messageSenderID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists()){
                                String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                                String name = Objects.requireNonNull(dataSnapshot.child("fullName").getValue()).toString();
                                final String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                final String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                final String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();


                                userName.setText(name);
                                Picasso.get().load(image).into(groupChatUserProfileImage);

                                groupChatUserProfileImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final String visit_user_id = universityReference.child(departments+semester).child(group).getKey();
                                        Intent intent = new Intent(ClassGroupChatActivity.this,AllGroupFriendsListActivity.class);
                                        intent.putExtra("visit_user_Id", visit_user_id);
                                        startActivity(intent);
                                    }
                                });

                                universityReference.child(departments+semester).child(group).child("Message").addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                                    {
                                        if (dataSnapshot.exists()) {
                                            AddOtherStudentCount = (int) dataSnapshot.getChildrenCount();
                                            addOtherStudentCount.setText("you and "+(AddOtherStudentCount)+" other member added");

                                        } else {
                                            addOtherStudentCount.setText("");
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendMessagebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                SendMessage();
            }
        });

        FetchMessages();

    }


    private void FetchMessages()
    {
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                    universityReference = FirebaseDatabase.getInstance().getReference().child("All University").child(university);

                    universityReference.child("All Students").child(messageSenderID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.exists())
                            {
                                ////////////////////////

                                String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();

                                universityReference.child(departments+semester).child(group).child("Messages")
                                        .addChildEventListener(new ChildEventListener() {
                                            @Override
                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                                            {
                                                if (dataSnapshot.exists())
                                                {
                                                    Messages messages = dataSnapshot.getValue(Messages.class);
                                                    messagesList.add(messages);
                                                    //MessagesAdapter messagesAdapter = new MessagesAdapter(messagesList);
                                                    groupMessageAdapter.notifyDataSetChanged();
                                                    userMessageList.smoothScrollToPosition(Objects.requireNonNull(userMessageList.getAdapter()).getItemCount());

                                                }
                                            }

                                            @Override
                                            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                            }

                                            @Override
                                            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                            }

                                            @Override
                                            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });




                                //////////////////////
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

    private void SendMessage()
    {

        //updateUserStatus("online");

        final String messageText = userMessageInputs.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ClassGroupChatActivity.this,"Please type a message first....",Toast.LENGTH_SHORT).show();
        } else
        {



            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.exists())
                    {
                        String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();
                        universityReference = FirebaseDatabase.getInstance().getReference().child("All University").child(university);

                        universityReference.child("All Students").child(messageSenderID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists()){
                                    String departments = Objects.requireNonNull(dataSnapshot.child("Departments").getValue()).toString();
                                    String semester = Objects.requireNonNull(dataSnapshot.child("Semester").getValue()).toString();
                                    String group = Objects.requireNonNull(dataSnapshot.child("Group").getValue()).toString();

                                    ///////////////////////////

                                    final String message_sender_reff = departments + semester + "/"+ group + "/" + "Message"+ "/"+ messageSenderID + "/" + "Messages";
                                    final String message_reciver_reff = departments + semester + "/"+ group + "/"+ "Messages";



                                    DatabaseReference user_message_key = universityReference.child(departments).child(semester).child(group).child(messageSenderID)
                                            .child("Messages").child("Messages").push();

                                    String message_pust_id = user_message_key.getKey();

                                    Calendar calForDate = Calendar.getInstance();
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                                    final String saveCurrentDate = currentDate.format(calForDate.getTime());

                                    Calendar calForTime = Calendar.getInstance();
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
                                    final String saveCurrentTime = currentTime.format(calForDate.getTime());

                                    Map messageTextBody = new HashMap();
                                    messageTextBody.put("message", messageText);
                                    messageTextBody.put("time", saveCurrentTime);
                                    messageTextBody.put("date", saveCurrentDate);
                                    messageTextBody.put("type", "text");
                                    messageTextBody.put("from", messageSenderID);


                                    Map messageBodyDetails = new HashMap();

                                    messageBodyDetails.put(message_sender_reff + "/" + message_pust_id , messageTextBody);
                                    messageBodyDetails.put(message_reciver_reff + "/" + message_pust_id , messageTextBody);

                                    universityReference.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if (task.isSuccessful()){
                                                userMessageInputs.setText("");

                                            }

                                        }
                                    });


                                    ////////////////////////////
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
    }

}
