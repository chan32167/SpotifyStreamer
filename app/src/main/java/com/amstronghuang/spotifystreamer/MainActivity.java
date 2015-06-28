package com.amstronghuang.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.amstronghuang.spotifystreamer.model.ArtistDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

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

    protected ArrayList<ArtistDataSimple> artistList;

    private String searchText;

    SpotifyApi api = new SpotifyApi();

    SpotifyService spotify = api.getService();

    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            searchArtist();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Fresco.initialize(this);

        if (savedInstanceState == null || !savedInstanceState.containsKey("artistList")) {
            artistList = new ArrayList<>();
        } else {
            artistList = savedInstanceState.getParcelableArrayList("artistList");
        }

        artistAdapter = new ArtistAdapter(this, artistList);

        artistsLV.setAdapter(artistAdapter);

        artistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent topSongsIntent = new Intent(MainActivity.this, TopSongsActivity.class);
                ArtistDataSimple artist = (ArtistDataSimple) parent.getAdapter().getItem(position);
                topSongsIntent.putExtra("idArtist", artist.getId());
                topSongsIntent.putExtra("artist", artist.getArtistName());
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
                searchText = s.toString();
                if (s.toString().isEmpty()) {
                    artistList.clear();
                    artistAdapter.notifyDataSetChanged();
                    messageTV.setText("");
                } else {
                    handler.removeCallbacks(runnable);
                    handler.postDelayed(runnable, 300);
                }
            }
        });

    }

    private void searchArtist(){
        spotify.searchArtists(searchText, new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        artistList.clear();
                        for (Artist artist : artistsPager.artists.items) {
                            artistList.add(new ArtistDataSimple(artist.id, artist.images != null && !artist.images.isEmpty() ? artist.images.get(0).url : null, artist.name));
                        }
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("artistList", artistList);
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
