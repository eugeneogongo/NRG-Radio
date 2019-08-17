package com.nrgr.adio.Fragments;


import android.os.Bundle;
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

import com.nrgr.adio.Adapters.PlayListAdapter;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.ViewModel.ViewData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

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
    private SweetAlertDialog pageloader;

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
        pageloader = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pageloader.setTitle("Loading");
        pageloader.show();
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


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
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
            pageloader.dismissWithAnimation();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

    }
}
