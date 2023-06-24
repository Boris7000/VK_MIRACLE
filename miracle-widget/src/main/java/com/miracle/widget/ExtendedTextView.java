package com.miracle.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ExtendedTextView extends TextView implements ExtendedTextViewIcons{

    private final ExtendedTextHelper textHelper = new ExtendedTextHelper(this);

    public ExtendedTextView(@NonNull Context context) {
        this(context, null);
    }

    public ExtendedTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public ExtendedTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        textHelper.loadFromAttributes(attrs,defStyleAttr);
    }

    //////////////////////////////////////////////////////////////////

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(textHelper!=null) {
            textHelper.changeIconsDrawablesStates(getDrawableState());
        }
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if(textHelper!=null) {
            textHelper.jumpIconsDrawablesToState();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        textHelper.drawIcons(canvas);
    }

    //////////////////////////////////////////////////////////////////

    @Override
    public int getCompoundPaddingTop() {
        return textHelper.getCompoundPaddingTop(super.getCompoundPaddingTop());
    }

    @Override
    public int getCompoundPaddingBottom() {
        return textHelper.getCompoundPaddingBottom(super.getCompoundPaddingBottom());
    }

    @Override
    public int getCompoundPaddingLeft() {
        return textHelper.getCompoundPaddingLeft(super.getCompoundPaddingLeft());
    }

    @Override
    public int getCompoundPaddingRight() {
        return textHelper.getCompoundPaddingRight(super.getCompoundPaddingRight());
    }

    //////////////////////////////////////////////////////////////////

    @Override
    public void setIcon(@Nullable Drawable drawable, int pos) {
        textHelper.setIcon(drawable, pos);
    }

    @Override
    public void setIconResource(@DrawableRes int resourceId, int pos) {
        textHelper.setIconResource(resourceId, pos);
    }

    @Override
    public void setIconTint(@ColorInt int color, int pos) {
        textHelper.setIconTint(color, pos);
    }

    @Override
    public void setIconTintList(ColorStateList tintList, int pos) {
        textHelper.setIconTintList(tintList, pos);
    }

    @Override
    public void setIconTintMode(PorterDuff.Mode tintMode, int pos) {
        textHelper.setIconTintMode(tintMode, pos);
    }

    @Override
    public void setIconSize(int iconSize, int pos) {
        textHelper.setIconSize(iconSize, pos);
    }

    @Override
    public void setIconStickingToText(boolean stickingToText, int pos) {
        textHelper.setIconStickingToText(stickingToText, pos);
    }

    @Override
    public void setIconsTint(int color) {
        textHelper.setIconsTint(color);
    }

    @Override
    public void setIconsTintList(ColorStateList tintList) {
        textHelper.setIconsTintList(tintList);
    }

    @Override
    public void setIconsTintMode(PorterDuff.Mode tintMode) {
        textHelper.setIconsTintMode(tintMode);
    }

    @Override
    public void setIconsSize(int iconSize) {
        textHelper.setIconsSize(iconSize);
    }

    @Override
    public void setIconsStickingToText(boolean stickingToText) {
        textHelper.setIconsStickingToText(stickingToText);
    }
}
