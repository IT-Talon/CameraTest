package com.talon.camerademo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 003 on 2017-02-14.
 */
public class AppCache {
    public static final String DATA_KEY = "DataKey";

    private static Map<String, Object> dataMap;


    public static void addData(String key, Object data) {
        if (dataMap == null) {
            dataMap = new HashMap<>();
        }

        dataMap.put(key, data);
    }

    public static Object removeData(String key) {
        return dataMap == null ? null : dataMap.remove(key);
    }

}