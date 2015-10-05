package com.udacity.nanodegree.spotifystreamer.fragments;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Wearable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.core.PlayerService;
import com.udacity.nanodegree.spotifystreamer.utils.SpotifyUtils;

import java.io.IOException;
import java.util.ArrayList;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by oscarfuentes on 21-07-15.
 */
public class SongFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, PlayerService.OnStateChange{

    public static final String TAG="SongFragment";
    private static final long TiME_UPDATE_PROGRESS=1000;

    private TextView mArtistName;
    private TextView mAlbumName;
    private TextView mSongName;
    private TextView mActualTime;
    private TextView mEndTime;
    private ImageView mSongImage;
    private ImageButton mBackButton;
    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;
    private SeekBar mSeekBar;
    private Track mTrack;
    private String mArtist;
    private PlayerService mPlayer;
    private int mCurrentTrackPosition;
    private Tracks mTracks;
    private ArrayList<String> mUrls;
    private GoogleApiClient mGoogleApiClient;
    private Handler mHandler=new Handler();



    private Runnable mProgressRunnable= new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(this);
            if(mPlayer.isPlaying()) {
                int progress = (int) ((mPlayer.getCurrentPosition()* 100) / mPlayer.getDuration()) ;
                mSeekBar.setProgress(progress);
                String minutes = (String.valueOf(mPlayer.getCurrentPosition() / 60000));
                int secondsint=(mPlayer.getCurrentPosition() % 60000) / 1000;
                String seconds = secondsint<10?"0"+String.valueOf(secondsint):String.valueOf(secondsint);
                mActualTime.setText(minutes + ":" + seconds);
            }
            mHandler.postDelayed(this, TiME_UPDATE_PROGRESS);
        }
    };



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.song_fragment,container,false);
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        if(bundle!=null) {
                            SpotifyUtils.logd(bundle.toString());
                        }
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        SpotifyUtils.logd(String.valueOf(i));
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        SpotifyUtils.logd(String.valueOf(connectionResult.getErrorCode()));
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        init(v);
        setViewsListeners();
        updateTrack();
        return v;
    }
    @Override
    public void onStart() {
        super.onStart();
        if (null != mGoogleApiClient && !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    private void init(View v){
        mArtistName=(TextView) v.findViewById(R.id.song_fragment_artist_name);
        mAlbumName=(TextView) v.findViewById(R.id.song_fragment_album_name);
        mSongName=(TextView) v.findViewById(R.id.song_fragment_song_name);
        mActualTime=(TextView) v.findViewById(R.id.song_fragment_actual_time);
        mEndTime=(TextView) v.findViewById(R.id.song_fragment_end_time);
        mSongImage=(ImageView) v.findViewById(R.id.song_fragment_song_image);
        mBackButton=(ImageButton) v.findViewById(R.id.song_fragment_back_button);
        mPlayPauseButton=(ImageButton) v.findViewById(R.id.song_fragment_play_button);
        mNextButton=(ImageButton) v.findViewById(R.id.song_fragment_next_button);
        mSeekBar=(SeekBar) v.findViewById(R.id.song_fragment_progress_bar);
        mPlayer=new PlayerService();
        mPlayer.initPlayer(mUrls, mCurrentTrackPosition, getActivity(), this);
    }
    private void setViewsListeners(){
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayer.isPlaying()){
                    mPlayer.pause();
                    mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
                }else{
                    mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
                    mPlayer.play();
                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentTrackPosition!=0){
                    mCurrentTrackPosition=mCurrentTrackPosition-1;
                    mTrack=mTracks.tracks.get(mCurrentTrackPosition);
                    updateTrack();
                }else{
                    mCurrentTrackPosition=mTracks.tracks.size()-1;
                    mTrack=mTracks.tracks.get(mCurrentTrackPosition);
                    updateTrack();
                }
                mPlayer.previousSong();
            }
        });
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentTrackPosition<mTracks.tracks.size()){
                    mCurrentTrackPosition=mCurrentTrackPosition+1;
                    mTrack=mTracks.tracks.get(mCurrentTrackPosition);
                    updateTrack();
                }else{
                    mCurrentTrackPosition=0;
                    mTrack=mTracks.tracks.get(mCurrentTrackPosition);
                    updateTrack();
                }
                mPlayer.nextSong();
            }
        });
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }
                if (mPlayer.getCurrentState() != PlayerService.STATE_IDLE && mPlayer.getCurrentState() != PlayerService.STATE_NOT_INIT) {
                    int seek = (int) (mPlayer.getDuration() * (progress / 100f));
                    mPlayer.seekTo(seek);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void updateTrack(){
        if(mTrack!=null){
            if(mArtist!=null){
                mArtistName.setText(mArtist);
            }
            mAlbumName.setText(mTrack.album.name);
            mSongName.setText(mTrack.name);
            String minutes=(String.valueOf(mTrack.duration_ms/60000));
            String seconds=String.valueOf((mTrack.duration_ms%60000)/1000);
            mEndTime.setText(minutes + ":" + seconds);

            if(mTrack.album.images.size()>0) {
                Picasso.with(getActivity()).load(mTrack.album.images.get(0).url).into(mSongImage, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        mSongImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_image));
                    }
                });
            }else{
                mSongImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_image));
            }
            mSeekBar.setProgress(0);
            mActualTime.setText("00:00");
        }
    }

    public void setTrack(String artist, Tracks tracks,int position){
        mTrack=tracks.tracks.get(position);
        mArtist=artist;
        mTracks=tracks;
        mCurrentTrackPosition =position;
        mUrls=new ArrayList<String>();
        for(int i=0;i<mTracks.tracks.size();i++){
            mUrls.add(mTracks.tracks.get(i).preview_url);
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            mPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStateChanged() {
        if(!this.isDetached()) {
            if (mPlayer.isPlaying()) {
                mHandler.removeCallbacks(mProgressRunnable);
                mHandler.postDelayed(mProgressRunnable, TiME_UPDATE_PROGRESS);
                String minutes = (String.valueOf(mPlayer.getDuration() / 60000));
                String seconds = String.valueOf((mPlayer.getDuration() % 60000) / 1000);
                mEndTime.setText(minutes + ":" + seconds);
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
            } else {
                mHandler.removeCallbacks(mProgressRunnable);
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_play));
            }
        }

    }

    @Override
    public void onCompletion() {
        if(mCurrentTrackPosition<mTracks.tracks.size()){
            mCurrentTrackPosition=mCurrentTrackPosition+1;
            mTrack=mTracks.tracks.get(mCurrentTrackPosition);
            updateTrack();
        }else{
            mCurrentTrackPosition=0;
            mTrack=mTracks.tracks.get(mCurrentTrackPosition);
            updateTrack();
        }
    }


//    @Override
//    public void run() {
//        int currentPosition = 0;
//        int total = mPlayer.getDuration();
//        mSeekBar.setMax(total);
//        while (mPlayer != null && currentPosition < total) {
//            try {
//                Thread.sleep(1000);
//                currentPosition = mPlayer.getCurrentPosition();
//            } catch (InterruptedException e) {
//                return;
//            } catch (Exception e) {
//                return;
//            }
//            mSeekBar.setProgress(currentPosition);
//        }
//    }
}
