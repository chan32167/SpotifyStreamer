package com.amstronghuang.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements SearchFragment.ArtistSearchFragmentCallback {

    boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTwoPane = getResources().getBoolean(R.bool.sw600);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
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
    public void onItemSelected(String artistID, String artistName) {
        if (mTwoPane) {
            TopSongsFragment topTracksFragment =
                    TopSongsFragment.newInstance(artistID, artistName);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.track_detail_container, topTracksFragment).commit();
        } else {
            Intent topSongsIntent = new Intent(this, TopSongsActivity.class);
            //ArtistDataSimple artist = (ArtistDataSimple) parent.getAdapter().getItem(position);
            topSongsIntent.putExtra("idArtist", artistID);
            topSongsIntent.putExtra("artist", artistName);
            startActivity(topSongsIntent);
        }
    }
}
