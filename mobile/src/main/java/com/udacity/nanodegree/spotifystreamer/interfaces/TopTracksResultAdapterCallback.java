package com.udacity.nanodegree.spotifystreamer.interfaces;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by oscarBudo on 21-06-15.
 */
public interface TopTracksResultAdapterCallback {

    void onItemClick(int position);
}
