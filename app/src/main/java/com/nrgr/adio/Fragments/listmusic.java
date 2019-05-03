package com.nrgr.adio.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.nrg.radio.PageLoader;
import com.nrgr.adio.Adapters.PlayListAdapter;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.ViewModel.ViewData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class listmusic extends Fragment {


    public static final int ITEMS_PER_AD = 4;
    //private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111";
    private static final String AD_UNIT_ID = "ca-app-pub-1444752230904711/8574980788";
    private PlayListAdapter adapter;
    private RecyclerView playlist;
    private ViewData _View_data;
    private PageLoader pageloader;

    public listmusic() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_listmusic, container, false);

        initView(view);
        return view;
    }

    private void initView(View view) {
        pageloader = view.findViewById(R.id.pageloader);
        pageloader.startProgress();
        playlist = view.findViewById(R.id.playlist);
        adapter = new PlayListAdapter(getActivity());
        adapter.setHasStableIds(true);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        playlist.setLayoutManager(layoutManager);
        playlist.setHasFixedSize(true);

        playlist.setAdapter(adapter);
        playlist.setItemAnimator(new DefaultItemAnimator());


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
       if(event.equals(Constants.NOINTERNET)){
           pageloader.stopProgressAndFailed();
       }else if(event.equals(Constants.DataSetChanged)){

           Music music = (Music) adapter.get(PlayListAdapter.currentplaying);
           music.setIsplaying(true);
           adapter.notifyItemChanged(PlayListAdapter.currentplaying, music);
           if (PlayListAdapter.previousplaying != PlayListAdapter.currentplaying) {
               Music music2 = (Music) adapter.get(PlayListAdapter.previousplaying);
               music2.setIsplaying(false);
               adapter.notifyItemChanged(PlayListAdapter.previousplaying, music2);
           }

       } else if (event.equals(Constants.PlAYBACK_PAUSE) && PlayListAdapter.currentplaying != -1) {
           Music music = (Music) adapter.get(PlayListAdapter.currentplaying);
           music.setIsplaying(false);
           adapter.notifyItemChanged(PlayListAdapter.currentplaying, music);
       }else if(event.equals(Constants.PLAYBACK_RESUME)){
           Music music = (Music) adapter.get(PlayListAdapter.currentplaying);
           music.setIsplaying(true);
           adapter.notifyItemChanged(PlayListAdapter.currentplaying, music);

       }

    }

    /**
     * Sets up and loads the banner ads.
     */
    private void loadBannerAds() {
        // Load the first banner ad in the items list (subsequent ads will be loaded automatically
        // in sequence).
        loadBannerAd(4);
    }

    /**
     * Loads the banner ads in the items list.
     */
    private void loadBannerAd(final int index) {

        if (index >= adapter.getMusicList().size()) {
            return;
        }

        Object item = adapter.getMusicList().get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad"
                    + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                // The previous banner ad loaded successfully, call this method again to
                // load the next ad in the items list.

                Log.i(listmusic.class.getCanonicalName(), "Added loaded successfully");
                loadBannerAd(index + ITEMS_PER_AD);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // The previous banner ad failed to load. Call this method again to load
                // the next ad in the items list.
                Log.e("MainActivity", "The previous banner ad failed to load. Attempting to"
                        + " load the next banner ad in the items list.");
                loadBannerAd(index + ITEMS_PER_AD);
            }
        });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void addBannerAds() {
        // Loop through the items array and place a new banner ad in every ith position in
        // the items List.
        for (int i = 4; i <= adapter.getMusicList().size(); i += ITEMS_PER_AD) {
            final AdView adView = new AdView(getContext());
            adView.setAdSize(AdSize.MEDIUM_RECTANGLE);
            adView.setAdUnitId(AD_UNIT_ID);
            adapter.addItem(i, adView);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        for (Object item : adapter.getMusicList()) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.destroy();
            }
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        for (Object item : adapter.getMusicList()) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        for (Object item : adapter.getMusicList()) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _View_data = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ViewData.class);
        _View_data.getPlaylist().observe(getActivity(), object -> {
            adapter.setMusicList(object);
            adapter.notifyDataSetChanged();
            playlist.setVisibility(View.VISIBLE);
            pageloader.stopProgress();
            pageloader.setVisibility(View.GONE);
            addBannerAds();
            loadBannerAds();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
