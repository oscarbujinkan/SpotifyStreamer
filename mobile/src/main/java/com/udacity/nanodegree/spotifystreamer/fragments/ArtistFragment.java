package com.udacity.nanodegree.spotifystreamer.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.activities.MainActivity;
import com.udacity.nanodegree.spotifystreamer.adapters.ArtistsResultAdapter;
import com.udacity.nanodegree.spotifystreamer.data.SearchSpotifyDownload;
import com.udacity.nanodegree.spotifystreamer.interfaces.ArtistResultAdapterCallback;
import com.udacity.nanodegree.spotifystreamer.interfaces.SearchSpotifyCallback;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

/**
 * Created by oscarBudo on 21-06-15.
 */
public class ArtistFragment extends Fragment implements SearchSpotifyCallback, ArtistResultAdapterCallback {
    private RecyclerView mRecyclerView;
    private EditText mSearchBox;
    private static final long TIME_TO_SEARCH=1000;
    private static final int MIN_SEARCH_TEXT_SIZE=3;
    private boolean isSearching=false;
    private Handler mHandler=new Handler();
    private SearchSpotifyDownload mSearchSpotifyDownload;
    private ArtistsResultAdapter mAdapter;
    public static final String TAG="ArtistFragment";


    private Runnable mSearchRunnable=new Runnable() {
        @Override
        public void run() {
            if(!isSearching){
                searchArtist();
            }
        }
    };

    private TextWatcher mSearchBoxWatcher= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mHandler.removeCallbacks(mSearchRunnable);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length()>MIN_SEARCH_TEXT_SIZE){
                mHandler.removeCallbacks(mSearchRunnable);
                mHandler.postDelayed(mSearchRunnable,TIME_TO_SEARCH);
            }
        }
    };
    private TextView.OnEditorActionListener mSearchBoxEditorListener= new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                mHandler.removeCallbacks(mSearchRunnable);
                searchArtist();
                return true;
            }
            return false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v=LayoutInflater.from(getActivity()).inflate(R.layout.artist_fragment,container,false);
        mRecyclerView=(RecyclerView) v.findViewById(R.id.artist_result_recyclerview);
        mSearchBox=(EditText) v.findViewById(R.id.artist_result_search_box);
        mSearchBox.addTextChangedListener(mSearchBoxWatcher);
        mSearchBox.setOnEditorActionListener(mSearchBoxEditorListener);
        mSearchSpotifyDownload=new SearchSpotifyDownload();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return v;
    }

    private void searchArtist(){
        isSearching=true;
        mSearchSpotifyDownload.searchArtist(mSearchBox.getText().toString(),this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(Object object) {
        if(object instanceof ArtistsPager) {
            mAdapter = new ArtistsResultAdapter(getActivity(), (ArtistsPager) object, this);
            mRecyclerView.setAdapter(mAdapter);
            isSearching = false;
        }else if(object instanceof Tracks){
            TopTracksFragment ttf=new TopTracksFragment();
            ttf.setTracks((Tracks) object);
            ((MainActivity) getActivity()).addFragmentToStack(ttf, TopTracksFragment.TAG);
        }
    }

    @Override
    public void onFail() {
        isSearching=false;
    }

    @Override
    public void onItemClick(Artist artist) {
        mSearchSpotifyDownload.searchTopTenSongs(artist.id,this);
    }

}
