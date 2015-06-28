package com.amstronghuang.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
        Fresco.initialize(this);

        SpotifyApi api = new SpotifyApi();

        final SpotifyService spotify = api.getService();

        if (savedInstanceState == null || !savedInstanceState.containsKey("trackDataSimpleList")) {
            trackDataSimpleList = new ArrayList<>();
        } else {
            trackDataSimpleList = savedInstanceState.getParcelableArrayList("trackDataSimpleList");
        }

        topSongsAdapter = new TopSongsAdapter(this, trackDataSimpleList);

        artistsLV.setAdapter(topSongsAdapter);

        Map<String, Object> extraData = new HashMap<>();
        extraData.put("country", "US");

        spotify.getArtistTopTrack(getIntent().getStringExtra("idArtist"), extraData, new Callback<Tracks>() {
            @Override
            public void success(Tracks tracks, Response response) {
                trackDataSimpleList.clear();
                for (Track track : tracks.tracks) {
                    trackDataSimpleList.add(new TrackDataSimple(track.id, track.album.images != null && !track.album.images.isEmpty() ? track.album.images.get(0).url : null, track.album.name, track.name, track.artists.get(0).name, track.preview_url));
                }
                TopSongsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        topSongsAdapter.notifyDataSetChanged();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle("Top " + trackDataSimpleList.size() + " Track");
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

        artistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent musicPlayerIntent = new Intent(TopSongsActivity.this, MusicPlayerActivity.class);
                musicPlayerIntent.putExtra("position", position);
                musicPlayerIntent.putParcelableArrayListExtra("trackDataSimpleList", trackDataSimpleList);
                startActivity(musicPlayerIntent);
            }
        });
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
