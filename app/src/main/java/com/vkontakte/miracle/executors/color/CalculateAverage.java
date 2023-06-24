package com.vkontakte.miracle.executors.color;

import static com.miracle.engine.util.ImageUtil.getAverageHSLFromBitmap;

import android.graphics.Bitmap;

import androidx.core.graphics.ColorUtils;

import com.miracle.engine.async.AsyncExecutor;

public class CalculateAverage extends AsyncExecutor<Integer> {

    private final Bitmap bitmap;

    public CalculateAverage(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    @Override
    public Integer inBackground() {
        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
        float[] hsl = getAverageHSLFromBitmap(scaled);
        float[] hslEdited = new float[]{
                hsl[0],
                Math.min(hsl[1],0.65f),
                Math.max(Math.min(hsl[2], 0.43f), 0.23f),
        };
        return ColorUtils.HSLToColor(hslEdited);
    }
}
