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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.nrgr.adio.Adapters.PlayListAdapter;
import com.nrgr.adio.R;
import com.nrgr.adio.ViewModel.ViewData;

/**
 * A simple {@link Fragment} subclass.
 */
public class listmusic extends Fragment {


    PlayListAdapter adapter;
    private RecyclerView playlist;
    private ViewData _View_data;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        _View_data = ViewModelProviders.of(getActivity()).get(ViewData.class);
        _View_data.getPlaylist().observe(getActivity(), music -> {
            adapter.setMusicList(music);
            adapter.notifyDataSetChanged();
        });
    }
}
