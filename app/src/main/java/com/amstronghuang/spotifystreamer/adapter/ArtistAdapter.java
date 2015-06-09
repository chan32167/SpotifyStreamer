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
import com.amstronghuang.spotifystreamer.model.ArtistDataSimple;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;

public class ArtistAdapter extends BaseAdapter {

    private Context context;
    private List<ArtistDataSimple> artistArrayList;

    public ArtistAdapter(Context context, List<ArtistDataSimple> artistArrayList) {
        this.context = context;
        this.artistArrayList = artistArrayList;
    }

    @Override
    public int getCount() {
        return artistArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return artistArrayList.get(position);
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
            convertView = mInflater.inflate(R.layout.artist_list_item, null);
            holder = new ViewHolder();
            holder.artistName = (TextView) convertView.findViewById(R.id.artistNameTV);
            holder.artistDrawee = (SimpleDraweeView) convertView.findViewById(R.id.artistDrawee);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ArtistDataSimple artist = artistArrayList.get(position);
        if (artist != null) {
            holder.artistName.setText(artist.getArtistName());
            Uri uri;
            if (artist.getImgUrl() != null) {
                uri = Uri.parse(artist.getImgUrl());
            } else {
                uri = Uri.parse("res://drawable/" + R.drawable.nophotosmall);
            }
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(ImageRequest.fromUri(uri))
                    .setOldController(holder.artistDrawee.getController())
                    .build();
            holder.artistDrawee.setController(controller);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView artistName;
        SimpleDraweeView artistDrawee;
    }

}
