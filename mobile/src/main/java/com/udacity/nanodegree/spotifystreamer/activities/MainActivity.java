package com.udacity.nanodegree.spotifystreamer.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.adapters.ArtistsResultAdapter;
import com.udacity.nanodegree.spotifystreamer.data.SearchSpotifyDownload;
import com.udacity.nanodegree.spotifystreamer.interfaces.ArtistResultAdapterCallback;
import com.udacity.nanodegree.spotifystreamer.interfaces.SearchSpotifyCallback;

import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import kaaes.spotify.webapi.android.models.Tracks;

public class MainActivity extends AppCompatActivity implements SearchSpotifyCallback, ArtistResultAdapterCallback{

    private RecyclerView mRecyclerView;
    private EditText mSearchBox;
    private static final long TIME_TO_SEARCH=1000;
    private static final int MIN_SEARCH_TEXT_SIZE=3;
    private boolean isSearching=false;
    private Handler mHandler=new Handler();
    private SearchSpotifyDownload mSearchSpotifyDownload;
    private ArtistsResultAdapter mAdapter;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView=(RecyclerView) findViewById(R.id.main_result_recyclerview);
        mSearchBox=(EditText) findViewById(R.id.main_result_search_box);
        mSearchBox.addTextChangedListener(mSearchBoxWatcher);
        mSearchSpotifyDownload=new SearchSpotifyDownload();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    private void searchArtist(){
        isSearching=true;
        mSearchSpotifyDownload.searchArtist(mSearchBox.getText().toString(),this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
            mAdapter = new ArtistsResultAdapter(this, (ArtistsPager) object, this);
            mRecyclerView.setAdapter(mAdapter);
            isSearching = false;
        }else if(object instanceof Tracks){

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
