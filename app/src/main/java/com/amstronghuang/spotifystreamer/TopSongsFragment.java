package com.amstronghuang.spotifystreamer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class TopSongsFragment extends Fragment {

    @InjectView(R.id.topSongsLV)
    protected ListView artistsLV;

    protected TopSongsAdapter topSongsAdapter;

    protected ArrayList<TrackDataSimple> trackDataSimpleList;

    public static TopSongsFragment newInstance(String artistID, String artistName) {
        TopSongsFragment fragment = new TopSongsFragment();

        Bundle args = new Bundle();
        args.putString("idArtist", artistID);
        args.putString("artist", artistName);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_top_songs, container, false);
        ButterKnife.inject(this, rootView);
        Fresco.initialize(this.getActivity());

        SpotifyApi api = new SpotifyApi();

        final SpotifyService spotify = api.getService();

        if (savedInstanceState == null || !savedInstanceState.containsKey("trackDataSimpleList")) {
            trackDataSimpleList = new ArrayList<>();
        } else {
            trackDataSimpleList = savedInstanceState.getParcelableArrayList("trackDataSimpleList");
        }

        topSongsAdapter = new TopSongsAdapter(this.getActivity(), trackDataSimpleList);

        artistsLV.setAdapter(topSongsAdapter);

        Map<String, Object> extraData = new HashMap<>();
        extraData.put("country", "US");

        Bundle args = getArguments();
        if (args != null) {
            String artistName = args.getString("artist");
            String artistID = args.getString("idArtist");

            spotify.getArtistTopTrack(artistID, extraData, new Callback<Tracks>() {
                @Override
                public void success(Tracks tracks, Response response) {
                    trackDataSimpleList.clear();
                    for (Track track : tracks.tracks) {
                        trackDataSimpleList.add(new TrackDataSimple(track.id, track.album.images != null && !track.album.images.isEmpty() ? track.album.images.get(0).url : null, track.album.name, track.name, track.artists.get(0).name, track.preview_url));
                    }
                    TopSongsFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            topSongsAdapter.notifyDataSetChanged();
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
                    Intent musicPlayerIntent = new Intent(TopSongsFragment.this.getActivity(), MusicPlayerActivity.class);
                    musicPlayerIntent.putExtra("position", position);
                    musicPlayerIntent.putParcelableArrayListExtra("trackDataSimpleList", trackDataSimpleList);
                    startActivity(musicPlayerIntent);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("trackDataSimpleList", trackDataSimpleList);
        super.onSaveInstanceState(outState);
    }


}
