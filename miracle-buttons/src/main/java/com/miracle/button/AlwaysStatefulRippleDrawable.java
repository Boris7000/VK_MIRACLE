package com.miracle.button;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AlwaysStatefulRippleDrawable extends RippleDrawable {

    public AlwaysStatefulRippleDrawable(@NonNull ColorStateList color,
                                        @Nullable Drawable content,
                                        @Nullable Drawable mask) {
        super(color, content, mask);
    }




    @Override
    public boolean isStateful() {
        return true;
    }

}
