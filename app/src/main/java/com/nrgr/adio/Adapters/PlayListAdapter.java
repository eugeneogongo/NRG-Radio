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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.Widget.ColorPicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.LruCache;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {


    private Context context;
    private List<Music> musicList = new ArrayList<>();
    static  boolean wasplaying = false;
   public static int previousplaying = -1;
    public PlayListAdapter(Context context) {
        this.context = context;

    }
    public  Music get(int i){
        return musicList.get(i);
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
        Picasso.get().load(music.getPiclink()).placeholder(R.drawable.nrgradio).into(holder.imgHero, new Callback() {
            @Override
            public void onSuccess() {
                BitmapDrawable drawable = (BitmapDrawable) holder.imgHero.getDrawable();
                final Bitmap bitmap = drawable.getBitmap();
                holder.musiccard.setBackgroundColor(ColorPicker.getColor(bitmap, context));
            }

            @Override
            public void onError(Exception e) {

            }
        });
        if(music.isIsplaying()){
            holder.btnPlay.setImageResource(R.drawable.ic_pausebutton);
        }else{
            holder.btnPlay.setImageResource(R.drawable.ic_playbutton);
        }
        holder.txtMusicname.setText(music.getTitle());
        holder.btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(music.isIsplaying()){
                   sendPause(music);
                }else{
                    sendPlayMedia(music,position);
                }
            }
        });
    }

    private void sendPause(Music music){
        wasplaying = true;
        EventBus.getDefault().post(Constants.PAUSE_ACTION);
       music.setIsplaying(false);
       musicList.set(previousplaying,music);
       previousplaying = -1;
       EventBus.getDefault().post(Constants.DataSetChanged);
    }

    /**
     * Plays Send Play Music
     * @param music
     * @param position
     */
    private void sendPlayMedia(Music music, int position){
         EventBus.getDefault().post(Constants.START);
        new Handler().postDelayed(() -> EventBus.getDefault().postSticky(music), 300);

        if(previousplaying !=- 1){
            Music music1 = musicList.get(previousplaying);
            music1.setIsplaying(false);
            music.setIsplaying(true);
            musicList.set(previousplaying,music1);
            musicList.set(position,music);
            previousplaying = position;

        }else{
            previousplaying = position;
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHero;
        ImageView btnPlay;
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
