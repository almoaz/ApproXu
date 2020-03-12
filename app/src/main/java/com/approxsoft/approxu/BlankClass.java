package com.approxsoft.approxu;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class BlankClass {

     /**userReff.child(CurrentUserId).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
        {
            if (dataSnapshot.exists()){
                String name = dataSnapshot.child("fullName").getValue().toString();
                String stdid = dataSnapshot.child("stdId").getValue().toString();
                String gender = dataSnapshot.child("gender").getValue().toString();
                String email = dataSnapshot.child("email").getValue().toString();
                String image = dataSnapshot.child("profileImage").getValue().toString();
                String University = dataSnapshot.child("university").getValue().toString();

                HashMap universityMap = new HashMap();
                universityMap.put("profileImage",image);
                universityMap.put("fullName",name);
                universityMap.put("stdId",stdid);
                universityMap.put("Gender",gender);
                universityMap.put("Email",email);
                universityMap.put("university",University);


                universiryReff.child(University).child("All Students").child(CurrentUserId).updateChildren(universityMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(AddUniversityActivity.this,AddUniversity2Activity.class);
                            startActivity(intent);
                            Animatoo.animateSlideLeft(AddUniversityActivity.this);
                        }

                    }
                });
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });**/


    /**
     *
     if (dataSnapshot.exists())
     {
     String University = dataSnapshot.child("university").getValue().toString();
     String Departments = dataSnapshot.child("Departments").getValue().toString();
     String Semester = dataSnapshot.child("Semester").getValue().toString();
     String Group = dataSnapshot.child("Group").getValue().toString();

     SubjectReff = FirebaseDatabase.getInstance().getReference().child("All University").child(University).child(Departments).child(Semester).child(Group);
     retrieveData();
     }
     */

    /**
     *
     *
     * app iD
     ca-app-pub-8490579220553667~9575898429


     unit ID
     ca-app-pub-8490579220553667/6896041993

     */
}
