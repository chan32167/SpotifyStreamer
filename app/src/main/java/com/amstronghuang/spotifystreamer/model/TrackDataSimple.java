package com.amstronghuang.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amstronghuang on 6/9/15.
 */
public class TrackDataSimple implements Parcelable {

    public static final Parcelable.Creator<TrackDataSimple> CREATOR = new Parcelable.Creator<TrackDataSimple>() {
        public TrackDataSimple createFromParcel(Parcel in) {
            return new TrackDataSimple(in);
        }

        public TrackDataSimple[] newArray(int size) {
            return new TrackDataSimple[size];
        }
    };
    private String id;
    private String imgUrl;
    private String albumName;
    private String trackName;

    public TrackDataSimple(String id, String imgUrl, String albumName, String trackName) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.albumName = albumName;
        this.trackName = trackName;
    }

    private TrackDataSimple(Parcel in) {
        id = in.readString();
        imgUrl = in.readString();
        albumName = in.readString();
        trackName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imgUrl);
        dest.writeString(albumName);
        dest.writeString(trackName);
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getId() {
        return id;
    }
}

