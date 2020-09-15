package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VersionUpdateActivity extends AppCompatActivity {

    TextView ShowVersionUpdate, ShowNewVersionCode, ShowNewVersionWhyImportantMessage, ShowNewVersionWhyImportantLearnMore, ShowCurrentVersion;
    Button ShowUpdateVersionUpdateBtn;
    DatabaseReference VersionReff, userReff;
    FirebaseAuth mAuth;
    String CurrentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        VersionReff = FirebaseDatabase.getInstance().getReference().child("Approxu");
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(CurrentUserId).child("Version");

        ShowVersionUpdate = findViewById(R.id.version_update_message);
        ShowNewVersionCode = findViewById(R.id.new_version_update_code);
        ShowNewVersionWhyImportantMessage = findViewById(R.id.why_this_new_version);
        ShowNewVersionWhyImportantLearnMore = findViewById(R.id.why_new_version_update_learn_more);
        ShowCurrentVersion = findViewById(R.id.current_version_update);

        ShowUpdateVersionUpdateBtn = findViewById(R.id.click_to_new_version_update_btn);


        ShowNewVersionCode.setVisibility(View.INVISIBLE);
        ShowNewVersionWhyImportantMessage.setVisibility(View.INVISIBLE);
        ShowNewVersionWhyImportantLearnMore.setVisibility(View.INVISIBLE);
        ShowUpdateVersionUpdateBtn.setVisibility(View.INVISIBLE);

        VersionReff.child("Version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String versionCode = dataSnapshot.child("version").getValue().toString();
                    final String url = dataSnapshot.child("versionUri").getValue().toString();

                    userReff.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            if (dataSnapshot.child("version").getValue().equals(versionCode)){
                                ShowVersionUpdate.setText("Not Available version update");
                                ShowNewVersionCode.setVisibility(View.INVISIBLE);
                                ShowNewVersionWhyImportantMessage.setVisibility(View.INVISIBLE);
                                ShowNewVersionWhyImportantLearnMore.setVisibility(View.INVISIBLE);
                                ShowUpdateVersionUpdateBtn.setVisibility(View.INVISIBLE);
                            }
                            else
                            {
                                ShowVersionUpdate.setText("Available version update");
                                ShowNewVersionCode.setVisibility(View.VISIBLE);
                                ShowNewVersionWhyImportantMessage.setVisibility(View.VISIBLE);
                                ShowNewVersionWhyImportantLearnMore.setVisibility(View.VISIBLE);
                                ShowUpdateVersionUpdateBtn.setVisibility(View.VISIBLE);

                                final String uri = url;
                                ShowUpdateVersionUpdateBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.setData(Uri.parse(uri));
                                        startActivity(intent);
                                    }
                                });


                                ShowNewVersionCode.setText("New Update Version " +versionCode);
                            }

                            if (dataSnapshot.exists())
                            {
                                String CurrentVersionCode = dataSnapshot.child("version").getValue().toString();

                                ShowCurrentVersion.setText("Current version " + CurrentVersionCode);
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



    }
}
