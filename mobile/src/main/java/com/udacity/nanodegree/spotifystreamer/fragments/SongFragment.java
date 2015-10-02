package com.udacity.nanodegree.spotifystreamer.fragments;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.spotifystreamer.R;

import java.io.IOException;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by oscarfuentes on 21-07-15.
 */
public class SongFragment extends Fragment implements Runnable,SeekBar.OnSeekBarChangeListener{

    public static final String TAG="SongFragment";

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
    private MediaPlayer mPlayer;
    private int mCurrentTrackPosition;
    private Tracks mTracks;

    private MediaPlayer.OnPreparedListener mOnPrepared=new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            String minutes=(String.valueOf(mp.getDuration()/60000));
            String seconds=String.valueOf((mp.getDuration() % 60000) / 1000);
            mEndTime.setText(minutes + ":" + seconds);
            if(!mp.isPlaying()){
                mp.start();
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
            }else{
                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_pause));
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.song_fragment,container,false);
        init(v);
        setViewsListeners();
        updateTrack();
        return v;
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
        mPlayer=new MediaPlayer();
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
                    mPlayer.start();
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
            }
        });
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
            mPlayer.setOnPreparedListener(mOnPrepared);
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mPlayer.setDataSource(mTrack.preview_url);
                mPlayer.prepareAsync();
                mPlayer.start();
                new Thread(this).start();
            } catch (IOException e) {
                e.printStackTrace();
            }

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

        }
    }

    public void setTrack(String artist, Tracks tracks,int position){
        mTrack=tracks.tracks.get(position);
        mArtist=artist;
        mTracks=tracks;
        mCurrentTrackPosition =position;

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
    public void run() {
        int currentPosition = 0;
        int total = mPlayer.getDuration();
        mSeekBar.setMax(total);
        while (mPlayer != null && currentPosition < total) {
            try {
                Thread.sleep(1000);
                currentPosition = mPlayer.getCurrentPosition();
            } catch (InterruptedException e) {
                return;
            } catch (Exception e) {
                return;
            }
            mSeekBar.setProgress(currentPosition);
        }
    }
}
