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

    /*
    * apply plugin: 'com.android.application'


android {
    compileSdkVersion 'android-R'
    buildToolsVersion '29.0.2'
    defaultConfig {
        applicationId "com.approxsoft.approxu"
        minSdkVersion 22
        targetSdkVersion 29
        versionCode 1
        versionName "1.0"
        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'androidx.appcompat:appcompat:1.2.0-rc01'
    implementation 'com.google.android.material:material:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    implementation 'com.google.firebase:firebase-analytics:17.4.1'
    implementation 'com.google.firebase:firebase-auth:19.3.1'
    implementation 'com.google.firebase:firebase-database:19.3.0'
    implementation 'com.google.firebase:firebase-core:17.4.1'
    implementation 'androidx.legacy:legacy-support-v4:1.0.0'
    implementation 'com.google.android.gms:play-services-ads:19.1.0'
    testImplementation 'junit:junit:4.13'
    implementation 'de.hdodenhof:circleimageview:3.1.0'
    api 'com.theartofdev.edmodo:android-image-cropper:2.8.0'
    implementation 'com.google.firebase:firebase-storage:19.1.1'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    implementation 'androidx.recyclerview:recyclerview:1.1.0'
    implementation 'com.squareup.picasso:picasso:2.71828'
    //implementation 'com.github.bumptech.glide:glide:4.11.0'
    //annotationProcessor 'com.github.bumptech.glide:compiler:4.11.0'
    implementation 'com.firebaseui:firebase-ui-database:6.2.0'
    implementation 'id.zelory:compressor:2.1.0'
    implementation 'com.github.mohammadatif:Animatoo:master'
    implementation 'com.borjabravo:readmoretextview:2.1.0'
    implementation 'com.iceteck.silicompressorr:silicompressor:2.2.3'
    //implementation 'com.google.firebase:firebase-perf:19.0.6'
}
apply plugin: 'com.google.gms.google-services'
//apply plugin: 'com.google.firebase.firebase-perf'
*/
}
