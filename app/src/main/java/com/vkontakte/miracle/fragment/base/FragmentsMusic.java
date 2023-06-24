package com.vkontakte.miracle.fragment.base;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.fragment.audio.FragmentOfflineAudioNested;
import com.vkontakte.miracle.fragment.catalog.ATabsFragmentCatalogSections;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Catalog;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;

public class FragmentsMusic extends ATabsFragmentCatalogSections {

    @Override
    public ArrayList<NestedMiracleFragmentFabric> getErrorTabs() {
        ArrayList<NestedMiracleFragmentFabric> fabrics = new ArrayList<>();
        String title="";
        Context context = getContext();
        if(context!=null){
            title = context.getString(R.string.audio_offline);
        }
        fabrics.add(new FragmentOfflineAudioNested.Fabric(title));
        return fabrics;
    }

    @Override
    public boolean asyncLoadTabs() {
        return true;
    }

    @Override
    public Call<JSONObject> requestCall() {
        User user = StorageUtil.get().currentUser();
        return Catalog.getAudio(user.getId(), user.getAccessToken());
    }

    @Override
    public void onSearchButtonClicked() {
        NavigationUtil.goToAudioSearch(null, getContext());
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public Fragment createFragment() {
            return new FragmentsMusic();
        }
    }
}
