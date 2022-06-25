package com.vkontakte.miracle;

import static com.vkontakte.miracle.engine.util.UIUtil.clearLightStatusBar;
import static com.vkontakte.miracle.engine.util.UIUtil.setLightStatusBar;
import static com.vkontakte.miracle.engine.view.ActivityRootView.STATE_AUDIO;
import static com.vkontakte.miracle.engine.view.ActivityRootView.STATE_CLEAR;
import static com.vkontakte.miracle.engine.view.ActivityRootView.STATE_STANDARD;
import static com.vkontakte.miracle.engine.view.ActivityRootView.TYPE_LAND;
import static com.vkontakte.miracle.engine.view.ActivityRootView.TYPE_PORTRAIT;
import static com.vkontakte.miracle.engine.view.ActivityRootView.TYPE_TABLET;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationBarView;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.fragment.tabs.TabsAdapter;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.view.ActivityRootView;
import com.vkontakte.miracle.engine.view.fragmentContainer.TabsFragmentContainer;
import com.vkontakte.miracle.engine.view.fragmentContainer.TabsFragmentContainer.TabsFragmentController;
import com.vkontakte.miracle.fragment.base.FragmentDialogs;
import com.vkontakte.miracle.fragment.base.FragmentFeed;
import com.vkontakte.miracle.fragment.base.FragmentMenu;
import com.vkontakte.miracle.fragment.base.FragmentsMusic;
import com.vkontakte.miracle.login.LoginActivity;
import com.vkontakte.miracle.login.UnregisterDevice;
import com.vkontakte.miracle.longpoll.LongPollServiceController;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.OnPlayerEventListener;
import com.vkontakte.miracle.player.PlayerServiceController;
import com.vkontakte.miracle.player.fragment.FragmentPlayer;
import com.vkontakte.miracle.player.fragment.FragmentPlaying;

import java.util.ArrayList;

public class MiracleActivity extends AppCompatActivity {

    private MiracleApp miracleApp;
    private ProfileItem userItem;
    private ActivityRootView rootView;
    private TabsFragmentContainer tabsFragmentContainer;
    private NavigationBarView navigationBarView;

    private ViewStub playerBottomSheetStub;
    private FrameLayout playerBottomSheet;
    private FrameLayout playerBar;
    private ViewPager2 viewPager2;
    private BottomSheetBehavior<ViewStub> playerBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private final OnPlayerEventListener onPlayerEventListener = new OnPlayerEventListener() {
        @Override
        public void onBufferChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlaybackPositionChange(AudioPlayerData playerData) {

        }

        @Override
        public void onSongChange(AudioPlayerData playerData, boolean animate) {
            showingPlayer = true;
            showPlayerBottomSheet();
        }

        @Override
        public void onPlayWhenReadyChange(AudioPlayerData playerData, boolean animate) {

        }

        @Override
        public void onRepeatModeChange(AudioPlayerData playerData) {

        }

        @Override
        public void onPlayerClose() {
            showingPlayer = false;
            hidePlayerBottomSheet();
        }
    };
    private final ArrayList<OnApplyWindowInsetsListener> onApplyWindowInsetsListeners = new ArrayList<>();
    private WindowInsetsCompat windowInsets;
    private boolean showingPlayer = false;

    private final PlayerServiceController playerServiceController = PlayerServiceController.get();


    private View.OnLayoutChangeListener playerBarChangeListener;
    private View.OnLayoutChangeListener navigationBarChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(savedInstanceState==null){
            savedInstanceState = getIntent().getBundleExtra("saved_state");
        }

        super.onCreate(savedInstanceState);

        miracleApp = (MiracleApp) getApplication();

        setTheme(miracleApp.getThemeRecourseId());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        StorageUtil storageUtil = StorageUtil.get();
        ProfileItem profileItem = storageUtil.currentUser();
        if(profileItem==null) {
            SettingsUtil.get().storeAuthorized(false);
            Intent intent = new Intent(miracleApp, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        setUserItem(profileItem);

        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);

        navigationBarView = rootView.findViewById(R.id.bottomNavigationView);

        tabsFragmentContainer = rootView.findViewById(R.id.frameContainer);

        iniWindowInsets();

        iniPlayerBottomSheetBehavior(savedInstanceState);

        iniFragmentController(savedInstanceState);
    }

