package com.vkontakte.miracle.model.audio;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWC;

import java.util.ArrayList;

public class DownloadedAudios implements AudioItemWC {

    private final ArrayList<ItemDataHolder> items;

    public DownloadedAudios(ArrayList<ItemDataHolder> items) {
        this.items = items;
    }

    @Override
    public ArrayList<ItemDataHolder> getAudioItems() {
        return items;
    }

}
