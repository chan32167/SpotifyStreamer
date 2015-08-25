package com.amstronghuang.spotifystreamer;

import android.app.Dialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amstronghuang.spotifystreamer.model.TrackDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MusicPlayerFragment extends DialogFragment {

    @Bind(R.id.artistNameTV)
    protected TextView artistNameTV;
    @Bind(R.id.albumNameTV)
    protected TextView albumNameTV;
    @Bind(R.id.trackNameTV)
    protected TextView trackNameTV;
    @Bind(R.id.seekBar)
    protected SeekBar seekBar;
    @Bind(R.id.albumDrawee)
    protected SimpleDraweeView albumDrawee;
    @Bind(R.id.playPauseIB)
    protected ImageButton playPauseButton;
    @Bind(R.id.play_track_current_time)
    protected TextView currentTime;
    @Bind(R.id.play_track_total_time)
    protected TextView totalTime;
    private ArrayList<TrackDataSimple> trackDataSimpleList;
    private int position;
    private TrackDataSimple trackDataSimple;
    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private double timeElapsed = 0, finalTime = 0;
    private boolean readyToPlay = false;
    private Runnable updateSeekBarTime = new Runnable() {
        public void run() {
            //get current position
            timeElapsed = mediaPlayer.getCurrentPosition();
            //set seekbar progress
            seekBar.setProgress((int) timeElapsed);
            currentTime.setText(transformToTime((long) timeElapsed));
            //repeat yourself that again in 100 miliseconds
            if (mediaPlayer.isPlaying()) {
                handler.postDelayed(this, 100);
            }
        }
    };

    public static MusicPlayerFragment newInstance(ArrayList<TrackDataSimple> trackDataSimpleList, int position) {
        MusicPlayerFragment fragment = new MusicPlayerFragment();

        Bundle args = new Bundle();
        args.putParcelableArrayList("trackDataSimpleList", trackDataSimpleList);
        args.putInt("position", position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_music_player, container, false);

        ButterKnife.bind(this, rootView);
        Fresco.initialize(this.getActivity());

        trackDataSimpleList = getArguments().getParcelableArrayList("trackDataSimpleList");
        position = getArguments().getInt("position", 0);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setPlayButton();
            }
        });
        setTrackData();
        return rootView;
    }

    private void setTrackData() {
        readyToPlay = false;
        trackDataSimple = trackDataSimpleList.get(position);
        artistNameTV.setText(trackDataSimple.getArtistName());
        albumNameTV.setText(trackDataSimple.getAlbumName());
        trackNameTV.setText(trackDataSimple.getTrackName());
        albumDrawee.setImageURI(Uri.parse(trackDataSimple.getImgUrl()));

        mediaPlayer.reset();
        setPlayButton();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.setDataSource(trackDataSimple.getDemoTrackUrl());
                    mediaPlayer.prepareAsync();
                    seekBar.setEnabled(false);
                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            readyToPlay = true;
                            seekBar.setEnabled(true);
                            finalTime = mediaPlayer.getDuration();
                            seekBar.setMax((int) finalTime);
                            totalTime.setText(transformToTime((long) finalTime));
                            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                private boolean wasPlaying = false;

                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                    wasPlaying = mediaPlayer.isPlaying();
                                    if (wasPlaying) {
                                        mediaPlayer.pause();
                                    }
                                }

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                    int progress = seekBar.getProgress();
                                    mediaPlayer.seekTo(progress);
                                    if (wasPlaying) {
                                        mediaPlayer.start();
                                        handler.postDelayed(updateSeekBarTime, 100);
                                    }
                                }
                            });
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick(R.id.playPauseIB)
    public void playPause(View view) {
        if (readyToPlay) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                setPlayButton();
            } else {
                mediaPlayer.start();
                timeElapsed = mediaPlayer.getCurrentPosition();
                seekBar.setProgress((int) timeElapsed);
                handler.postDelayed(updateSeekBarTime, 100);
                setPauseButton();
            }
        } else {
            Toast.makeText(this.getActivity(), "Loading song... retry in a few seconds", Toast.LENGTH_SHORT).show();
        }
    }

    private void setPauseButton() {
        playPauseButton.setImageResource(android.R.drawable.ic_media_pause);
    }

    private void setPlayButton() {
        playPauseButton.setImageResource(android.R.drawable.ic_media_play);
    }

    @OnClick(R.id.nextIB)
    public void next(View view) {
        if (position < trackDataSimpleList.size() - 1) {
            position++;
            setTrackData();
        } else {
            Toast.makeText(this.getActivity(), "No next song", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.previousIB)
    public void previous(View view) {
        if (position > 0) {
            position--;
            setTrackData();
        } else {
            Toast.makeText(this.getActivity(), "No previous song", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onPause() {
        super.onPause();
        mediaPlayer.reset();
    }

    private String transformToTime(long duration) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

}
