package com.vkontakte.miracle.engine.view.photoGridView;

import static com.miracle.engine.util.DimensionsUtil.dpToPx;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_PHOTO;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.recycler.IRecyclerView;
import com.miracle.engine.recycler.MiracleViewRecycler;
import com.miracle.engine.recycler.RecyclerController;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.photos.holders.PhotoGridItemViewHolder;
import com.vkontakte.miracle.fragment.photos.FragmentPhotoViewerDialog;
import com.vkontakte.miracle.fragment.photos.PhotoDialogItem;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWC;

import java.util.ArrayList;

public class PhotoStackedView extends FrameLayout implements IRecyclerView {

    private final RecyclerController recyclerController;

    /////////////////////////////////////////////////////////////////////////////////////////////////

    private final int spacing;
    private int measuredWidth = -1;
    private int rowLength;
    //calculation result
    private final ArrayList<PhotoGridItem> photoGridItems = new ArrayList<>();
    //enter data
    private final ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
    //clear media data
    private final ArrayList<MediaItem> mediaItems = new ArrayList<>();
    private boolean canApplyChanges = true;
    private boolean needChanges = false;

    public PhotoStackedView(Context context) {
        this(context, null);
    }

    public PhotoStackedView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        recyclerController = new RecyclerController(LayoutInflater.from(context));

        if(attrs!=null) {
            TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PhotoStackedView, 0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(context, R.styleable.PhotoStackedView, attrs, attributes, 0, 0);
            }

            spacing = (int) attributes.getDimension(R.styleable.PhotoStackedView_itemsSpacing, dpToPx(context, 2));
        }else {
            spacing = (int)  dpToPx(context, 2);
        }
    }

    public void calculate(){
        int count = mediaItems.size();
        int childWidth = (measuredWidth-(spacing*(rowLength-1)))/rowLength;

        photoGridItems.clear();
        for (int i = 0; i < count; i++) {
            PhotoGridItem photoGridItem = new PhotoGridItem();
            photoGridItem.itemDataHolder = itemDataHolders.get(i);
            PhotoGridPosition photoGridPosition = new PhotoGridPosition();
            photoGridPosition.sizeX = childWidth;
            photoGridPosition.sizeY = childWidth;
            photoGridPosition.marginY = spacing;
            photoGridPosition.marginX = i*(spacing+photoGridPosition.sizeX);
            photoGridItem.gridPosition = photoGridPosition;
            photoGridItems.add(photoGridItem);
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

    public void setPhotos(ArrayList<ItemDataHolder> itemDataHolders, int rowLength) {

        canApplyChanges = false;

        ///////////////////////////////////////////////////////////
        recyclerController.resolveSingleTypeItems(this, itemDataHolders, this.itemDataHolders);

        ArrayList<RecyclerView.ViewHolder> buffer = recyclerController.getBuffer();
        for(int i=0;i<buffer.size();i++){
            final int finalI = i;
            buffer.get(i).itemView.setOnClickListener(view -> {
                ItemDataHolder itemDataHolder = this.itemDataHolders.get(finalI);
                DataItemWrap<?,?> dataItemWrap = (DataItemWrap<?,?>) itemDataHolder;
                PhotoItemWC photoItemWC = (PhotoItemWC) dataItemWrap.getHolder();
                ArrayList<ItemDataHolder> mediaItems = photoItemWC.getPhotoItems();
                ArrayList<PhotoDialogItem> photoDialogItems = new ArrayList<>();
                for (int j=0; j<mediaItems.size();j++){
                    ItemDataHolder mediaItem = mediaItems.get(j);
                    PhotoDialogItem photoDialogItem = new PhotoDialogItem(mediaItem);
                    int itemIndex = itemDataHolders.indexOf(mediaItem);
                    if(itemIndex>-1){
                        RecyclerView.ViewHolder viewHolder = buffer.get(itemIndex);
                        if(viewHolder instanceof PhotoGridItemViewHolder) {
                            PhotoGridItem photoGridItem = photoGridItems.get(itemIndex);
                            PhotoGridPosition gridPosition = photoGridItem.getGridPosition();
                            PhotoGridItemViewHolder wrappedPhotoGridItemViewHolder = (PhotoGridItemViewHolder) viewHolder;
                            View child = wrappedPhotoGridItemViewHolder.itemView;
                            int[] location = new int[2];
                            child.getLocationInWindow(location);
                            int rawX = location[0];
                            int rawY = location[1];
                            Drawable drawable = wrappedPhotoGridItemViewHolder.getDrawable();
                            Drawable drwNewCopy = drawable == null ? null : drawable
                                    .getConstantState().newDrawable().mutate();
                            photoDialogItem.setWidth(gridPosition.sizeX);
                            photoDialogItem.setHeight(gridPosition.sizeY);
                            photoDialogItem.setRawX(rawX);
                            photoDialogItem.setRawY(rawY);
                            photoDialogItem.setPreview(drwNewCopy);
                        }
                    }
                    photoDialogItems.add(photoDialogItem);
                }

                int itemIndex = mediaItems.indexOf(itemDataHolder);

                FragmentPhotoViewerDialog.PhotoViewerData photoViewerData =
                        new FragmentPhotoViewerDialog.PhotoViewerData(photoDialogItems, itemIndex);
                new FragmentPhotoViewerDialog(photoViewerData).show(((AppCompatActivity)getContext())
                        .getSupportFragmentManager(),"");
            });
        }

        this.itemDataHolders.clear();
        this.itemDataHolders.addAll(itemDataHolders);

        this.rowLength = rowLength;
        canApplyChanges = true;
        needChanges = true;
        mediaItems.clear();
        for(ItemDataHolder itemDataHolder : itemDataHolders){
            DataItemWrap<?,?> dataItemWrap = (DataItemWrap<?,?>) itemDataHolder;
            MediaItem mediaItem = (MediaItem) dataItemWrap.getItem();
            mediaItems.add(mediaItem);
        }
        requestLayout();
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
