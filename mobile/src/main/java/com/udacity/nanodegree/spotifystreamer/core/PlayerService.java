package com.udacity.nanodegree.spotifystreamer.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.activities.MainActivity;
import com.udacity.nanodegree.spotifystreamer.interfaces.PlayerServiceInterface;

public class PlayerService extends Service implements PlayerServiceInterface, OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener{
    public final static int PAUSED = 0;
    public final static int PLAYING = 1;

    private final IBinder mBinder = new LocalBinder();
    private RemoteViews mRemoteViews;
    private RemoteViews mRemoteViewsSmall;
    public static final String ACTION_CLOSE="com.udacity.nanodegree.spotifystreamer.ACTION_CLOSE";
    public static final String ACTION_FORWARD="com.udacity.nanodegree.spotifystreamer.ACTION_FORWARD";
    public static final String ACTION_BACK="com.udacity.nanodegree.spotifystreamer.ACTION_BACK";
    public static final String ACTION_PLAYPAUSE="com.udacity.nanodegree.spotifystreamer.ACTION_PLAYPAUSE";
    private NotificationManager mNotifiManager;
    private boolean isNotificationVisible=false;
    private MediaPlayer mPlayer;
    private static final int NOTIFICATION_ID = 40140333;
    public static final int STATE_ERROR = -1;
    public static final int STATE_NOT_INIT = 0;
    public static final int STATE_IDLE = 1;
    public static final int STATE_PREPARING = 2;
    public static final int STATE_PREPARED = 3;
    public static final int STATE_PLAYING = 4;
    public static final int STATE_PAUSED = 5;
    public static final int STATE_STOPED = 6;
    private static final int REQUEST_CODE_STOP = 0;

    private int mCurrentState = STATE_NOT_INIT;
    private ArrayList<String> mUrls;
    private int mCurrentSongPosition;
    private Notification mNotification;


    private AsyncTask<Void, Void, Void> seekBarChanger;


