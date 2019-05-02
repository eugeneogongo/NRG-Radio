package com.nrgr.adio.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nrgr.adio.Fragments.listmusic;
import com.nrgr.adio.Media.PlayerService;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.Widget.ColorPicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Home extends AppCompatActivity implements View.OnClickListener {

    private boolean isplaying = false;
    private ImageView nowplayingimage;
    private TextView nowplaying;
    private ImageButton btnplaying;
    private LinearLayout bottomnavigation;
    Music np;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initView();

    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(np!=null){
            outState.putSerializable("np",np);

        }

    }

    private void initView() {

        showFragment(new listmusic(), "Home");
        FrameLayout framedata = findViewById(R.id.framedata);
        framedata.setOnClickListener(this);
        nowplayingimage = findViewById(R.id.nowplayingimage);
        nowplaying = findViewById(R.id.nowplaying);
        btnplaying = findViewById(R.id.btnplaying);
        btnplaying.setOnClickListener(this);
        bottomnavigation = findViewById(R.id.bottomnavigation);

    }

    private void showFragment(Fragment fragment, String Tag) {
        getSupportFragmentManager().
                beginTransaction()
                .replace(R.id.framedata, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStart() {

        EventBus.getDefault().register(this);

        super.onStart();
    }

       @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sendCommand(Constants.STOP);

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Music music) {

        np = music;
        if (isplaying) {
            sendCommand(Constants.PLAY_ACTION);
        } else {
            sendCommand(Constants.START);
            isplaying = true;
        }

        UpdateNowPlaying(music);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        if (event.equals(Constants.START)) {
            sendCommand(Constants.START);
            isplaying = true;
            btnplaying.setImageResource(R.drawable.ic_pausebutton);
        } else if (event.equals(Constants.PAUSE_ACTION)) {
            isplaying = false;
            btnplaying.setImageResource(R.drawable.ic_playbutton);
            sendCommand(Constants.PAUSE_ACTION);
        }else if(event.equals(Constants.RESUME)){
            sendCommand(Constants.RESUME);
            btnplaying.setImageResource(R.drawable.ic_pausebutton);
            isplaying = true;

        }

    }

    private void sendCommand(String command) {
        Intent playservice = new Intent(Home.this, PlayerService.class);
        playservice.setAction(command);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(playservice);
        } else {
            startService(playservice);
        }


    }
    private void UpdateNowPlaying(Music music) {
        btnplaying.setImageResource(R.drawable.ic_pausebutton);
        bottomnavigation.setVisibility(View.VISIBLE);
        Picasso.get().load(music.getPiclink()).noFade()
                .placeholder(R.drawable.ic_nrg).into(nowplayingimage, new Callback() {
            @Override
            public void onSuccess() {
                BitmapDrawable drawable = (BitmapDrawable) nowplayingimage.getDrawable();
                final Bitmap bitmap = drawable.getBitmap();
                int color = ColorPicker.getColor(bitmap, Home.this);
                bottomnavigation.setBackgroundColor(color);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        nowplaying.setText(music.getTitle());
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnplaying) {
            if (isplaying) {
                sendCommand(Constants.PAUSE_ACTION);
                btnplaying.setImageResource(R.drawable.ic_playbutton);
                isplaying = false;
                EventBus.getDefault().post(Constants.PlAYBACK_PAUSE);
            } else {
                sendCommand(Constants.RESUME);
                btnplaying.setImageResource(R.drawable.ic_pausebutton);
                isplaying = true;
                EventBus.getDefault().post(Constants.PLAYBACK_RESUME);
            }
        }

    }
}
