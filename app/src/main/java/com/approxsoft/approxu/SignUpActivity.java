package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText signUpFirstName, signUpLastName, signUpEmail, signUpPassword, signUpConfirmPassword , signUpGender;
    private Button signUpBtn;
    private TextView backToSignInActivity;
    private ProgressBar progressBar;
    private String emailPattern ="[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    private CheckBox signUpCheckBox;

    private FirebaseAuth mAuth;
    private DatabaseReference userReff;
    private String currentUserId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        userReff = FirebaseDatabase.getInstance().getReference();



        signUpFirstName = findViewById(R.id.sign_up_first_name);
        signUpLastName = findViewById(R.id.sign_up_last_name);
        signUpEmail = findViewById(R.id.sign_up_email);
        signUpPassword = findViewById(R.id.sign_up_password);
        signUpConfirmPassword = findViewById(R.id.sign_up_confirm_password);
        signUpGender = findViewById(R.id.sign_up_gender);
        signUpBtn = findViewById(R.id.sign_up_btn);
        progressBar = findViewById(R.id.sign_up_progressBar);
        backToSignInActivity = findViewById(R.id.go_to_sign_in_activity);
        signUpCheckBox = findViewById(R.id.term_condition_check_box);


        signUpFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        signUpLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
               checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        signUpGender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
              checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signUpCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signUpCheckBox.isChecked())
                {
                    signUpBtn.setEnabled(true);
                    signUpBtn.setTextColor(Color.rgb(112, 112, 112));
                }
                else {
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.argb(50,112, 112, 112));
                }
            }
        });


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        backToSignInActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent signInIntent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(signInIntent);
                Animatoo.animateSlideRight(SignUpActivity.this);
            }
        });
    }



    public void checkEmailAndPassword(){
        if (signUpEmail.getText().toString().matches(emailPattern)){
            if (signUpPassword.getText().toString().matches(signUpConfirmPassword.getText().toString())){
                progressBar.setVisibility(View.VISIBLE);
                signUpBtn.setEnabled(false);
                signUpBtn.setTextColor(Color.argb(50,112,112,112));

                mAuth.createUserWithEmailAndPassword(signUpEmail.getText().toString(),signUpConfirmPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){

                                    currentUserId = mAuth.getCurrentUser().getUid();

                                    HashMap userMap = new HashMap();
                                    userMap.put("firstName",signUpFirstName.getText().toString());
                                    userMap.put("lastName",signUpLastName.getText().toString());
                                    userMap.put("email",signUpEmail.getText().toString());
                                    userMap.put("university","None");
                                    userMap.put("departments","None");
                                    userMap.put("date","None");
                                    userMap.put("time","None");
                                    userMap.put("profileImage","None");
                                    userMap.put("coverImage","None");
                                    userMap.put("dateOfBirth","None");
                                    userMap.put("gender",signUpGender.getText().toString());
                                    userMap.put("homeTown","None");
                                    userMap.put("semester","None");
                                    userMap.put("stdId","None");
                                    userMap.put("currentCity","None");
                                    userMap.put("religion","none");
                                    userMap.put("phoneNo","None");
                                    userMap.put("uid",currentUserId);


                                    userReff.child("All Users").child(currentUserId).updateChildren(userMap).addOnCompleteListener( new OnCompleteListener() {
                                        @Override
                                        public void onComplete(@NonNull Task task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendEmailVerification();
                                            }
                                            else
                                            {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(SignUpActivity.this,error,Toast.LENGTH_SHORT).show();
                                                progressBar.setVisibility(View.INVISIBLE);
                                            }
                                        }
                                    });



                                }else {

                                }

                            }
                        });

            }else {
                Toast.makeText(SignUpActivity.this,"Password Does not match",Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(SignUpActivity.this,"Email bad format",Toast.LENGTH_SHORT).show();
        }
    }

    public void checkInputs(){

        if (!TextUtils.isEmpty(signUpFirstName.getText().toString())) {
            if (!TextUtils.isEmpty(signUpLastName.getText().toString())){
                if (!TextUtils.isEmpty(signUpEmail.getText().toString())) {
                    if (!TextUtils.isEmpty(signUpPassword.getText().toString()) && signUpPassword.length() >= 8) {
                        if (!TextUtils.isEmpty(signUpConfirmPassword.getText().toString()) && signUpConfirmPassword.length() >= 8) {
                            if (!TextUtils.isEmpty(signUpGender.getText().toString())) {
                                signUpBtn.setEnabled(false);
                                signUpBtn.setTextColor(Color.rgb(112, 112, 112));
                            } else {
                                signUpBtn.setEnabled(false);
                                signUpBtn.setTextColor(Color.rgb(112, 112, 112));
                            }

                        } else {
                            signUpBtn.setEnabled(false);
                            signUpBtn.setTextColor(Color.rgb(112, 112, 112));
                        }

                    } else {
                        signUpBtn.setEnabled(false);
                        signUpBtn.setTextColor(Color.rgb(112, 112, 112));
                    }
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.rgb(112, 112, 112));

                } else {
                    signUpBtn.setEnabled(false);
                    signUpBtn.setTextColor(Color.rgb(112, 112, 112));
                }
        }else {
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(Color.rgb(112,112,112));
         }
        }else {
            signUpBtn.setEnabled(false);
            signUpBtn.setTextColor(Color.rgb(112,112,112));
        }

    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser =mAuth.getCurrentUser();
        if (firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(SignUpActivity.this,"Email Verification send to your email",Toast.LENGTH_SHORT).show();
                        sendUserToSignInActivity();
                        SignUpActivity.this.finish();
                        mAuth.signOut();

                    }else {
                        Toast.makeText(SignUpActivity.this,"Database id Down Please wait",Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }


    }

    private void sendUserToSignInActivity() {
        Intent signUpIntent = new Intent(SignUpActivity.this, SignInActivity.class);
        startActivity(signUpIntent);
        Animatoo.animateSlideRight(this);
    }



}
