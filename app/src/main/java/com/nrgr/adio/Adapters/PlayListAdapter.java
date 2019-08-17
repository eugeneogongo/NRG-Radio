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

import com.nrgr.adio.Fragments.listmusic;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.Widget.ColorPicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static int previousplaying = -1;
    public static int currentplaying = -1;

    private Context context;
    private List<Object> musicList = new ArrayList<>();

    public void addItem(int i, Object object) {
        musicList.add(i, object);
    }

    public List<Object> getMusicList() {
        return musicList;
    }
    public PlayListAdapter(Context context) {
        this.context = context;

    }

    public void setMusicList(List<Object> musicList) {
        this.musicList.addAll(musicList);
    }

    public Object get(int i) {
        return musicList.get(i);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.playlist_item, parent, false);
                return new MyViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

                Music music = (Music) musicList.get(position);
                MyViewHolder holder1 = (MyViewHolder) holder;
                holder1.setIsRecyclable(false);
                Picasso.get().load(music.getPiclink()).into(holder1.imgHero, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable drawable = (BitmapDrawable) holder1.imgHero.getDrawable();
                        final Bitmap bitmap = drawable.getBitmap();
                        holder1.musiccard.setBackgroundColor(ColorPicker.getColor(bitmap, context));
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
                if (music.isIsplaying()) {
                    holder1.btnPlay.setImageResource(R.drawable.ic_pausebutton);
                } else {
                    holder1.btnPlay.setImageResource(R.drawable.ic_playbutton);
                }
                holder1.txtMusicname.setText(music.getTitle());
                holder1.btnPlay.setOnClickListener(view -> {
                    if (music.isIsplaying()) {
                        sendPause(music);
                    } else {
                        sendPlayMedia(music, position);
                    }
                });
        }


    private void sendPause(Music music){

        EventBus.getDefault().post(Constants.PAUSE_ACTION);
       music.setIsplaying(false);
        musicList.set(currentplaying, music);
        currentplaying = -1;
       EventBus.getDefault().post(Constants.DataSetChanged);
    }

    /**
     * Plays Send Play Music
     * @param music
     * @param position
     */
    private void sendPlayMedia(Music music, int position){
         EventBus.getDefault().post(Constants.START);
        previousplaying = currentplaying;
        new Handler().postDelayed(() -> EventBus.getDefault().postSticky(music), 300);

        if (currentplaying != -1) {
            Music music1 = (Music) musicList.get(currentplaying);
            music1.setIsplaying(false);
            music.setIsplaying(true);
            musicList.set(currentplaying, music1);
            musicList.set(position,music);
            currentplaying = position;

        }else{
            currentplaying = position;
            previousplaying = currentplaying;
            music.setIsplaying(true);
            musicList.set(position,music);
        }

        EventBus.getDefault().post(Constants.DataSetChanged);

    }


    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public long getItemId(int position) {
        return (long) musicList.get(position).hashCode();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHero;
        ImageView btnPlay;
        TextView txtMusicname;
        FrameLayout musiccard;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            btnPlay = itemView.findViewById(R.id.btnplay);
            imgHero = itemView.findViewById(R.id.imgHero);
            txtMusicname = itemView.findViewById(R.id.txtPlaylistname);
            musiccard = itemView.findViewById(R.id.musiccard);
            btnPlay.setBackgroundResource(R.drawable.circularbg);
        }
    }

}
