package com.vkontakte.miracle.adapter.audio;

import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_PLAYLIST_CREATION;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.miracle.engine.adapter.MiracleInstantLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.MiracleViewHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.async.baseExecutors.SimpleTimer;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.model.audio.PlaylistCreationItem;

import java.util.ArrayList;

public class CreatePlaylistAdapter extends MiracleInstantLoadAdapter {

    private ChangeListener changeListener;
    private String title = "";
    private String description = "";

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    private void setTitle(String title){
        if(!this.title.equals(title)) {
            this.title = title;
            if (changeListener != null) {
                changeListener.onTitleChange(title);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    private void setDescription(String description){
        if(!this.description.equals(description)) {
            this.description = description;
        }
    }

    @Override
    public void onLoading() throws Exception {

        ArrayList<ItemDataHolder> itemDataHolders = getItemDataHolders();

        if(!loaded()){
            itemDataHolders.add(new PlaylistCreationItem());
        }

        setFinallyLoaded(true);
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_WRAPPED_AUDIO, new WrappedAudioViewHolder.Fabric());
        arrayMap.put(TYPE_PLAYLIST_CREATION, new PlaylistCreationViewHolderFabric());
        return arrayMap;
    }

    public class PlaylistCreationViewHolder extends MiracleViewHolder {

        private final EditText titleEditText;
        private boolean handle = true;

        public PlaylistCreationViewHolder(@NonNull View itemView) {
            super(itemView);
            titleEditText = itemView.findViewById(R.id.titleEditText);
            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if(handle) {
                        setTitle(editable.toString().trim());
                    }
                }
            };
            titleEditText.addTextChangedListener(textWatcher);
        }

        @Override
        public void bind(ItemDataHolder itemDataHolder) {
            handle = false;
            titleEditText.setText(title);
            handle = true;
        }
    }
    public class PlaylistCreationViewHolderFabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new PlaylistCreationViewHolder(inflater.inflate(R.layout.view_playlist_creation, viewGroup, false));
        }
    }

    public interface ChangeListener {
        void onTitleChange(String title);
    }
}
