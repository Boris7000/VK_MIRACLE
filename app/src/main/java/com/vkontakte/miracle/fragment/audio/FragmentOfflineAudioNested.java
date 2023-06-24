package com.vkontakte.miracle.fragment.audio;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.tabs.nested.NestedMiracleFragmentFabric;
import com.miracle.engine.fragment.tabs.nested.templates.NestedRecyclerFragment;
import com.vkontakte.miracle.adapter.audio.DownloadedAdapter;

public class FragmentOfflineAudioNested extends NestedRecyclerFragment {

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new DownloadedAdapter();
    }

    public static class Fabric extends NestedMiracleFragmentFabric {
        public Fabric(String title){
            super(title, -1);
        }

        @NonNull
        @Override
        public FragmentOfflineAudioNested createFragment() {
            return new FragmentOfflineAudioNested();
        }
    }

}
