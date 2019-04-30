package com.nrgr.adio.Media;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nrgr.adio.Activities.Home;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;
import com.nrgr.adio.Util.Constants;
import com.nrgr.adio.Widget.ColorPicker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlayerService extends Service implements ExoPlayer.EventListener {


    SimpleExoPlayer exoPlayer = null;
    private boolean isPlaying = false;
    private String nrgchannel="Nrg_01";
    Music tempmusic;
    private DefaultDataSourceFactory dataSourceFactory;
    private DefaultExtractorsFactory extractorsFactory;
    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyReceiver myNoisyAudioStreamReceiver = new BecomingNoisyReceiver();


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState) {
            case ExoPlayer.STATE_READY:
                exoPlayer.setPlayWhenReady(true);
                break;
        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Toast.makeText(this, "We are having Problem Connecting to the internet", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        TrackSelector trackSelector = new DefaultTrackSelector();

        LoadControl loadControl = new DefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);

        exoPlayer.addListener(this);
        dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Nrg.Radio"), null);
        extractorsFactory = new DefaultExtractorsFactory();
        registerReceiver(myNoisyAudioStreamReceiver, intentFilter);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        switch (intent.getAction()) {
            case Constants.START:
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                break;
            case Constants.PLAY_ACTION:
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                exoPlayer.setPlayWhenReady(true);
                isPlaying = true;
                break;
            case Constants.PAUSE_ACTION:
                exoPlayer.removeListener(this);
                exoPlayer.setPlayWhenReady(false);
                stopForeground(true);
                break;
            case Constants.RESUME:
                showNotification(tempmusic);
                registerReceiver(myNoisyAudioStreamReceiver, intentFilter);
                exoPlayer.setPlayWhenReady(true);
                isPlaying = true;
                break;
        }
        return START_STICKY;
    }

    private void showNotification(Music music) {
        Intent notificationIntent = new Intent(this, Home.class);
        notificationIntent.setAction(Constants.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Intent playIntent = new Intent(this, PlayerService.class);
        playIntent.setAction(Constants.PAUSE_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        final Bitmap[] icon = {BitmapFactory.decodeResource(getResources(),
                R.drawable.breakfast)};
        Picasso.get().load(music.getPiclink()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                icon[0] = bitmap;
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });


        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(music.getTitle())
                .setTicker("Now Playing")
                .setContentText(music.getTitle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(Bitmap.createScaledBitmap(icon[0], 128, 128, false))
                .setContentIntent(pendingIntent)
                .setChannelId(nrgchannel)
                .setColorized(true)
                .setColor(ColorPicker.getColor(icon[0], this))
                .setOngoing(true)
                .addAction(R.drawable.ic_playbutton, "Pause",
                        pplayIntent)
                .build();
        startForeground(Constants.NOTIFICATION_ID,
                notification);

    }

    private void showNotification() {
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Nrg Radio")
                .setChannelId(nrgchannel)
                .setOngoing(true)
                .build();

        startForeground(Constants.NOTIFICATION_ID,
                notification);
    }


    public void PlayPause(boolean Play) {
        isPlaying = Play;
        exoPlayer.setPlayWhenReady(isPlaying);
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void prepareExoPlayerFromURL(Music music) {

        try {




            MediaSource audioSource = new ExtractorMediaSource(Uri.parse(music.getStreamlink()), dataSourceFactory, extractorsFactory, null, null);

            exoPlayer.prepare(audioSource);

        } catch (Exception ex) {
            Log.e("Error Playing music", ex.getMessage());
        }


    }

    public void stopPlay() {
        exoPlayer.stop();
    }

    public class ServiceBinder extends Binder {
        public PlayerService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PlayerService.this;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    void createChannel(){
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        String id = nrgchannel;


        int importance = NotificationManager.IMPORTANCE_MIN;

        @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(id, nrgchannel,importance);

        mChannel.setDescription("Nrg Channel Music");
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mChannel.setSound(null,null);
        mNotificationManager.createNotificationChannel(mChannel);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Music music) {
        tempmusic = music;
        showNotification(music);
        prepareExoPlayerFromURL(music);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        exoPlayer.removeListener(this);
        exoPlayer.release();
        unregisterReceiver(myNoisyAudioStreamReceiver);

    }

    private class BecomingNoisyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                exoPlayer.stop();
                EventBus.getDefault().post(Constants.PAUSE_ACTION);
            }
        }
    }

}