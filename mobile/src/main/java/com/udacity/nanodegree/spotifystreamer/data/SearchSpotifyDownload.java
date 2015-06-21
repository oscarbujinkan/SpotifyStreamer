package com.udacity.nanodegree.spotifystreamer.data;

import android.os.AsyncTask;

import com.udacity.nanodegree.spotifystreamer.interfaces.SearchSpotifyCallback;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by oscarfuentes on 19-06-15.
 */
public class SearchSpotifyDownload {

    private SpotifyApi mSpotifyApi;
    private SpotifyService mSpotifyService;

    public SearchSpotifyDownload(){
        mSpotifyApi=new SpotifyApi();
        mSpotifyService= mSpotifyApi.getService();
    }

    public void searchArtist(final String query, final SearchSpotifyCallback ssc){
        new AsyncTask<Void,Void,ArtistsPager>(){

            @Override
            protected ArtistsPager doInBackground(Void... params) {
                ArtistsPager results = mSpotifyService.searchArtists(query);
                return results;
            }

            @Override
            protected void onPostExecute(ArtistsPager artist) {
                if(artist!=null){
                    ssc.onSuccess(artist);
                }else{
                    ssc.onFail();
                }

            }
        }.execute();
    }
    public void searchTopTenSongs(final String artistId, final SearchSpotifyCallback ssc){
        new AsyncTask<Void,Void,Tracks>(){

            @Override
            protected Tracks doInBackground(Void... params) {
                Tracks results = mSpotifyService.getArtistTopTrack(artistId);
                return results;
            }

            @Override
            protected void onPostExecute(Tracks artist) {
                if(artist!=null){
                    ssc.onSuccess(artist);
                }else{
                    ssc.onFail();
                }

            }
        }.execute();
    }
}
