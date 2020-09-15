package com.approxsoft.approxu.Adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.approxsoft.approxu.Model.Messages;
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

public class PageMessagesAdapter extends RecyclerView.Adapter<PageMessagesAdapter.PageMessageViewHolder>
{
    private List<Messages> pageMessagesList;
    private FirebaseAuth mAuth;
    String messageSenderID2 = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public PageMessagesAdapter(List<Messages> userMessagesList)
    {
        this.pageMessagesList = userMessagesList;
    }

    static class PageMessageViewHolder extends RecyclerView.ViewHolder

    {
        TextView SenderMessageText, ReceiverMessageText,ReceiverMessageSeen, ReceiverDateAndTime;
        CircleImageView receiverProfileImage;
        PageMessageViewHolder(@NonNull View itemView) {

            super(itemView);

            SenderMessageText = itemView.findViewById(R.id.sender_message_text);
            ReceiverMessageText = itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage = itemView.findViewById(R.id.message_profile_image);
            ReceiverMessageSeen = itemView.findViewById(R.id.message_seen_text);
            ReceiverDateAndTime = itemView.findViewById(R.id.receiver_date_and_time);

        }
    }

    @NonNull
    @Override
    public PageMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {


        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_layout_of_users, parent,false);
        mAuth = FirebaseAuth.getInstance();

        return new PageMessageViewHolder(view);
    }

    @SuppressLint("RtlHardcoded")
    @Override
    public void onBindViewHolder(@NonNull final PageMessageViewHolder holder, int position)
    {
        final Messages messages = pageMessagesList.get(position);


        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        //Boolean fromMessageSeenType = messages.getSeenType();

        DatabaseReference userDatabaseReference = FirebaseDatabase.getInstance().getReference().child("All Users").child(fromUserID);
        userDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String image = Objects.requireNonNull(dataSnapshot.child("profileImage").getValue()).toString();
                    Picasso.get().load(image).placeholder(R.drawable.profile_icon).into(holder.receiverProfileImage);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        if (fromMessageType.equals("page"))
        {

            holder.ReceiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
            holder.SenderMessageText.setVisibility(View.INVISIBLE);
            holder.ReceiverDateAndTime.setVisibility(View.INVISIBLE);
            //holder.ReciverMessageSeen.setVisibility(View.INVISIBLE);

            if (fromUserID.equals(messageSenderID2))
            {
                if (position == pageMessagesList.size()-1)
                {
                    if (messages.getSeenType().equals("true"))
                    {
                        holder.ReceiverMessageSeen.setText("Seen");
                    }else {
                        holder.ReceiverMessageSeen.setText("Delivered");
                    }
                }else {
                    holder.ReceiverMessageSeen.setVisibility(View.INVISIBLE);
                }

                holder.SenderMessageText.setVisibility(View.VISIBLE);




                holder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.SenderMessageText.setTextColor(Color.WHITE);
                holder.SenderMessageText.setGravity(Gravity.LEFT);
                holder.SenderMessageText.setText(messages.getMessage());

            }
            else
            {




                holder.ReceiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverProfileImage.setVisibility(View.VISIBLE);


                if (position == pageMessagesList.size()-1)
                {
                    holder.ReceiverMessageText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            holder.ReceiverDateAndTime.setVisibility(View.VISIBLE);
                            holder.ReceiverDateAndTime.setText(messages.getDate() + " " + messages.getTime());

                            holder.ReceiverMessageText.setOnClickListener(new View.OnClickListener() {
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






                holder.ReceiverMessageText.setBackgroundResource(R.drawable.reciver_message_text_background);
                holder.ReceiverMessageText.setTextColor(Color.BLACK);
                holder.ReceiverMessageText.setGravity(Gravity.LEFT);
                holder.ReceiverMessageText.setText(messages.getMessage());

            }

        }




    }


    @Override
    public int getItemCount()
    {
        return pageMessagesList.size();
    }
}

