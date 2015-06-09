package com.amstronghuang.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amstronghuang.spotifystreamer.adapter.ArtistAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.artistsLV)
    protected ListView artistsLV;

    @InjectView(R.id.searchET)
    protected EditText searchET;

    @InjectView(R.id.messageTV)
    protected TextView messageTV;

    protected ArtistAdapter artistAdapter;

    protected List<Artist> artistList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Fresco.initialize(this);

        SpotifyApi api = new SpotifyApi();

        final SpotifyService spotify = api.getService();

        artistList = new ArrayList<>();

        artistAdapter = new ArtistAdapter(this, artistList);

        artistsLV.setAdapter(artistAdapter);

        artistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent topSongsIntent = new Intent(MainActivity.this, TopSongsActivity.class);
                Artist artist = (Artist) parent.getAdapter().getItem(position);
                topSongsIntent.putExtra("idArtist", artist.id);
                topSongsIntent.putExtra("artist", artist.name);
                startActivity(topSongsIntent);
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    artistList.clear();
                    artistAdapter.notifyDataSetChanged();
                    messageTV.setText("");
                } else {
                    spotify.searchArtists(s.toString(), new Callback<ArtistsPager>() {
                                @Override
                                public void success(ArtistsPager artistsPager, Response response) {
                                    artistList.clear();
                                    artistList.addAll(artistsPager.artists.items);
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            artistAdapter.notifyDataSetChanged();
                                            if (artistList.isEmpty()) {
                                                messageTV.setText("No results, please refine search");
                                            } else {
                                                messageTV.setText("");
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void failure(RetrofitError error) {

                                }
                            }

                    );
                }
            }
        });

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
