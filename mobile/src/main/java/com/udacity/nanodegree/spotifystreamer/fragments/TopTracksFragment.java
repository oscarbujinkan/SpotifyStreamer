package com.udacity.nanodegree.spotifystreamer.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.adapters.TopTracksResultAdapter;
import com.udacity.nanodegree.spotifystreamer.interfaces.TopTracksResultAdapterCallback;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by oscarBudo on 21-06-15.
 */
public class TopTracksFragment extends Fragment implements TopTracksResultAdapterCallback{

    private RecyclerView mTopRecyclerView;
    private Tracks mTracks;
    public static final String TAG="TopTrackFragment";
    private TopTracksResultAdapter mAdapter;
    private String mArtist;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.top_track_title));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtist);
        View v= inflater.inflate(R.layout.top_track_fragment,container,false);
        mTopRecyclerView=(RecyclerView) v.findViewById(R.id.top_track_result_recyclerview);
        mTopRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(mTracks!=null) {
            mAdapter = new TopTracksResultAdapter(getActivity(), mTracks, this);
            mTopRecyclerView.setAdapter(mAdapter);
        }
        return v;
    }

    public void setTracks(Tracks tracks){
        mTracks=tracks;
    }
    public Tracks getTracks(){
        return mTracks;
    }
    public void setArtist(String artist){
        mArtist=artist;
    }

    @Override
    public void onItemClick(Track track) {

    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            TopTracksFragment ttf= (TopTracksFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState,this.getClass().getName());
            mTracks=ttf.getTracks();
            mAdapter=new TopTracksResultAdapter(getActivity(),mTracks,this);
            mTopRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getActivity().getSupportFragmentManager().putFragment(outState,this.getClass().getName(),this);
    }

}
