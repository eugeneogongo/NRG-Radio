package com.nrgr.adio.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.Widget.ColorPicker;
import com.nrgr.adio.Widget.PlayPauseButton;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {


    private Context context;
    private List<Music> musicList = new ArrayList<>();
    int playingposition = 0;
    public PlayListAdapter(Context context) {
        this.context = context;

    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.playlist_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);
        int cacheSize = 15 * 1024 * 1024; // 4MiB
        Picasso picload = new Picasso.Builder(context).memoryCache(new LruCache(cacheSize)).build();
        Picasso.get().load(music.getPiclink()).noFade().into(holder.imgHero, new Callback() {
            @Override
            public void onSuccess() {
                BitmapDrawable drawable = (BitmapDrawable) holder.imgHero.getDrawable();
                final Bitmap bitmap = drawable.getBitmap();

                holder.btnPlay.setColor(ColorPicker.getLight(bitmap, context));
                holder.musiccard.setBackgroundColor(ColorPicker.getColor(bitmap, context));
            }

            @Override
            public void onError(Exception e) {

            }
        });

        holder.txtMusicname.setText(music.getTitle());
        holder.btnPlay.setOnClickListener(view -> {

            EventBus.getDefault().post(Constants.START);
            new Handler().postDelayed(() -> {
                EventBus.getDefault().postSticky(music);
                holder.btnPlay.setPlayed(true);
                playingposition = position;
                holder.btnPlay.startAnimation();
                notifyDataSetChanged();
            }, 300);




        });

    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public long getItemId(int position) {
        return (long) musicList.get(position).hashCode();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHero;
        PlayPauseButton btnPlay;
        TextView txtMusicname;
        FrameLayout musiccard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPlay = itemView.findViewById(R.id.btnplay);
            imgHero = itemView.findViewById(R.id.imgHero);
            txtMusicname = itemView.findViewById(R.id.txtPlaylistname);
            musiccard = itemView.findViewById(R.id.musiccard);
            btnPlay.setBackgroundResource(R.drawable.circularbg);
        }
    }
}
