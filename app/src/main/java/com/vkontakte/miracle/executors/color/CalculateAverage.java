package com.vkontakte.miracle.executors.color;

import static com.vkontakte.miracle.engine.util.ImageUtil.getAverageHSLFromBitmap;

import android.graphics.Bitmap;

import androidx.core.graphics.ColorUtils;

import com.vkontakte.miracle.engine.async.AsyncExecutor;

public class CalculateAverage extends AsyncExecutor<Integer> {

    private final Bitmap bitmap;

    public CalculateAverage(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    public Integer inBackground() {
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
        float[] hsl = getAverageHSLFromBitmap(scaled);
        hsl[1] =  Math.min(hsl[1],0.52f);
        hsl[2] = Math.max(Math.min(hsl[2],0.43f),0.23f);
        return ColorUtils.HSLToColor(hsl);
    }
}
