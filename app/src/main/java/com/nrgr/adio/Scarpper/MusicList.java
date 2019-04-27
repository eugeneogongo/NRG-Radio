package com.nrgr.adio.Scarpper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


public class MusicList {


    private final List<Observer> observerArrayList = new ArrayList<>();

    private void notifySuccess(ArrayList<Music> music) {
        for (Observer observer : observerArrayList) observer.OnSuccess(music);
    }

    public void addOnCompleteListener(Observer observer) {
        observerArrayList.add(observer);
        ParseData();
    }

    private void ParseData() {

        ArrayList<Music> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("https://nrg.radio").get();
            Elements musicitem = doc.select("#qtchannelslistcontainer a");
            for (Element headline : musicitem) {
                Music music = new Music();
                music.setPiclink(headline.attr("data-background"));
                music.setTitle(headline.attr("data-title"));
                music.setStreamlink(headline.attr("data-playtrack"));
                list.add(music);

            }

        } catch (Exception ex) {
            NotifyFailure(ex.getMessage());
            return;
        }

        notifySuccess(list);
    }

    private void NotifyFailure(String error) {
        for (Observer observer : observerArrayList) {
            observer.OnFailure(error);
        }
    }

    public interface Observer {
        void OnSuccess(ArrayList<Music> musiclist);

        void OnFailure(String error);
    }
}

