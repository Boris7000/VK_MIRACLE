package com.vkontakte.miracle.engine.view.photoGridView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.model.photos.fields.Size;

public class PhotoGridItemView extends FrameLayout {

    private ImageView imageView;
    private TextView textView;
    private boolean initialized = false;

    public PhotoGridItemView(@NonNull Context context) {
        super(context);
    }

    public PhotoGridItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PhotoGridItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onFinishInflate() { super.onFinishInflate();  if(!initialized)init();}



    public void init(){

        initialized =false;
        imageView = (ImageView) getChildAt(0);
        textView = (TextView) getChildAt(1);

    }

    public void set(PhotoGridItem photoGridItem, boolean need3x2) {
        if (!initialized) init();

        MediaItem mediaItem = photoGridItem.mediaItem;
        PhotoGridPosition photoGridPosition = photoGridItem.gridPosition;

        Size size = mediaItem.getSizeForWidth((int) (photoGridPosition.sizeX/1.5f),need3x2);
        if(size!=null) {
            Picasso.get().load(size.getUrl()).into(imageView);
        }else (imageView).setImageDrawable(null);

    }

}
