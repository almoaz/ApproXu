package com.approxsoft.approxu.Adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.approxsoft.approxu.Model.Messages;
import com.approxsoft.approxu.PersonProfileActivity;
import com.approxsoft.approxu.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    String messageSenderID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public GroupMessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder

    {
        TextView SenderMessageText, ReciverMessageText,ReceiverDateAndTime;
        CircleImageView reciverProfileImage;
        MessageViewHolder(@NonNull View itemView) {

            super(itemView);

            SenderMessageText = itemView.findViewById(R.id.sender_message_text);
            ReciverMessageText = itemView.findViewById(R.id.receiver_message_text);
            reciverProfileImage = itemView.findViewById(R.id.message_profile_image);
            ReceiverDateAndTime = itemView.findViewById(R.id.receiver_date_and_time);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_users, parent,false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {
        final Messages messages = userMessagesList.get(position);

        final String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(fromUserID);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String university = Objects.requireNonNull(dataSnapshot.child("university").getValue()).toString();

                    DatabaseReference universityReff = FirebaseDatabase.getInstance().getReference().child("All University").child(university).child("All Students");
                    universityReff.child(fromUserID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                             if (dataSnapshot.exists())
                            {
                                String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();

                                Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(holder.reciverProfileImage);

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

        if (fromMessageType.equals("text"))
        {
            holder.ReciverMessageText.setVisibility(View.INVISIBLE);
            holder.reciverProfileImage.setVisibility(View.INVISIBLE);
            holder.SenderMessageText.setVisibility(View.INVISIBLE);
            holder.ReceiverDateAndTime.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderID))


            {

                holder.SenderMessageText.setVisibility(View.VISIBLE);

                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.SenderMessageText.setTextColor(Color.WHITE);
                holder.SenderMessageText.setGravity(Gravity.LEFT);
                holder.SenderMessageText.setText(messages.getMessage());

            }
            else
            {

                holder.ReciverMessageText.setVisibility(View.VISIBLE);
                holder.reciverProfileImage.setVisibility(View.VISIBLE);

                if (position == userMessagesList.size()-1)
                {
                    holder.ReciverMessageText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.ReceiverDateAndTime.setVisibility(View.VISIBLE);
                            holder.ReceiverDateAndTime.setText(messages.getDate() + " " + messages.getTime());

                            holder.ReciverMessageText.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    holder.ReceiverDateAndTime.setVisibility(View.INVISIBLE);
                                }
                            });
                        }
                    });
                }else {
                    holder.ReceiverDateAndTime.setVisibility(View.INVISIBLE);
                }




                holder.ReciverMessageText.setBackgroundResource(R.drawable.reciver_message_text_background);
                holder.ReciverMessageText.setTextColor(Color.BLACK);
                holder.ReciverMessageText.setGravity(Gravity.LEFT);
                holder.ReciverMessageText.setText(messages.getMessage());

            }
        }

    }

    @Override
    public int getItemCount()
    {
        return userMessagesList.size();
    }
}

