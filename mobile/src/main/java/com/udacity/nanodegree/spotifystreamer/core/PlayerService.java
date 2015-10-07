package com.udacity.nanodegree.spotifystreamer.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.activities.MainActivity;
import com.udacity.nanodegree.spotifystreamer.interfaces.PlayerServiceInterface;

import java.io.IOException;
import java.util.ArrayList;

public class PlayerService extends Service implements PlayerServiceInterface, OnPreparedListener, MediaPlayer.OnSeekCompleteListener,
        MediaPlayer.OnBufferingUpdateListener, OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener{

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
    private Context mContext;
    private OnStateChange mStateChange;




    @Override
    public void onCreate() {
        super.onCreate();
        mNotifiManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);;
    }

   public void initPlayer(ArrayList<String> urls,int position, Context ctx,OnStateChange callback) {
        setCurrentState(STATE_IDLE);
        mUrls=urls;
        mCurrentSongPosition=position;
       mStateChange=callback;
       mContext=ctx;
       if(mPlayer!=null){
           try {
               mPlayer.reset();
               mPlayer.setDataSource(mUrls.get(position));
               mPlayer.prepareAsync();
               setCurrentState(STATE_PREPARED);
           } catch (IOException e) {
               e.printStackTrace();
           }
       }else {
           try {
               mPlayer = new MediaPlayer();
               mPlayer.setWakeMode(mContext, PowerManager.PARTIAL_WAKE_LOCK);
               mPlayer.setOnBufferingUpdateListener(this);
               mPlayer.setOnCompletionListener(this);
               mPlayer.setOnErrorListener(this);
               mPlayer.setOnInfoListener(this);
               mPlayer.setOnPreparedListener(this);
               mPlayer.setOnSeekCompleteListener(this);
               if (mUrls != null) {
                   mPlayer.setDataSource(mUrls.get(position));
                   mPlayer.prepareAsync();
                   setCurrentState(STATE_PREPARED);
               }
           } catch (UnsatisfiedLinkError e) {
               e.printStackTrace();
           } catch (IOException e) {
               e.printStackTrace();
           }
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
            if(mStateChange!=null){
                mStateChange.onCompletion();
            }
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
        if (mCurrentState == STATE_PREPARED) {
            mp.start();
            setCurrentState(STATE_PLAYING);

        }
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }

    public class LocalBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
        }
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
        mPlayer.stop();
        mPlayer.reset();
        setCurrentState(STATE_STOPED);

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
        mRemoteViewsSmall = new RemoteViews(mContext.getPackageName(),
                R.layout.notification_control_mini);


        //Define what you want to do after clicked the button in notification.
        //Here we launcher a service by an action named "ACTION_STOP" which will stop the music play.
        close = new Intent(ACTION_CLOSE);
        pendingIntentclose = PendingIntent.getService(mContext,
                REQUEST_CODE_STOP, close,
                PendingIntent.FLAG_UPDATE_CURRENT);
        playpause = new Intent(ACTION_PLAYPAUSE);
        pendingIntentplaypause = PendingIntent.getService(mContext,
                REQUEST_CODE_STOP, playpause,
                PendingIntent.FLAG_UPDATE_CURRENT);

        forward = new Intent(ACTION_FORWARD);
        pendingIntentforward = PendingIntent.getService(mContext,
                REQUEST_CODE_STOP, forward,
                PendingIntent.FLAG_UPDATE_CURRENT);

        back = new Intent(ACTION_BACK);
        pendingIntentback = PendingIntent.getService(mContext,
                REQUEST_CODE_STOP, back,
                PendingIntent.FLAG_UPDATE_CURRENT);



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
        }else if(isPlaying()){
            mRemoteViewsSmall.setImageViewResource(R.id.mediacontroller_play_pause, R.drawable.ic_action_pause);
        }


        //Create the notification instance.
        mNotification = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.mipmap.ic_launcher).setOngoing(true)
                .setWhen(System.currentTimeMillis())
                .setContent(mRemoteViewsSmall)
                .setContentIntent(
                        PendingIntent.getActivity(mContext, 10,
                                new Intent(mContext, MainActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP),
                                0)
                )
                .build();


            startForeground(NOTIFICATION_ID, mNotification);


        //Show the notification in the notification bar.

    }

    public void setCurrentState(int mCurrentState) {
        this.mCurrentState = mCurrentState;
        if(mStateChange!=null){
            mStateChange.onStateChanged();
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(ACTION_PLAYPAUSE)) {
                    if(isPlaying()){
                        pause();
                        mRemoteViewsSmall.setImageViewResource(R.id.mediacontroller_play_pause, R.drawable.ic_action_play);
                    }else{
                        play();
                        mRemoteViewsSmall.setImageViewResource(R.id.mediacontroller_play_pause, R.drawable.ic_action_pause);
                    }
                    if (mNotifiManager == null) {
                        mNotifiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    }
                    startForeground(NOTIFICATION_ID, mNotification);
                }else if(action.equals(ACTION_BACK)) {
                    previousSong();
                }else if(action.equals(ACTION_FORWARD)){
                    nextSong();
                }else if(action.equals(ACTION_CLOSE)){
                    closePlayer();
                }
            }
        }
        return Service.START_STICKY;
    }

    public int getNextSongPosition(){
        mCurrentSongPosition=mCurrentSongPosition+1<mUrls.size()?mCurrentSongPosition+1:0;
        return mCurrentSongPosition;
    }
    public int getPreviousSongPosition(){
        mCurrentSongPosition=mCurrentSongPosition!=0?mCurrentSongPosition-1:mUrls.size()-1;
        return mCurrentSongPosition;
    }

    public boolean isPlaying(){
        return mCurrentState==STATE_PLAYING;
    }

    public interface OnStateChange{
        void onStateChanged();
        void onCompletion();
    }
    public int getDuration(){
        return mPlayer.getDuration();
    }
    public int getCurrentPosition(){
        return mPlayer.getCurrentPosition();
    }
    public int getCurrentState(){
        return mCurrentState;
    }

    public void killNotification() {
        if(isNotificationVisible) {
            stopForeground(true);
//            mHandler.removeCallbacks(mUpdateNotificationRunnable);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(NOTIFICATION_ID);
            isNotificationVisible = false;
        }
    }
    private void closePlayer(){
        stopSelf();
        killNotification();
        System.exit(0);

    }
}