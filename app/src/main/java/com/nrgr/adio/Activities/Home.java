package com.nrgr.adio.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nrgr.adio.Fragments.listmusic;
import com.nrgr.adio.Media.PlayerService;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Widget.ColorPicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class Home extends AppCompatActivity implements View.OnClickListener {

    boolean isBound = false;
    PlayerService.ServiceBinder binder;
    PlayerService mplayer;
    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            binder = (PlayerService.ServiceBinder) iBinder;
            mplayer = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

            Log.e(Home.class.getCanonicalName(), "onServiceDisconnected");
            isBound = false;
        }
    };
    private boolean isplaying = false;
    private FrameLayout framedata;
    private ImageView nowplayingimage;
    private TextView nowplaying;
    private ImageButton btnplaying;
    private LinearLayout bottomnavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = new Intent(getApplicationContext(), PlayerService.class);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        initView();
    }

    private void initView() {

        showFragment(new listmusic(), "Home");
        framedata = findViewById(R.id.framedata);
        framedata.setOnClickListener(this);
        nowplayingimage = findViewById(R.id.nowplayingimage);
        nowplaying = findViewById(R.id.nowplaying);
        btnplaying = findViewById(R.id.btnplaying);
        btnplaying.setOnClickListener(this);
        bottomnavigation = findViewById(R.id.bottomnavigation);

    }

    private void showFragment(Fragment fragment, String Tag) {
        getSupportFragmentManager().
                beginTransaction().addToBackStack(Tag)
                .replace(R.id.framedata, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        // Unbind from the service
        if (isBound) {
            getApplicationContext().unbindService(mConnection);
            isBound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Music music) {
        if (!mplayer.isPlaying()) {
            mplayer.prepareExoPlayerFromURL(Uri.parse(music.getStreamlink()));
            mplayer.PlayPause(true);
            isplaying = mplayer.isPlaying();
        } else {
            mplayer.stopPlay();
            mplayer.prepareExoPlayerFromURL(Uri.parse(music.getStreamlink()));
            mplayer.PlayPause(true);
            isplaying = mplayer.isPlaying();
        }
        UpdateNowPlaying(music);

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
        switch (v.getId()) {
            case R.id.btnplaying:
                if (mplayer == null) return;
                if (isplaying) {
                    mplayer.PlayPause(false);
                    btnplaying.setImageResource(R.drawable.ic_playbutton);
                    isplaying = false;

                } else {
                    mplayer.PlayPause(true);
                    btnplaying.setImageResource(R.drawable.ic_pausebutton);
                    isplaying = true;
                }

                break;
        }
    }
}
