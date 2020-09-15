package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.approxsoft.approxu.Model.MessageData;
import com.approxsoft.approxu.Model.Users;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    private RecyclerView myFriendList;
    private RecyclerView friendsMessageList;
    private CircleImageView messageProfileImage;
    private TextView messageUserName;

    private FirebaseAuth mAuth;
    private String currentUserId,userId, userType;
    private DatabaseReference userReff, FriendsReff, MessageReff,messageReff, PageReference;
    private Toolbar mToolBar;
    SwipeRefreshLayout refreshLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userId = getIntent().getExtras().get("userId").toString();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        FriendsReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Friends").child(currentUserId);
        MessageReff = FirebaseDatabase.getInstance().getReference().child("messages").child(userId);
        messageReff = FirebaseDatabase.getInstance().getReference().child("messages");
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");

        mToolBar = findViewById(R.id.message_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");


        messageProfileImage = findViewById(R.id.message_profile_image);
        messageUserName = findViewById(R.id.message_user_name);
        refreshLayout = findViewById(R.id.message_refresh);


        myFriendList = findViewById(R.id.message_friends_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        myFriendList.setLayoutManager(linearLayoutManager);

        friendsMessageList = findViewById(R.id.friends_message_view_list);
        friendsMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setOrientation(RecyclerView.VERTICAL);
        friendsMessageList.setLayoutManager(linearLayoutManager2);




        DisplayAllFriends();

        DisplayAllFriendsMessage();

        updateUserStatus("online");

        userReff.child(currentUserId).child("MessageNotification").child(currentUserId).removeValue();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                // startActivity(intent);
                //Animatoo.animateFade(HomeActivity.this);
                DisplayAllFriends();

                DisplayAllFriendsMessage();
                refreshLayout.setRefreshing(false);

            }
        });




        userReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                Users users = dataSnapshot.getValue(Users.class);
                messageUserName.setText(users.getFullName());

                if (users.getProfileImage().equals("default"))
                {
                    messageProfileImage.setImageResource(R.drawable.profile_icon);

                }else
                {
                    Picasso.get().load(users.getProfileImage()).into(messageProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        userReff.child(currentUserId).child("userState")
                .updateChildren(CurrentStatemap);
    }


    private void DisplayAllFriends() {

        Query query = FriendsReff.orderByChild("date"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<Friends> options = new FirebaseRecyclerOptions.Builder<Friends>().setQuery(query, Friends.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<Friends, MessageActivity.FriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageActivity.FriendViewHolder friendsViewHolder, final int position, @NonNull Friends friends) {


                final String usersIDs = getRef(position).getKey();
                //updateUserStatus("online");




                userReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            final String userName = dataSnapshot.child("fullName").getValue().toString();
                            final String profileimage = dataSnapshot.child("profileImage").getValue().toString();
                            final String type;

                            if (dataSnapshot.hasChild("userState"))
                            {
                                type = dataSnapshot.child("userState").child("type").getValue().toString();

                                if (type.equals("online"))
                                {
                                    friendsViewHolder.onlineStatus.setVisibility(View.VISIBLE);
                                }
                                else
                                {
                                    friendsViewHolder.onlineStatus.setVisibility(View.INVISIBLE);
                                }

                            }

                            friendsViewHolder.setFullName(userName);
                            friendsViewHolder.setProfileImage(getApplicationContext(), profileimage);

                            friendsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    CharSequence options[] = new CharSequence[]
                                            {
                                                    userName + "'s Profile",
                                                    "Send Message"
                                            };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MessageActivity.this);
                                    builder.setTitle("Select Option");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            if (which == 0)
                                            {
                                                Intent profileIntent = new Intent(MessageActivity.this,PersonProfileActivity.class);
                                                profileIntent.putExtra("visit_user_id",usersIDs);
                                                startActivity(profileIntent);
                                            }
                                            if (which == 1)
                                            {
                                                Intent chatIntent = new Intent(MessageActivity.this,ChatActivity.class);
                                                chatIntent.putExtra("visit_user_id",usersIDs);
                                                chatIntent.putExtra("userId",currentUserId);
                                                chatIntent.putExtra("userName",userName);
                                                chatIntent.putExtra("type","user");
                                                chatIntent.putExtra("from","user");
                                                startActivity(chatIntent);
                                            }
                                        }
                                    });
                                    builder.show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent ,false);
                return new FriendViewHolder(view);
            }
        };
        adapter.startListening();
        myFriendList.setAdapter(adapter);
    }
    public class FriendViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView onlineStatus;

        public FriendViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            onlineStatus = itemView.findViewById(R.id.online_status);
            //onlineStatus.setVisibility(View.VISIBLE);
        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.message_friends_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.message_friends_name);
            myName.setText(fullName);
        }

    }

    private void DisplayAllFriendsMessage() {

        Query query2 = MessageReff.orderByChild("time"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<MessageData> options = new FirebaseRecyclerOptions.Builder<MessageData>().setQuery(query2, MessageData.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<MessageData, MessageActivity.MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final MessageActivity.MessageViewHolder messageViewHolder, final int position, @NonNull final MessageData messageData) {



                final String usersIDs = getRef(position).getKey();



                MessageReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        //final String id = messageViewHolder.setFrom(messageData.getFrom());
                        if (dataSnapshot.exists())
                        {
                            String condition = dataSnapshot.child("condition").getValue().toString();
                            String type = dataSnapshot.child("type").getValue().toString();

                           if (type.equals("user"))
                           {

                               userReff.child(usersIDs).addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.exists()) {
                                           final String userName = dataSnapshot.child("fullName").getValue().toString();
                                           final String profileimage = dataSnapshot.child("profileImage").getValue().toString();

                                           messageViewHolder.setFullName(userName);
                                           messageViewHolder.setProfileImage(getApplicationContext(), profileimage);

                                           messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v)
                                               {
                                                   MessageReff.child(usersIDs).child("condition").setValue("true");

                                                   Intent chatIntent = new Intent(MessageActivity.this,ChatActivity.class);
                                                   chatIntent.putExtra("visit_user_id",usersIDs);
                                                   chatIntent.putExtra("userId",userId);
                                                   chatIntent.putExtra("type","user");
                                                   chatIntent.putExtra("from","user");
                                                   startActivity(chatIntent);
                                               }


                                           });
                                       }

                                   }



                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });

                               if (condition.equals("false"))
                               {
                                   messageViewHolder.messageLayout.setBackgroundColor(Color.rgb(241,242,245));
                                   messageViewHolder.setMessage(messageData.getMessage());
                                   //messageViewHolder.setDate(messageData.getDate());
                                   messageViewHolder.setTime(messageData.getTime());
                               }
                               else if (condition.equals("true"))
                               {

                                   messageViewHolder.setMessage(messageData.getMessage());
                                   // messageViewHolder.setDate(messageData.getDate());
                                   messageViewHolder.setTime(messageData.getTime());
                               }

                               userReff.child(currentUserId).child("Friends").child(currentUserId).addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.hasChild(usersIDs))
                                       {
                                           messageViewHolder.messageDeleteBtn.setVisibility(View.GONE);
                                           messageViewHolder.messageDeleteBtn.setEnabled(false);
                                       }
                                       else
                                       {
                                           messageViewHolder.Time.setVisibility(View.GONE);
                                           messageViewHolder.messageDeleteBtn.setVisibility(View.VISIBLE);
                                           messageViewHolder.messageDeleteBtn.setEnabled(true);


                                           messageViewHolder.messageDeleteBtn.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   messageReff.child(currentUserId).child(usersIDs).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                       @Override
                                                       public void onComplete(@NonNull Task<Void> task)
                                                       {
                                                           if (task.isSuccessful())
                                                           {
                                                               messageReff.child(usersIDs).child(currentUserId).removeValue();

                                                           }
                                                       }
                                                   });
                                               }
                                           });
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });


                           }else if (type.equals("page"))
                           {
                               PageReference.child(usersIDs).addValueEventListener(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       if (dataSnapshot.exists()) {
                                           final String userName = dataSnapshot.child("pageName").getValue().toString();
                                           final String profileimage = dataSnapshot.child("pageProfileImage").getValue().toString();

                                           messageViewHolder.setFullName(userName);
                                           messageViewHolder.setProfileImage(getApplicationContext(), profileimage);

                                           messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v)
                                               {
                                                   MessageReff.child(usersIDs).child("condition").setValue("true");

                                                   Intent chatIntent = new Intent(MessageActivity.this,PageChatActivity.class);
                                                   chatIntent.putExtra("visit_user_id",usersIDs);
                                                   chatIntent.putExtra("userId",userId);
                                                   chatIntent.putExtra("type","page");
                                                   chatIntent.putExtra("from","user");
                                                   startActivity(chatIntent);
                                               }


                                           });
                                       }

                                   }



                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });

                               if (condition.equals("false"))
                               {
                                   messageViewHolder.messageLayout.setBackgroundColor(Color.rgb(241,242,245));
                                   messageViewHolder.setMessage(messageData.getMessage());
                                   //messageViewHolder.setDate(messageData.getDate());
                                   messageViewHolder.setTime(messageData.getTime());
                               }
                               else if (condition.equals("true"))
                               {

                                   messageViewHolder.setMessage(messageData.getMessage());
                                   // messageViewHolder.setDate(messageData.getDate());
                                   messageViewHolder.setTime(messageData.getTime());
                               }
                           }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });





            }

            public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_message_layout, parent ,false);
                return new MessageViewHolder(view);
            }
        };
        adapter.startListening();
        friendsMessageList.setAdapter(adapter);
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView messageDeleteBtn, Time;
        LinearLayout messageLayout;



        public MessageViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            messageDeleteBtn = mView.findViewById(R.id.message_Delete_Btn);
            messageLayout = mView.findViewById(R.id.message_linear_layout);
            Time = mView.findViewById(R.id.friend_message_time);

        }

        public void setProfileImage(Context applicationContext, String profileimage) {
            CircleImageView image = (CircleImageView) mView.findViewById(R.id.friend_message_profile_image);
            Picasso.get().load(profileimage).placeholder(R.drawable.profile_icon).into(image);
        }

        public void setFullName(String fullName){
            TextView myName = (TextView) mView.findViewById(R.id.friend_message_profile_full_name);
            myName.setText(fullName);
        }
        public void setMessage(String message){
            TextView messages = (TextView) mView.findViewById(R.id.friend_message_text);
            messages.setText(message);
        }

        public void setTime(String time){
            TextView times = (TextView) mView.findViewById(R.id.friend_message_time);
            times.setText(time);
        }




    }


    @Override
    protected void onUserLeaveHint() {
        updateUserStatus("offline");
        userReff.child(currentUserId).child("MessageNotification").child(currentUserId).removeValue();
        super.onUserLeaveHint();
    }

    @Override
    protected void onDestroy() {
        updateUserStatus("offline");
        userReff.child(currentUserId).child("MessageNotification").child(currentUserId).removeValue();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        updateUserStatus("offline");
        userReff.child(currentUserId).child("MessageNotification").child(currentUserId).removeValue();
        super.onBackPressed();
    }
}
