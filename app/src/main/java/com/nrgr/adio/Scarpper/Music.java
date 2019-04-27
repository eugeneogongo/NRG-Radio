package com.nrgr.adio.Scarpper;


public class Music {

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

    void setStreamlink(String streamlink) {
        this.streamlink = streamlink;
    }

    @Override
    public String toString() {
        return "Title: " + getTitle() + "" +
                "\nLink: " + getStreamlink()
                + "\nPic: " + getPiclink();
    }
}
