package com.nrgr.adio.Media;

import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.nrgr.adio.Scarpper.Music;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class DescriptionAdapter implements PlayerNotificationManager.MediaDescriptionAdapter {
    Music music;

    public void setMusic(Music music) {
        this.music = music;
    }

    @Override
    public String getCurrentContentTitle(Player player) {

        return music.getTitle();
    }

    @Nullable
    @Override
    public PendingIntent createCurrentContentIntent(Player player) {
        return null;
    }

    @Nullable
    @Override
    public String getCurrentContentText(Player player) {
        return music.getTitle();
    }

    @Nullable
    @Override
    public Bitmap getCurrentLargeIcon(Player player, PlayerNotificationManager.BitmapCallback callback) {
        final Bitmap[] bit = {null};
        Picasso.get().load(music.getPiclink())
.into(new Target() {
    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        bit[0] = bitmap;
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
});

    return bit[0];
    }
}
