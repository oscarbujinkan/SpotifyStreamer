package com.udacity.nanodegree.spotifystreamer.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.activities.MainActivity;
import com.udacity.nanodegree.spotifystreamer.adapters.TopTracksResultAdapter;
import com.udacity.nanodegree.spotifystreamer.interfaces.TopTracksResultAdapterCallback;

import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * For save the instance I use some code find in stackoverflow was parts of
 * code.
 *
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
        View v= inflater.inflate(R.layout.top_track_fragment,container,false);
        mTopRecyclerView=(RecyclerView) v.findViewById(R.id.top_track_result_recyclerview);
        mTopRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(mTracks!=null) {
            mAdapter = new TopTracksResultAdapter(getActivity(), mTracks, this);
            mTopRecyclerView.setAdapter(mAdapter);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(((MainActivity)getActivity()).isTablet()){
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtist);
        }else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.top_track_title));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtist);

        }
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    public void setTracks(Tracks tracks){
        mTracks=tracks;
    }
    public Tracks getTracks(){
        return mTracks;
    }
    public void setArtist(String artist){
        mArtist=artist;
        if(getActivity()!=null&&((MainActivity)getActivity()).isTablet()){
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtist);
        }
    }

   @Override
    public void onItemClick(int i) {
        SongFragment sf=new SongFragment();
        sf.setTrack(mArtist,mTracks,i);
        ((MainActivity) getActivity()).addFragmentToStack(sf, SongFragment.TAG);

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

    public void updateList(){
        mAdapter=new TopTracksResultAdapter(getActivity(),mTracks,this);
        mTopRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getActivity().getSupportFragmentManager().putFragment(outState, this.getClass().getName(), this);
    }

    public void updateTitle(){
        if(((MainActivity)getActivity()).isTablet()){
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtist);
        }else{
            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.top_track_title));
            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(mArtist);

        }
    }

}
