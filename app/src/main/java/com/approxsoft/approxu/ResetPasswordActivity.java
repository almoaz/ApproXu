package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button resetBtn;
    private TextView goBack;
    private FirebaseAuth firebaseAuth;
    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailIconText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();


        resetEmail = findViewById(R.id.reset_password_in_email);
        resetBtn = findViewById(R.id.reset_password_btn);
        goBack = findViewById(R.id.go_back);
        emailIconContainer = findViewById(R.id.forgot_password_email_icon_container);
        emailIcon = findViewById(R.id.forgot_password_email_icon);
        emailIconText = findViewById(R.id.forgot_password_email_icon_text);
        progressBar = findViewById(R.id.forgot_password_progressbar);

        resetEmail.addTextChangedListener(new TextWatcher() {
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



        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                resetBtn.setEnabled(false);
                resetBtn.setTextColor(Color.argb(50,112,112,112));

                firebaseAuth.sendPasswordResetEmail(resetEmail.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0, emailIcon.getWidth()/2,emailIcon.getHeight()/2);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.RESTART);
                                    scaleAnimation.setRepeatCount(1);
                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            emailIconText.setText("Email send successfully! Check Your Inbox");
                                            emailIconText.setTextColor(getResources().getColor(R.color.neviBlue));

                                            TransitionManager.beginDelayedTransition(emailIconContainer);
                                            emailIcon.setVisibility(View.VISIBLE);

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                            emailIcon.setImageResource(R.mipmap.green_email);
                                            emailIconText.setVisibility(View.VISIBLE);

                                        }
                                    });
                                    emailIcon.startAnimation(scaleAnimation);



                                }else {
                                    String error =task.getException().getMessage();


                                    emailIconText.setText(error);
                                    emailIconText.setTextColor(getResources().getColor(R.color.neviBlue));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);
                                }
                                resetBtn.setEnabled(true);
                                resetBtn.setTextColor(Color.rgb(255,255,255));
                                progressBar.setVisibility(View.GONE);


                            }
                        });
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPasswordActivity.this,SignInActivity.class);
                startActivity(intent);
                Animatoo.animateSlideRight(ResetPasswordActivity.this);
            }
        });

    }

    private void checkInputs() {
        if (TextUtils.isEmpty(resetEmail.getText())){
            resetBtn.setEnabled(false);
            resetBtn.setTextColor(Color.argb(50,112,112,112));
            resetEmail.setText("");
        }else {

            resetBtn.setEnabled(true);
            resetBtn.setTextColor(Color.rgb(112,112,112));

        }


    }


}
