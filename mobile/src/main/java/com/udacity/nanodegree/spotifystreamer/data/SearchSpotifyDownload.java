package com.udacity.nanodegree.spotifystreamer.data;

import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udacity.nanodegree.spotifystreamer.core.Config;
import com.udacity.nanodegree.spotifystreamer.interfaces.SearchSpotifyCallback;
import com.udacity.nanodegree.spotifystreamer.models.SpotifyArtist;

import java.io.IOException;
import java.net.URL;

/**
 * Created by oscarfuentes on 19-06-15.
 */
public class SearchSpotifyDownload {

    public void searchArtist(final String query, final SearchSpotifyCallback ssc){
        new AsyncTask<Void,Void,SpotifyArtist>(){

            @Override
            protected SpotifyArtist doInBackground(Void... params) {
                ObjectMapper mapper = new ObjectMapper();

                try {

                        SpotifyArtist artist = mapper.readValue(new URL(String.format(Config.SEARCH_ARTIST_URL,query)), SpotifyArtist.class);

                    return artist;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SpotifyArtist artist) {
                if(artist!=null){
                    ssc.onSuccess(artist);
                }else{
                    ssc.onFail();
                }

            }
        }.execute();
    }
}
