package com.udacity.nanodegree.spotifystreamer.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.udacity.nanodegree.spotifystreamer.R;
import com.udacity.nanodegree.spotifystreamer.core.PlayerService;
import com.udacity.nanodegree.spotifystreamer.fragments.ArtistFragment;
import com.udacity.nanodegree.spotifystreamer.fragments.SongFragment;
import com.udacity.nanodegree.spotifystreamer.fragments.TopTracksFragment;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    private boolean mTabletView=false;
    private static PlayerService mPlayer;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_main);
        if(savedInstanceState==null) {
            mServiceIntent = new Intent(this, PlayerService.class);
            startService(mServiceIntent);
        }
        if(getSupportFragmentManager().findFragmentById(R.id.fragment_artist)!=null) {
            mTabletView=true;
            if (savedInstanceState == null ) {
                addFragmentToStack(new TopTracksFragment(), TopTracksFragment.TAG);
            }
        }else{
            mTabletView=false;
            if(savedInstanceState==null&&getSupportFragmentManager().getBackStackEntryCount()==0){
                addFragmentToStack(new ArtistFragment(),ArtistFragment.TAG);
            }
            shouldDisplayHomeUp();
        }
        getSupportFragmentManager().addOnBackStackChangedListener(this);

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


        return super.onOptionsItemSelected(item);
    }

    public Fragment addFragmentToStack(Fragment f, String tag){

        Fragment fg=getSupportFragmentManager().findFragmentByTag(tag);
        if(fg==null) {
            if(tag.equals(SongFragment.TAG)&&mTabletView){
                ((SongFragment)f).show(getSupportFragmentManager(),tag);
            }else {
                getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_content, f, tag).addToBackStack(tag).commit();
                shouldDisplayHomeUp();
            }

        }else{
            getSupportFragmentManager().popBackStackImmediate(tag, 0);
            getSupportFragmentManager().beginTransaction().commit();
            return fg;
        }
        return null;
    }

    public void shouldDisplayHomeUp(){
        //Enable Up button only  if there are entries in the back stack
        boolean canback = getSupportFragmentManager().getBackStackEntryCount()>1;
        if (!canback) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
            getSupportActionBar().setSubtitle("");

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
    }

    @Override
    public void onBackStackChanged() {
        shouldDisplayHomeUp();
        Fragment fmtoptrack=getSupportFragmentManager().findFragmentByTag(TopTracksFragment.TAG);
        Fragment fmsong=getSupportFragmentManager().findFragmentByTag(SongFragment.TAG);
        if(fmtoptrack!=null&&fmtoptrack.isAdded()&&fmsong==null){
            ((TopTracksFragment)fmtoptrack).updateTitle();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount()== 1){
            finish();
        }
        super.onBackPressed();
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null &&
                cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public PlayerService getPlayer(){
        return mPlayer;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mServiceIntent!=null&&vPlayerServiceConnection!=null) {
            bindService(mServiceIntent, vPlayerServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mPlayer!=null&&mPlayer.isPlaying()){
            mPlayer.showControllerInNotification();
        }
    }
    private ServiceConnection vPlayerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            PlayerService.LocalBinder binder=(PlayerService.LocalBinder) service;
            mPlayer = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayer = null;
        }
    };

    public boolean isTablet(){
        return mTabletView;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayer.killNotification();
        mPlayer.stopSelf();
    }
}
