package com.vkontakte.miracle.fragment.photos;

import android.graphics.drawable.Drawable;

import com.vkontakte.miracle.engine.view.photoGridView.MediaItem;

public class PhotoViewerItem {

    private final MediaItem mediaItem;
    private Drawable preview;
    private final float rawX;
    private final float rawY;
    private final int width;
    private final int height;

    public PhotoViewerItem(MediaItem mediaItem, Drawable preview, float rawX, float rawY, int width, int height){
        this.mediaItem = mediaItem;
        this.preview = preview;
        this.rawX = rawX;
        this.rawY = rawY;
        this.width = width;
        this.height = height;
    }

    public MediaItem getMediaItem() {
        return mediaItem;
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
