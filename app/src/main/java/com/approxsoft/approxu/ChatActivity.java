package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.approxsoft.approxu.Adapter.MessagesAdapter;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolBar;
    private ImageView sendMessagebtn;
    private EditText userMessageInputs;
    private RecyclerView userMessageList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessagesAdapter messagesAdapter;
    private String messageReciverID, messageReciverName, messageSenderID,saveCurrentDate,saveCurrentTime;

    private TextView reciverName, userLastSeen;
    private CircleImageView reciverProfileimage;
    private DatabaseReference RootReff,UserReff;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();

        RootReff = FirebaseDatabase.getInstance().getReference();
        UserReff = FirebaseDatabase.getInstance().getReference().child("All Users");

        messageReciverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReciverName = getIntent().getExtras().get("userName").toString();



        IntializeFields();

        DisplayReceiverInfo();

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
        RootReff.child("Messages").child(messageSenderID).child(messageReciverID)
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

    private void SendMessage()
    {

        updateUserStatus("online");

        final String messageText = userMessageInputs.getText().toString();

        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(ChatActivity.this,"Please type a message first....",Toast.LENGTH_SHORT).show();
        } else
        {
            final String message_sender_reff = "Messages/" + messageSenderID + "/" + messageReciverID;
            final String message_reciver_reff = "Messages/" + messageReciverID + "/" + messageSenderID;

            DatabaseReference user_message_key = RootReff.child("Messages").child(messageSenderID).child(messageReciverID)
                    .child(messageReciverID).push();

            String message_pust_id = user_message_key.getKey();

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
            saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
            saveCurrentTime = currentTime.format(calForDate.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("time", saveCurrentTime);
            messageTextBody.put("date", saveCurrentDate);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", messageSenderID);


            Map messageBodyDetails = new HashMap();

            messageBodyDetails.put(message_sender_reff + "/" + message_pust_id , messageTextBody);
            messageBodyDetails.put(message_reciver_reff + "/" + message_pust_id , messageTextBody);

            RootReff.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                  if (task.isSuccessful()){
                      userMessageInputs.setText("");

                      Map sendingMap = new HashMap();
                      sendingMap.put("message", messageText);
                      sendingMap.put("time", saveCurrentTime);
                      sendingMap.put("date", saveCurrentDate);

                      RootReff.child("messages").child(messageSenderID).child(messageReciverID).updateChildren(sendingMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {

                              if (task.isSuccessful())
                              {
                                  Map reciverMap = new HashMap();
                                  reciverMap.put("message", messageText);
                                  reciverMap.put("time", saveCurrentTime);
                                  reciverMap.put("date", saveCurrentDate);
                                  RootReff.child("messages").child(messageReciverID).child(messageSenderID).updateChildren(reciverMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                      @Override
                                      public void onComplete(@NonNull Task<Void> task) {
                                          if (task.isSuccessful())
                                          {

                                              Map notificationMap = new HashMap();
                                              notificationMap.put("time",saveCurrentTime);
                                              notificationMap.put("message", messageText);

                                              RootReff.child("All Users").child(messageReciverID).child("MessageNotification").child(messageReciverID).child(messageSenderID).updateChildren(notificationMap).addOnCompleteListener(new OnCompleteListener() {
                                                  @Override
                                                  public void onComplete(@NonNull Task task)
                                                  {
                                                     if (task.isSuccessful())
                                                     {
                                                         Toast.makeText(ChatActivity.this,"Message send successfully...",Toast.LENGTH_SHORT).show();
                                                     }
                                                  }
                                              });

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
    }


    private void updateUserStatus(String state)
    {
        String SaveCurrentDate, SaveCurrentTime;

        Calendar callForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyy");
        SaveCurrentDate =currentDate.format(callForDate.getTime());

        Calendar callForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
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
        reciverName.setText(messageReciverName);

        RootReff.child("All Users").child(messageReciverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                    final String type = dataSnapshot.child("userState").child("type").getValue().toString();
                    final String lastDate = dataSnapshot.child("userState").child("date").getValue().toString();
                    final String lastTime = dataSnapshot.child("userState").child("time").getValue().toString();
                    if (type.equals("online"))
                    {
                        userLastSeen.setText("online");
                    }
                    else
                    {
                        userLastSeen.setText("last seen: " + lastTime + " " + lastDate);
                    }

                    Picasso.get().load(profileImage).placeholder(R.drawable.profile_holder).into(reciverProfileimage);
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

        messagesAdapter = new MessagesAdapter(messagesList);
        userMessageList = findViewById(R.id.messages_list_of_users);
        userMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        //linearLayoutManager.setStackFromEnd(true);
        userMessageList.setLayoutManager(linearLayoutManager);
        userMessageList.setAdapter(messagesAdapter);


        sendMessagebtn = findViewById(R.id.send_message_btn);
        userMessageInputs = findViewById(R.id.message_text);




    }
}
