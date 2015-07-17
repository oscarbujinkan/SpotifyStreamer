package com.udacity.nanodegree.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.adapters.holders.TopTracksResultHolder;
import com.udacity.nanodegree.spotifystreamer.interfaces.TopTracksResultAdapterCallback;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by oscarfuentes on 19-06-15.
 */
public class TopTracksResultAdapter extends RecyclerView.Adapter<TopTracksResultHolder>{

    private Tracks mTracks;
    private Context mContext;
    private TopTracksResultAdapterCallback mCallback;


    public TopTracksResultAdapter(Context context, Tracks tracks, TopTracksResultAdapterCallback l){
        mContext=context;
        mTracks =tracks;
        mCallback=l;

    }

    @Override
    public TopTracksResultHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View v= LayoutInflater.from(mContext).inflate(R.layout.top_track_result_item, viewGroup,false);
        TopTracksResultHolder holder= new TopTracksResultHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onItemClick(mTracks.tracks.get(i));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final TopTracksResultHolder topTracksResultHolder, int i) {
        if(i< mTracks.tracks.size()) {
            Track track= mTracks.tracks.get(i);
            topTracksResultHolder.topTrackName.setText(track.name);
            topTracksResultHolder.topAlbumName.setText(track.album.name);
            if(track.album.images.size()>0) {
                Picasso.with(mContext).load(track.album.images.get(0).url).into(topTracksResultHolder.topTrackImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (topTracksResultHolder.progress != null) {
                            topTracksResultHolder.progress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {
                        if (topTracksResultHolder.progress != null) {
                            topTracksResultHolder.progress.setVisibility(View.GONE);
                        }
                    }
                });
            }else{
                topTracksResultHolder.topTrackImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_no_image));
            }

        }

    }

    @Override
    public int getItemCount() {
        return mTracks.tracks.size();
    }
}
