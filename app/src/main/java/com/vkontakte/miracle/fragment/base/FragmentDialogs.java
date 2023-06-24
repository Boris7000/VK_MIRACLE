package com.vkontakte.miracle.fragment.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.FragmentFabric;
import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.messages.ConversationsAdapter;

public class FragmentDialogs extends BaseRecyclerFragment {

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new ConversationsAdapter();
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.dialogs;
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public Fragment createFragment() {
            return new FragmentDialogs();
        }
    }
}
