package com.vkontakte.miracle.fragment.audio;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.PlaylistAdapter;

public class FragmentPlaylist extends BaseRecyclerFragment {

    private String playlistId;
    private String ownerId;
    private String accessKey;

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new PlaylistAdapter(playlistId, ownerId, accessKey);
    }

    @Override
    public int requestTitleTextResId() {
        return R.string.playlist;
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        playlistId = savedInstanceState.getString("playlistId");
        ownerId = savedInstanceState.getString("ownerId");
        accessKey = savedInstanceState.getString("accessKey");
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        savedInstanceState.remove("playlistId");
        savedInstanceState.remove("ownerId");
        savedInstanceState.remove("accessKey");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("playlistId", playlistId);
        outState.putString("ownerId", ownerId);
        outState.putString("accessKey", accessKey);
    }
}
