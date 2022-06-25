package com.vkontakte.miracle.fragment.base;

import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;
import static com.vkontakte.miracle.engine.util.FragmentUtil.goToProfile;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.button.SwitchButton;
import com.miracle.button.TextViewButton;
import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.DeviceUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogFriends;
import com.vkontakte.miracle.fragment.catalog.FragmentCatalogGroups;
import com.vkontakte.miracle.fragment.photos.FragmentUserPhotos;
import com.vkontakte.miracle.fragment.settings.FragmentSettings;
import com.vkontakte.miracle.login.UpdateCurrentUserData;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.LastSeen;

public class FragmentMenu extends SimpleMiracleFragment {

    private View rootView;
    private MiracleApp miracleApp;
    private MiracleActivity miracleActivity;
    private LinearLayout blockScreen;
    private UpdateCurrentUserData updateCurrentUserData;
    private SwitchButton nightModeSwitchButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        miracleApp = MiracleApp.getInstance();
        miracleActivity = getMiracleActivity();

        rootView = inflater.inflate(R.layout.fragment_menu, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setScrollView(rootView.findViewById(R.id.scrollView));
        scrollAndElevate(getScrollView(), getAppBarLayout(), miracleActivity);
        blockScreen = rootView.findViewById(R.id.fragmentBlockScreen);

        UpdateCurrentUserData.onCompleteListener onCompleteListener = (profileItem, hasChanges) -> {
            getSwipeRefreshLayout().setRefreshing(false);
            if(hasChanges){
                miracleActivity.setUserItem(profileItem);
                setParameters();
            }
        };
        if(savedInstanceState==null){
            (updateCurrentUserData = new UpdateCurrentUserData(onCompleteListener)).start();
        } else {
            block();
        }

        setSwipeRefreshLayout(rootView.findViewById(R.id.refreshLayout), () ->{
            if(updateCurrentUserData == null || updateCurrentUserData.workIsDone()){
                (updateCurrentUserData = new UpdateCurrentUserData(onCompleteListener)).start();
            }
        });

        nightModeSwitchButton = rootView.findViewById(R.id.dark_theme_switch);
        nightModeSwitchButton.setChecked(miracleApp.nightMode());
        nightModeSwitchButton.setOnClickListener(view -> {
            if (updateCurrentUserData != null && !updateCurrentUserData.workIsDone()) {
                return;
            }
            nightModeSwitchButton.setOnClickListener(null);
            nightModeSwitchButton.setChecked(!miracleApp.nightMode(), true);
            block();
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                miracleApp.swapNightMode();
            }, nightModeSwitchButton.getAnimationDuration());
        });

        setButtons();

        setParameters();

        return rootView;
    }

    private void setParameters(){
        ProfileItem profileItem = miracleActivity.getUserItem();
        FrameLayout frameLayout = rootView.findViewById(R.id.profileLink);

        frameLayout.setOnClickListener(view -> goToProfile(profileItem,getMiracleActivity()));

        TextView username = frameLayout.findViewById(R.id.current_user_name);
        username.setText(profileItem.getFullName());

        ImageView avatar = frameLayout.findViewById(R.id.photo);
        avatar.setImageBitmap(StorageUtil.get().loadBitmap("profileImage200.png"));

        ImageView online_status =  frameLayout.findViewById(R.id.onlineStatus);

        if(profileItem.isOnline()){
            if(online_status.getVisibility()!=VISIBLE) {
                online_status.setVisibility(VISIBLE);
            }
            LastSeen lastSeen = profileItem.getLastSeen();
            online_status.setImageResource(lastSeen.getPlatform()==7?R.drawable.ic_online_16:R.drawable.ic_online_mobile_16);
            online_status.setBackgroundResource(lastSeen.getPlatform()==7?R.drawable.ic_online_subtract_16 :R.drawable.ic_online_mobile_subtract_16);
        } else {
            online_status.setVisibility(View.GONE);
        }

    }

    private void setButtons(){
        TextView settingsButton = rootView.findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> miracleActivity.addFragment(new FragmentSettings()));

        TextView photosButton = rootView.findViewById(R.id.photos);
        photosButton.setOnClickListener(v -> miracleActivity.addFragment(new FragmentUserPhotos()));

        TextView groupsButton = rootView.findViewById(R.id.groups);
        groupsButton.setOnClickListener(v -> miracleActivity.addFragment(new FragmentCatalogGroups()));

        TextView friendsButton = rootView.findViewById(R.id.friends);
        friendsButton.setOnClickListener(v -> miracleActivity.addFragment(new FragmentCatalogFriends()));

        TextViewButton exitButton = rootView.findViewById(R.id.exit);
        exitButton.setOnClickListener(v -> {
            block();
            miracleActivity.exitFromAccount();
        });
    }

    private void unblock(){blockScreen.setClickable(false);}

    private void block(){blockScreen.setClickable(true);}

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentMenu();
        }
    }


    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        unblock();
    }
}
