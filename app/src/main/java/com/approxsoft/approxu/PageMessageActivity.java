package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

public class PageMessageActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_page_message);

        userId = getIntent().getExtras().get("userId").toString();
        userType = getIntent().getExtras().get("type").toString();
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        FriendsReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId).child("Friends").child(currentUserId);
        MessageReff = FirebaseDatabase.getInstance().getReference().child("messages").child(userId);
        messageReff = FirebaseDatabase.getInstance().getReference().child("messages");
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");
        PageReference = FirebaseDatabase.getInstance().getReference().child("All Pages");

        mToolBar = findViewById(R.id.page_message_tool_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");


        messageProfileImage = findViewById(R.id.page_message_profile_image);
        messageUserName = findViewById(R.id.page_message_user_name);
        refreshLayout = findViewById(R.id.page_message_refresh);




        friendsMessageList = findViewById(R.id.page_friends_message_view_list);
        friendsMessageList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setReverseLayout(true);
        linearLayoutManager2.setStackFromEnd(true);
        linearLayoutManager2.setOrientation(RecyclerView.VERTICAL);
        friendsMessageList.setLayoutManager(linearLayoutManager2);



        DisplayAllFriendsMessage();



        updateUserStatus("online");

        PageReference.child(userId).child("MessageNotification").child(userId).removeValue();


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Intent intent = new Intent(HomeActivity.this,HomeActivity.class);
                // startActivity(intent);
                //Animatoo.animateFade(HomeActivity.this);

                DisplayAllFriendsMessage();
                refreshLayout.setRefreshing(false);

            }
        });




        PageReference.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String name = dataSnapshot.child("pageName").getValue().toString();
                    String image = dataSnapshot.child("pageProfileImage").getValue().toString();

                    messageUserName.setText(name);
                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(messageProfileImage);
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


    private void DisplayAllFriendsMessage() {

        Query query2 = MessageReff.orderByChild("time"); // haven't implemented a proper list sort yet.

        FirebaseRecyclerOptions<MessageData> options = new FirebaseRecyclerOptions.Builder<MessageData>().setQuery(query2, MessageData.class).build();

        FirebaseRecyclerAdapter adapter = new FirebaseRecyclerAdapter<MessageData, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PageMessageActivity.MessageViewHolder messageViewHolder, final int position, @NonNull final MessageData messageData) {



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

                            if (type.equals("page"))
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
                                                    MessageReff.child(usersIDs).child("from").setValue("true");

                                                    Intent chatIntent = new Intent(PageMessageActivity.this,PageChatActivity.class);
                                                    chatIntent.putExtra("visit_user_id",usersIDs);
                                                    chatIntent.putExtra("userId",userId);
                                                    chatIntent.putExtra("type","user");
                                                    chatIntent.putExtra("from","page");
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
}
