package com.amstronghuang.spotifystreamer;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.amstronghuang.spotifystreamer.model.TrackDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class MusicPlayerActivity extends AppCompatActivity {

    private ArrayList<TrackDataSimple> trackDataSimpleList;

    MusicPlayerFragment musicPlayerFragment;

    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        mTwoPane = getResources().getBoolean(R.bool.sw600);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Top Tracks");
            getSupportActionBar().setSubtitle(getIntent().getStringExtra("artist"));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.bind(this);
        Fresco.initialize(this);

        showDialog();

    }

    public void showDialog() {
        trackDataSimpleList = getIntent().getParcelableArrayListExtra("trackDataSimpleList");
        FragmentManager fragmentManager = getSupportFragmentManager();
        musicPlayerFragment =
                MusicPlayerFragment.newInstance(trackDataSimpleList, getIntent().getIntExtra("position", 0));

        if (mTwoPane) {
            // The device is using a large layout, so show the fragment as a dialog
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(musicPlayerFragment, "dialog");
            musicPlayerFragment.show(fragmentManager, "dialog");
        } else {
            // The device is smaller, so show the fragment fullscreen
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            // For a little polish, specify a transition animation
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
            transaction.replace(android.R.id.content, musicPlayerFragment)
                    .addToBackStack(null).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_player, menu);
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
}
