package com.vkontakte.miracle.fragment.audio;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.adapter.audio.DownloadedAdapter;
import com.vkontakte.miracle.engine.fragment.NestedMiracleFragmentFabric;
import com.vkontakte.miracle.engine.fragment.nested.NestedRecyclerFragment;

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
