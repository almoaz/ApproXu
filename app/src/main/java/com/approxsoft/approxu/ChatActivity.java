package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.approxsoft.approxu.Adapter.MessagesAdapter;
import com.approxsoft.approxu.Model.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolBar;
    private ImageView sendMessagebtn,onlineIcon,StarBtn, ImageSendView, ImageDelete, VideoCallBtn;
    private EditText userMessageInputs;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    String messageReciverID, messageReciverName, messageSenderID,saveCurrentDate,saveCurrentTime, type,typeCondition, From, typeFrom,Image1DownloadUrl;
    private LinearLayout linearLayout1, linearLayout2,linearLayout3,linearLayout4;
    Uri uri1;
    private ProgressDialog loadingBar;
    private RelativeLayout relativeLayout;
    private int message = 0;
    public static final int Gallery_pick = 1;
    TextView reciverName, userLastSeen, DownloadUrl1,DownloadUrl;
    private CircleImageView reciverProfileimage;
    private DatabaseReference RootReff,UserReff, reference,reference2,RootRef, PageReference;
    private FirebaseAuth mAuth;
    ValueEventListener seenListener;
    StorageReference postImagesReff;
    ConstraintLayout constraintLayout;
    RelativeLayout ImojiListLayout;
    ImageView imojiListDelete,imoji1,imoji2,imoji3,imoji4,imoji5,imoji6,imoji7,imoji8,imoji9,imoji10;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth = FirebaseAuth.getInstance();
        messageSenderID = getIntent().getExtras().get("userId").toString();

        RootReff = FirebaseDatabase.getInstance().getReference();
        RootRef = FirebaseDatabase.getInstance().getReference();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");
        postImagesReff = FirebaseStorage.getInstance().getReference();
        messageReciverID = getIntent().getExtras().get("visit_user_id").toString();
        type = getIntent().getExtras().get("type").toString();
        From = getIntent().getExtras().get("from").toString();

        updateUserStatus("online");




        IntializeFields();




        DisplayReceiverInfo();
        typeCondition = "user";
        typeFrom = From;

        sendMessagebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DownloadUrl.setText(Image1DownloadUrl);
                SendMessage();

            }
        });



        RootRef.child("All Users").child(messageReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String type = dataSnapshot.child("userState").child("type").getValue().toString();
                    final String lastDate = dataSnapshot.child("userState").child("date").getValue().toString();
                    final String lastTime = dataSnapshot.child("userState").child("time").getValue().toString();

                    if (dataSnapshot.hasChild("VisitId"))
                    {

                        String visitor = dataSnapshot.child("VisitId").getValue().toString();

                        if (visitor.equals(messageSenderID))
                        {
                            userLastSeen.setText("Typing...");

                        }
                    }else if (type.equals("online"))
                    {
                        userLastSeen.setText("online");
                        onlineIcon.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        userLastSeen.setText("last seen: " + lastTime + " " + lastDate);
                        onlineIcon.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        FetchMessages();
        //seenMessage();




    }


    /**private void seenMessage(final String messageReciverID)
    {
        reference = FirebaseDatabase.getInstance().getReference().child("Messages").child(messageReciverID).child(messageSenderID);
        reference2 = FirebaseDatabase.getInstance().getReference().child("Messages").child(messageSenderID).child(messageReciverID);
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Messages messages = snapshot.getValue(Messages.class);
                    if (messages.getFrom().equals(messageReciverID)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("seenType", true);

                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/




    private void FetchMessages()
    {
        RootReff.child("Messages").child(messageReciverID).child(messageSenderID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        if (dataSnapshot.exists())
                        {
                            Messages messages = dataSnapshot.getValue(Messages.class);

                            messagesList.add(messages);
                            //MessagesAdapter messagesAdapter = new MessagesAdapter(messagesList);
                            messagesAdapter.notifyDataSetChanged();
                            userMessageList.smoothScrollToPosition(userMessageList.getAdapter().getItemCount());

                            if (messages.getFrom().equals(messageSenderID)){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("seenType", "true");
                                dataSnapshot.getRef().updateChildren(hashMap);
                            }
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
    }

    /** private void seenMessage()
    {
        RootRef.child("Messages").child(messageReciverID).child(messageSenderID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        if (dataSnapshot.exists())
                        {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            if (messages.getFrom().equals(messageReciverID)){
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("seenType", true);

                                dataSnapshot.getRef().updateChildren(hashMap);
                            }

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
    }*/

    private void SendMessage()
    {



        constraintLayout.setVisibility(View.GONE);
        final String messageText = userMessageInputs.getText().toString();

            final String message_reciver_reff = "Messages/" + messageReciverID + "/" + messageSenderID;
            final String message_sender_reff = "Messages/" + messageSenderID + "/" + messageReciverID;


            DatabaseReference user_message_key = RootReff.child("Messages").child(messageSenderID).child(messageReciverID).child(messageReciverID).push();

            String message_pust_id = user_message_key.getKey();

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
            saveCurrentTime = currentTime.format(calForDate.getTime());

            final String downloadurl = DownloadUrl.getText().toString();
            final String Type = type;
            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", typeCondition);
            messageTextBody.put("seenType", "false");
            messageTextBody.put("fileUrl",downloadurl);
            messageTextBody.put("from", messageSenderID);

            Map messageTextBody2 = new HashMap();
            messageTextBody2.put("message", messageText);
            messageTextBody2.put("time", saveCurrentTime);
            messageTextBody2.put("date", saveCurrentDate);
            messageTextBody2.put("type", typeFrom);
            messageTextBody2.put("seenType", "false");
            messageTextBody2.put("fileUrl",downloadurl);
            messageTextBody2.put("from", messageSenderID);

            UserReff.child(messageSenderID).child("VisitId").removeValue();

            Map messageBodyDetails = new HashMap();


            messageBodyDetails.put(message_reciver_reff + "/" + message_pust_id , messageTextBody);
            messageBodyDetails.put(message_sender_reff + "/" + message_pust_id , messageTextBody2);

            RootReff.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                  if (task.isSuccessful()){
                      userMessageInputs.setText("");
                      DownloadUrl.setText("");

                      Map sendingMap = new HashMap();
                      sendingMap.put("message", messageText);
                      sendingMap.put("time", saveCurrentTime);
                      sendingMap.put("date", saveCurrentDate);
                      sendingMap.put("condition", "true");
                      sendingMap.put("type", typeCondition);

                      RootReff.child("messages").child(messageSenderID).child(messageReciverID).updateChildren(sendingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {

                              if (task.isSuccessful())
                              {
                                  Map reciverMap = new HashMap();
                                  reciverMap.put("message", messageText);
                                  reciverMap.put("time", saveCurrentTime);
                                  reciverMap.put("date", saveCurrentDate);
                                  reciverMap.put("condition", "false");
                                  reciverMap.put("type", typeFrom);
                                  RootReff.child("messages").child(messageReciverID).child(messageSenderID).updateChildren(reciverMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if (task.isSuccessful())
                                          {

                                             if (Type.equals("user"))
                                             {
                                                 Map notificationMap = new HashMap();
                                                 notificationMap.put("time",saveCurrentTime);
                                                 notificationMap.put("message", messageText);
                                                 notificationMap.put("type", Type);

                                                 RootReff.child("All Users").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                     @Override
                                                     public void onComplete(@NonNull Task task)
                                                     {
                                                         if (task.isSuccessful())
                                                         {
                                                             //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 });
                                             }
                                             else if (Type.equals("page"))
                                             {
                                                 Map notificationMap = new HashMap();
                                                 notificationMap.put("time",saveCurrentTime);
                                                 notificationMap.put("message", messageText);
                                                 notificationMap.put("type", Type);

                                                 RootReff.child("All Pages").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                     @Override
                                                     public void onComplete(@NonNull Task task)
                                                     {
                                                         if (task.isSuccessful())
                                                         {
                                                             //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                         }
                                                     }
                                                 });
                                             }

                                          }
                                          else
                                          {
                                              String message = task.getException().getMessage();
                                              Toast.makeText(ChatActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                              userMessageInputs.setText("");
                                          }
                                      }
                                  });
                              }

                          }
                      });
                  }

                }
            });

    }

    private void SendImoji()
    {



        final String message_reciver_reff = "Messages/" + messageReciverID + "/" + messageSenderID;
        final String message_sender_reff = "Messages/" + messageSenderID + "/" + messageReciverID;


        DatabaseReference user_message_key = RootReff.child("Messages").child(messageSenderID).child(messageReciverID).child(messageReciverID).push();

        String message_pust_id = user_message_key.getKey();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        saveCurrentTime = currentTime.format(calForDate.getTime());

        final String downloadurl1 = DownloadUrl1.getText().toString();
        final String Type = type;
        Map messageTextBody = new HashMap();
        messageTextBody.put("message", "");
        messageTextBody.put("time", saveCurrentTime);
        messageTextBody.put("date", saveCurrentDate);
        messageTextBody.put("type", typeCondition);
        messageTextBody.put("seenType", "false");
        messageTextBody.put("fileUrl",downloadurl1);
        messageTextBody.put("from", messageSenderID);

        Map messageTextBody2 = new HashMap();
        messageTextBody2.put("message", "");
        messageTextBody2.put("time", saveCurrentTime);
        messageTextBody2.put("date", saveCurrentDate);
        messageTextBody2.put("type", typeFrom);
        messageTextBody2.put("seenType", "false");
        messageTextBody2.put("fileUrl",downloadurl1);
        messageTextBody2.put("from", messageSenderID);

        UserReff.child(messageSenderID).child("VisitId").removeValue();

        Map messageBodyDetails = new HashMap();


        messageBodyDetails.put(message_reciver_reff + "/" + message_pust_id , messageTextBody);
        messageBodyDetails.put(message_sender_reff + "/" + message_pust_id , messageTextBody2);

        RootReff.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task)
            {
                if (task.isSuccessful()){
                    DownloadUrl1.setText("");

                    Map sendingMap = new HashMap();
                    sendingMap.put("message", "Imoji");
                    sendingMap.put("time", saveCurrentTime);
                    sendingMap.put("date", saveCurrentDate);
                    sendingMap.put("condition", "true");
                    sendingMap.put("type", typeCondition);

                    RootReff.child("messages").child(messageSenderID).child(messageReciverID).updateChildren(sendingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                Map reciverMap = new HashMap();
                                reciverMap.put("message", "Imoji");
                                reciverMap.put("time", saveCurrentTime);
                                reciverMap.put("date", saveCurrentDate);
                                reciverMap.put("condition", "false");
                                reciverMap.put("type", typeFrom);
                                RootReff.child("messages").child(messageReciverID).child(messageSenderID).updateChildren(reciverMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful())
                                        {

                                            if (Type.equals("user"))
                                            {
                                                Map notificationMap = new HashMap();
                                                notificationMap.put("time",saveCurrentTime);
                                                notificationMap.put("message", "imoji");
                                                notificationMap.put("type", Type);

                                                RootReff.child("All Users").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                            else if (Type.equals("page"))
                                            {
                                                Map notificationMap = new HashMap();
                                                notificationMap.put("time",saveCurrentTime);
                                                notificationMap.put("message", "imoji");
                                                notificationMap.put("type", Type);

                                                RootReff.child("All Pages").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task)
                                                    {
                                                        if (task.isSuccessful())
                                                        {
                                                            //Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                        }
                                        else
                                        {
                                            String message = task.getException().getMessage();
                                            Toast.makeText(ChatActivity.this,"Error:"+message,Toast.LENGTH_SHORT).show();
                                            userMessageInputs.setText("");
                                        }
                                    }
                                });
                            }

                        }
                    });
                }

            }
        });

    }





    private void updateUserStatus(String state)
    {
        String SaveCurrentDate, SaveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        SaveCurrentDate =currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm aa");
        SaveCurrentTime =currentTime.format(callForTime.getTime());


        Map CurrentStatemap = new HashMap();
        CurrentStatemap.put("time", SaveCurrentTime);
        CurrentStatemap.put("date", SaveCurrentDate);
        CurrentStatemap.put("type", state);

        UserReff.child(messageSenderID).child("userState")
                .updateChildren(CurrentStatemap);
    }

    private void DisplayReceiverInfo()
    {


        RootReff.child("All Users").child(messageReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                    final String name = dataSnapshot.child("fullName").getValue().toString();

                    reciverName.setText(name);


                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(reciverProfileimage);

                    reciverName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent pageIntent = new Intent(ChatActivity.this,PersonProfileActivity.class);
                            pageIntent.putExtra("visit_user_id",messageReciverID);
                            startActivity(pageIntent);
                        }
                    });

                    reciverProfileimage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            Intent pageIntent = new Intent(ChatActivity.this,PersonProfileActivity.class);
                            pageIntent.putExtra("visit_user_id",messageReciverID);
                            startActivity(pageIntent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void IntializeFields() {
        chatToolBar = findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolBar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater layoutInflater =(LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = layoutInflater.inflate(R.layout.chat_custom_bar,null);
        actionBar.setCustomView(action_bar_view);

        reciverName = findViewById(R.id.custom_profile_name);
        userLastSeen = findViewById(R.id.custom_user_last_seen);
        reciverProfileimage = findViewById(R.id.custom_profile_image);

        loadingBar = new ProgressDialog(this);
        constraintLayout = findViewById(R.id.image_constraint_layout);
        ImageDelete = findViewById(R.id.image_delete);
        StarBtn = findViewById(R.id.send_star_icon);
        DownloadUrl1 = findViewById(R.id.download_url1);
        DownloadUrl = findViewById(R.id.download_url);
        ImageSendView = findViewById(R.id.message_image_view);
        ///VideoCallBtn = findViewById(R.id.video_call_btn);

        relativeLayout = findViewById(R.id.chat_relative_layout);
        linearLayout1 = findViewById(R.id.chat_linerLayout1);
        linearLayout2 = findViewById(R.id.chat_linerLayout2);
        linearLayout3 = findViewById(R.id.chat_linerLayout3);
        linearLayout4 = findViewById(R.id.chat_linerLayout4);
        ImojiListLayout = findViewById(R.id.imoji_list_layout);
        imojiListDelete = findViewById(R.id.imoji_list_delete);
        imoji1 = findViewById(R.id.imoji1);
        imoji2 = findViewById(R.id.imoji2);
        imoji3 = findViewById(R.id.imoji3);
        imoji4 = findViewById(R.id.imoji4);
        imoji5 = findViewById(R.id.imoji5);
        imoji6 = findViewById(R.id.imoji6);
        imoji7 = findViewById(R.id.imoji7);
        imoji8 = findViewById(R.id.imoji8);
        imoji9 = findViewById(R.id.imoji9);
        imoji10 = findViewById(R.id.imoji10);

        messagesAdapter = new MessagesAdapter(messagesList);
        userMessageList = findViewById(R.id.messages_list_of_users);
        userMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messagesAdapter);


        sendMessagebtn = findViewById(R.id.send_message_btn);
        userMessageInputs = findViewById(R.id.message_text);
        onlineIcon = findViewById(R.id.message_online_status);


        userMessageInputs.addTextChangedListener(new TextWatcher() {
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

        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                linearLayout1.setVisibility(View.VISIBLE);
                linearLayout2.setVisibility(View.VISIBLE);
                linearLayout3.setVisibility(View.VISIBLE);
                linearLayout4.setVisibility(View.GONE);
                relativeLayout.setBackgroundResource(R.drawable.post_btn_background);
            }
        });

        linearLayout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, Gallery_pick);
            }
        });

        StarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2Ffull_gold_star.jpg?alt=media&token=a63e9ac8-460b-4360-9058-4e914c73a07e");
                SendImoji();
            }

        });
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ImojiListLayout.setVisibility(View.VISIBLE);


            }
        });
        imojiListDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImojiListLayout.setVisibility(View.GONE);
            }
        });
        imoji1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji1.png?alt=media&token=32f39fa2-3d73-4b86-88a2-12531ea52cb6");
                SendImoji();
            }
        });

        imoji2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji2.png?alt=media&token=05ec1bf7-2869-4294-9365-7c37932126df");
                SendImoji();
            }
        });

        imoji3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji3.png?alt=media&token=e1b117d9-e6fd-4010-bbb8-85875f6c89a2");
                SendImoji();
            }
        });

        imoji4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji4.png?alt=media&token=16d8bf58-c0c2-4902-a8ed-906a6e712f1f");
                SendImoji();
            }
        });

        imoji5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji5.png?alt=media&token=8db8293c-5b40-4aa8-a3a5-432e9537de20");
                SendImoji();
            }
        });

        imoji6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji6.png?alt=media&token=535c33be-06f4-468a-8fcb-3d0a1802a63b");
                SendImoji();
            }
        });

        imoji7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji7.png?alt=media&token=f19a1e54-6347-4173-b553-ba8a3ff5edee");
                SendImoji();
            }
        });

        imoji8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji8.png?alt=media&token=14499f5a-3936-451f-9f69-f522deff0bb6");
                SendImoji();
            }
        });

        imoji9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji9.png?alt=media&token=e387e317-8919-44c7-8a98-f93f9004b4ad");
                SendImoji();
            }
        });
        imoji10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadUrl1.setText("https://firebasestorage.googleapis.com/v0/b/approxu-76449.appspot.com/o/ApproXu%20imoji%20%2FImoji10.png?alt=media&token=dc1822ef-59c6-4fa8-967a-67c2916d94f9");
                SendImoji();
            }
        });

        ImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                postImagesReff.child("Profile Images").child(messageSenderID).child("MessageSendImage").child(uri1.getLastPathSegment()+ ".jpg").delete();
                DownloadUrl.setText("");
                constraintLayout.setVisibility(View.GONE);
                if (userMessageInputs.getText().toString().equals(""))
                {
                    relativeLayout.setBackgroundResource(R.drawable.post_btn_background);
                    StarBtn.setVisibility(View.VISIBLE);
                    linearLayout1.setVisibility(View.VISIBLE);
                    linearLayout2.setVisibility(View.VISIBLE);
                    linearLayout3.setVisibility(View.VISIBLE);
                    linearLayout4.setVisibility(View.GONE);
                    StarBtn.setVisibility(View.VISIBLE);
                    sendMessagebtn.setVisibility(View.GONE);
                }
                else
                {
                    relativeLayout.setBackgroundResource(R.drawable.registration_input_background);
                    StarBtn.setVisibility(View.GONE);
                    linearLayout1.setVisibility(View.GONE);
                    linearLayout2.setVisibility(View.GONE);
                    linearLayout3.setVisibility(View.GONE);
                    linearLayout4.setVisibility(View.VISIBLE);
                }
            }
        });

        /**VideoCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(ChatActivity.this,VideoCallActivity.class);
                intent.putExtra("visit_user_id",messageReciverID);
                startActivity(intent);
            }
        });*/

    }

    private void checkInputs()
    {
        if (!TextUtils.isEmpty(userMessageInputs.getText().toString()))
        {
            UserReff.child(messageSenderID).child("VisitId").setValue(messageReciverID);
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.GONE);
            linearLayout3.setVisibility(View.GONE);

            StarBtn.setVisibility(View.GONE);
            sendMessagebtn.setVisibility(View.VISIBLE);
            relativeLayout.setBackgroundResource(R.drawable.registration_input_background);
            linearLayout4.setVisibility(View.VISIBLE);


        }else
        {
            UserReff.child(messageSenderID).child("VisitId").removeValue();



            //relativeLayout2.setBackground(Drawable.createFromPath(String.valueOf(R.drawable.post_btn_background)));
            relativeLayout.setBackgroundResource(R.drawable.post_btn_background);

            sendMessagebtn.setVisibility(View.GONE);
            StarBtn.setVisibility(View.VISIBLE);
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.VISIBLE);
            linearLayout3.setVisibility(View.VISIBLE);
            linearLayout4.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == Gallery_pick && resultCode == RESULT_OK)
        {
            final Uri imageUri = data.getData();
            if (imageUri!=null) {

                loadingBar.setTitle("Image Load");
                loadingBar.setMessage("Load your selected image..");
                loadingBar.show();
                relativeLayout.setBackgroundResource(R.drawable.registration_input_background);
                StarBtn.setVisibility(View.GONE);
                linearLayout1.setVisibility(View.GONE);
                linearLayout2.setVisibility(View.GONE);
                linearLayout3.setVisibility(View.GONE);
                linearLayout4.setVisibility(View.GONE);

                final File file1 = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this,imageUri),new File(this.getCacheDir(),"temp1")));
                final Uri uri11 = Uri.fromFile(file1);



                //StorageReference filePath = postImagesReff.child("Profile Images").child(currentUserId).child("PagePostImage").child(imageUri.getLastPathSegment()+ ".jpg");                         //loadingBar.setTitle("Profile Image");
                StorageReference filePath1 = postImagesReff.child("Profile Images").child(messageSenderID).child("MessageSendImage").child(imageUri.getLastPathSegment()+ ".jpg");
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
                                        sendMessagebtn.setVisibility(View.VISIBLE);
                                        DownloadUrl.setText(Image1DownloadUrl);
                                        Picasso.get().load(Image1DownloadUrl).into(ImageSendView);
                                        constraintLayout.setVisibility(View.VISIBLE);
                                        loadingBar.dismiss();
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



        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
