package com.duyts.newspaper.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class LinkModel implements Parcelable {

    String url;
    String title;
    String image;

    public LinkModel(String url, String title, String image) {
        this.url = url;
        this.title = title;
        this.image = image;
    }



    public static final Creator<LinkModel> CREATOR = new Creator<LinkModel>() {
        @Override
        public LinkModel createFromParcel(Parcel in) {
            return new LinkModel(in);
        }

        @Override
        public LinkModel[] newArray(int size) {
            return new LinkModel[size];
        }
    };

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(image);
    }

    protected LinkModel(Parcel in) {
        url = in.readString();
        title = in.readString();
        image = in.readString();
    }
}
