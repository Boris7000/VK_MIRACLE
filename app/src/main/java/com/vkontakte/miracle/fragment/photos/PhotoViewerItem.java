package com.vkontakte.miracle.fragment.photos;

import android.graphics.drawable.Drawable;

import com.miracle.engine.adapter.holder.ItemDataHolder;

public class PhotoViewerItem {

    private final ItemDataHolder itemDataHolder;
    private Drawable preview;
    private final float rawX;
    private final float rawY;
    private final int width;
    private final int height;

    public PhotoViewerItem(ItemDataHolder itemDataHolder, Drawable preview, float rawX, float rawY, int width, int height){
        this.itemDataHolder = itemDataHolder;
        this.preview = preview;
        this.rawX = rawX;
        this.rawY = rawY;
        this.width = width;
        this.height = height;
    }

    public ItemDataHolder getItemDataHolder() {
        return itemDataHolder;
    }

    public Drawable getPreview() {
        return preview;
    }

    public float getRawX() {
        return rawX;
    }

    public float getRawY() {
        return rawY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void clearPreview(){
        preview = null;
    }

}
