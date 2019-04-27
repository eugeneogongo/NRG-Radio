package com.nrgr.adio.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.nrgr.adio.R;
import com.nrgr.adio.ViewModel.ViewData;
import com.nrgr.adio.Widget.ColorPicker;
import com.nrgr.adio.Widget.Loader;

import java.util.Random;

public class SplashScreen extends AppCompatActivity {

    private ImageView Imagebanner;
    private FrameLayout layout;
    private int[] images = new int[]{R.drawable.breakfast, R.drawable.transit, R.drawable.splash};
    private Loader loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initView();
    }

    private void initView() {
        int image = setImage();
        Imagebanner = findViewById(R.id.Imagebanner);
        Imagebanner.setImageResource(image);
        BitmapDrawable drawable;

        drawable = (BitmapDrawable) Imagebanner.getDrawable();
        final Bitmap bitmap = drawable.getBitmap();
        int color = ColorPicker.getColor(bitmap, this);
        layout = findViewById(R.id.layout);
        layout.setBackgroundColor(color);
        loader = findViewById(R.id.loader);
        ViewData _View_data = ViewModelProviders.of(this).get(ViewData.class);
        //Start home after the ball bounce animation ends
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Home.class);
                startActivity(intent);
                finish();
            }
        }, (loader.getMovieDuration() + 1000));
    }

    private int setImage() {
        return images[new Random().nextInt(3)];
    }
}
