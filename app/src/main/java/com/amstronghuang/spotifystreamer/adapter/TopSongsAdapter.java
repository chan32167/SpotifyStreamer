package com.amstronghuang.spotifystreamer.adapter;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.amstronghuang.spotifystreamer.R;
import com.amstronghuang.spotifystreamer.model.TrackDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class TopSongsAdapter extends BaseAdapter {

    private Context context;
    private List<TrackDataSimple> trackList;

    public TopSongsAdapter(Context context, List<TrackDataSimple> trackList) {
        this.context = context;
        this.trackList = trackList;
    }

    @Override
    public int getCount() {
        return trackList.size();
    }

    @Override
    public Object getItem(int position) {
        return trackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        DecimalFormat formatter = new DecimalFormat("#,###.##", symbols);
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.top_songs_list_item, null);
            holder = new ViewHolder();
            holder.trackName = (TextView) convertView.findViewById(R.id.trackNameTV);
            holder.albumName = (TextView) convertView.findViewById(R.id.albumNameTV);
            holder.albumDrawee = (SimpleDraweeView) convertView.findViewById(R.id.albumDrawee);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TrackDataSimple track = trackList.get(position);
        if (track != null) {
            holder.trackName.setText(track.getTrackName());
            holder.albumName.setText(track.getAlbumName());
            Uri uri;
            if (track.getImgUrl() != null) {
                uri = Uri.parse(track.getImgUrl());
            } else {
                uri = Uri.parse("res://drawable/" + R.drawable.nophotosmall);
            }
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(ImageRequest.fromUri(uri))
                    .setOldController(holder.albumDrawee.getController())
                    .build();
            holder.albumDrawee.setController(controller);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView trackName;
        TextView albumName;
        SimpleDraweeView albumDrawee;
    }

}
