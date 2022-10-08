package com.vkontakte.miracle.fragment.audio;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.DownloadedAdapter;
import com.vkontakte.miracle.engine.fragment.side.SideRecyclerFragment;
import com.vkontakte.miracle.network.Constants;

public class FragmentOfflineAudio extends SideRecyclerFragment {
    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new DownloadedAdapter();
    }

    @Override
    public String requestTitleText() {
        Context context = getContext();
        if(context!=null){
            return context.getString(R.string.audio_offline);
        }
        return super.requestTitleText();
    }
}
