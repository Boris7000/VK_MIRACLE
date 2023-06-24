package com.vkontakte.miracle.fragment.base;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.wall.FeedAdapter;

public class FragmentFeed extends BaseRecyclerFragment {

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_with_recycleview_dark_base, container, false);
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new FeedAdapter();
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.feed;
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public Fragment createFragment() {
            return new FragmentFeed();
        }
    }
}
