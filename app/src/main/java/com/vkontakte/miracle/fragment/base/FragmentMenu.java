package com.vkontakte.miracle.fragment.base;

import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwnerFriends;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwnerGroups;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToOwnerPhotos;
import static com.vkontakte.miracle.engine.util.NavigationUtil.goToProfile;

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miracle.button.SwitchButton;
import com.miracle.button.TextViewButton;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.base.BaseRefreshListFragment;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.fragment.settings.FragmentSettings;
import com.vkontakte.miracle.login.UpdateCurrentUserData;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.LastSeen;

public class FragmentMenu extends BaseRefreshListFragment {

    private View rootView;
    private LinearLayout blockScreen;
    private UpdateCurrentUserData updateCurrentUserData;
    private UpdateCurrentUserData.onCompleteListener onCompleteListener;
    private SwitchButton nightModeSwitchButton;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = super.onCreateView(inflater, container, savedInstanceState);

        onCompleteListener = (profileItem, hasChanges) -> {
            getSwipeRefreshLayout().setRefreshing(false);
            if(hasChanges){
                setParameters();
            }
        };
        if(savedInstanceState==null){
            (updateCurrentUserData = new UpdateCurrentUserData(onCompleteListener)).start();
        } else {
            block();
        }

        setButtons();

        setParameters();

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        blockScreen = rootView.findViewById(R.id.fragmentBlockScreen);
        nightModeSwitchButton = rootView.findViewById(R.id.dark_theme_switch);
    }

    @Override
    public void initViews() {
        super.initViews();
        nightModeSwitchButton.setChecked(MiracleApp.getInstance().nightMode());
        nightModeSwitchButton.setOnClickListener(view -> {
            if (updateCurrentUserData != null && !updateCurrentUserData.workIsDone()) {
                return;
            }
            nightModeSwitchButton.setOnClickListener(null);
            nightModeSwitchButton.setChecked(!MiracleApp.getInstance().nightMode(), true);
            block();
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    MiracleApp.getInstance().swapNightMode(),
                    nightModeSwitchButton.getAnimationDuration());
        });
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener requestOnRefreshListener() {
        return () ->{
            if(updateCurrentUserData == null || updateCurrentUserData.workIsDone()){
                (updateCurrentUserData = new UpdateCurrentUserData(onCompleteListener)).start();
            }
        };
    }

    private void setParameters(){
        ProfileItem profileItem = StorageUtil.get().currentUser();
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
        ProfileItem profileItem = StorageUtil.get().currentUser();

        TextView settingsButton = rootView.findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> getMiracleActivity().addFragment(new FragmentSettings()));

        TextView photosButton = rootView.findViewById(R.id.photos);
        photosButton.setOnClickListener(v -> goToOwnerPhotos(profileItem.getId(), getMiracleActivity()));

        TextView groupsButton = rootView.findViewById(R.id.groups);
        groupsButton.setOnClickListener(v -> goToOwnerGroups(profileItem.getId(), getMiracleActivity()));

        TextView friendsButton = rootView.findViewById(R.id.friends);
        friendsButton.setOnClickListener(v -> goToOwnerFriends(profileItem.getId(), getMiracleActivity()));

        TextViewButton exitButton = rootView.findViewById(R.id.exit);
        exitButton.setOnClickListener(v -> {
            block();
            getMiracleActivity().exitFromAccount();
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
