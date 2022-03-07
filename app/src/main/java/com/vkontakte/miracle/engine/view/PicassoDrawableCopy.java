package com.vkontakte.miracle.engine.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.widget.ImageView;

public class PicassoDrawableCopy  extends BitmapDrawable {
    // Only accessed from main thread.
    private float fadeDuration = 200f; //ms

    /**
     * Create or update the drawable on the target {@link ImageView} to display the supplied bitmap
     * image.
     */
    public static void setBitmap(ImageView target, Context context, Bitmap bitmap) {
        setBitmap(target,context,bitmap,200f);
    }

    public static void setBitmap(ImageView target, Context context, Bitmap bitmap, float fadeDuration) {
        Drawable placeholder = target.getDrawable();
        if (placeholder instanceof Animatable) {
            ((Animatable) placeholder).stop();
        }
        PicassoDrawableCopy drawable =
                new PicassoDrawableCopy(context, bitmap, placeholder, fadeDuration);
        target.setImageDrawable(drawable);
    }

    /**
     * Create or update the drawable on the target {@link ImageView} to display the supplied
     * placeholder image.
     */
    public static void setPlaceholder(ImageView target, Drawable placeholderDrawable) {
        target.setImageDrawable(placeholderDrawable);
        if (target.getDrawable() instanceof Animatable) {
            ((Animatable) target.getDrawable()).start();
        }
    }


    Drawable placeholder;

    long startTimeMillis;
    boolean animating;
    int alpha = 0xFF;

    PicassoDrawableCopy(Context context, Bitmap bitmap, Drawable placeholder, float fadeDuration) {
        super(context.getResources(), bitmap);

        boolean fade = fadeDuration>0;
        if (fade) {
            this.fadeDuration = fadeDuration;
            this.placeholder = placeholder;
            animating = true;
            startTimeMillis = SystemClock.uptimeMillis();
        }
    }

    @Override public void draw(Canvas canvas) {
        if (!animating) {
            super.draw(canvas);
        } else {
            float normalized = (SystemClock.uptimeMillis() - startTimeMillis) / fadeDuration;
            if (normalized >= 1f) {
                animating = false;
                placeholder = null;
                super.draw(canvas);
            } else {
                if (placeholder != null) {
                    placeholder.draw(canvas);
                }

                // setAlpha will call invalidateSelf and drive the animation.
                int partialAlpha = (int) (alpha * normalized);
                super.setAlpha(partialAlpha);
                super.draw(canvas);
                super.setAlpha(alpha);
            }
        }
    }

    @Override public void setAlpha(int alpha) {
        this.alpha = alpha;
        if (placeholder != null) {
            placeholder.setAlpha(alpha);
        }
        super.setAlpha(alpha);
    }

    @Override public void setColorFilter(ColorFilter cf) {
        if (placeholder != null) {
            placeholder.setColorFilter(cf);
        }
        super.setColorFilter(cf);
    }

    @Override protected void onBoundsChange(Rect bounds) {
        if (placeholder != null) {
            placeholder.setBounds(bounds);
        }
        super.onBoundsChange(bounds);
    }
}
