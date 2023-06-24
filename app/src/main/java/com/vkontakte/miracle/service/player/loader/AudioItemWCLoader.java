package com.vkontakte.miracle.service.player.loader;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWC;

import java.util.ArrayList;

public interface AudioItemWCLoader {
    ArrayList<ItemDataHolder> load() throws Exception;
    AudioItemWC getContainer();
    boolean canLoadMore();
}
