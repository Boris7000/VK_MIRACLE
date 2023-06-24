package com.vkontakte.miracle.engine.util;

import com.miracle.engine.async.AsyncExecutor;
import com.miracle.engine.async.ExecutorConveyor;

public class StatisticsController {

    private final ExecutorConveyor<Boolean> trackEvents = new ExecutorConveyor<>();

    private static StatisticsController instance;

    public StatisticsController(){
        instance = this;
    }

    public static StatisticsController getInstance(){
        return new StatisticsController();
    }

    public static StatisticsController get() {
        if (null == instance) {
            instance = StatisticsController.getInstance();
        }
        return instance;
    }

    public void sendStatistics(AsyncExecutor<Boolean> asyncExecutor){
        trackEvents.addAsyncExecutor(asyncExecutor);
    }

}
