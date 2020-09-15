package com.approxsoft.approxu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.Connection;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;

import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class VideoCallStremActivity extends AppCompatActivity
        implements Session.SessionListener,
        PublisherKit.PublisherListener, Session.ConnectionListener {


    String api_Key = "46765062";
    String session_Id = "2_MX40Njc2NTA2Mn5-MTU5MDYyODg4NzcwNX5FZ0RLd04yNXFXVExmaWtTbEdyVnhmTUd-fg";
    String token = "T1==cGFydG5lcl9pZD00Njc2NTA2MiZzaWc9ODA3YzdiMzY3NTNjYjQ3ZDBhZjkzODRkNzA3Zjk2NjI5MjQ4ZDI1NTpzZXNzaW9uX2lkPTJfTVg0ME5qYzJOVEEyTW41LU1UVTVNRFl5T0RnNE56Y3dOWDVGWjBSTGQwNHlOWEZYVkV4bWFXdFRiRWR5Vm5obVRVZC1mZyZjcmVhdGVfdGltZT0xNTkwNjI4OTQ0Jm5vbmNlPTAuMjUyNTcwNjI2MDAzNTMyNTMmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTU5MDcxNTM0NCZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";

    private static final String LoG_TAG = VideoCallStremActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PREM = 124;

    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;

    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;

    private DatabaseReference UserReference;
    String userId;
    FirebaseAuth mAuth;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_strem);

        mAuth = FirebaseAuth.getInstance();
        userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

        UserReference = FirebaseDatabase.getInstance().getReference().child("All Users");

        ImageView closeVideoChatBtn = findViewById(R.id.cancel_video_call_icons);
        closeVideoChatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                UserReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.child(userId).hasChild("Ringing"))
                        {
                            UserReference.child(userId).child("Ringing").removeValue();

                            if (mPublisher !=null)
                            {
                                mPublisher.destroy();
                            }
                            if (mSubscriber !=null)
                            {
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(VideoCallStremActivity.this,HomeActivity.class));
                            finish();
                        }
                        if (dataSnapshot.child(userId).hasChild("Calling"))
                        {
                            UserReference.child(userId).child("Calling").removeValue();

                            if (mPublisher !=null)
                            {
                                mPublisher.destroy();
                            }
                            if (mSubscriber !=null)
                            {
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(VideoCallStremActivity.this,HomeActivity.class));
                            finish();
                        }
                        else
                        {
                            if (mPublisher !=null)
                            {
                                mPublisher.destroy();
                            }
                            if (mSubscriber !=null)
                            {
                                mSubscriber.destroy();
                            }

                            startActivity(new Intent(VideoCallStremActivity.this,HomeActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        requestPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, VideoCallStremActivity.class);
    }
    @AfterPermissionGranted(RC_VIDEO_APP_PREM)
    private void requestPermission()
    {
        String[] perms = {Manifest.permission.INTERNET,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
        if (EasyPermissions.hasPermissions(this, perms))
        {
            mPublisherViewContainer = findViewById(R.id.publisher_container);
            mSubscriberViewContainer = findViewById(R.id.subscriber_container);


            mSession = new Session.Builder(this, api_Key, session_Id).build();
            mSession.setConnectionListener(VideoCallStremActivity.this);
            mSession.connect(token);


        }
        else
        {
            EasyPermissions.requestPermissions(this,"Hi this app need Mic and Camera, Please Allow.", RC_VIDEO_APP_PREM, perms);
        }
    }


    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream)
    {


        Log.i(LoG_TAG, "Stream Received");

        if (mSubscriber == null)
        {
            mSubscriber = new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {

    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {

    }

    // 2.publishing a stream to the session;
    @Override
    public void onConnected(Session session)
    {
        Log.i(LoG_TAG, "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoCallStremActivity.this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView)
        {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {

        Log.i(LoG_TAG, "Stream Disconnected");
    }

    // 3. subscribing to the stream
    @Override
    public void onStreamReceived(Session session, Stream stream)
    {
        Log.i(LoG_TAG, "Stream Received");

        if (mSubscriber == null)
        {
            mSubscriber = new Subscriber.Builder(this,stream).build();
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {

        Log.i(LoG_TAG, "Stream Dropped");

        if (mSubscriber != null)
        {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {

        Log.i(LoG_TAG, "Stream Error");
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {

    }

    @Override
    public void onConnectionCreated(Session session, Connection connection)
    {
        Log.i(LoG_TAG, "Session Connected");
        mPublisher = new Publisher.Builder(this).build();
        mPublisher.setPublisherListener(VideoCallStremActivity.this);

        mPublisherViewContainer.addView(mPublisher.getView());

        if (mPublisher.getView() instanceof GLSurfaceView)
        {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }

        mSession.publish(mPublisher);
    }

    @Override
    public void onConnectionDestroyed(Session session, Connection connection) {

    }
}
