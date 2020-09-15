package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SecurityAndLoginActivity extends AppCompatActivity {

    Toolbar mToolBar;
    EditText currentPassword, newPassword, retypePassword;
    TextView subCurrentPassword, subNewPassword, subRetypePassword, lockProfileBtn, unlockProfileBtn;
    Button changePasswordBtn;

    DatabaseReference userReff, ChangePasswordReff;
    FirebaseAuth mAuth;
    String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_and_login);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        userReff = FirebaseDatabase.getInstance().getReference().child("All Users");


        mToolBar = findViewById(R.id.security_and_login_app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Security and Sign In");

        currentPassword = findViewById(R.id.current_password);
        newPassword = findViewById(R.id.new_password);
        retypePassword = findViewById(R.id.re_type_new_password);
        subCurrentPassword = findViewById(R.id.sub_current_password);
        subNewPassword = findViewById(R.id.sub_new_password);
        subRetypePassword = findViewById(R.id.sub_new_retype_password);
        changePasswordBtn = findViewById(R.id.save_changes_password);
        lockProfileBtn = findViewById(R.id.lock_profile_btn);
        unlockProfileBtn = findViewById(R.id.unlock_profile_btn);

        userReff.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    final String CurrentPassword = dataSnapshot.child("Password").getValue().toString();

                    ChangePasswordReff = FirebaseDatabase.getInstance().getReference().child("All Users").child(currentUserId);

                    changePasswordBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            changePasswordBtn.setEnabled(false);
                            subCurrentPassword.setVisibility(View.INVISIBLE);
                            subNewPassword.setVisibility(View.INVISIBLE);
                            subRetypePassword.setVisibility(View.INVISIBLE);
                            if (currentPassword.getText().toString().equals(CurrentPassword))
                            {
                               if (newPassword.getText().toString().length() >= 8)
                               {
                                   if (newPassword.getText().toString().equals(retypePassword.getText().toString()))
                                   {
                                       FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                       user.updatePassword(newPassword.getText().toString())
                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task)
                                                   {
                                                       if (task.isSuccessful())
                                                       {
                                                           subCurrentPassword.setText("");
                                                           subNewPassword.setText("");
                                                           subRetypePassword.setText("");
                                                           ChangePasswordReff.child("Password").setValue(newPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                               @Override
                                                               public void onComplete(@NonNull Task<Void> task)
                                                               {
                                                                   if (task.isSuccessful())
                                                                   {
                                                                       CharSequence[] options = new CharSequence[]
                                                                               {
                                                                                       "Sign out",
                                                                                       "Stay sign in"
                                                                               };

                                                                       final AlertDialog.Builder builder = new AlertDialog.Builder(SecurityAndLoginActivity.this);
                                                                       builder.setTitle("Select Option");
                                                                       builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                           @Override
                                                                           public void onClick(DialogInterface dialog, int which)
                                                                           {
                                                                               if (which == 0)
                                                                               {
                                                                                   mAuth.signOut();
                                                                                   Intent profileIntent = new Intent(SecurityAndLoginActivity.this,SignInActivity.class);
                                                                                   startActivity(profileIntent);
                                                                               }
                                                                               if (which == 1)
                                                                               {
                                                                                   Intent profileIntent = new Intent(SecurityAndLoginActivity.this,SecurityAndLoginActivity.class);
                                                                                   startActivity(profileIntent);
                                                                                   finish();

                                                                               }
                                                                           }
                                                                       });
                                                                       builder.show();
                                                                   }
                                                               }
                                                           });
                                                       }
                                                   }
                                               });
                                   }
                                   else
                                   {
                                       changePasswordBtn.setEnabled(true);
                                       Toast.makeText(SecurityAndLoginActivity.this,"Don't match re-type password",Toast.LENGTH_SHORT).show();
                                   }
                               }else
                               {
                                   changePasswordBtn.setEnabled(true);
                                   Toast.makeText(SecurityAndLoginActivity.this,"New password at list 8 characters",Toast.LENGTH_SHORT).show();
                               }
                            }
                            else {
                                changePasswordBtn.setEnabled(true);
                                Toast.makeText(SecurityAndLoginActivity.this,"Wrong current password",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if (dataSnapshot.child("profile").getValue().equals("unlock"))
                {
                    unlockProfileBtn.setVisibility(View.INVISIBLE);
                    unlockProfileBtn.setEnabled(false);
                    lockProfileBtn.setVisibility(View.VISIBLE);
                    lockProfileBtn.setEnabled(true);
                    lockProfileBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userReff.child(currentUserId).child("profile").setValue("lock").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        lockProfileBtn.setVisibility(View.INVISIBLE);
                                        lockProfileBtn.setEnabled(false);
                                        unlockProfileBtn.setVisibility(View.VISIBLE);
                                        unlockProfileBtn.setEnabled(true);
                                    }
                                }
                            });
                        }
                    });
                }else if (dataSnapshot.child("profile").getValue().equals("lock"))
                {
                    lockProfileBtn.setVisibility(View.INVISIBLE);
                    lockProfileBtn.setEnabled(false);
                    unlockProfileBtn.setVisibility(View.VISIBLE);
                    unlockProfileBtn.setEnabled(true);
                    unlockProfileBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userReff.child(currentUserId).child("profile").setValue("unlock").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        unlockProfileBtn.setVisibility(View.INVISIBLE);
                                        unlockProfileBtn.setEnabled(false);
                                        lockProfileBtn.setVisibility(View.VISIBLE);
                                        lockProfileBtn.setEnabled(true);
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


        currentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                subCurrentPassword.setVisibility(View.VISIBLE);
                subNewPassword.setVisibility(View.INVISIBLE);
                subRetypePassword.setVisibility(View.INVISIBLE);
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                subCurrentPassword.setVisibility(View.INVISIBLE);
                subNewPassword.setVisibility(View.VISIBLE);
                subRetypePassword.setVisibility(View.INVISIBLE);
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        retypePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                subCurrentPassword.setVisibility(View.INVISIBLE);
                subNewPassword.setVisibility(View.INVISIBLE);
                subRetypePassword.setVisibility(View.VISIBLE);
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void checkInputs() {

        if (!TextUtils.isEmpty(currentPassword.getText().toString().trim()))
        {

            if (!TextUtils.isEmpty(newPassword.getText().toString().trim()))
            {


                if (!TextUtils.isEmpty(retypePassword.getText().toString().trim()))
                {
                    changePasswordBtn.setEnabled(true);
                }
                else
                {
                    subRetypePassword.setVisibility(View.INVISIBLE);
                    changePasswordBtn.setEnabled(false);
                }
            }
            else
            {
                subNewPassword.setVisibility(View.INVISIBLE);
                changePasswordBtn.setEnabled(false);
            }
        }
        else
        {
            subCurrentPassword.setVisibility(View.INVISIBLE);
            changePasswordBtn.setEnabled(false);
        }

    }


}
