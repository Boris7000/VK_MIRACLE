package com.vkontakte.miracle.fragment.audio;

import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.DownloadedAdapter;

public class FragmentOfflineAudio extends BaseRecyclerFragment {
    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new DownloadedAdapter();
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.audio_offline;
    }
}
