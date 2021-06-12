package com.duyts.newspaper.model;

import android.graphics.Bitmap;

import java.util.UUID;

public class LinkModel {

    private String id;
    private String url;
    private String title;
    private String image;
    private Bitmap imageBitMap;

    public LinkModel(String url) {
        this.url = url;
        id = UUID.randomUUID().toString();
    }
    public LinkModel(String url, String title, String image) {
        this.url = url;
        this.title = title;
        this.image = image;
        id = UUID.randomUUID().toString();
    }

    public LinkModel(String url, String title,String image, Bitmap imageBitMap) {
        this.url = url;
        this.title = title;
        this.image = image;
        this.imageBitMap = imageBitMap;
        id = UUID.randomUUID().toString();
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

    public Bitmap getImageBitMap() {
        return imageBitMap;
    }

    public void setImageBitMap(Bitmap imageBitMap) {
        this.imageBitMap = imageBitMap;
    }

    public String getId() {
        return id;
    }
}

