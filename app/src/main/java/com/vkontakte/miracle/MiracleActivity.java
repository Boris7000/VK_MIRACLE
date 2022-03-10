package com.vkontakte.miracle;

import static com.vkontakte.miracle.engine.util.UIUtil.clearLightStatusBar;
import static com.vkontakte.miracle.engine.util.UIUtil.setLightStatusBar;
import static com.vkontakte.miracle.engine.view.ActivityRootView.STATE_AUDIO;
import static com.vkontakte.miracle.engine.view.ActivityRootView.STATE_CLEAR;
import static com.vkontakte.miracle.engine.view.ActivityRootView.STATE_STANDARD;
import static com.vkontakte.miracle.engine.view.ActivityRootView.TYPE_LAND;
import static com.vkontakte.miracle.engine.view.ActivityRootView.TYPE_PORTRAIT;
import static com.vkontakte.miracle.engine.view.ActivityRootView.TYPE_SW600DP;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.tabs.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.fragment.tabs.TabsAdapter;
import com.vkontakte.miracle.engine.util.DimensionsUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.view.ActivityRootView;
import com.vkontakte.miracle.engine.view.bottomNavigation.MiracleBottomNavigationMenu;
import com.vkontakte.miracle.engine.view.fragmentContainer.FragmentContainer;
import com.vkontakte.miracle.player.fragment.FragmentPlayer;
import com.vkontakte.miracle.fragment.base.*;
import com.vkontakte.miracle.player.fragment.FragmentPlaying;
import com.vkontakte.miracle.login.LoginActivity;
import com.vkontakte.miracle.login.UnregisterDevice;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.player.AudioPlayerData;
import com.vkontakte.miracle.player.OnPlayerEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class MiracleActivity extends AppCompatActivity {

    private MiracleApp miracleApp;
    private ProfileItem userItem;
    private ActivityRootView rootView;
    private FragmentContainer fragmentContainer;
    private MiracleBottomNavigationMenu bottomNavigationMenu;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(savedInstanceState==null){
            savedInstanceState = getIntent().getBundleExtra("saved_state");
        }

        super.onCreate(savedInstanceState);

        miracleApp = (MiracleApp) getApplication();

        setTheme(miracleApp.getThemeRecourseId());

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ArrayList<ProfileItem> profileItems = StorageUtil.loadUsers(miracleApp);
        if(profileItems.isEmpty()) {
            miracleApp.getSettingsUtil().storeAuthorized(false);
            Intent intent = new Intent(miracleApp, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        } else {
            userItem = StorageUtil.loadUsers(miracleApp).get(0);
        }

        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.rootView);

        bottomNavigationMenu = rootView.findViewById(R.id.bottomNavigationMenu);

        fragmentContainer = rootView.findViewById(R.id.frameContainer);

        iniWindowInsets();

        iniPlayerThings(savedInstanceState);

        iniFragmentController(savedInstanceState);

        miracleApp.getLongPollServiceController().startExecuting();
    }

    private void iniWindowInsets(){

        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, windowInsets) -> {

            this.windowInsets = windowInsets;

            updateInsets();

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
            int bottomInsets = Math.max(systemBarsInsets.bottom,imeInsets.bottom);
            int dp56 = (int) DimensionsUtil.dpToPx(56, this);

            switch (rootView.getType()){
                case TYPE_PORTRAIT: {
                    switch (rootView.getState()) {
                        case STATE_CLEAR: {
                            fragmentContainer.setPadding(0, systemBarsInsets.top,
                                    0, bottomInsets);
                            break;
                        }
                        case STATE_STANDARD: {
                            bottomNavigationMenu.setPadding(0, 0,
                                    0, bottomInsets);
                            fragmentContainer.setPadding(0, systemBarsInsets.top,
                                    0, bottomInsets + dp56);
                            break;
                        }
                        case STATE_AUDIO: {
                            bottomNavigationMenu.setPadding(0, 0,
                                    0, bottomInsets);
                            playerBottomSheetBehavior.setPeekHeight(
                                    bottomInsets + dp56 + dp56);
                            fragmentContainer.setPadding(0, systemBarsInsets.top,
                                    0, playerBottomSheetBehavior.getPeekHeight());
                            break;
                        }
                    }
                    break;
                }
                case TYPE_LAND:
                case TYPE_SW600DP: {
                    switch (rootView.getState()) {
                        case STATE_CLEAR: {
                            fragmentContainer.setPadding(systemBarsInsets.left, systemBarsInsets.top,
                                    systemBarsInsets.right, bottomInsets);
                            break;
                        }
                        case STATE_STANDARD: {
                            bottomNavigationMenu.setPadding(systemBarsInsets.left, systemBarsInsets.top,
                                    0, bottomInsets);
                            fragmentContainer.setPadding(systemBarsInsets.left + dp56, systemBarsInsets.top,
                                    systemBarsInsets.right, bottomInsets);
                            break;
                        }
                        case STATE_AUDIO: {
                            playerBar.setPadding(systemBarsInsets.left,0,systemBarsInsets.right,0);
                            playerBottomSheetBehavior.setPeekHeight(
                                    Math.max(systemGesturesInsets.bottom, bottomInsets)+dp56);
                            bottomNavigationMenu.setPadding(systemBarsInsets.left, systemBarsInsets.top,
                                    0, playerBottomSheetBehavior.getPeekHeight());
                            fragmentContainer.setPadding(systemBarsInsets.left + dp56, systemBarsInsets.top,
                                    systemBarsInsets.right, playerBottomSheetBehavior.getPeekHeight());
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
                FragmentContainer.ControllerSavedData controllerSavedData =
                        (FragmentContainer.ControllerSavedData) miracleApp.getLargeDataStorage().getLargeData(key);

                fragmentContainer.setController(new FragmentContainer.TabsFragmentController(getSupportFragmentManager(),controllerSavedData,savedInstanceState));
                bottomNavigationMenu.setupWithFragmentContainer(fragmentContainer);
                bottomNavigationMenu.select(savedInstanceState.getInt("SelectedTab",1), false);
            }
        } else {
            fragmentContainer.setController(new FragmentContainer.TabsFragmentController(getSupportFragmentManager(),
                    new ArrayList<>(Arrays.asList(new FragmentMenu.Fabric(), new FragmentFeed.Fabric(),
                    new FragmentDialogs.Fabric(), new FragmentsMusic.Fabric()))));
            bottomNavigationMenu.setupWithFragmentContainer(fragmentContainer);
            bottomNavigationMenu.select(1);
        }
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
        viewPager2.setAdapter(new TabsAdapter(getSupportFragmentManager(), getLifecycle(), fabrics));

        playerBar.setOnClickListener(view -> playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));

    }

    public void addFragment(MiracleFragment fragment){
        fragmentContainer.addFragment(fragment);
        if(playerBottomSheetIsExpanded()) {
            playerBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    public void hideNavigationBars() {
        if(bottomNavigationMenu.getVisibility()!= View.GONE){
            bottomNavigationMenu.setVisibility(View.GONE);
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
        if(bottomNavigationMenu.getVisibility()!=View.VISIBLE){
            bottomNavigationMenu.setVisibility(View.VISIBLE);
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
            bottomNavigationMenu.setAlpha(1);
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
            if(bottomNavigationMenu.getVisibility()!=View.VISIBLE){
                bottomNavigationMenu.setVisibility(View.VISIBLE);
            }
            bottomNavigationMenu.setAlpha(1);
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

    public boolean fragmentBack(){
        if(fragmentContainer.getFragmentCount()>1) {
            fragmentContainer.back();
            return true;
        }else {
            return false;
        }
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

        outState.putInt("SelectedTab",bottomNavigationMenu.getSelectedIndex());

        outState.putInt("playerSheetBehaviorState",playerBottomSheetBehavior.getState());

        FragmentContainer.ControllerSavedData controllerSavedData = fragmentContainer.saveState(outState);
        if(controllerSavedData!=null){
            outState.putString("ControllerSavedData",
                    miracleApp.getLargeDataStorage().storeLargeData(controllerSavedData,
                            miracleApp.getLargeDataStorage().createUniqueKey()));
        }

        super.onSaveInstanceState(outState);
    }

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

    public void iniPlayerThings(Bundle savedInstanceState){

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
                        if(bottomNavigationMenu.getVisibility()!=View.GONE) {
                            bottomNavigationMenu.setVisibility(View.GONE);

                        }
                        if(playerBar.getVisibility()!=View.GONE) {
                            playerBar.setVisibility(View.GONE);

                        }
                        playerBar.setAlpha(0);
                        bottomNavigationMenu.setAlpha(0);
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
                        if(bottomNavigationMenu.getVisibility()!=View.VISIBLE) {
                            bottomNavigationMenu.setVisibility(View.VISIBLE);

                        }
                        bottomNavigationMenu.setAlpha(1);
                        if(playerBar.getVisibility()!=View.VISIBLE) {
                            playerBar.setVisibility(View.VISIBLE);

                        }
                        playerBar.setAlpha(1);
                        break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float alpha = 1f-slideOffset;

                if(alpha>0){
                    if(bottomNavigationMenu.getVisibility()!=View.VISIBLE) {
                        bottomNavigationMenu.setVisibility(View.VISIBLE);
                    }
                    if(playerBar.getVisibility()!=View.VISIBLE) {
                        playerBar.setVisibility(View.VISIBLE);
                    }
                }

                if(slideOffset>0){
                    if(viewPager2.getVisibility()!=View.VISIBLE) {
                        viewPager2.setVisibility(View.VISIBLE);
                    }
                }

                playerBar.setAlpha(alpha);
                bottomNavigationMenu.setAlpha(alpha);
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

        miracleApp.getPlayerServiceController().addOnPlayerEventListener(onPlayerEventListener);
    }

    @Override
    protected void onDestroy() {
        miracleApp.getPlayerServiceController().removeOnPlayerEventListener(onPlayerEventListener);
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

    @Override
    protected void onResume() {
        super.onResume();
        miracleApp.getLongPollServiceController().startExecuting();
    }

    public void exitFromAccount(){
        miracleApp.getPlayerServiceController().actionStop();
        miracleApp.getLongPollServiceController().actionStop();
        miracleApp.getSettingsUtil().storeAuthorized(false);//сброс индекса текущего пользователя
        new UnregisterDevice(userItem.getAccessToken(),miracleApp).start();
        Intent intent = new Intent(miracleApp, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_reverse,R.anim.slide_out_reverse);
    }
}