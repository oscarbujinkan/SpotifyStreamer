package com.udacity.nanodegree.spotifystreamer.activities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.udacity.nanodegree.spotifystreamer.R;

public class MainActivity extends Activity {

    private TextView mTextView;
    private ImageView mPlayButton;
    private ImageView mNextButton;
    private ImageView mPreviousButton;
    public static final String ACTION_FORWARD="com.udacity.nanodegree.spotifystreamer.ACTION_FORWARD";
    public static final String ACTION_BACK="com.udacity.nanodegree.spotifystreamer.ACTION_BACK";
    public static final String ACTION_PLAYPAUSE="com.udacity.nanodegree.spotifystreamer.ACTION_PLAYPAUSE";
    private static final int REQUEST_CODE_STOP = 0;
    private static final int PLAYING=0;
    private static final int PAUSE=1;
    private int mState=0;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mPlayButton=(ImageView) stub.findViewById(R.id.rect_activity_play);
                mPreviousButton=(ImageView) stub.findViewById(R.id.rect_activity_back);
                mNextButton=(ImageView) stub.findViewById(R.id.rect_activity_forward);
                mPlayButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_48dp,null));
                Intent playpause = null;
                Intent back = null;
                Intent forward = null;

                playpause = new Intent(ACTION_PLAYPAUSE);
                final PendingIntent pendingIntentplaypause = PendingIntent.getService(getApplicationContext(),
                        REQUEST_CODE_STOP, playpause,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                forward = new Intent(ACTION_FORWARD);
                PendingIntent pendingIntentforward = PendingIntent.getService(getApplicationContext(),
                        REQUEST_CODE_STOP, forward,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                back = new Intent(ACTION_BACK);
                PendingIntent pendingIntentback = PendingIntent.getService(getApplicationContext(),
                        REQUEST_CODE_STOP, back,
                        PendingIntent.FLAG_UPDATE_CURRENT);



                mPlayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            pendingIntentplaypause.send();
                            if(mState==PLAYING){
                                mPlayButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_white_48dp,null));
                                mState=PAUSE;
                            }else{
                                mPlayButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_white_48dp,null));
                                mState=PLAYING;
                            }

                        } catch (PendingIntent.CanceledException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                    }
                })
                .addApi(Wearable.API)
                .build();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (null != mGoogleApiClient && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }
}
