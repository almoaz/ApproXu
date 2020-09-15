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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity {

    EditText signInEmail, signInPassword;
    FirebaseAnalytics mFirebaseAnalytics;
    TextView createNewAccount, forgotPassword, SubSignInEmailText, SubSignInPasswordText, goBack;
    private Button signInBtn;
    private ProgressBar progressBar;
    FirebaseAuth mAuth;
    String emailPattern ="[a-z0-9._-]+@[a-z]+.[a-z]+";
    String currenUserId,CURRENT_STATE;
    DatabaseReference userReff;
    RelativeLayout signInLayout, signUpLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mAuth = FirebaseAuth.getInstance();


        signInEmail = findViewById(R.id.sign_in_email);
        signInPassword = findViewById(R.id.sign_in_password);
        createNewAccount = findViewById(R.id.create_a_new_account);
        forgotPassword = findViewById(R.id.forgot_password);
        signInBtn = findViewById(R.id.sign_in_btn);
        progressBar = findViewById(R.id.sign_in_progressBar);
        SubSignInEmailText = findViewById(R.id.sub_email_text);
        SubSignInPasswordText = findViewById(R.id.sub_password_text);
        signInLayout = findViewById(R.id.sign_in_layout);
        signUpLayout = findViewById(R.id.sign_up_layout);
        goBack = findViewById(R.id.go_to_sign_in_activity);

        CURRENT_STATE = "sign_in_activity";


        signInEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                SubSignInEmailText.setVisibility(View.VISIBLE);
                SubSignInPasswordText.setVisibility(View.INVISIBLE);
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

                SubSignInPasswordText.setVisibility(View.VISIBLE);
                SubSignInEmailText.setVisibility(View.INVISIBLE);
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
                signInLayout.setVisibility(View.GONE);
                signUpLayout.setVisibility(View.VISIBLE);
                CURRENT_STATE = "sign_up_activity";
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                signUpLayout.setVisibility(View.GONE);
                signInLayout.setVisibility(View.VISIBLE);
                CURRENT_STATE = "sign_in_activity";

            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubSignInEmailText.setVisibility(View.INVISIBLE);
                SubSignInPasswordText.setVisibility(View.INVISIBLE);
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

    @Override
    public void onBackPressed() {

        if (CURRENT_STATE.equals("sign_in_activity"))
        {

            SignInActivity.super.onBackPressed();

            if (CURRENT_STATE.equals("sign_in_activity"))
            {
                Toast.makeText(SignInActivity.this,"Try again",Toast.LENGTH_LONG).show();
                if (CURRENT_STATE.equals("sign_in_activity"))
                {
                    SignInActivity.super.onBackPressed();
                }
            }
        }
        else if (CURRENT_STATE.equals("sign_up_activity"))
        {
            signUpLayout.setVisibility(View.GONE);
            signInLayout.setVisibility(View.VISIBLE);



        }

    }

    public void checkInputs(){

            if (!TextUtils.isEmpty(signInEmail.getText().toString()) ){

                if (!TextUtils.isEmpty(signInPassword.getText().toString())){


                        signInBtn.setEnabled(true);
                        signInBtn.setTextColor(Color.rgb(97,89,89));

            }else {

                    SubSignInPasswordText.setVisibility(View.INVISIBLE);
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(Color.argb(50,112,112,112));
            }
        }else {
                SubSignInEmailText.setVisibility(View.INVISIBLE);
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
                                    String error = Objects.requireNonNull(task.getException()).getMessage();
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerify = Objects.requireNonNull(firebaseUser).isEmailVerified();
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
