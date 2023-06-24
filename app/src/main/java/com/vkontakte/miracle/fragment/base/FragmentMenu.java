package com.vkontakte.miracle.fragment.base;

import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.miracle.button.view.SwitchButton;
import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.base.templates.BaseRefreshListFragment;
import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.context.ContextExtractor;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.login.UpdateCurrentUserData;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.model.users.fields.LastSeen;

public class FragmentMenu extends BaseRefreshListFragment {

    private LinearLayout blockScreen;
    private UpdateCurrentUserData updateCurrentUserData;
    private UpdateCurrentUserData.OnCompleteListener onCompleteListener;
    private SwitchButton nightModeSwitchButton;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        onCompleteListener = (user, hasChanges) -> {
            getSwipeRefreshLayout().setRefreshing(false);
            if(hasChanges){
                setParameters(view);
            }
        };

        if(savedInstanceState==null){
            (updateCurrentUserData = new UpdateCurrentUserData(onCompleteListener)).start();
        } else {
            block();
        }
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void inflateContent(@NonNull LayoutInflater inflater, @NonNull LinearLayout container) {
        inflater.inflate(R.layout.fragment_content_menu, container, true);
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.profile;
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        blockScreen = rootView.findViewById(R.id.fragmentBlockScreen);
        nightModeSwitchButton = rootView.findViewById(R.id.dark_theme_switch);
    }

    @CallSuper
    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        super.initViews(rootView, savedInstanceState);
        nightModeSwitchButton.setChecked(MainApp.getInstance().nightMode());
        nightModeSwitchButton.setOnClickListener(view -> {
            if (updateCurrentUserData != null && !updateCurrentUserData.workIsDone()) {
                return;
            }
            nightModeSwitchButton.setOnClickListener(null);
            nightModeSwitchButton.setChecked(!MainApp.getInstance().nightMode(), true);
            block();
            new Handler(Looper.getMainLooper()).postDelayed(() ->
                    MainApp.getInstance().swapNightMode(),
                    nightModeSwitchButton.getAnimationDuration());
        });

        setButtons(rootView);

        setParameters(rootView);
    }

    @Override
    public SwipeRefreshLayout.OnRefreshListener requestOnRefreshListener() {
        return () ->{
            if(updateCurrentUserData == null || updateCurrentUserData.workIsDone()){
                (updateCurrentUserData = new UpdateCurrentUserData(onCompleteListener)).start();
            }
        };
    }

    private void setParameters(@NonNull View rootView){
        User user = StorageUtil.get().currentUser();
        FrameLayout frameLayout = rootView.findViewById(R.id.profileLink);

        frameLayout.setOnClickListener(view -> NavigationUtil.goToProfile(user, getContext()));

        TextView username = frameLayout.findViewById(R.id.current_user_name);
        username.setText(user.getFullName());

        ImageView avatar = frameLayout.findViewById(R.id.photo);
        avatar.setImageBitmap(StorageUtil.get().loadBitmap("userImage200.png"));

        ImageView online_status =  frameLayout.findViewById(R.id.onlineStatus);

        if(user.isOnline()){
            if(online_status.getVisibility()!=VISIBLE) {
                online_status.setVisibility(VISIBLE);
            }
            LastSeen lastSeen = user.getLastSeen();
            online_status.setImageResource(lastSeen.getPlatform()==7?R.drawable.ic_online_16:R.drawable.ic_online_mobile_16);
            online_status.setBackgroundResource(lastSeen.getPlatform()==7?R.drawable.ic_online_subtract_16 :R.drawable.ic_online_mobile_subtract_16);
        } else {
            online_status.setVisibility(View.GONE);
        }

    }

    private void setButtons(@NonNull View rootView){
        User user = StorageUtil.get().currentUser();

        Button settingsButton = rootView.findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> NavigationUtil.goToSettings(getContext()));

        Button photosButton = rootView.findViewById(R.id.photos);
        photosButton.setOnClickListener(v -> NavigationUtil.goToOwnerPhotos(user.getId(), getContext()));

        Button groupsButton = rootView.findViewById(R.id.groups);
        groupsButton.setOnClickListener(v -> NavigationUtil.goToOwnerGroups(user.getId(), getContext()));

        Button friendsButton = rootView.findViewById(R.id.friends);
        friendsButton.setOnClickListener(v -> NavigationUtil.goToOwnerFriends(user.getId(), getContext()));

        Button videosButton = rootView.findViewById(R.id.videos);
        videosButton.setOnClickListener(v -> {});

        Button exitButton = rootView.findViewById(R.id.exit);
        exitButton.setOnClickListener(v -> {
            block();
            MainActivity mainActivity = ContextExtractor.extractMainActivity(getContext());
            if(mainActivity!=null) {
                mainActivity.exitFromAccount();
            }
        });

    }

    private void unblock(){blockScreen.setClickable(false);}

    private void block(){blockScreen.setClickable(true);}

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public Fragment createFragment() {
            return new FragmentMenu();
        }
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        unblock();
    }


}
