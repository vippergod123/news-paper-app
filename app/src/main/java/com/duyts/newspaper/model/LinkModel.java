package com.duyts.newspaper.model;

import org.json.JSONObject;

public class LinkModel {

    String url;
    String title;
    String image;

    public LinkModel(String url, String title, String image) {
        this.url = url;
        this.title = title;
        this.image = image;
    }

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

}
