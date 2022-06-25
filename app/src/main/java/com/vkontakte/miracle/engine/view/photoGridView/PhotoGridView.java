package com.vkontakte.miracle.engine.view.photoGridView;

import static com.vkontakte.miracle.engine.adapter.MiracleViewRecycler.resolveSingleTypeItems;
import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_PHOTO_ITEM;
import static com.vkontakte.miracle.engine.util.DeviceUtil.getWindowHeight;
import static com.vkontakte.miracle.engine.util.DimensionsUtil.dpToPx;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.holders.PhotoGridItemViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleViewRecycler;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.fragment.photos.FragmentPhotoViewerDialog;
import com.vkontakte.miracle.fragment.photos.PhotoViewerItem;

import java.util.ArrayList;

public class PhotoGridView extends FrameLayout{

    private final LayoutInflater inflater;
    private final ArrayList<RecyclerView.ViewHolder> cache = new ArrayList<>();
    private final ArrayMap<Integer, ViewHolderFabric> viewHolderFabricMap = new ArrayMap<>();
    private MiracleViewRecycler recycledViewPool = new MiracleViewRecycler();
    {
        viewHolderFabricMap.put(TYPE_PHOTO_ITEM, new PhotoGridViewHolderFabric());
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private final static int DEFAULT_SPACING_DP = 2;
    private final static int DEFAULT_LAYOUT_RES = R.layout.view_photo_grid_item;
    private final int layoutResId;

    private final int spacing;
    private int measuredWidth = -1;
    private final int maxHeight;
    private final ArrayList<PhotoGridItem> photoGridItems = new ArrayList<>();
    private ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
    private boolean canApplyChanges = true;
    private boolean needChanges = false;

    public PhotoGridView(Context context) {
        this(context, null);
    }

    public PhotoGridView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        maxHeight = (int) (getWindowHeight(context)*0.75f);
        if(attrs!=null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PhotoGridView, 0, 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(context, R.styleable.PhotoGridView, attrs, attributes, 0, 0);
            }
            layoutResId = attributes.getResourceId(R.styleable.PhotoGridView_listItemLayout, DEFAULT_LAYOUT_RES);
            spacing = (int) attributes.getDimension(R.styleable.PhotoGridView_spacing, dpToPx(context, DEFAULT_SPACING_DP));
        }else {
            spacing = (int)  dpToPx(context, DEFAULT_SPACING_DP);
            layoutResId = DEFAULT_LAYOUT_RES;
        }
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
            PhotoGridItemViewHolder viewHolder = (PhotoGridItemViewHolder) cache.get(p);
            viewHolder.bind(photoGridItem);

            View itemView = viewHolder.itemView;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) itemView.getLayoutParams();
            PhotoGridPosition position = photoGridItem.gridPosition;

            params.width = position.sizeX;
            params.height = position.sizeY;
            params.topMargin = position.marginY;
            params.leftMargin = position.marginX;

            itemView.measure(widthMeasureSpec, heightMeasureSpec);
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
            PhotoGridItemViewHolder viewHolder = (PhotoGridItemViewHolder) cache.get(p);
            viewHolder.bind(photoGridItem);

            View itemView = viewHolder.itemView;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) itemView.getLayoutParams();
            PhotoGridPosition position = photoGridItem.gridPosition;

            params.width = position.sizeX;
            params.height = position.sizeY;
            params.topMargin = position.marginY;
            params.leftMargin = position.marginX;

            itemView.layout(position.marginX, position.marginY, params.rightMargin, params.bottomMargin);
        }
        super.onLayout(changed, l, t, r, b);
    }

    public void setPhotos(ArrayList<ItemDataHolder> itemDataHolders) {

        canApplyChanges = false;

        resolveSingleTypeItems(this, itemDataHolders, this.itemDataHolders, cache, recycledViewPool,
                viewHolderFabricMap, inflater);

        for(int i=0;i<cache.size();i++){
            final int finalI = i;
            cache.get(i).itemView.setOnClickListener(view -> {
                ArrayList<PhotoViewerItem> photoViewerItems = new ArrayList<>();
                for(int j=0; j<cache.size(); j++){
                    RecyclerView.ViewHolder viewHolder = cache.get(j);
                    if(viewHolder instanceof PhotoGridItemViewHolder){
                        PhotoGridItem photoGridItem = photoGridItems.get(j);
                        PhotoGridPosition gridPosition = photoGridItem.getGridPosition();
                        PhotoGridItemViewHolder wrappedPhotoGridItemViewHolder = (PhotoGridItemViewHolder) viewHolder;
                        View child = wrappedPhotoGridItemViewHolder.itemView;
                        int[]location = new int[2];
                        child.getLocationInWindow(location);
                        int rawX = location[0];
                        int rawY = location[1];
                        Drawable drawable = wrappedPhotoGridItemViewHolder.getDrawable();
                        Drawable drwNewCopy = drawable==null?null:drawable
                                .getConstantState().newDrawable().mutate();
                        photoViewerItems.add(new PhotoViewerItem(photoGridItem.mediaItem, drwNewCopy,
                                rawX, rawY, gridPosition.sizeX,gridPosition.sizeY));
                    }
                }
                FragmentPhotoViewerDialog.PhotoViewerData photoViewerData =
                        new FragmentPhotoViewerDialog.PhotoViewerData(photoViewerItems, finalI);
                new FragmentPhotoViewerDialog(photoViewerData).show(((AppCompatActivity)getContext())
                        .getSupportFragmentManager(),"");
            });
        }

        this.itemDataHolders = itemDataHolders;

        canApplyChanges = true;
        needChanges = true;
        photoGridItems.clear();
        for(ItemDataHolder itemDataHolder : itemDataHolders){
            PhotoGridItem photoGridItem = new PhotoGridItem();
            photoGridItem.mediaItem = (MediaItem) itemDataHolder;
            photoGridItems.add(photoGridItem);
        }
        requestLayout();
    }

    public void setRecycledViewPool(MiracleViewRecycler recycledViewPool) {
        this.recycledViewPool = recycledViewPool;
    }

    public class PhotoGridViewHolderFabric implements ViewHolderFabric {
        @Override
        public RecyclerView.ViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PhotoGridItemViewHolder(inflater.inflate(layoutResId,viewGroup, false));
        }
    }

}