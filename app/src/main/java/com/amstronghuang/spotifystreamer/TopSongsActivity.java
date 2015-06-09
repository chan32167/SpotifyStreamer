package com.amstronghuang.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.amstronghuang.spotifystreamer.adapter.TopSongsAdapter;
import com.amstronghuang.spotifystreamer.model.TrackDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Track;
import kaaes.spotify.webapi.android.models.Tracks;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class TopSongsActivity extends AppCompatActivity {

    @InjectView(R.id.topSongsLV)
    protected ListView artistsLV;

    protected TopSongsAdapter topSongsAdapter;

    protected ArrayList<TrackDataSimple> trackList;

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
        Fresco.initialize(this);

        SpotifyApi api = new SpotifyApi();

        final SpotifyService spotify = api.getService();

        if (savedInstanceState == null || !savedInstanceState.containsKey("trackList")) {
            trackList = new ArrayList<>();
        } else {
            trackList = savedInstanceState.getParcelableArrayList("trackList");
        }

        topSongsAdapter = new TopSongsAdapter(this, trackList);

        artistsLV.setAdapter(topSongsAdapter);

        Map<String, Object> extraData = new HashMap<>();
        extraData.put("country", "US");

        spotify.getArtistTopTrack(getIntent().getStringExtra("idArtist"), extraData, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                trackList.clear();
                for (Track track : tracks.tracks) {
                    trackList.add(new TrackDataSimple(track.id, track.album.images != null && !track.album.images.isEmpty() ? track.album.images.get(0).url : null, track.album.name, track.name));
                }
                TopSongsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        topSongsAdapter.notifyDataSetChanged();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Top " + trackList.size() + " Track");
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trackList", trackList);
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
