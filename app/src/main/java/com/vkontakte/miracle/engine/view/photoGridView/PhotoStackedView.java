package com.vkontakte.miracle.engine.view.photoGridView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.model.photos.PhotoItem;

import java.util.ArrayList;

import static com.vkontakte.miracle.engine.util.DimensionsUtil.dpToPx;

public class PhotoStackedView extends RelativeLayout {

    private final int spacing;
    private int measuredWidth = -1;
    private int rowLength;
    private final ArrayList<PhotoGridItem> photoGridItems = new ArrayList<>();
    private boolean canApplyChanges = true;
    private boolean needChanges = false;

    public PhotoStackedView(Context context) {
        this(context, null);
    }

    public PhotoStackedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if(attrs!=null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PhotoStackedView, 0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(context, R.styleable.PhotoStackedView, attrs, attributes, 0, 0);
            }

            spacing = (int) attributes.getDimension(R.styleable.PhotoStackedView_itemsSpacing, dpToPx(2, getContext()));
        }else {
            spacing = (int)  dpToPx(2, getContext());
        }
    }

    public void calculate(){
        int count = photoGridItems.size();
        int childWidth = (measuredWidth-(spacing*(rowLength-1)))/rowLength;

        for (int p = 0; p < count; p++) {
            PhotoGridItem photoGridItem = photoGridItems.get(p);
            PhotoGridPosition photoGridPosition = new PhotoGridPosition();
            photoGridPosition.sizeX = childWidth;
            photoGridPosition.sizeY = childWidth;
            photoGridPosition.marginY = spacing;
            photoGridPosition.marginX = p*(spacing+photoGridPosition.sizeX);
            photoGridItem.gridPosition = photoGridPosition;
        }
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

            child.measure(position.sizeX, position.sizeY);
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

            child.layout(position.marginX, position.marginY, params.rightMargin, params.bottomMargin);
        }
        super.onLayout(changed, l, t, r, b);
    }

    public void setPhotos(ArrayList<PhotoItem> photos, int rowLength) {

        this.rowLength = rowLength;

        if(photos.isEmpty()){
            if(getVisibility()!=GONE) {
                setVisibility(GONE);
            }
            return;
        } else {
            if(getVisibility()!=VISIBLE) {
                setVisibility(VISIBLE);
            }
        }


        if (photos.size() != photoGridItems.size()) {

            canApplyChanges = false;

            int k = photos.size() - getChildCount();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            for (int j = 0; j < k; j++) {
                inflater.inflate(R.layout.view_photo_grid_item, this, true);
            }

            for (int i = photos.size(); i < getChildCount(); i++) {
                View view = getChildAt(i);
                if (view.getVisibility() != GONE) {
                    view.setVisibility(GONE);
                }
            }

            for (int g = 0; g < photos.size(); g++) {

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
        for(PhotoItem photoItem : photos){
            PhotoGridItem photoGridItem = new PhotoGridItem();
            photoGridItem.mediaItem = photoItem;
            photoGridItems.add(photoGridItem);
        }
        requestLayout();
    }
}
