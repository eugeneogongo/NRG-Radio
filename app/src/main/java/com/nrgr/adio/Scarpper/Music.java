package com.nrgr.adio.Scarpper;

import java.io.Serializable;

public class Music implements Serializable {

    private boolean isplaying = false;

    public boolean isIsplaying() {
        return isplaying;
    }

    public void setIsplaying(boolean isplaying) {
        this.isplaying = isplaying;
    }

    private String title, piclink, streamlink;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPiclink() {
        return piclink;
    }

    public void setPiclink(String piclink) {
        this.piclink = piclink;
    }

    public String getStreamlink() {
        return streamlink;
    }

    public void setStreamlink(String streamlink) {
        this.streamlink = streamlink;
    }

    @Override
    public String toString() {
        return "Title: " + getTitle() + "" +
                "\nLink: " + getStreamlink()
                + "\nPic: " + getPiclink();
    }
}
