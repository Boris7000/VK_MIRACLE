package com.vkontakte.miracle.engine.view.zoomableImageView;

public interface OnPhotoActionListener {
    void onRelease(ZoomableImageView2 image);
    void onDrag(ZoomableImageView2 image, float rawX, float rawY);
    void onSingleTap(ZoomableImageView2 image);
}
