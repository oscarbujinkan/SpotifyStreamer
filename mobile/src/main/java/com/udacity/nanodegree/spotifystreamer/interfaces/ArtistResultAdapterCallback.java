package com.udacity.nanodegree.spotifystreamer.interfaces;

import kaaes.spotify.webapi.android.models.Artist;

/**
 * Created by oscarBudo on 21-06-15.
 */
public interface ArtistResultAdapterCallback {

    void onItemClick(Artist artist);
}
