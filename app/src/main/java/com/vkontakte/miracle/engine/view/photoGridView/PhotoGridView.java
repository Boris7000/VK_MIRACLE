package com.vkontakte.miracle.engine.view.photoGridView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;

import java.util.ArrayList;

import static com.vkontakte.miracle.engine.util.DeviceUtil.getDisplayHeight;
import static com.vkontakte.miracle.engine.util.DimensionsUtil.dpToPx;

public class PhotoGridView extends RelativeLayout {

    private final int spacing;
    private int measuredWidth = -1;
    private final int maxHeight;
    private final ArrayList<PhotoGridItem> photoGridItems = new ArrayList<>();
    private boolean canApplyChanges = true;
    private boolean needChanges = false;

    public PhotoGridView(Context context) {
        this(context, null);
    }

    public PhotoGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if(attrs!=null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PhotoGridView, 0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(context, R.styleable.PhotoGridView, attrs, attributes, 0, 0);
            }

            spacing = (int) attributes.getDimension(R.styleable.PhotoGridView_spacing, dpToPx(2, getContext()));
        }else {
            spacing = (int)  dpToPx(2, getContext());
        }
        maxHeight = (int) (getDisplayHeight(context)*0.75f);
    }


    public void calculate(){
        if(photoGridItems.isEmpty()) return;
        GridCalculator gridCalculator = new GridCalculator(photoGridItems);
        gridCalculator.calculateGrid(measuredWidth, maxHeight, spacing);
        needChanges = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(!canApplyChanges) return;

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (measuredWidth != parentWidth||needChanges) {
            measuredWidth = parentWidth;
            calculate();
        }

        for (int p = 0; p < photoGridItems.size(); p++) {

            PhotoGridItem photoGridItem = photoGridItems.get(p);
            PhotoGridPosition position = photoGridItem.gridPosition;
            PhotoGridItemView child = (PhotoGridItemView) getChildAt(p);
            child.set(photoGridItem, false);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();

            params.width = position.sizeX;
            params.height = position.sizeY;
            params.topMargin = position.marginY;
            params.leftMargin = position.marginX;

            Log.d("irjfirjfir","m"+position.sizeX+" "+position.sizeY);

            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(!canApplyChanges) return;

        int parentWidth = MeasureSpec.getSize(getWidth());

        if (measuredWidth != parentWidth||needChanges) {
            measuredWidth = parentWidth;
            calculate();
        }

        for (int p = 0; p < photoGridItems.size(); p++) {

            PhotoGridItem photoGridItem = photoGridItems.get(p);
            PhotoGridPosition position = photoGridItem.gridPosition;
            PhotoGridItemView child = (PhotoGridItemView) getChildAt(p);
            child.set(photoGridItem, false);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) child.getLayoutParams();

            params.width = position.sizeX;
            params.height = position.sizeY;
            params.topMargin = position.marginY;
            params.leftMargin = position.marginX;

            Log.d("irjfirjfir","l"+position.sizeX+" "+position.sizeY);

            child.layout(position.marginX, position.marginY, params.rightMargin, params.bottomMargin);
        }
        super.onLayout(changed, l, t, r, b);
    }

    public void setPhotos(ArrayList<MediaItem> mediaItems) {

        if(mediaItems.isEmpty()){
            if(getVisibility()!=GONE) {
                setVisibility(GONE);
            }
            return;
        } else {
            if(getVisibility()!=VISIBLE) {
                setVisibility(VISIBLE);
            }
        }


        if (mediaItems.size() != photoGridItems.size()) {

            canApplyChanges = false;

            int k = mediaItems.size() - getChildCount();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (int j = 0; j < k; j++) {
                inflater.inflate(R.layout.view_photo_grid_item, this, true);
            }

            for (int i = mediaItems.size(); i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getVisibility() != GONE) {
                    view.setVisibility(GONE);
                }
            }

            for (int g = 0; g < mediaItems.size(); g++) {

                View view = getChildAt(g);
                if (view.getVisibility() != VISIBLE) {
                    view.setVisibility(VISIBLE);
                }

            /*
            int finalG = g;
            postGridItemView.setOnClickListener(v -> {
                TODO здесь должен быть механизм открытия фото
            });
            */

            }
            canApplyChanges = true;
        }
        needChanges = true;
        photoGridItems.clear();
        for(MediaItem mediaItem : mediaItems){
            PhotoGridItem photoGridItem = new PhotoGridItem();
            photoGridItem.mediaItem = mediaItem;
            photoGridItems.add(photoGridItem);
        }
        requestLayout();
    }
}