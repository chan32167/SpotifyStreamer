package com.amstronghuang.spotifystreamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import com.amstronghuang.spotifystreamer.adapter.ArtistAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
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

        spotify.getArtistTopTrack("");

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                spotify.searchArtists(s.toString(), new Callback<ArtistsPager>() {
                            @Override
                            public void success(ArtistsPager artistsPager, Response response) {
                                artistList.clear();
                                artistList.addAll(artistsPager.artists.items);
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        artistAdapter.notifyDataSetChanged();
                                    }
                                });

                            }

                            @Override
                            public void failure(RetrofitError error) {

                            }
                        }

                );
            }
        });


        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new Callback<Album>()

                {
                    @Override
                    public void success(Album album, Response response) {
                        Log.d("Album success", album.name);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d("Album failure", error.toString());
                    }
                }

        );
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
