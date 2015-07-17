package com.udacity.nanodegree.spotifystreamer.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.nanodegree.spotifystreamer.R;

/**
 * Created by oscarfuentes on 19-06-15.
 */
public class TopTracksResultHolder extends RecyclerView.ViewHolder {

    public ImageView topTrackImage;
    public TextView topTrackName;
    public TextView topAlbumName;
    public ProgressBar progress;

    public TopTracksResultHolder(View itemView) {
        super(itemView);
        topTrackImage =(ImageView) itemView.findViewById(R.id.top_track_result_cover_artist);
        topTrackName =(TextView) itemView.findViewById(R.id.top_track_result_artist_text);
        topAlbumName =(TextView) itemView.findViewById(R.id.top_track_result_album_text);
        progress=(ProgressBar) itemView.findViewById(R.id.top_track_result_cover_spinner);
        itemView.setClickable(true);

    }
}
