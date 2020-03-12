package com.approxsoft.approxu.Adapter;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabaseReff;

    public MessagesAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList = userMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder

    {
        public TextView SenderMessageText, ReciverMessageText;
        public CircleImageView reciverProfileImage;
        public MessageViewHolder(@NonNull View itemView) {

            super(itemView);

            SenderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            ReciverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            reciverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
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

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
    {
        final Messages messages = userMessagesList.get(position);
        String messageSenderID = mAuth.getCurrentUser().getUid();

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        userDatabaseReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(fromUserID);
        userDatabaseReff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    String image = dataSnapshot.child("profileImage").getValue().toString();

                    Picasso.get().load(image).placeholder(R.drawable.profile_holder).into(holder.reciverProfileImage);
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




                holder.ReciverMessageText.setBackgroundResource(R.drawable.reciver_message_text_background);
                holder.ReciverMessageText.setTextColor(Color.WHITE);
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

