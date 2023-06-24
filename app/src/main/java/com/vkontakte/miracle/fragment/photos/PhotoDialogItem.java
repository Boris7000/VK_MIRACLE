package com.vkontakte.miracle.fragment.photos;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.adapter.holder.ItemDataHolder;

public class PhotoDialogItem {
    private final ItemDataHolder itemDataHolder;
    private Drawable preview;
    private float rawX = -1f;
    private float rawY = - 1f;
    private int width = -1;
    private int height = -1;

    public PhotoDialogItem(ItemDataHolder itemDataHolder) {
        this.itemDataHolder = itemDataHolder;
    }

    @NonNull
    public ItemDataHolder getItemDataHolder() {
        return itemDataHolder;
    }

    @Nullable
    public Drawable getPreview() {
        return preview;
    }

    public void setPreview(Drawable preview) {
        this.preview = preview;
    }

    public float getRawX() {
        return rawX;
    }

    public void setRawX(float rawX) {
        this.rawX = rawX;
    }

    public float getRawY() {
        return rawY;
    }

    public void setRawY(float rawY) {
        this.rawY = rawY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
