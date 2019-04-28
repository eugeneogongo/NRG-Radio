package com.nrgr.adio.Media;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
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
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.nrgr.adio.R;
import com.nrgr.adio.Scarpper.Music;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class PlayerService extends Service implements ExoPlayer.EventListener {

    private final IBinder mBinder = new ServiceBinder();
    SimpleExoPlayer exoPlayer = null;
    private boolean isPlaying = false;
    DescriptionAdapter descriptionAdapter;
    private PlayerNotificationManager manager;
    private String nrgchannel="Nrg_01";

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        descriptionAdapter = new DescriptionAdapter();
        manager = new PlayerNotificationManager(this,nrgchannel,1111,descriptionAdapter);
        manager.setUseNavigationActions(false);
        manager.setOngoing(true);
        manager.setColor(Color.BLACK);
        manager.setColorized(true);
        manager.setUseChronometer(false);
        manager.setSmallIcon(R.drawable.exo_notification_small_icon);
        manager.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        manager.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        manager.setFastForwardIncrementMs(0);

        manager.setNotificationListener(new PlayerNotificationManager.NotificationListener() {
    @Override
    public void onNotificationStarted(int notificationId, Notification notification) {
        Log.i(PlayerService.class.getCanonicalName(),"Foreground!");
        startForeground(11111111,notification);
    }

    @Override
    public void onNotificationCancelled(int notificationId) {

    }
});

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
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
        return mBinder;
    }

    public void prepareExoPlayerFromURL(Music music) {
        descriptionAdapter.setMusic(music);
        try {

            TrackSelector trackSelector = new DefaultTrackSelector();

            LoadControl loadControl = new DefaultLoadControl();

            exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
            manager.setPlayer(exoPlayer);
            exoPlayer.addListener(this);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "Nrg.Radio"), null);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();


            MediaSource audioSource = new ExtractorMediaSource(Uri.parse(music.getStreamlink()), dataSourceFactory, extractorsFactory, null, null);


            exoPlayer.prepare(audioSource);

        } catch (Exception ex) {
            Log.i("Error Occurred", "Error");

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


        int importance = NotificationManager.IMPORTANCE_MAX;

        @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel(id, nrgchannel,importance);

        mChannel.setDescription("Nrg Channel Music");
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        mChannel.setSound(null,null);
        mNotificationManager.createNotificationChannel(mChannel);
    }


}