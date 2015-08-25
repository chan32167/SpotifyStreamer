package com.amstronghuang.spotifystreamer;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.amstronghuang.spotifystreamer.adapter.ArtistAdapter;
import com.amstronghuang.spotifystreamer.model.ArtistDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class SearchFragment extends Fragment {

    @Bind(R.id.artistsLV)
    protected ListView artistsLV;

    @Bind(R.id.searchET)
    protected EditText searchET;

    @Bind(R.id.messageTV)
    protected TextView messageTV;

    protected ArtistAdapter artistAdapter;

    protected ArrayList<ArtistDataSimple> artistList;
    SpotifyApi api = new SpotifyApi();
    SpotifyService spotify = api.getService();
    private String searchText;
    private Handler handler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (getActivity() != null) {
                searchArtist();
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_main, container, false);
        ButterKnife.bind(this, rootView);
        Fresco.initialize(this.getActivity());

        if (savedInstanceState == null || !savedInstanceState.containsKey("artistList")) {
            artistList = new ArrayList<>();
        } else {
            artistList = savedInstanceState.getParcelableArrayList("artistList");
        }

        artistAdapter = new ArtistAdapter(this.getActivity(), artistList);

        artistsLV.setAdapter(artistAdapter);

        artistsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistDataSimple artist = (ArtistDataSimple) parent.getAdapter().getItem(position);
                ((MainActivity) getActivity()).onItemSelected(artist.getId(), artist.getArtistName());
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
        return rootView;
    }

    private void searchArtist() {
        spotify.searchArtists(searchText, new Callback<ArtistsPager>() {
                    @Override
                    public void success(ArtistsPager artistsPager, Response response) {
                        artistList.clear();
                        for (Artist artist : artistsPager.artists.items) {
                            artistList.add(new ArtistDataSimple(artist.id, artist.images != null && !artist.images.isEmpty() ? artist.images.get(0).url : null, artist.name));
                        }

                        getActivity().runOnUiThread(new Runnable() {
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("artistList", artistList);
        super.onSaveInstanceState(outState);
    }

    public interface ArtistSearchFragmentCallback {
        void onItemSelected(String artistID, String artistName);
    }

}
