package com.approxsoft.approxu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class BlankActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blank);


        /**
         final String PostKey = getRef(position).getKey();
         final String Post_Key = getRef(position).getKey();
         // final String visit_user_id = userRef.child(currentUserId).getKey();
         holder.time.setText(String.format("    %s", model.getTime()));
         holder.date.setText(String.format("    %s", model.getDate()));
         holder.description.setText(model.getDescription());



         holder.setStarButtonStatus(PostKey);
         holder.setCommentsCount(PostKey);

         PostsRef.child(Post_Key).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
        if (dataSnapshot.exists())
        {
        final String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
        userRef.child(Uid).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
        String profileImage = dataSnapshot.child("profileImage").getValue().toString();
        String name = dataSnapshot.child("fullName").getValue().toString();

        holder.username.setText(name);
        Picasso.get().load(profileImage).placeholder(R.drawable.profile_icon).into(holder.user_profile_image);

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });

        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
        if (dataSnapshot.child("Friends").child(currentUserId).hasChild(Uid))
        {
        holder.CommentsPostBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
        Intent commentsIntent = new Intent(HomeActivity.this,CommentsActivity.class);
        commentsIntent.putExtra("PostKey",PostKey);
        startActivity(commentsIntent);
        }
        });
        }else {
        holder.CommentsPostBtn.setVisibility(View.INVISIBLE);
        holder.CommentsPostBtn.setEnabled(false);
        holder.commentsCount.setVisibility(View.INVISIBLE);
        holder.commentsCount.setEnabled(false);
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



         holder.user_profile_image.setOnClickListener(new View.OnClickListener()
         {
         @Override
         public void onClick(View v)
         {

         assert PostKey != null;
         PostsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot)
         {
         if (dataSnapshot.exists())
         {
         String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
         final String visit_user_id = userRef.child(Uid).getKey();

         Intent findProfileIntent = new Intent(HomeActivity.this, PersonProfileActivity.class);
         findProfileIntent.putExtra("visit_user_id", visit_user_id);
         startActivity(findProfileIntent);
         }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
         });



         }
         });
         holder.username.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        assert PostKey != null;
        PostsRef.child(PostKey).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
        if (dataSnapshot.exists())
        {
        String Uid = Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
        final String visit_user_id = userRef.child(Uid).getKey();

        Intent findProfileIntent = new Intent(HomeActivity.this, PersonProfileActivity.class);
        findProfileIntent.putExtra("visit_user_id", visit_user_id);
        startActivity(findProfileIntent);
        }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });
        }
        });
         holder.description.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        holder.description.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent clickPostIntent = new Intent(HomeActivity.this,ClickPostActivity.class);
        clickPostIntent.putExtra("PostKey",PostKey);
        startActivity(clickPostIntent);
        }
        });
        }
        });



         holder.StarPostBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
        StarChecker = true;

        StarRef.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
        if (StarChecker.equals(true))
        {
        if (dataSnapshot.child(Objects.requireNonNull(Post_Key)).child("Star").child(Post_Key).hasChild(currentUserId))
        {
        StarRef.child(Post_Key).child("Star").child(Post_Key).child(currentUserId).child("condition").removeValue();
        StarChecker = false;
        }
        else
        {
        StarRef.child(Post_Key).child("Star").child(Post_Key).child(currentUserId).child("condition").setValue(true);
        StarChecker = false;
        }
        }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
        });
        }
        });

         holder.DisplayNoOfStar.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent commentsIntent = new Intent(HomeActivity.this,StarFriendActivity.class);
        commentsIntent.putExtra("PostKey",PostKey);
        startActivity(commentsIntent);
        }
        });
         */



    }
}
