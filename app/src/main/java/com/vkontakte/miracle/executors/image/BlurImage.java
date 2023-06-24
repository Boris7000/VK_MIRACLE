package com.vkontakte.miracle.executors.image;

import static com.miracle.engine.util.ImageUtil.fastBlur;

import android.graphics.Bitmap;

import com.miracle.engine.async.AsyncExecutor;

public class BlurImage extends AsyncExecutor<Bitmap> {

    private final Bitmap bitmap;
    private final float scale;
    private final int radius;

    public BlurImage(Bitmap bitmap, float scale, int radius){
        this.bitmap = bitmap;
        this.scale = scale;
        this.radius = radius;
    }

    @Override
    public Bitmap inBackground() {
        return fastBlur(bitmap, scale, radius);
    }
}
