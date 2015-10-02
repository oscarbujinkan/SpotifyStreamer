package com.udacity.nanodegree.spotifystreamer.interfaces;

/**
 * Created by oscarfuentes on 02-10-15.
 */
public interface PlayerServiceInterface {
    public void play();
    public void pause();
    public void stop();
    public void nextSong();
    public void previousSong();
    public void seekTo(int second);
}
