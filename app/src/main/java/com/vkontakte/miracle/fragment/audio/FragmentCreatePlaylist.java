package com.vkontakte.miracle.fragment.audio;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.CreatePlaylistAdapter;

public class FragmentCreatePlaylist extends BaseRecyclerFragment {

    private MenuItem doneMenuItem;

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);
        RecyclerView.Adapter<?> adapter = getRecyclerView().getAdapter();
        if(adapter instanceof CreatePlaylistAdapter){
            CreatePlaylistAdapter createPlaylistAdapter = (CreatePlaylistAdapter) adapter;
            createPlaylistAdapter.setChangeListener(title ->{
                if(doneMenuItem!=null){
                    doneMenuItem.setEnabled(!title.isEmpty());
                }
            });
        }
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        return new CreatePlaylistAdapter();
    }

    @Override
    public String requestTitleText() {
        Context context = getContext();
        if(context!=null){
            return context.getString(R.string.new_playlist);
        }
        return super.requestTitleText();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.done_tool_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.done) {
            Log.d("ejfiejifef","я пидорас"); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        doneMenuItem = menu.findItem(R.id.done);
        RecyclerView.Adapter<?> adapter = getRecyclerView().getAdapter();
        if(adapter instanceof CreatePlaylistAdapter){
            CreatePlaylistAdapter createPlaylistAdapter = (CreatePlaylistAdapter) adapter;
            doneMenuItem.setEnabled(!createPlaylistAdapter.getTitle().isEmpty());
        }
    }

}
