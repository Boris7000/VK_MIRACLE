package com.vkontakte.miracle.engine.util;

import android.util.ArrayMap;

import java.util.concurrent.ThreadLocalRandom;

public class LargeDataStorage {

    private final ArrayMap<String,Object> sparseArray = new ArrayMap<>();

    public String storeLargeData(Object data, String key){
        sparseArray.put(key,data);
        return key;
    }

    public Object getLargeData(String key){
        Object controller = sparseArray.get(key);
        sparseArray.remove(key);
        return controller;
    }

    public String createUniqueKey(){

        StringBuilder key = new StringBuilder();

        for (int i=0; i<6; i++) key.append(ThreadLocalRandom.current().nextInt(0, 9 + 1));

        String key1 = key.toString();

        if(sparseArray.get(key1)==null) return key1;
        else return createUniqueKey();
    }
}
