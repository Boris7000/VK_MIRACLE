package com.vkontakte.miracle;

import static com.miracle.engine.util.ImageUtil.bitmapFromLayerDrawable;
import static com.miracle.engine.util.ImageUtil.getAverageHSLFromBitmap;
import static com.miracle.engine.util.UIUtil.clearLightStatusBar;
import static com.miracle.engine.util.UIUtil.setLightStatusBar;
import static com.miracle.engine.view.ActivityRootView.STATE_CLEAR;
import static com.miracle.engine.view.ActivityRootView.STATE_STANDARD;
import static com.miracle.engine.view.ActivityRootView.TYPE_LAND;
import static com.miracle.engine.view.ActivityRootView.TYPE_PORTRAIT;
import static com.miracle.engine.view.ActivityRootView.TYPE_TABLET;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.miracle.engine.activity.tabs.TabsActivity;
import com.miracle.engine.activity.tabs.TabsActivityController;
import com.miracle.engine.async.AsyncExecutor;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.tabs.adapters.SimpleTabsAdapter;
import com.miracle.engine.view.TabsFragmentContainer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.vkontakte.miracle.engine.picasso.ATarget;
import com.vkontakte.miracle.engine.util.IMEUtil;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.fragment.base.FragmentDialogs;
import com.vkontakte.miracle.fragment.base.FragmentFeed;
import com.vkontakte.miracle.fragment.base.FragmentMenu;
import com.vkontakte.miracle.fragment.base.FragmentsMusic;
import com.vkontakte.miracle.fragment.player.FragmentPlayer;
import com.vkontakte.miracle.fragment.player.FragmentPlaying;
import com.vkontakte.miracle.login.LoginActivity;
import com.vkontakte.miracle.login.UnregisterDevice;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.fields.Album;
import com.vkontakte.miracle.model.audio.fields.Photo;
import com.vkontakte.miracle.service.longpoll.LongPollServiceController;
import com.vkontakte.miracle.service.player.AudioPlayerEventListener;
import com.vkontakte.miracle.service.player.AudioPlayerMedia;
import com.vkontakte.miracle.service.player.AudioPlayerServiceController;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends TabsActivity {

    ///Всякие плеерные дела
    public static final int STATE_AUDIO = 2;
    private ViewStub playerBottomSheetStub;
    private FrameLayout playerBottomSheet;
    private BottomSheetBehavior<ViewStub> playerBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private FrameLayout playerBar;
    private ViewPager2 viewPager;
    private boolean showingPlayer = false;
    private int color;
    private Bitmap placeholderImage;
    private String previousImageUrl = "none";
    private final AudioPlayerEventListener audioPlayerEventListener = new AudioPlayerEventListener() {
        @Override
        public void onMediaItemChange(AudioPlayerMedia audioPlayerMedia) {
            showingPlayer = true;
            showPlayerBottomSheet();
            createTarget(audioPlayerMedia.getCurrentAudioItem());
        }

        @Override
        public void onPlayerClose() {
            showingPlayer = false;
            hidePlayerBottomSheet();
        }
    };
    private final Target target = new ATarget() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            new AsyncExecutor<Boolean>() {
                int averageColor;
                @Override
                public Boolean inBackground() {
                    float[] hsl = getAverageHSLFromBitmap(bitmap);
                    float[] hslEdited = new float[]{
                            hsl[0],
                            Math.min(hsl[1],0.65f),
                            Math.max(Math.min(hsl[2], 0.50f), 0.18f),
                    };
                    averageColor = ColorUtils.HSLToColor(hslEdited);
                    return true;
                }
                @Override
                public void onExecute(Boolean object) {
                    animateToColor(averageColor);
                }
            }.start();
        }
        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            onBitmapLoaded(placeholderImage, null);
        }
    };

    private View.OnLayoutChangeListener playerBarChangeListener;
    private View.OnLayoutChangeListener navigationBarChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(StorageUtil.get().currentUser()==null) {
            SettingsUtil.get().storeAuthorized(false);
            Intent intent = new Intent(MainApp.getInstance(), LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        if(savedInstanceState==null){
            savedInstanceState = savedInstanceStateCrutch();
        }

        super.onCreate(savedInstanceState);

        placeholderImage = bitmapFromLayerDrawable(MainApp.getInstance(),
                R.drawable.audio_placeholder_image_colored_large, 75, 75);

        restorePlayerBarState(savedInstanceState);

        AudioPlayerServiceController.get().addOnPlayerEventListener(audioPlayerEventListener);
    }

    @Override
    public int getRootViewResource() {
        return R.layout.activity_main;
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        playerBottomSheetStub = rootView.findViewById(R.id.playerBottomSheetStub);
        playerBottomSheetBehavior = BottomSheetBehavior.from(playerBottomSheetStub);
    }

    @Override
    public void initViews() {
        super.initViews();
        playerBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_SETTLING: {
                        IMEUtil.hideKeyboard(MainActivity.this);
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED:{
                        IMEUtil.hideKeyboard(MainActivity.this);
                        if(!MainApp.getInstance().nightMode()) {
                            clearLightStatusBar(getWindow().getDecorView());
                        }
                        if(viewPager.getVisibility()!=View.VISIBLE) {
                            viewPager.setVisibility(View.VISIBLE);
                        }
                        viewPager.setAlpha(1);
                        getTabsActivityController().getNavigationBarView().setAlpha(0);
                        playerBar.setAlpha(0);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED:{
                        if(!MainApp.getInstance().nightMode()) {
                            setLightStatusBar(getWindow().getDecorView());
                        }
                        if(viewPager.getVisibility()!=View.GONE) {
                            viewPager.setVisibility(View.GONE);
                        }
                        viewPager.setAlpha(0);
                        viewPager.setCurrentItem(0,false);
                        getTabsActivityController().getNavigationBarView().setAlpha(1);
                        playerBar.setAlpha(1);
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float alpha = 1f-slideOffset;
                if(slideOffset>0){
                    if(viewPager.getVisibility()!=View.VISIBLE) {
                        viewPager.setVisibility(View.VISIBLE);
                    }
                }

                float transitionPercent = (1-alpha);
                playerBar.setAlpha(alpha);
                playerBar.setTranslationY(-transitionPercent*playerBar.getHeight());
                NavigationBarView navigationBarView = getTabsActivityController().getNavigationBarView();
                navigationBarView.setAlpha(alpha);
                navigationBarView.setTranslationY(transitionPercent*navigationBarView.getHeight());
                viewPager.setAlpha(slideOffset);
            }
        });
    }

    @CallSuper
    @Override
    public void onNewWindowInsets(WindowInsetsCompat windowInsets){
        updateInsets(windowInsets);
    }

    private void updateInsets(WindowInsetsCompat windowInsets){

        if(windowInsets!=null) {
            Insets systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets systemGesturesInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            Insets imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            //height of player bar and bottom navigation menu
            final int bottomInsets = Math.max(systemGesturesInsets.bottom, Math.max(systemBarsInsets.bottom, imeInsets.bottom));

            TabsActivityController tabsActivityController = getTabsActivityController();
            NavigationBarView navigationBarView = tabsActivityController.getNavigationBarView();
            TabsFragmentContainer tabsFragmentContainer = tabsActivityController.getTabsFragmentContainer();

            switch (getRootView().getType()){
                case TYPE_PORTRAIT: {
                    switch (getRootView().getState()) {
                        case STATE_CLEAR: {

                            if(playerBar!=null) {
                                playerBar.removeOnLayoutChangeListener(playerBarChangeListener);
                            }
                            navigationBarView.removeOnLayoutChangeListener(navigationBarChangeListener);

                            if(tabsFragmentContainer.getPaddingBottom()!=bottomInsets||
                                    tabsFragmentContainer.getPaddingTop()!=systemBarsInsets.top) {
                                tabsFragmentContainer.setPadding(
                                        tabsFragmentContainer.getPaddingLeft(),
                                        0,//systemBarsInsets.top,
                                        tabsFragmentContainer.getPaddingRight(),
                                        bottomInsets);
                            }
                            break;
                        }
                        case STATE_STANDARD: {

                            if(playerBar!=null) {
                                playerBar.removeOnLayoutChangeListener(playerBarChangeListener);
                            }
                            navigationBarView.removeOnLayoutChangeListener(navigationBarChangeListener);

                            navigationBarChangeListener = (view, i, i1, i2, i3, i4, i5, i6, i7) -> {
                                if(navigationBarView.getPaddingBottom()==bottomInsets) {
                                    if (tabsFragmentContainer.getPaddingBottom() != navigationBarView.getHeight() ||
                                            tabsFragmentContainer.getPaddingTop() != systemBarsInsets.top) {
                                        tabsFragmentContainer.setPadding(
                                                tabsFragmentContainer.getPaddingLeft(),
                                                0,//systemBarsInsets.top,
                                                tabsFragmentContainer.getPaddingRight(),
                                                navigationBarView.getHeight());
                                    }
                                }
                            };

                            navigationBarView.addOnLayoutChangeListener(navigationBarChangeListener);

                            if(navigationBarView.getPaddingBottom()!=bottomInsets) {
                                navigationBarView.setPadding(
                                        navigationBarView.getPaddingLeft(),
                                        navigationBarView.getPaddingTop(),
                                        navigationBarView.getPaddingRight(),
                                        bottomInsets);
                            } else {
                                navigationBarChangeListener.onLayoutChange(navigationBarView,0,0,0,0,0,0,0,0);
                            }

                            break;
                        }
                        case STATE_AUDIO: {

                            navigationBarView.removeOnLayoutChangeListener(navigationBarChangeListener);

                            navigationBarChangeListener = (view, i, i1, i2, i3, i4, i5, i6, i7) -> {
                                if(navigationBarView.getPaddingBottom()==bottomInsets) {
                                    if (playerBottomSheetBehavior.getPeekHeight() != playerBar.getHeight() + navigationBarView.getHeight()) {
                                        playerBottomSheetBehavior.setPeekHeight(playerBar.getHeight() + navigationBarView.getHeight());
                                    }
                                    if (tabsFragmentContainer.getPaddingBottom() != playerBottomSheetBehavior.getPeekHeight() ||
                                            tabsFragmentContainer.getPaddingTop() != systemBarsInsets.top) {
                                        tabsFragmentContainer.setPadding(
                                                tabsFragmentContainer.getPaddingLeft(),
                                                0,//systemBarsInsets.top,
                                                tabsFragmentContainer.getPaddingRight(),
                                                playerBottomSheetBehavior.getPeekHeight());
                                    }
                                }
                            };

                            navigationBarView.addOnLayoutChangeListener(navigationBarChangeListener);

                            playerBar.removeOnLayoutChangeListener(playerBarChangeListener);

                            playerBarChangeListener = (view1, i8, i11, i21, i31, i41, i51, i61, i71) -> {
                                if(navigationBarView.getPaddingBottom()==bottomInsets) {
                                    if (playerBottomSheetBehavior.getPeekHeight() != playerBar.getHeight() + navigationBarView.getHeight()) {
                                        playerBottomSheetBehavior.setPeekHeight(playerBar.getHeight() + navigationBarView.getHeight());
                                    }
                                    if (tabsFragmentContainer.getPaddingBottom() != playerBottomSheetBehavior.getPeekHeight() ||
                                            tabsFragmentContainer.getPaddingTop() != systemBarsInsets.top) {
                                        tabsFragmentContainer.setPadding(
                                                tabsFragmentContainer.getPaddingLeft(),
                                                0,//systemBarsInsets.top,
                                                tabsFragmentContainer.getPaddingRight(),
                                                playerBottomSheetBehavior.getPeekHeight());
                                    }
                                }
                            };

                            playerBar.addOnLayoutChangeListener(playerBarChangeListener);

                            if(navigationBarView.getPaddingBottom()!=bottomInsets) {
                                navigationBarView.setPadding(
                                        navigationBarView.getPaddingLeft(),
                                        navigationBarView.getPaddingTop(),
                                        navigationBarView.getPaddingRight(),
                                        bottomInsets);
                            } else {
                                navigationBarChangeListener.onLayoutChange(navigationBarView,0,0,0,0,0,0,0,0);
                            }

                            break;
                        }
                    }
                    break;
                }
                case TYPE_LAND:
                case TYPE_TABLET: {
                    switch (getRootView().getState()) {
                        case STATE_CLEAR: {

                            if(playerBar!=null) {
                                playerBar.removeOnLayoutChangeListener(playerBarChangeListener);
                            }
                            navigationBarView.removeOnLayoutChangeListener(navigationBarChangeListener);

                            if(tabsFragmentContainer.getPaddingBottom()!=bottomInsets||
                                    tabsFragmentContainer.getPaddingTop()!=systemBarsInsets.top||
                                    tabsFragmentContainer.getPaddingLeft()!=systemBarsInsets.left||
                                    tabsFragmentContainer.getPaddingRight()!=systemBarsInsets.right) {
                                tabsFragmentContainer.setPadding(
                                        systemBarsInsets.left,
                                        0,//systemBarsInsets.top,
                                        systemBarsInsets.right,
                                        bottomInsets);
                            }
                            break;
                        }
                        case STATE_STANDARD: {

                            if(playerBar!=null) {
                                playerBar.removeOnLayoutChangeListener(playerBarChangeListener);
                            }
                            navigationBarView.removeOnLayoutChangeListener(navigationBarChangeListener);

                            navigationBarChangeListener = (view, i, i1, i2, i3, i4, i5, i6, i7) -> {
                                if(navigationBarView.getPaddingBottom()==bottomInsets&&
                                        navigationBarView.getPaddingTop()==systemBarsInsets.top&&
                                        navigationBarView.getPaddingLeft()==systemBarsInsets.left) {
                                    if (tabsFragmentContainer.getPaddingBottom() != bottomInsets ||
                                            tabsFragmentContainer.getPaddingTop() != systemBarsInsets.top ||
                                            tabsFragmentContainer.getPaddingLeft() != navigationBarView.getWidth() ||
                                            tabsFragmentContainer.getPaddingRight() != systemBarsInsets.right) {
                                        tabsFragmentContainer.setPadding(
                                                navigationBarView.getWidth(),
                                                0,//systemBarsInsets.top,
                                                systemBarsInsets.right,
                                                bottomInsets);
                                    }
                                }
                            };

                            navigationBarView.addOnLayoutChangeListener(navigationBarChangeListener);

                            if(navigationBarView.getPaddingBottom()!=bottomInsets||
                                    navigationBarView.getPaddingTop()!=systemBarsInsets.top||
                                    navigationBarView.getPaddingLeft()!=systemBarsInsets.left) {
                                navigationBarView.setPadding(
                                        systemBarsInsets.left,
                                        systemBarsInsets.top,
                                        navigationBarView.getPaddingRight(),
                                        bottomInsets);
                            } else {
                                navigationBarChangeListener.onLayoutChange(navigationBarView,0,0,0,0,0,0,0,0);
                            }

                            break;
                        }
                        case STATE_AUDIO: {

                            playerBar.removeOnLayoutChangeListener(playerBarChangeListener);

                            playerBarChangeListener = (view1, i8, i11, i21, i31, i41, i51, i61, i71) -> {
                                if(playerBar.getPaddingBottom()==bottomInsets&&
                                        playerBar.getPaddingLeft()==systemBarsInsets.left&&
                                        playerBar.getPaddingRight()==systemBarsInsets.right) {

                                    if (playerBottomSheetBehavior.getPeekHeight() != playerBar.getHeight()) {
                                        playerBottomSheetBehavior.setPeekHeight(playerBar.getHeight());
                                    }

                                    if (navigationBarView.getPaddingBottom() != playerBottomSheetBehavior.getPeekHeight() ||
                                            navigationBarView.getPaddingTop() != systemBarsInsets.top ||
                                            navigationBarView.getPaddingLeft() != systemBarsInsets.left) {
                                        navigationBarView.setPadding(
                                                systemBarsInsets.left,
                                                systemBarsInsets.top,
                                                navigationBarView.getPaddingRight(),
                                                playerBottomSheetBehavior.getPeekHeight());
                                    } else {
                                        navigationBarChangeListener.onLayoutChange(playerBar,0,0,0,0,0,0,0,0);
                                    }
                                }
                            };

                            playerBar.addOnLayoutChangeListener(playerBarChangeListener);

                            navigationBarView.removeOnLayoutChangeListener(navigationBarChangeListener);

                            navigationBarChangeListener = (view, i, i1, i2, i3, i4, i5, i6, i7) -> {
                                if(navigationBarView.getPaddingBottom()==playerBottomSheetBehavior.getPeekHeight()&&
                                        navigationBarView.getPaddingTop()==systemBarsInsets.top&&
                                        navigationBarView.getPaddingLeft()==systemBarsInsets.left) {
                                    if (tabsFragmentContainer.getPaddingBottom() != playerBottomSheetBehavior.getPeekHeight() ||
                                            tabsFragmentContainer.getPaddingTop() != systemBarsInsets.top ||
                                            tabsFragmentContainer.getPaddingLeft() != navigationBarView.getWidth() ||
                                            tabsFragmentContainer.getPaddingRight() != systemBarsInsets.right) {
                                        tabsFragmentContainer.setPadding(
                                                navigationBarView.getWidth(),
                                                0,//systemBarsInsets.top,
                                                systemBarsInsets.right,
                                                playerBottomSheetBehavior.getPeekHeight());
                                    }
                                }
                            };

                            navigationBarView.addOnLayoutChangeListener(navigationBarChangeListener);

                            if(playerBar.getPaddingBottom()!=bottomInsets||
                                    playerBar.getPaddingLeft()!=systemBarsInsets.left||
                                    playerBar.getPaddingRight()!=systemBarsInsets.right) {
                                playerBar.setPadding(
                                        systemBarsInsets.left,
                                        playerBar.getPaddingTop(),
                                        systemBarsInsets.right,
                                        bottomInsets);
                            } else {
                                playerBarChangeListener.onLayoutChange(playerBar,0,0,0,0,0,0,0,0);
                            }

                            break;
                        }
                    }
                    break;
                }
            }
        }
    }

    private void restorePlayerBarState(Bundle savedInstanceState){
        if(getIntent().getBooleanExtra("PlayerBottomSheetExpanded",false)){
            playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        getIntent().removeExtra("PlayerBottomSheetExpanded");

        if(savedInstanceState!=null) {
            if (!savedInstanceState.isEmpty()) {
                int state = savedInstanceState.getInt("playerSheetBehaviorState", -1);
                if(state>0) {
                    playerBottomSheetBehavior.setState(state);
                    savedInstanceState.remove("playerSheetBehaviorState");
                }
            }
        }
    }

    private void inflatePlayerBar(){
        playerBottomSheet = (FrameLayout) playerBottomSheetStub.inflate();

        playerBar = playerBottomSheet.findViewById(R.id.playerBar);
        viewPager = playerBottomSheet.findViewById(R.id.viewPager);
        viewPager.setTag(target);
        applyColor(color = ColorUtils.HSLToColor(new float[]{0,0,0.13f}));

        bottomSheetCallback.onStateChanged(playerBottomSheet, playerBottomSheetBehavior.getState());

        View child = viewPager.getChildAt(0);
        if (child instanceof RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
            child.setNestedScrollingEnabled(false);
        }

        ArrayList<FragmentFabric> fabrics = new ArrayList<>();

        fabrics.add(new FragmentPlayer.Fabric());
        fabrics.add(new FragmentPlaying.Fabric());
        //very laggy
        //viewPager2.setOffscreenPageLimit(1);
        viewPager.setAdapter(new SimpleTabsAdapter(getSupportFragmentManager(), getLifecycle(), fabrics));

        playerBar.setOnClickListener(view -> playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));
    }

    public void hideNavigationBars() {
        NavigationBarView navigationBarView = getTabsActivityController().getNavigationBarView();
        if(navigationBarView.getVisibility()!= View.GONE){
            navigationBarView.setVisibility(View.GONE);
        }
        if(showingPlayer){
            if(playerBottomSheet!=null&&playerBottomSheet.getVisibility()!=View.GONE) {
                playerBottomSheet.setVisibility(View.GONE);
            }
        }
        getRootView().setState(STATE_CLEAR);
        updateInsets(getWindowInsets());
    }

    public void showNavigationBars(){
        NavigationBarView navigationBarView = getTabsActivityController().getNavigationBarView();
        if(navigationBarView.getVisibility()!=View.VISIBLE){
            navigationBarView.setVisibility(View.VISIBLE);
        }
        if(showingPlayer){
            if (playerBottomSheet == null) {
                inflatePlayerBar();
            }
            if(getRootView().getState()!=STATE_AUDIO) {
                if (playerBottomSheet.getVisibility() != View.VISIBLE) {
                    playerBottomSheet.setVisibility(View.VISIBLE);
                }
                getRootView().setState(STATE_AUDIO);
            }
        } else {
            if (playerBottomSheet != null && playerBottomSheet.getVisibility() != View.GONE) {
                playerBottomSheet.setVisibility(View.GONE);
                if(playerBottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_COLLAPSED) {
                    playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            navigationBarView.setAlpha(1);
            getRootView().setState(STATE_STANDARD);
        }
        updateInsets(getWindowInsets());
    }

    public void hidePlayerBottomSheet(){
        if(getRootView().getState()!=STATE_CLEAR) {
            if (playerBottomSheet != null && playerBottomSheet.getVisibility() != View.GONE) {
                playerBottomSheet.setVisibility(View.GONE);
                if(playerBottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_COLLAPSED) {
                    playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            NavigationBarView navigationBarView = getTabsActivityController().getNavigationBarView();
            if(navigationBarView.getVisibility()!=View.VISIBLE){
                navigationBarView.setVisibility(View.VISIBLE);
            }
            navigationBarView.setAlpha(1);
            getRootView().setState(STATE_STANDARD);
            updateInsets(getWindowInsets());
        }
    }

    public void showPlayerBottomSheet(){
        if(getRootView().getState()!=STATE_CLEAR) {
            if (playerBottomSheet == null) {
                inflatePlayerBar();
            }
            if(getRootView().getState()!=STATE_AUDIO) {
                if (playerBottomSheet.getVisibility() != View.VISIBLE) {
                    playerBottomSheet.setVisibility(View.VISIBLE);
                }
                getRootView().setState(STATE_AUDIO);
                updateInsets(getWindowInsets());
            }
        }
    }

    public boolean playerBottomSheetIsExpanded(){
        return showingPlayer && playerBottomSheetBehavior!=null
                && playerBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED;
    }

    private void createTarget(AudioItem audioItem){
        Picasso.get().cancelRequest(target);
        if(audioItem.getAlbum()!=null){
            Album album = audioItem.getAlbum();
            if(album.getThumb()!=null){
                Photo thumb = album.getThumb();
                if(thumb.getPhoto135()!=null){
                    if(!thumb.getPhoto135().equals(previousImageUrl)) {
                        Picasso.get().load(previousImageUrl = thumb.getPhoto135()).into(target);
                    }
                    return;
                }
            }
        }
        if(!previousImageUrl.equals("")) {
            previousImageUrl = "";
            target.onBitmapLoaded(placeholderImage, null);
        }
    }

    private void animateToColor(int toColor) {
        final int old = color;
        ValueAnimator animator = ValueAnimator.ofArgb(old, toColor);
        animator.addUpdateListener(animation -> applyColor(color = (int) animation.getAnimatedValue()));
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
    }

    private void applyColor(int color){
        PorterDuffColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
        if(viewPager !=null){
            viewPager.getBackground().setColorFilter(colorFilter);
        }
    }

    @CallSuper
    @Override
    public boolean addFragment(Fragment fragment) {
        boolean fragmentAdded = super.addFragment(fragment);
        if(fragmentAdded) {
            if (playerBottomSheetIsExpanded()) {
                playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        return fragmentAdded;
    }

    @CallSuper
    @Override
    public void onBackPressed() {
        if(playerBottomSheetIsExpanded()) {
            playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra("PlayerBottomSheetExpanded",false)){
            playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        intent.removeExtra("PlayerBottomSheetExpanded");
    }

    @Override
    protected void onDestroy() {
        AudioPlayerServiceController.get().removeOnPlayerEventListener(audioPlayerEventListener);
        super.onDestroy();
    }

    @Override
    public ArrayMap<Integer, FragmentFabric> loadTabs() {
        ArrayMap<Integer, FragmentFabric> fabrics = new ArrayMap<>();
        fabrics.put(R.id.tab_user, new FragmentMenu.Fabric());
        fabrics.put(R.id.tab_feed, new FragmentFeed.Fabric());
        fabrics.put(R.id.tab_dialogs, new FragmentDialogs.Fabric());
        fabrics.put(R.id.tab_music, new FragmentsMusic.Fabric());
        return fabrics;
    }

    @Override
    public ArrayMap<Integer, FragmentFabric> getErrorTabs() {
        ArrayMap<Integer, FragmentFabric> fabrics = new ArrayMap<>();
        fabrics.put(R.id.tab_feed, new FragmentsMusic.Fabric());
        return fabrics;
    }

    @Override
    public int defaultTab() {
        return R.id.tab_feed;
    }

    public void exitFromAccount(){
        LongPollServiceController.get().actionStop();
        AudioPlayerServiceController.get().stop();
        SettingsUtil.get().storeAuthorized(false);//сброс индекса текущего пользователя
        new UnregisterDevice(this, StorageUtil.get().currentUser().getAccessToken()).start();
        Intent intent = new Intent(MainApp.getInstance(), LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("playerSheetBehaviorState");
        String key = savedInstanceState.getString("Adapter");
        if(key!=null){
            LargeDataStorage.get().removeLargeData(key);
            savedInstanceState.remove("Adapter");
        }
    }

    @CallSuper
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("playerSheetBehaviorState", playerBottomSheetBehavior.getState());
        if(viewPager!=null){
            Object object = viewPager.getAdapter();
            if(object!=null) {
                outState.putString("Adapter", LargeDataStorage.get().storeLargeData(object));
            }
        }
    }

}
