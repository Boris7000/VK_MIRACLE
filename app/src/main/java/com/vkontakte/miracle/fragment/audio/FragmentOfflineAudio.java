package com.vkontakte.miracle.fragment.audio;

import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.DownloadedAdapter;
import com.vkontakte.miracle.engine.fragment.side.SideRecyclerFragment;

public class FragmentOfflineAudio extends SideRecyclerFragment {
    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new DownloadedAdapter();
    }

    @Override
    public String requestTitleText() {
        return getMiracleActivity().getString(R.string.audio_offline);
    }
}
