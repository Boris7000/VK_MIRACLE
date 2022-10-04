package com.vkontakte.miracle.executors.audio;

import static com.vkontakte.miracle.engine.util.StorageUtil.MP3S_NAME;
import static com.vkontakte.miracle.engine.util.StorageUtil.SONGS_NAME;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.users.ProfileItem;

import java.io.File;
import java.util.ArrayList;

public class EraseAudio extends AsyncExecutor<Boolean> {

    private final AudioItem audioItem;
    private final ProfileItem profileItem;

    public EraseAudio(AudioItem audioItem) {
        this.audioItem = audioItem;
        profileItem = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {

        try {

            StorageUtil storageUtil = StorageUtil.get();

            File cachesDir = storageUtil.getUserCachesDir(profileItem);

            File mp3sDir = new File(cachesDir, MP3S_NAME);
            File mp3File = new File(mp3sDir, audioItem.getOwnerId()+"_"+audioItem.getId()+".mp3");

            storageUtil.deleteFile(mp3File);

            ArrayList<ItemDataHolder> currentAudios =
                    new StorageUtil.ArrayListReader<ItemDataHolder>(storageUtil).
                            read(SONGS_NAME, cachesDir, object -> (ItemDataHolder) object);

            audioItem.setDownloaded(null);

            currentAudios.remove(audioItem);

            storageUtil.writeObject(SONGS_NAME, cachesDir, currentAudios);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public AudioItem getAudioItem() {
        return audioItem;
    }
}
