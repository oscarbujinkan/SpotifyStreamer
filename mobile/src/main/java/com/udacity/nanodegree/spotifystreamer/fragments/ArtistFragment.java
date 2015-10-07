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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static final long TIME_TO_SEARCH=800;
    private static final String SEARCHING_BOOL="issearching";
    private static final String ARISTS_RESULT="artists";
    private static final String POSITION_SELECTED_KEY="selected_key";
    private static final int MIN_SEARCH_TEXT_SIZE=3;
    private boolean isSearching=false;
    private Handler mHandler=new Handler();
    private SearchSpotifyDownload mSearchSpotifyDownload;
    private ArtistsResultAdapter mAdapter;
    private String mActualArtist;
    private static ArtistsPager mActualArtists;
    public static final String TAG="ArtistFragment";
    private int mCurrentPosition;
    private boolean mConfigurationChange=false;
    private ProgressBar mSpinner;


    private Runnable mSearchRunnable=new Runnable() {
        @Override
        public void run() {
            if(!isSearching){
                if(!mConfigurationChange) {
                    searchArtist();
                }else{
                    mConfigurationChange=false;
                }
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
        mSpinner=(ProgressBar) v.findViewById(R.id.artist_result_spinner);
        mSearchSpotifyDownload=new SearchSpotifyDownload();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(savedInstanceState!=null){
            if(mActualArtists!=null){
                mHandler.removeCallbacks(mSearchRunnable);
                mAdapter = new ArtistsResultAdapter(getActivity(), mActualArtists, this);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
        if(savedInstanceState!=null&&savedInstanceState.containsKey(POSITION_SELECTED_KEY)){
            mCurrentPosition=savedInstanceState.getInt(POSITION_SELECTED_KEY);
            if(mAdapter!=null) {
                mAdapter.setSelectedItem(mCurrentPosition);
                mAdapter.notifyDataSetChanged();
            }
            mRecyclerView.scrollToPosition(mCurrentPosition);
        }
        return v;
    }


    private void searchArtist(){
        isSearching=true;
        mSpinner.setVisibility(View.VISIBLE);
        mSearchSpotifyDownload.searchArtist(mSearchBox.getText().toString(),this);

    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            ArtistFragment af= (ArtistFragment) getActivity().getSupportFragmentManager().getFragment(savedInstanceState,this.getClass().getName());
            mActualArtists=af.getActualArtists();
            isSearching=false;
            mConfigurationChange=true;

        }
        mSearchBox.addTextChangedListener(mSearchBoxWatcher);
        mSearchBox.setOnEditorActionListener(mSearchBoxEditorListener);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION_SELECTED_KEY,mCurrentPosition);
        getActivity().getSupportFragmentManager().putFragment(outState, this.getClass().getName(), this);

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


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSuccess(Object object) {
        if(object instanceof ArtistsPager) {
            if(((ArtistsPager) object)!=null&&((ArtistsPager) object).artists!=null&&((ArtistsPager) object).artists.items.size()>0) {
                mActualArtists=(ArtistsPager) object;
                if(getActivity()!=null) {
                    mAdapter = new ArtistsResultAdapter(getActivity(), (ArtistsPager) object, this);
                    mRecyclerView.setAdapter(mAdapter);
                }
            }else{
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), R.string.no_artist_found_message, Toast.LENGTH_LONG).show();
                }
            }
            isSearching = false;
        }else if(object instanceof Tracks){
            TopTracksFragment ttf=new TopTracksFragment();
            ttf.setTracks((Tracks) object);
            ttf.setArtist(mActualArtist);
            Fragment fg=((MainActivity) getActivity()).addFragmentToStack(ttf, TopTracksFragment.TAG);
            if(fg!=null){
                ((TopTracksFragment)fg).setTracks((Tracks) object);
                ((TopTracksFragment)fg).setArtist(mActualArtist);
                ((TopTracksFragment)fg).updateList();
            }
        }
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onFail() {
        isSearching=false;
        if(getActivity()!=null) {
            if (!((MainActivity) getActivity()).isOnline()) {
                Toast.makeText(getActivity(), R.string.no_connection, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getActivity(), R.string.no_artist_found_message, Toast.LENGTH_LONG).show();
            }
        }
        mSpinner.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(Artist artist, int position) {
        mActualArtist=artist.name;
        mSearchSpotifyDownload.searchTopTenSongs(artist.id,this);
        mCurrentPosition=position;
        mAdapter.notifyDataSetChanged();
    }
    public ArtistsPager getActualArtists(){
        return mActualArtists;
    }
}
