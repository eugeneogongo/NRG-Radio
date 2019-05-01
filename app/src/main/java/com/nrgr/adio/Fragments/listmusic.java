package com.nrgr.adio.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nrgr.adio.Adapters.PlayListAdapter;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.ViewModel.ViewData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import com.nrg.radio.PageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class listmusic extends Fragment {


    PlayListAdapter adapter;
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
           adapter.notifyDataSetChanged();
       }else if(event.equals(Constants.PlAYBACK_PAUSE) && PlayListAdapter.previousplaying != -1) {
           Music music = adapter.get(PlayListAdapter.previousplaying);
           music.setIsplaying(false);
           adapter.notifyItemChanged(PlayListAdapter.previousplaying, music);
           adapter.notifyDataSetChanged();
       }else if(event.equals(Constants.PLAYBACK_RESUME)){
           Music music = adapter.get(PlayListAdapter.previousplaying);
           music.setIsplaying(true);
           adapter.notifyItemChanged(PlayListAdapter.previousplaying, music);
           adapter.notifyDataSetChanged();
       }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _View_data = ViewModelProviders.of(getActivity()).get(ViewData.class);
        _View_data.getPlaylist().observe(getActivity(), music -> {
            adapter.setMusicList(music);
            adapter.notifyDataSetChanged();
            playlist.setVisibility(View.VISIBLE);
            pageloader.stopProgress();
            pageloader.setVisibility(View.GONE);
        });
    }
}
