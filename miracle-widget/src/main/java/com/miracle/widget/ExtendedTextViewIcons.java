package com.miracle.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

public interface ExtendedTextViewIcons {

    void setIcon(@Nullable Drawable drawable, int pos);
    void setIconResource(@DrawableRes int resourceId, int pos);

    void setIconTint(@ColorInt int color, int pos);
    void setIconTintList(ColorStateList tintList, int pos);
    void setIconTintMode(PorterDuff.Mode tintMode, int pos);

    void setIconSize(int iconSize, int pos);
    void setIconStickingToText(boolean stickingToText, int pos);

    /////////////////////////////////////////////////////////

    void setIconsTint(@ColorInt int color);
    void setIconsTintList(ColorStateList tintList);
    void setIconsTintMode(PorterDuff.Mode tintMode);

    void setIconsSize(int iconSize);
    void setIconsStickingToText(boolean stickingToText);

    /////////////////////////////////////////////////////////

}
