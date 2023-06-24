package com.vkontakte.miracle.model.audio.wraps;

import com.vkontakte.miracle.service.player.loader.AudioItemWCLoader;

public interface LoadableAudioItemWC extends AudioItemWC{
    AudioItemWCLoader createAudioLoader();
}
