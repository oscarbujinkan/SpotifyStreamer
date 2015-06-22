package com.udacity.nanodegree.spotifystreamer.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.adapters.holders.ArtistResultHolder;
import com.udacity.nanodegree.spotifystreamer.interfaces.ArtistResultAdapterCallback;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * Created by oscarfuentes on 19-06-15.
 */
public class ArtistsResultAdapter extends RecyclerView.Adapter<ArtistResultHolder>{

    private ArtistsPager mArtists;
    private Context mContext;
    private ArtistResultAdapterCallback mCallback;


    public ArtistsResultAdapter(Context context, ArtistsPager artists, ArtistResultAdapterCallback l){
        mContext=context;
        mArtists=artists;
        mCallback=l;

    }

    @Override
    public ArtistResultHolder onCreateViewHolder(ViewGroup viewGroup, final int i) {

        View v= LayoutInflater.from(mContext).inflate(R.layout.artist_result_item, viewGroup,false);
        ArtistResultHolder holder= new ArtistResultHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallback.onItemClick(mArtists.artists.items.get(i));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ArtistResultHolder artistResultHolder, int i) {
        if(i<mArtists.artists.items.size()) {
            Artist artist=mArtists.artists.items.get(i);
            artistResultHolder.artistName.setText(artist.name);
            if(artist.images.size()>0) {
                Picasso.with(mContext).load(artist.images.get(0).url).into(artistResultHolder.artistImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        if (artistResultHolder.progress != null) {
                            artistResultHolder.progress.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError() {
                        if (artistResultHolder.progress != null) {
                            artistResultHolder.progress.setVisibility(View.GONE);
                        }
                    }
                });
            }else{
                artistResultHolder.artistImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_no_image));
            }

        }

    }

    @Override
    public int getItemCount() {
        return mArtists.artists.items.size();
    }
}
