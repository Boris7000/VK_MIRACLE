package com.vkontakte.miracle.engine.util;

import android.util.ArrayMap;
import android.util.Log;

import java.util.concurrent.ThreadLocalRandom;

public class LargeDataStorage {

    private final ArrayMap<String,Object> sparseArray = new ArrayMap<>();
    private static LargeDataStorage instance;

    public LargeDataStorage(){
        instance = this;
    }

    public static LargeDataStorage getInstance(){
        return new LargeDataStorage();
    }

    public String storeLargeData(Object data, String key){
        sparseArray.put(key,data);
        return key;
    }

    public String storeLargeData(Object data){
        String key = createUniqueKey();
        sparseArray.put(key,data);
        Log.d("eihieigheihgihg",data.toString()+" added, size = "+sparseArray.entrySet().size());
        return key;
    }

    public Object getLargeData(String key){
        Object object = sparseArray.get(key);
        removeLargeData(key);
        return object;
    }

    public void removeLargeData(String key){
        sparseArray.remove(key);
        Log.d("eihieigheihgihg","removed, size = "+sparseArray.entrySet().size());
    }

    public String createUniqueKey(){

        StringBuilder key = new StringBuilder();

        for (int i=0; i<6; i++) key.append(ThreadLocalRandom.current().nextInt(0, 9 + 1));

        String key1 = key.toString();

        if(sparseArray.get(key1)==null) return key1;
        else return createUniqueKey();
    }

    public static LargeDataStorage get(){
        if (null == instance){
            instance = LargeDataStorage.getInstance();
        }
        return instance;
    }

}
