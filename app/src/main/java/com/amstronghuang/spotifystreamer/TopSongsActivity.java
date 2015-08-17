package com.amstronghuang.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.amstronghuang.spotifystreamer.model.TrackDataSimple;

import java.util.ArrayList;

import butterknife.ButterKnife;


public class TopSongsActivity extends AppCompatActivity {

    protected ArrayList<TrackDataSimple> trackDataSimpleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_songs);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Top Tracks");
            getSupportActionBar().setSubtitle(getIntent().getStringExtra("artist"));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.inject(this);

        TopSongsFragment topTracksFragment =
                TopSongsFragment.newInstance(getIntent().getStringExtra("idArtist"), getIntent().getStringExtra("artis"));

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.track_detail_container, topTracksFragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trackDataSimpleList", trackDataSimpleList);
        super.onSaveInstanceState(outState);
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
}
