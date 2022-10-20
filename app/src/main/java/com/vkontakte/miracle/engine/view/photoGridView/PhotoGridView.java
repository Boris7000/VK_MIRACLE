package com.vkontakte.miracle.engine.view.photoGridView;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_PHOTO;
import static com.vkontakte.miracle.engine.util.DeviceUtil.getWindowHeight;
import static com.vkontakte.miracle.engine.util.DimensionsUtil.dpToPx;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.recycler.IRecyclerView;
import com.vkontakte.miracle.engine.recycler.MiracleViewRecycler;
import com.vkontakte.miracle.engine.recycler.RecyclerController;
import com.vkontakte.miracle.engine.view.photoGridView.calculator.GridCalculator;
import com.vkontakte.miracle.fragment.photos.FragmentPhotoViewerDialog;
import com.vkontakte.miracle.fragment.photos.PhotoViewerItem;
import com.vkontakte.miracle.model.DataItemWrap;

import java.util.ArrayList;

public class PhotoGridView extends FrameLayout implements IRecyclerView {

    private final RecyclerController recyclerController;

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
        recyclerController = new RecyclerController(LayoutInflater.from(context));
        recyclerController.getViewHolderFabricMap()
                .put(TYPE_WRAPPED_PHOTO, new PhotoGridViewHolderFabric());

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

        ArrayList<RecyclerView.ViewHolder> buffer = recyclerController.getBuffer();
        for (int p = 0; p < photoGridItems.size(); p++) {
            PhotoGridItem photoGridItem = photoGridItems.get(p);
            PhotoGridItemViewHolder viewHolder = (PhotoGridItemViewHolder) buffer.get(p);
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

        ArrayList<RecyclerView.ViewHolder> buffer = recyclerController.getBuffer();
        for (int p = 0; p < photoGridItems.size(); p++) {
            PhotoGridItem photoGridItem = photoGridItems.get(p);
            PhotoGridItemViewHolder viewHolder = (PhotoGridItemViewHolder) buffer.get(p);
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

        recyclerController.resolveSingleTypeItems(this, itemDataHolders, this.itemDataHolders);

        ArrayList<RecyclerView.ViewHolder> buffer = recyclerController.getBuffer();
        for(int i=0;i<buffer.size();i++){
            final int finalI = i;
            buffer.get(i).itemView.setOnClickListener(view -> {
                ArrayList<PhotoViewerItem> photoViewerItems = new ArrayList<>();
                for(int j=0; j<buffer.size(); j++){
                    RecyclerView.ViewHolder viewHolder = buffer.get(j);
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
            DataItemWrap<?,?> dataItemWrap = (DataItemWrap<?,?>) itemDataHolder;
            PhotoGridItem photoGridItem = new PhotoGridItem();
            photoGridItem.mediaItem = (MediaItem) dataItemWrap.getItem();
            photoGridItems.add(photoGridItem);
        }
        requestLayout();
    }

    @Override
    public MiracleViewRecycler getRecycledViewPool() {
        return null;
    }

    @Override
    public void setRecycledViewPool(MiracleViewRecycler recycledViewPool) {
        recyclerController.setRecycledViewPool(recycledViewPool);
    }

    public class PhotoGridViewHolderFabric implements ViewHolderFabric {
        @Override
        public RecyclerView.ViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PhotoGridItemViewHolder(inflater.inflate(layoutResId,viewGroup, false));
        }
    }

}