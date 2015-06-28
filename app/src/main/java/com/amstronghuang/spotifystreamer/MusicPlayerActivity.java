package com.amstronghuang.spotifystreamer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amstronghuang.spotifystreamer.model.TrackDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MusicPlayerActivity extends AppCompatActivity {

    private ArrayList<TrackDataSimple> trackDataSimpleList;
    private int position;
    private TrackDataSimple trackDataSimple;

    private MediaPlayer mediaPlayer;

    private Handler handler = new Handler();

    private double timeElapsed = 0, finalTime = 0;

    private boolean readyToPlay = false;

    @InjectView(R.id.artistNameTV)
    protected TextView artistNameTV;
    @InjectView(R.id.albumNameTV)
    protected TextView albumNameTV;
    @InjectView(R.id.trackNameTV)
    protected TextView trackNameTV;

    @InjectView(R.id.seekBar)
    protected SeekBar seekBar;

    @InjectView(R.id.albumDrawee)
    protected SimpleDraweeView albumDrawee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Top Tracks");
            getSupportActionBar().setSubtitle(getIntent().getStringExtra("artist"));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        ButterKnife.inject(this);
        Fresco.initialize(this);

        trackDataSimpleList = this.getIntent().getParcelableArrayListExtra("trackDataSimpleList");
        position = this.getIntent().getIntExtra("position", 0);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        seekBar.setClickable(false);
        setTrackData();

    }

    private void setTrackData() {
        readyToPlay = false;
        trackDataSimple = trackDataSimpleList.get(position);
        artistNameTV.setText(trackDataSimple.getArtistName());
        albumNameTV.setText(trackDataSimple.getAlbumName());
        trackNameTV.setText(trackDataSimple.getTrackName());
        albumDrawee.setImageURI(Uri.parse(trackDataSimple.getImgUrl()));

        mediaPlayer.reset();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setDataSource(trackDataSimple.getDemoTrackUrl());
                    mediaPlayer.prepareAsync();
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            readyToPlay = true;
                            finalTime = mediaPlayer.getDuration();
                            seekBar.setMax((int) finalTime);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void play(View view) {
        if (readyToPlay) {
            mediaPlayer.start();
            timeElapsed = mediaPlayer.getCurrentPosition();
            seekBar.setProgress((int) timeElapsed);
            handler.postDelayed(updateSeekBarTime, 100);
        } else {
            Toast.makeText(this, "Loading song... retry in a few seconds", Toast.LENGTH_SHORT).show();
        }

    }

    public void pause(View view) {
        mediaPlayer.pause();
    }

    public void next(View view) {
        if (position < trackDataSimpleList.size()) {
            position++;
            setTrackData();
        } else {
            Toast.makeText(this, "No next song", Toast.LENGTH_SHORT).show();
        }
    }

    public void previous(View view) {
        if (position > 0) {
            position--;
            setTrackData();
        } else {
            Toast.makeText(this, "No previous song", Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekBar.setProgress((int) timeElapsed);

            //repeat yourself that again in 100 miliseconds
            handler.postDelayed(this, 100);
        }
    };


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
