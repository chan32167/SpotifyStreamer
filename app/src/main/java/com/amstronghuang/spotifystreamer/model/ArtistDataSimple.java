package com.amstronghuang.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by amstronghuang on 6/9/15.
 */
public class ArtistDataSimple implements Parcelable {

    public static final Parcelable.Creator<ArtistDataSimple> CREATOR = new Parcelable.Creator<ArtistDataSimple>() {
        public ArtistDataSimple createFromParcel(Parcel in) {
            return new ArtistDataSimple(in);
        }

        public ArtistDataSimple[] newArray(int size) {
            return new ArtistDataSimple[size];
        }
    };
    private String id;
    private String imgUrl;
    private String artistName;

    public ArtistDataSimple(String id, String imgUrl, String artistName) {
        this.id = id;
        this.imgUrl = imgUrl;
        this.artistName = artistName;
    }

    private ArtistDataSimple(Parcel in) {
        id = in.readString();
        imgUrl = in.readString();
        artistName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imgUrl);
        dest.writeString(artistName);
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getArtistName() {
        return artistName;
    }

    public String getId() {
        return id;
    }
}
