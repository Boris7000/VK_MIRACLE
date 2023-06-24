package com.vkontakte.miracle.executors.audio;

import com.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Audio;
import com.vkontakte.miracle.service.player.statistics.AudioPlaybackStatisticsData;

public class SendPlaybackStatistics extends AsyncExecutor<Boolean> {

    private final AudioPlaybackStatisticsData statisticsData;


    public SendPlaybackStatistics(AudioPlaybackStatisticsData statisticsData){
        this.statisticsData = statisticsData;
    }

    @Override
    public Boolean inBackground() {
        try {
            User user = StorageUtil.get().currentUser();
            if(user!=null) {
                Audio.sendPlaybackStatistics(statisticsData, user.getAccessToken()).execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
