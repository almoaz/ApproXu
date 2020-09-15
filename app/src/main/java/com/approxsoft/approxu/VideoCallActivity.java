package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.HashMap;

public class VideoCallActivity extends AppCompatActivity {

    TextView UserName;
    ImageView ReceiverImageview,MakeCallBtn, CancelCallBtn;
    String receiverUserId = "" , senderId, SenderUserName,SenderProfileImage, ReciverUserName = "",ReciverProfileImage = "", Checker = "";
    String CallingId = "", RingingId = "";
    DatabaseReference UserReference;
    FirebaseAuth firebaseAuth;

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        receiverUserId = getIntent().getExtras().get("visit_user_id").toString();

        firebaseAuth = FirebaseAuth.getInstance();
        senderId = firebaseAuth.getCurrentUser().getUid();

        UserReference = FirebaseDatabase.getInstance().getReference().child("All Users");

        mediaPlayer = MediaPlayer.create(this,R.raw.pristine);

        UserName = findViewById(R.id.calling_user_name);
        MakeCallBtn = findViewById(R.id.Send_call);
        CancelCallBtn = findViewById(R.id.Cancel_call);
        ReceiverImageview = findViewById(R.id.receiver_image);

        getProfileInformation();

        MakeCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mediaPlayer.stop();

                final HashMap<String, Object> callingPickupMap = new HashMap<>();
                callingPickupMap.put("picked","picked");

                UserReference.child(senderId).child("Ringing")
                        .updateChildren(callingPickupMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task)
                            {
                                if (task.isSuccessful())
                                {
                                    Intent intent = new Intent(VideoCallActivity.this,VideoCallStremActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        CancelCallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                mediaPlayer.stop();
                Checker = "Clicked";
                CancelCallingUser();

            }
        });





    }


    @Override
    protected void onStart() {

        super.onStart();

        mediaPlayer.start();

        UserReference.child(receiverUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!Checker.equals("Clicked") && !dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing"))
                {


                    final HashMap<String , Object> callInfo = new HashMap<>();
                    callInfo.put("calling", receiverUserId);



                    UserReference.child(senderId).child("Calling").updateChildren(callInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {

                                final HashMap<String , Object> ringingInfo = new HashMap<>();
                                ringingInfo.put("ringing", senderId);

                                UserReference.child(receiverUserId).child("Ringing").updateChildren(ringingInfo);
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        UserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(senderId).hasChild("Ringing") && dataSnapshot.child(receiverUserId).hasChild("Calling"))
                {
                    MakeCallBtn.setVisibility(View.VISIBLE);
                }
                if (dataSnapshot.child(receiverUserId).child("Ringing").hasChild("picked"))
                {
                    mediaPlayer.stop();
                    Intent intent = new Intent(VideoCallActivity.this,VideoCallStremActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getProfileInformation()
    {
        UserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.child(receiverUserId).exists())
                {
                    ReciverUserName = dataSnapshot.child(receiverUserId).child("fullName").getValue().toString();
                    ReciverProfileImage = dataSnapshot.child(receiverUserId).child("profileImage").getValue().toString();

                    UserName.setText(ReciverUserName);
                    Picasso.get().load(ReciverProfileImage).into(ReceiverImageview);
                }
                else
                if (dataSnapshot.child(senderId).exists())
                {
                    SenderUserName = dataSnapshot.child(senderId).child("fullName").getValue().toString();
                    SenderProfileImage = dataSnapshot.child(senderId).child("profileImage").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void CancelCallingUser()
    {
        ///sender side
        UserReference.child(senderId).child("Calling").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("calling"))
                {
                    CallingId = dataSnapshot.child("calling").getValue().toString();

                    UserReference
                            .child(CallingId)
                            .child("Ringing")
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        UserReference
                                                .child(senderId)
                                                .child("Calling")
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        startActivity(new Intent(VideoCallActivity.this,HomeActivity.class));
                                                        finish();
                                                    }
                                                });
                                    }
                                }
                            });
                }
                else
                {
                    startActivity(new Intent(VideoCallActivity.this,HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //receiver side

        UserReference.child(senderId).child("Ringing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("ringing"))
                {
                    CallingId = dataSnapshot.child("ringing").getValue().toString();

                    UserReference
                            .child(RingingId)
                            .child("Calling")
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        UserReference
                                                .child(senderId)
                                                .child("Ringing")
                                                .removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                        startActivity(new Intent(VideoCallActivity.this,HomeActivity.class));
                                                        finish();
                                                    }
                                                });
                                    }
                                }
                            });
                }
                else
                {
                    startActivity(new Intent(VideoCallActivity.this,HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

   /** private void MakeACallToUser()
    {
        UserReference.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.hasChild("Calling") && !dataSnapshot.hasChild("Ringing"))
                {
                    final HashMap<String , Object> callInfo = new HashMap<>();
                    callInfo.put("uid",senderId);
                    callInfo.put("name",SenderUserName);
                    callInfo.put("image",SenderProfileImage);
                    callInfo.put("calling", receiverUserId);



                    UserReference.child(senderId).child("Calling").updateChildren(callInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {

                                final HashMap<String , Object> ringingInfo = new HashMap<>();
                                ringingInfo.put("uid",receiverUserId);
                                ringingInfo.put("name",ReciverUserName);
                                ringingInfo.put("image",ReciverProfileImage);
                                ringingInfo.put("ringing", senderId);

                                UserReference.child(receiverUserId).child("Ringing").updateChildren(ringingInfo);
                            }
                        }
                    });



                }
                else
                {
                    MakeCallBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }*/
}
