package com.vkontakte.miracle.adapter.audio;

import static com.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_ERROR;
import static com.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_LOADING;
import static com.vkontakte.miracle.engine.util.StorageUtil.SONGS_NAME;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import android.util.ArrayMap;

import com.miracle.engine.adapter.MiracleInstantLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.miracle.engine.adapter.holder.error.ErrorViewHolder;
import com.miracle.engine.adapter.holder.loading.LoadingViewHolder;
import com.vkontakte.miracle.adapter.audio.holders.WrappedAudioViewHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.audio.DownloadedAudios;
import com.vkontakte.miracle.model.audio.wraps.AudioItemWF;
import com.vkontakte.miracle.model.users.User;

import java.io.File;
import java.util.ArrayList;

public class DownloadedAdapter extends MiracleInstantLoadAdapter {

    @Override
    public void onLoading() throws Exception {
        StorageUtil storageUtil = StorageUtil.get();
        User user = storageUtil.currentUser();
        File cachesDir = storageUtil.getUserCachesDir(user);

        ArrayList<ItemDataHolder> audios = new StorageUtil.
                ArrayListReader<ItemDataHolder>(storageUtil).
                read(SONGS_NAME, cachesDir, object -> (ItemDataHolder) object);

        DownloadedAudios downloadedAudios = wrapToDownloads(audios);
        ArrayList<ItemDataHolder> wraps = downloadedAudios.getAudioItems();

        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        holders.clear();

        holders.addAll(wraps);

        setAddedCount(wraps.size());

        setFinallyLoaded(true);
    }

    public DownloadedAudios wrapToDownloads(ArrayList<ItemDataHolder> audios){
        ArrayList<ItemDataHolder> audiosWraps = new ArrayList<>();

        DownloadedAudios downloadedAudios = new DownloadedAudios(audiosWraps);

        AudioItemWF audioItemWF = new AudioItemWF();
        for (ItemDataHolder itemDataHolder: audios){
            AudioItem audioItem = (AudioItem) itemDataHolder;
            audiosWraps.add(audioItemWF.create(audioItem, downloadedAudios));
        }

        return downloadedAudios;
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_LOADING, new LoadingViewHolder.Fabric());
        arrayMap.put(TYPE_ERROR, new ErrorViewHolder.Fabric());
        arrayMap.put(TYPE_WRAPPED_AUDIO, new WrappedAudioViewHolder.Fabric());
        return arrayMap;
    }


}
