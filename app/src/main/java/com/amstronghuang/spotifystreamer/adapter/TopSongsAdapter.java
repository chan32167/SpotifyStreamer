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
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

public class TopSongsAdapter extends BaseAdapter {

    private Context context;
    private List<Track> trackList;

    public TopSongsAdapter(Context context, List<Track> trackList) {
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

        Track track = trackList.get(position);
        if (track != null) {
            holder.trackName.setText(track.name);
            holder.albumName.setText(track.album.name);
            Uri uri;
            Uri smallUri = null;
            if (track.album.images != null && !track.album.images.isEmpty()) {
                if (track.album.images.size() > 1) {
                    smallUri = Uri.parse(track.album.images.get(1).url);
                }
                uri = Uri.parse(track.album.images.get(0).url);
            } else {
                uri = Uri.parse("res://drawable/" + R.drawable.nophotosmall);
            }
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setLowResImageRequest(ImageRequest.fromUri(smallUri))
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
