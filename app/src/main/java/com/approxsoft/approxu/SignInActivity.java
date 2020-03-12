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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private EditText signInEmail, signInPassword;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView createNewAccount, forgotPassword;
    private Button signInBtn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private String emailPattern ="[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAuth = FirebaseAuth.getInstance();


        signInEmail = (EditText) findViewById(R.id.sign_in_email);
        signInPassword = (EditText) findViewById(R.id.sign_in_password);
        createNewAccount = (TextView) findViewById(R.id.create_a_new_account);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        signInBtn = (Button) findViewById(R.id.sign_in_btn);
        progressBar = findViewById(R.id.sign_in_progressBar);


        signInEmail.addTextChangedListener(new TextWatcher() {
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


        signInPassword.addTextChangedListener(new TextWatcher() {
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

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent signUpIntent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailAndPassword();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotIntent = new Intent(SignInActivity.this,ResetPasswordActivity.class);
                startActivity(forgotIntent);
            }
        });

        Bundle bundle = new Bundle();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


    }


    public void checkInputs(){

            if (!TextUtils.isEmpty(signInEmail.getText().toString()) ){
                if (!TextUtils.isEmpty(signInPassword.getText().toString())&& signInPassword.length() >= 8){

                        signInBtn.setEnabled(true);
                        signInBtn.setTextColor(Color.rgb(97,89,89));

            }else {
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,112,112,112));
            }
        }else {
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(Color.argb(50,112,112,112));
        }

    }


    public void checkEmailAndPassword(){
        if (signInEmail.getText().toString().matches(emailPattern)){
            if (signInPassword.length() >= 8){
                progressBar.setVisibility(View.VISIBLE);
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,112,112,112));

                mAuth.signInWithEmailAndPassword(signInEmail.getText().toString(),signInPassword.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    verifyEmailFirst();

                                }else {
                                    String error = task.getException().getMessage();
                                    Toast.makeText(SignInActivity.this,error,Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signInBtn.setEnabled(true);
                                    signInBtn.setTextColor(Color.rgb(97,89,89));
                                }

                            }
                        });

            }else {
                Toast.makeText(SignInActivity.this,"Incorrect email or password",Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(SignInActivity.this,"Incorrect email or password",Toast.LENGTH_SHORT).show();
        }
    }

    public void verifyEmailFirst(){
        FirebaseUser firebaseUser = mAuth.getInstance().getCurrentUser();
        boolean emailVerify = firebaseUser.isEmailVerified();
        if (emailVerify){
           sendUserToMainActivity();

        }else {
            Toast.makeText(SignInActivity.this,"Verify Your Email",Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.INVISIBLE);
            mAuth.signOut();
        }
    }

    private void sendUserToMainActivity() {
        Intent signInIntent = new Intent(SignInActivity.this,HomeActivity.class);
        startActivity(signInIntent);
        Animatoo.animateFade(this);
        finish();
    }
}
