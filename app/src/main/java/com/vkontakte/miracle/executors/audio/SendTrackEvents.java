package com.vkontakte.miracle.executors.audio;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.audio.AudioItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.network.methods.Audio;

public class SendTrackEvents extends AsyncExecutor<Boolean> {

    private final AudioItem audioItem;

    public SendTrackEvents(AudioItem audioItem){
        this.audioItem = audioItem;
    }

    @Override
    public Boolean inBackground() {
        try {
            ProfileItem profileItem = StorageUtil.get().currentUser();
            if(profileItem!=null) {
                Audio.trackEventsAudioPlay(audioItem.getId(), audioItem.getOwnerId(),
                        audioItem.getDuration(), profileItem.getAccessToken()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
