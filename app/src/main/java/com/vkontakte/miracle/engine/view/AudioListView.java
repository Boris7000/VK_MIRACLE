package com.vkontakte.miracle.engine.view;

import static com.vkontakte.miracle.engine.adapter.MiracleViewRecycler.resolveSingleTypeItems;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleViewRecycler;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;

import java.util.ArrayList;

public class AudioListView extends LinearLayout{

    private final LayoutInflater inflater;
    private final ArrayList<RecyclerView.ViewHolder> cache = new ArrayList<>();
    private final ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap = new ArrayMap<>();
    private MiracleViewRecycler recycledViewPool = new MiracleViewRecycler();
    {
        viewHolderFabricMap.put(TYPE_WRAPPED_AUDIO, new AudioListViewHolderFabric());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private final static int DEFAULT_LAYOUT_RES = R.layout.view_audio_item_post;
    private final int layoutResId;
    private boolean canApplyChanges = true;
    private ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
    private int measuredWidth = -1;

    public AudioListView(Context context) {
        this(context,null);
    }

    public AudioListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        if(attrs!=null){
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.AudioListView, 0, 0);
            layoutResId = attributes.getResourceId(R.styleable.AudioListView_listItemLayout, DEFAULT_LAYOUT_RES);
            attributes.recycle();
        } else {
            layoutResId = DEFAULT_LAYOUT_RES;
        }
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

        canApplyChanges = false;

        resolveSingleTypeItems(this, itemDataHolders, this.itemDataHolders, cache, recycledViewPool,
                viewHolderFabricMap, inflater);

        for(int i=0; i<cache.size(); i++){
            WrappedAudioViewHolder wrappedAudioViewHolder = (WrappedAudioViewHolder) cache.get(i);
            wrappedAudioViewHolder.bind(itemDataHolders.get(i));
        }

        this.itemDataHolders = itemDataHolders;
        canApplyChanges = true;
    }

    public void setRecycledViewPool(MiracleViewRecycler recycledViewPool) {
        this.recycledViewPool = recycledViewPool;
    }

    public class AudioListViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new WrappedAudioViewHolder(inflater.inflate(layoutResId, viewGroup, false));
        }
    }

}
