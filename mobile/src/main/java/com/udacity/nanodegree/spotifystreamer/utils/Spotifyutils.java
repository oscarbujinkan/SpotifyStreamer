package com.udacity.nanodegree.spotifystreamer.utils;

import android.util.Log;

import com.udacity.nanodegree.spotifystreamer.core.Config;

/**
 * Created by oscarfuentes on 05-10-15.
 */
public class SpotifyUtils {

    public static final String TAG="SpotifyUtils";

    public static void logd(String tag,String msg){
        if(Config.DEBUG){
            Log.d(tag,msg);
        }
    }
    public static void logd(String msg){
        if(Config.DEBUG){
            Log.d(TAG,msg);
        }
    }

}
