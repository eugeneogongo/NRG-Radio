package com.nrgr.adio.Widget;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;
import androidx.palette.graphics.Palette;

import com.nrgr.adio.R;

public class ColorPicker {
    public static Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }


    public static int getColor(Bitmap bitmap, Context mContext) {
        Palette p = createPaletteSync(bitmap);
        Palette.Swatch vibrantSwatch = p.getMutedSwatch();
        int backgroundColor = ContextCompat.getColor(mContext, R.color.colorAccent);
        // Check that the Vibrant swatch is available
        if (vibrantSwatch != null) {
            backgroundColor = vibrantSwatch.getRgb();
        }
        return backgroundColor;
    }
}
