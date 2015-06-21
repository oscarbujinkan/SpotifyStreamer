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
public class ArtistResultHolder extends RecyclerView.ViewHolder {

    public ImageView artistImage;
    public TextView artistName;
    public ProgressBar progress;

    public ArtistResultHolder(View itemView) {
        super(itemView);
        artistImage=(ImageView) itemView.findViewById(R.id.main_result_cover_artist);
        artistName=(TextView) itemView.findViewById(R.id.main_result_artist_text);
        progress=(ProgressBar) itemView.findViewById(R.id.main_result_cover_spinner);

    }
}
