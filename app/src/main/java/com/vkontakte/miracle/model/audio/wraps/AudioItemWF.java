package com.vkontakte.miracle.model.audio.wraps;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_WRAPPED_AUDIO;

import com.vkontakte.miracle.model.DataItemWrap;
import com.vkontakte.miracle.model.ItemDataWrapFabric;
import com.vkontakte.miracle.model.audio.AudioItem;

public class AudioItemWF implements ItemDataWrapFabric<AudioItem, AudioItemWC> {
    @Override
    public DataItemWrap<AudioItem, AudioItemWC> create(AudioItem item, AudioItemWC holder) {
        return new DataItemWrap<AudioItem, AudioItemWC>(item, holder) {
            @Override
            public int getViewHolderType() {
                        return TYPE_WRAPPED_AUDIO;
                    }
        };
    }
}
