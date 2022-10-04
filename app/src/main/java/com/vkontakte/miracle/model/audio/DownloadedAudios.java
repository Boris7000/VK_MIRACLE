package com.vkontakte.miracle.model.audio;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;

import java.util.ArrayList;

public class DownloadedAudios implements AudioWrapContainer {

    private final ArrayList<ItemDataHolder> items;

    public DownloadedAudios(ArrayList<ItemDataHolder> items) {
        this.items = items;
    }

    @Override
    public ArrayList<ItemDataHolder> getAudioItems() {
        return items;
    }

}
