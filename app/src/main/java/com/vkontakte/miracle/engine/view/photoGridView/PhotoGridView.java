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
import com.vkontakte.miracle.engine.view.photoGridView.calculator.CalculationItem;
import com.vkontakte.miracle.engine.view.photoGridView.calculator.GridCalculator;
import com.vkontakte.miracle.fragment.photos.FragmentPhotoViewerDialog;
import com.vkontakte.miracle.fragment.photos.PhotoDialogItem;
import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.photos.wraps.PhotoItemWC;

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
    //calculation result
    private final ArrayList<PhotoGridItem> photoGridItems = new ArrayList<>();
    //enter data
    private ArrayList<ItemDataHolder> itemDataHolders = new ArrayList<>();
    //clear media data
    private final ArrayList<MediaItem> mediaItems = new ArrayList<>();
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
        if(mediaItems.isEmpty()) return;
        GridCalculator gridCalculator = new GridCalculator(mediaItems);
        ArrayList<CalculationItem> calculationItems = gridCalculator.calculateGrid(measuredWidth, maxHeight, spacing);
        photoGridItems.clear();
        for (int i=0;i<calculationItems.size();i++){
            CalculationItem calculationItem = calculationItems.get(i);
            PhotoGridItem photoGridItem = new PhotoGridItem();
            photoGridItem.itemDataHolder = itemDataHolders.get(i);
            photoGridItem.gridPosition = calculationItem.gridPosition;
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

    public void setPhotos(ArrayList<ItemDataHolder> itemDataHolders) {

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

        this.itemDataHolders = itemDataHolders;
        ///////////////////////////////////////////////////////////

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