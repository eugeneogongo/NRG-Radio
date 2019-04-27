package com.nrgr.adio.Widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.util.AttributeSet;
import android.view.View;

import com.nrgr.adio.R;

import java.io.InputStream;

public class Loader extends View {

    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long movieDuration;
    private long mMovieStart;

    public Loader(Context context) {
        super(context);
        init(context);
    }

    public Loader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Loader(Context context, AttributeSet attrs,
                  int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @SuppressLint("ResourceType")
    private void init(Context context) {
        setFocusable(true);
        InputStream gifInputStream = context.getResources()
                .openRawResource(R.drawable.loader);

        gifMovie = Movie.decodeStream(gifInputStream);
        movieWidth = gifMovie.width();
        movieHeight = gifMovie.height();
        movieDuration = gifMovie.duration();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

    public int getMovieWidth() {
        return movieWidth;
    }

    public int getMovieHeight() {
        return movieHeight;
    }

    public long getMovieDuration() {
        return movieDuration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {   // first time
            mMovieStart = now;
        }

        if (gifMovie != null) {

            int dur = gifMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }

            int relTime = (int) ((now - mMovieStart) % dur);

            gifMovie.setTime(relTime);

            gifMovie.draw(canvas, 0, 0);
            invalidate();

        }

    }

}