    @Override
    public void onCreate() {
        super.onCreate();
        mNotifiManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);;
    }

    void initPlayer(ArrayList<String> urls,int position) {
        setCurrentState(STATE_IDLE);
        mUrls=urls;
        mCurrentSongPosition=position;
        try{
            mPlayer = new MediaPlayer();
            mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            mPlayer.setOnBufferingUpdateListener(this);
            mPlayer.setOnCompletionListener(this);
            mPlayer.setOnErrorListener(this);
            mPlayer.setOnInfoListener(this);
            mPlayer.setOnPreparedListener(this);
            mPlayer.setOnSeekCompleteListener(this);
            mPlayer.setDataSource(mUrls.get(position));

            mPlayer.prepareAsync();
            setCurrentState(STATE_PREPARED);
        }catch(UnsatisfiedLinkError e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public synchronized void play() {
        if (mCurrentState == STATE_PAUSED) {
            if(mPlayer!=null) {
                mPlayer.start();
                setCurrentState(STATE_PLAYING);
            }
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mUrls.get(getNextSongPosition()));
            mPlayer.prepareAsync();
            setCurrentState(STATE_PREPARED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
    }




    private void setSeekBarTracker() {
        if (seekBarChanger != null)
            seekBarChanger.cancel(false);
            seekBarChanger = null;
            seekBarChanger = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                while (mPlayer != null && mPlayer.getCurrentPosition() < mPlayer.getDuration()){
                    if (mCurrentState == PLAYING){
                        int currentPosition = mPlayer.getCurrentPosition();
//                        mSeekBar.setProgress(currentPosition);

//            			int minutes = currentPosition/60, seconds = currentPosition%60;
//                    	if (seconds >= 10) mMusicPlayerServiceBinder.setCurrentTime(minutes + ":" + seconds);
//                    	else mMusicPlayerServiceBinder.setCurrentTime(minutes + ":0" + seconds);
                    }

                    try { Thread.sleep(100); } catch (InterruptedException e) {}
                }
                return null;
            }
        };
        seekBarChanger.execute();
    }

    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            if(mPlayer!=null) {
                mPlayer.pause();
                setCurrentState(STATE_PAUSED);
            }
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void nextSong() {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mUrls.get(getNextSongPosition()));
            mPlayer.prepareAsync();
            setCurrentState(STATE_PREPARED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void previousSong() {
        try {
            mPlayer.reset();
            mPlayer.setDataSource(mUrls.get(getPreviousSongPosition()));
            mPlayer.prepareAsync();
            setCurrentState(STATE_PREPARED);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void seekTo(int second) {
        mPlayer.seekTo(second);
    }
    public void showControllerInNotification() {

        isNotificationVisible = true;
        PendingIntent pendingIntentclose = null;
        PendingIntent pendingIntentplaypause = null;
        PendingIntent pendingIntentback = null;
        PendingIntent pendingIntentforward = null;
        Intent close = null;
        Intent playpause = null;
        Intent back = null;
        Intent forward = null;


        //Inflate a remote view with a layout which you want to display in the notification bar.
        mRemoteViewsSmall = new RemoteViews(getPackageName(),
                R.layout.notification_control_mini);


        //Define what you want to do after clicked the button in notification.
        //Here we launcher a service by an action named "ACTION_STOP" which will stop the music play.
        close = new Intent(ACTION_CLOSE);
        pendingIntentclose = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_STOP, close,
                PendingIntent.FLAG_UPDATE_CURRENT);
        playpause = new Intent(ACTION_PLAYPAUSE);
        pendingIntentplaypause = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_STOP, playpause,
                PendingIntent.FLAG_UPDATE_CURRENT);

        forward = new Intent(ACTION_FORWARD);
        pendingIntentforward = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_STOP, forward,
                PendingIntent.FLAG_UPDATE_CURRENT);

        back = new Intent(ACTION_BACK);
        pendingIntentback = PendingIntent.getService(getApplicationContext(),
                REQUEST_CODE_STOP, back,
                PendingIntent.FLAG_UPDATE_CURRENT);


        //In R.layout.notification_control_bar,there is a button view identified by bar_btn_stop
        //We bind a pendingIntent with this button view so when user click the button,it will excute the intent action.
        mRemoteViews.setOnClickPendingIntent(R.id.controller_close,
                pendingIntentclose);
        mRemoteViews.setOnClickPendingIntent(R.id.mediacontroller_play_pause,
                pendingIntentplaypause);
        mRemoteViews.setOnClickPendingIntent(R.id.mediacontroller_undo,
                pendingIntentback);
        mRemoteViews.setOnClickPendingIntent(R.id.mediacontroller_forward,
                pendingIntentforward);

        mRemoteViewsSmall.setOnClickPendingIntent(R.id.controller_close,
                pendingIntentclose);
        mRemoteViewsSmall.setOnClickPendingIntent(R.id.mediacontroller_play_pause,
                pendingIntentplaypause);
        mRemoteViewsSmall.setOnClickPendingIntent(R.id.mediacontroller_undo,
                pendingIntentback);
        mRemoteViewsSmall.setOnClickPendingIntent(R.id.mediacontroller_forward,
                pendingIntentforward);

        if (mCurrentState == STATE_PAUSED) {
            mRemoteViewsSmall.setImageViewResource(R.id.mediacontroller_play_pause, R.drawable.ic_action_play);
        }


        //Create the notification instance.
        mNotification = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContent(mRemoteViewsSmall)
                .setContentIntent(
                        PendingIntent.getActivity(getApplicationContext(), 10,
                                new Intent(getApplicationContext(), MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                0)
                )
                .build();


            startForeground(NOTIFICATION_ID, mNotification);


        //Show the notification in the notification bar.

    }

    public void setCurrentState(int mCurrentState) {
        this.mCurrentState = mCurrentState;
    }

    public int getNextSongPosition(){
        mCurrentSongPosition=mCurrentSongPosition<mUrls.size()?mCurrentSongPosition+1:0;
        return mCurrentSongPosition;
    }
    public int getPreviousSongPosition(){
        mCurrentSongPosition=mCurrentSongPosition<mUrls.size()?mCurrentSongPosition-1:mUrls.size()-1;
        return mCurrentSongPosition;
    }
}