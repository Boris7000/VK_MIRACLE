package com.vkontakte.miracle.engine.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.R;

public class OneToOneSquareView extends FrameLayout {

    private int orientation;

    public static final int SIZE_FROM_WIDTH = 0;
    public static final int SIZE_FROM_HEIGHT = 1;

    public OneToOneSquareView(@NonNull Context context) {
        super(context);
    }

    public OneToOneSquareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.OneToOneSquareView, 0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveAttributeDataForStyleable(context, R.styleable.OneToOneSquareView, attrs, a, 0, 0);
        }

        orientation = a.getInt(R.styleable.OneToOneSquareView_sizeFrom, 0);
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(orientation==SIZE_FROM_WIDTH){
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec),MeasureSpec.getMode(widthMeasureSpec)));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec),MeasureSpec.getMode(heightMeasureSpec)), heightMeasureSpec);
        }

    }

}
