package com.vkontakte.miracle.fragment.wall;

import static com.miracle.engine.adapter.AdapterStates.SATE_FIRST_LOADING_COMPLETE;
import static com.miracle.engine.util.DimensionsUtil.getPxDpByAttributeId;
import static com.vkontakte.miracle.engine.util.ImageUtil.getOptimalSize;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.miracle.engine.util.DimensionsUtil;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.GroupAdapter;
import com.vkontakte.miracle.engine.util.DeviceUtil;
import com.vkontakte.miracle.model.catalog.fields.Image;
import com.vkontakte.miracle.model.groups.GroupItem;
import com.vkontakte.miracle.model.wall.fields.Cover;

public class FragmentGroup extends BaseRecyclerFragment {

    private ImageView coverIV;
    private String groupId;
    private String groupName;
    private String coverUrl;
    private CoverController controller;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_cover, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        coverIV = rootView.findViewById(R.id.cover);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        ViewCompat.setOnApplyWindowInsetsListener(
                coverIV, (v, insets) -> {
                    Activity activity = getActivity();
                    if(activity!=null) {
                        Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                        int statusBarHeight = systemBarsInsets.top;
                        int actionBarSize = getPxDpByAttributeId(activity, R.attr.actionBarSize);
                        int margin = (int) DimensionsUtil.dpToPx(activity, 77);
                        int radius = (int) DimensionsUtil.dpToPx(activity, 20);
                        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) coverIV.getLayoutParams();
                        layoutParams.height = statusBarHeight+actionBarSize+margin+radius;
                    }
                    return insets;
                });

        setCover();

        controller = new CoverController(rootView.getContext());


        controller.update();

        getRecyclerView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                controller.scroll(dy);
            }
        });
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new GroupAdapter(groupId);
    }

    @Override
    public String requestTitleText() {
        if(groupName!=null){
            return groupName;
        }
        return super.requestTitleText();
    }

    @CallSuper
    @Override
    public void onRecyclerAdapterStateChange(int state) {

        if (state == SATE_FIRST_LOADING_COMPLETE) {
            RecyclerView.Adapter<?> adapter = getRecyclerView().getAdapter();
            if (adapter instanceof GroupAdapter) {
                GroupAdapter groupAdapter = (GroupAdapter) adapter;
                GroupItem groupItem = groupAdapter.getGroupItem();
                if(groupItem!=null) {
                    if (groupName == null || groupName.isEmpty()) {
                        groupName = groupItem.getName();
                    }
                    if(coverUrl==null){
                        Cover cover = groupItem.getCover();
                        if(cover!=null){
                            if(cover.getEnabled()) {
                                if(!cover.getImages().isEmpty()) {
                                    Image image = getOptimalSize(cover.getImages(),
                                            coverIV.getWidth() == 0 ?
                                                    DeviceUtil.getDisplayWidth(coverIV.getContext()):
                                                    coverIV.getWidth(),
                                            coverIV.getHeight());
                                    if(image!=null){
                                        coverUrl = image.getUrl();
                                        setCover();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        super.onRecyclerAdapterStateChange(state);
    }

    @Override
    public boolean scrollAndElevateEnabled() {
        return false;
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("groupId");
        if(key!=null){
            groupId = key;
            savedInstanceState.remove("groupId");
        }
        key = savedInstanceState.getString("groupName");
        if(key!=null){
            groupName = key;
            savedInstanceState.remove("groupName");
        }
        key = savedInstanceState.getString("coverUrl");
        if(key!=null){
            coverUrl = key;
            savedInstanceState.remove("coverUrl");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("groupId");
        savedInstanceState.remove("groupName");
        savedInstanceState.remove("coverUrl");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if(groupId !=null){
            outState.putString("groupId", groupId);
        }
        if(groupName !=null){
            outState.putString("groupName", groupName);
        }
        if(coverUrl !=null){
            outState.putString("coverUrl", coverUrl);
        }
        super.onSaveInstanceState(outState);
    }

    private void setCover(){
        if(coverUrl!=null){
            Picasso.get().load(coverUrl).into(coverIV);
        }
    }

    @Override
    public void scrollToTop() {
        super.scrollToTop();
        controller.expand();
    }

    @Override
    public void onHide() {
        coverIV.animate().alpha(0f).setDuration(200).start();
        super.onHide();
    }

    @Override
    public void onShow() {
        coverIV.animate().alpha(1f).setDuration(500).start();
        super.onShow();
    }

    private class CoverController{

        final float z;
        final float margin;
        final float maxDy;

        int offset = 0;
        float alpha = 0;

        public CoverController(Context context){
            z = getPxDpByAttributeId(context, com.miracle.engine.R.attr.globalTranslationZ);
            margin = DimensionsUtil.dpToPx(context, 77);
            maxDy = margin;
        }

        public void scroll(int dy){
            if(dy!=0) {
                offset += dy;
                float newAlpha = Math.min(maxDy, offset) / maxDy;
                if (alpha != newAlpha) {
                    alpha = newAlpha;
                }
                update();
            }
        }

        private void update(){
            coverIV.setAlpha(1f - alpha);
            getTitle().setAlpha(alpha);
            AppBarLayout appBarLayout = getAppBarLayout();
            appBarLayout.getBackground().setAlpha((int) (255 * alpha));
            if (alpha == 1) {
                appBarLayout.setTranslationZ(z);
            } else {
                appBarLayout.setTranslationZ(0);
            }
        }

        public void expand(){
            scroll(-offset);
        }


    }
}