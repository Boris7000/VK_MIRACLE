package com.miracle.engine.adapter;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StableGridLayoutManager extends GridLayoutManager {

    public StableGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public StableGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public StableGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i = 0;
        try {
            i = super.scrollVerticallyBy(dy, recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i = 0;
        try {
            i = super.scrollHorizontallyBy(dx, recycler, state);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return i;
    }
}
