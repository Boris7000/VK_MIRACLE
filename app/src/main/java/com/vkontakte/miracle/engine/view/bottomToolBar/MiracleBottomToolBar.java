package com.vkontakte.miracle.engine.view.bottomToolBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.miracle.engine.util.DimensionsUtil;

public class MiracleBottomToolBar extends LinearLayout{

    private final int maxItemLength;

    public MiracleBottomToolBar(Context context) {
        this(context,null);
    }

    public MiracleBottomToolBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        maxItemLength = (int) DimensionsUtil.dpToPx(context, 80);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int childCount = getChildCount();
        int maxTotalHeight = childCount*maxItemLength;
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec-getPaddingEnd()-getPaddingStart());
        int parentHeight = View.MeasureSpec.getSize(heightMeasureSpec-getPaddingTop()-getPaddingBottom());

        if(getOrientation()== LinearLayout.HORIZONTAL){

            int childLength = maxItemLength;
            if(maxTotalHeight>parentWidth){
                childLength = parentWidth/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.width = childLength;
                child.measure(params.width, params.height);
            }
        } else {

            int childLength = maxItemLength;
            if(maxTotalHeight>parentHeight){
                childLength = parentHeight/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.height = childLength;
                child.measure(params.width, params.height);
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int childCount = getChildCount();
        int maxTotalHeight = childCount*maxItemLength;

        int parentWidth = getWidth()-getPaddingEnd()-getPaddingStart();
        int parentHeight = getHeight()-getPaddingTop()-getPaddingBottom();

        if(getOrientation()==LinearLayout.HORIZONTAL){

            int childLength = maxItemLength;
            if(maxTotalHeight>parentWidth){
                childLength = parentWidth/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.width = childLength;
                child.layout(0,0,0,0);
            }
        } else {

            int childLength = maxItemLength;
            if(maxTotalHeight>parentHeight){
                childLength = parentHeight/childCount;

            }
            for (int c = 0; c < childCount; c++) {
                View child = getChildAt(c);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                params.height = childLength;
                child.layout(0,0,0,0);
            }
        }

        super.onLayout(changed, l, t, r, b);
    }

}
