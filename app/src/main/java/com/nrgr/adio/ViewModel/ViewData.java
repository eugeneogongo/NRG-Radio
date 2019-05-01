package com.nrgr.adio.ViewModel;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Scarpper.MusicList;
import com.nrgr.adio.Util.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ViewData extends ViewModel {
    MutableLiveData<List<Music>> playlist;

    public LiveData<List<Music>> getPlaylist() {
        if (playlist == null) {
            playlist = new MutableLiveData<>();
            LoadData();
        }
        return playlist;
    }

    private void LoadData() {
        // Do an asynchronous operation to fetch articles
        new FetchData().execute();
    }

    class FetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            MusicList List = new MusicList();
            List.addOnCompleteListener(new MusicList.Observer() {
                @Override
                public void OnSuccess(ArrayList<Music> musiclist) {
                    playlist.postValue(musiclist);
                }

                @Override
                public void OnFailure(String error) {
                    Log.i(error, error);
                    EventBus.getDefault().post(Constants.NOINTERNET);
                }
            });

            return null;
        }

    }
}
