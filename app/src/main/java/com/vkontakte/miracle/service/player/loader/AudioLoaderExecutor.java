package com.vkontakte.miracle.service.player.loader;

import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.async.AsyncExecutor;

import java.util.ArrayList;

public abstract class AudioLoaderExecutor extends AsyncExecutor<ArrayList<ItemDataHolder>> {

    private final AudioItemWCLoader loader;

    protected AudioLoaderExecutor(AudioItemWCLoader loader) {
        this.loader = loader;
    }

    public AudioItemWCLoader getLoader() {
        return loader;
    }

    @Override
    public ArrayList<ItemDataHolder> inBackground() {
        try {
            return loader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
