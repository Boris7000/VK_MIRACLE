package com.vkontakte.miracle.engine.view;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.recycler.IRecyclerView;
import com.miracle.engine.recycler.MiracleViewRecycler;
import com.miracle.engine.recycler.RecyclerController;

import java.util.ArrayList;

public class RecycleListView extends LinearLayout implements IRecyclerView {

    private final RecyclerController recyclerController;

    /////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean canApplyChanges = true;
    private final ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
    private int measuredWidth = -1;

    public RecycleListView(Context context) {
        this(context,null);
    }

    public RecycleListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        recyclerController = new RecyclerController(LayoutInflater.from(context));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if(!canApplyChanges) return;

        measuredWidth = MeasureSpec.getSize(widthMeasureSpec);

        for (int p = 0; p < itemDataHolders.size(); p++) {
            View child = getChildAt(p);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            params.width = measuredWidth;
            child.measure(widthMeasureSpec, heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(!canApplyChanges) return;

        measuredWidth = MeasureSpec.getSize(getWidth());

        for (int p = 0; p < itemDataHolders.size(); p++) {
            View child = getChildAt(p);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
            params.width = measuredWidth;
            child.layout(params.leftMargin, params.topMargin, params.rightMargin, params.bottomMargin);
        }
        super.onLayout(changed, l, t, r, b);
    }

    public void setItems(ArrayList<ItemDataHolder> itemDataHolders){
        setItems(itemDataHolders, true);
    }

    public void setItems(ArrayList<ItemDataHolder> itemDataHolders, boolean multiType){

        canApplyChanges = false;

        if(multiType){
            recyclerController.resolveMultiTypeItems(this, itemDataHolders, this.itemDataHolders);
        } else {
            recyclerController.resolveSingleTypeItems(this, itemDataHolders, this.itemDataHolders);
        }

        ArrayList<RecyclerView.ViewHolder> buffer = recyclerController.getBuffer();
        for(int i=0; i<buffer.size(); i++){
            MiracleViewHolder miracleViewHolder = (MiracleViewHolder) buffer.get(i);
            miracleViewHolder.bind(itemDataHolders.get(i));
        }

        this.itemDataHolders.clear();
        this.itemDataHolders.addAll(itemDataHolders);

        canApplyChanges = true;
        requestLayout();
    }

    public void clearItems(){
        canApplyChanges = false;
        recyclerController.loadAllViewsToPool(this, this.itemDataHolders);
        this.itemDataHolders.clear();
        canApplyChanges = true;
    }

    @Override
    public MiracleViewRecycler getViewRecycler() {
        return recyclerController.getRecycledViewPool();
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        return recyclerController.getViewHolderFabricMap();
    }

    @Override
    public void setViewRecycler(MiracleViewRecycler recycledViewPool) {
        recyclerController.setRecycledViewPool(recycledViewPool);
    }
}