    private void iniWindowInsets(){

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, windowInsets) -> {
            this.windowInsets = windowInsets;

            rootView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    rootView.removeOnLayoutChangeListener(this);
                    updateInsets();
                }
            });

            for (OnApplyWindowInsetsListener a : onApplyWindowInsetsListeners) {
                a.onApplyWindowInsets(v,windowInsets);
            }
            return windowInsets;
        });
    }

    private void updateInsets(){

        if(windowInsets!=null) {
            Insets systemBarsInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets systemGesturesInsets = windowInsets.getInsets(WindowInsetsCompat.Type.systemGestures());
            Insets imeInsets = windowInsets.getInsets(WindowInsetsCompat.Type.ime());

            //height of player bar and bottom navigation menu
            final int bottomInsets = Math.max(systemGesturesInsets.bottom, Math.max(systemBarsInsets.bottom, imeInsets.bottom));

            switch (rootView.getType()){
                case TYPE_PORTRAIT: {
                    switch (rootView.getState()) {
                        case STATE_CLEAR: {

                            if(playerBar!=null) {
                                playerBar.removeOnLayoutChangeListener(playerBarChangeListener);
                            }
                            navigationBarView.removeOnLayoutChangeListener(navigationBarChangeListener);

                            if(tabsFragmentContainer.getPaddingBottom()!=bottomInsets||
                                    tabsFragmentContainer.getPaddingTop()!=systemBarsInsets.top) {
                                tabsFragmentContainer.setPadding(
                                        tabsFragmentContainer.getPaddingLeft(),
                                        systemBarsInsets.top,
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
                                                systemBarsInsets.top,
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
                                                systemBarsInsets.top,
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
                                                systemBarsInsets.top,
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
                    switch (rootView.getState()) {
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
                                        systemBarsInsets.top,
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
                                                systemBarsInsets.top,
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
                                                systemBarsInsets.top,
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


    private void iniFragmentController(Bundle savedInstanceState){
        if(savedInstanceState!=null){
            if(!savedInstanceState.isEmpty()){
                String key = savedInstanceState.getString("ControllerSavedData","");
                TabsFragmentController.ControllerSavedState controllerSavedData =
                        (TabsFragmentController.ControllerSavedState)
                                LargeDataStorage.get().getLargeData(key);
                if(controllerSavedData!=null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    TabsFragmentController tabsFragmentController =
                            new TabsFragmentController(fragmentManager);
                    tabsFragmentContainer.setController(tabsFragmentController);
                    tabsFragmentController.setUpWithNavigationBarView(navigationBarView);
                    tabsFragmentController.restoreFromSavedState(controllerSavedData, savedInstanceState);
                    return;
                }
            }
        }
        ArrayMap<Integer, FragmentFabric> fabrics = new ArrayMap<>();
        fabrics.put(R.id.tab_user, new FragmentMenu.Fabric());
        fabrics.put(R.id.tab_feed, new FragmentFeed.Fabric());
        fabrics.put(R.id.tab_dialogs, new FragmentDialogs.Fabric());
        fabrics.put(R.id.tab_music, new FragmentsMusic.Fabric());
        TabsFragmentController tabsFragmentController =
                new TabsFragmentController(getSupportFragmentManager(), fabrics);
        tabsFragmentContainer.setController(tabsFragmentController);
        tabsFragmentController.setUpWithNavigationBarView(navigationBarView);
        navigationBarView.setSelectedItemId(R.id.tab_feed);
    }

    private void iniPlayerBottomSheet(){

        playerBottomSheet = (FrameLayout) playerBottomSheetStub.inflate();

        playerBar = playerBottomSheet.findViewById(R.id.playerBar);
        viewPager2 = playerBottomSheet.findViewById(R.id.viewPager);

        bottomSheetCallback.onStateChanged(playerBottomSheet, playerBottomSheetBehavior.getState());

        View child = viewPager2.getChildAt(0);
        if (child instanceof RecyclerView) {
            child.setOverScrollMode(View.OVER_SCROLL_NEVER);
            child.setNestedScrollingEnabled(false);
        }

        ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();

        fabrics.add(new FragmentPlayer.Fabric());
        fabrics.add(new FragmentPlaying.Fabric());
        //very laggy
        //viewPager2.setOffscreenPageLimit(1);
        viewPager2.setAdapter(new TabsAdapter(getSupportFragmentManager(), getLifecycle(), fabrics));

        playerBar.setOnClickListener(view -> playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

    }

    private void iniPlayerBottomSheetBehavior(Bundle savedInstanceState){

        playerBottomSheetStub = rootView.findViewById(R.id.playerBottomSheetStub);
        playerBottomSheetBehavior = BottomSheetBehavior.from(playerBottomSheetStub);
        playerBottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                    case BottomSheetBehavior.STATE_DRAGGING:
                    case BottomSheetBehavior.STATE_HALF_EXPANDED:
                    case BottomSheetBehavior.STATE_SETTLING: {
                        break;
                    }
                    case BottomSheetBehavior.STATE_EXPANDED:{
                        if(!miracleApp.nightMode()) {
                            clearLightStatusBar(getWindow().getDecorView());
                        }
                        if(viewPager2.getVisibility()!=View.VISIBLE) {
                            viewPager2.setVisibility(View.VISIBLE);
                        }
                        viewPager2.setAlpha(1);
                        navigationBarView.setAlpha(0);
                        playerBar.setAlpha(0);
                        break;
                    }
                    case BottomSheetBehavior.STATE_COLLAPSED:{
                        if(!miracleApp.nightMode()) {
                            setLightStatusBar(getWindow().getDecorView());
                        }
                        if(viewPager2.getVisibility()!=View.GONE) {
                            viewPager2.setVisibility(View.GONE);
                        }
                        viewPager2.setAlpha(0);
                        viewPager2.setCurrentItem(0,false);
                        navigationBarView.setAlpha(1);
                        playerBar.setAlpha(1);
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float alpha = 1f-slideOffset;
                if(slideOffset>0){
                    if(viewPager2.getVisibility()!=View.VISIBLE) {
                        viewPager2.setVisibility(View.VISIBLE);
                    }
                }

                float transitionPercent = (1-alpha);
                playerBar.setAlpha(alpha);
                playerBar.setTranslationY(-transitionPercent*playerBar.getHeight());
                navigationBarView.setAlpha(alpha);
                navigationBarView.setTranslationY(transitionPercent*navigationBarView.getHeight());
                viewPager2.setAlpha(slideOffset);
            }
        });

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

        playerServiceController.addOnPlayerEventListener(onPlayerEventListener);
    }

    public void hideNavigationBars() {
        if(navigationBarView.getVisibility()!= View.GONE){
            navigationBarView.setVisibility(View.GONE);
        }
        if(showingPlayer){
            if(playerBottomSheet!=null&&playerBottomSheet.getVisibility()!=View.GONE) {
                playerBottomSheet.setVisibility(View.GONE);
            }
        }
        rootView.setState(STATE_CLEAR);
        updateInsets();
    }

    public void showNavigationBars(){
        if(navigationBarView.getVisibility()!=View.VISIBLE){
            navigationBarView.setVisibility(View.VISIBLE);
        }
        if(showingPlayer){
            if (playerBottomSheet == null) {
                iniPlayerBottomSheet();
            }
            if(rootView.getState()!=STATE_AUDIO) {
                if (playerBottomSheet.getVisibility() != View.VISIBLE) {
                    playerBottomSheet.setVisibility(View.VISIBLE);
                }
                rootView.setState(STATE_AUDIO);
            }
        } else {
            if (playerBottomSheet != null && playerBottomSheet.getVisibility() != View.GONE) {
                playerBottomSheet.setVisibility(View.GONE);
                if(playerBottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_COLLAPSED) {
                    playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            navigationBarView.setAlpha(1);
            rootView.setState(STATE_STANDARD);
        }
        updateInsets();
    }

    public void hidePlayerBottomSheet(){
        if(rootView.getState()!=STATE_CLEAR) {
            if (playerBottomSheet != null && playerBottomSheet.getVisibility() != View.GONE) {
                playerBottomSheet.setVisibility(View.GONE);
                if(playerBottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_COLLAPSED) {
                    playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
            if(navigationBarView.getVisibility()!=View.VISIBLE){
                navigationBarView.setVisibility(View.VISIBLE);
            }
            navigationBarView.setAlpha(1);
            rootView.setState(STATE_STANDARD);
            updateInsets();
        }
    }

    public void showPlayerBottomSheet(){
        if(rootView.getState()!=STATE_CLEAR) {
            if (playerBottomSheet == null) {
                iniPlayerBottomSheet();
            }
            if(rootView.getState()!=STATE_AUDIO) {
                if (playerBottomSheet.getVisibility() != View.VISIBLE) {
                    playerBottomSheet.setVisibility(View.VISIBLE);
                }
                rootView.setState(STATE_AUDIO);
                updateInsets();
            }
        }
    }

    public boolean playerBottomSheetIsExpanded(){
        return showingPlayer&&playerBottomSheetBehavior!=null&&
                playerBottomSheetBehavior.getState()==BottomSheetBehavior.STATE_EXPANDED;
    }

    public void addOnApplyWindowInsetsListener(OnApplyWindowInsetsListener onApplyWindowInsetsListener){
        if(windowInsets !=null) {
           onApplyWindowInsetsListener.onApplyWindowInsets(getWindow().getDecorView(),windowInsets);
        }
        onApplyWindowInsetsListeners.add(onApplyWindowInsetsListener);
    }

    public void removeOnApplyWindowInsetsListener(OnApplyWindowInsetsListener onApplyWindowInsetsListener){
        onApplyWindowInsetsListeners.remove(onApplyWindowInsetsListener);
    }

    public MiracleApp getMiracleApp() {
        return miracleApp;
    }

    public ViewPager2 getViewPager2() {
        return viewPager2;
    }

    public void addFragment(MiracleFragment fragment){
        TabsFragmentController tabsFragmentController = tabsFragmentContainer.getController();
        if(tabsFragmentController!=null) {
            tabsFragmentController.addFragment(fragment);
            if(playerBottomSheetIsExpanded()) {
                playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
    }

    public boolean fragmentBack(){
        TabsFragmentController tabsFragmentController = tabsFragmentContainer.getController();
        if(tabsFragmentController!=null){
            if(tabsFragmentController.getFragmentCount()>1){
                tabsFragmentController.back();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if(playerBottomSheetIsExpanded()) {
          playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
          return;
        }
        if (!fragmentBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("playerSheetBehaviorState",playerBottomSheetBehavior.getState());

        TabsFragmentController tabsFragmentController = tabsFragmentContainer.getController();

        if(tabsFragmentController!=null){
            TabsFragmentController.ControllerSavedState controllerSavedData =
                    tabsFragmentController.saveState(outState);
            outState.putString("ControllerSavedData", LargeDataStorage.get().storeLargeData(controllerSavedData));
        }
    }

    @NonNull
    public ProfileItem getUserItem() {
        return userItem;
    }

    public void setUserItem(ProfileItem userItem) {
        this.userItem = userItem;
    }

    @Override
    public void recreate() {
        Intent intent = getIntent();
        intent.setFlags(0);
        Bundle bundle = new Bundle();
        onSaveInstanceState(bundle);
        intent.putExtra("saved_state",bundle);
        startActivity(intent,bundle);
        finish();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        playerServiceController.removeOnPlayerEventListener(onPlayerEventListener);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getBooleanExtra("PlayerBottomSheetExpanded",false)){
            playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
        intent.removeExtra("PlayerBottomSheetExpanded");
    }

    public void exitFromAccount(){
        LongPollServiceController.get().actionStop();
        playerServiceController.actionStop();
        SettingsUtil.get().storeAuthorized(false);//сброс индекса текущего пользователя
        new UnregisterDevice(this, userItem.getAccessToken()).start();
        Intent intent = new Intent(miracleApp, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
    }
}