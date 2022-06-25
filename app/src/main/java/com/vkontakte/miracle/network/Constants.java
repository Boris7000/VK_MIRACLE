package com.vkontakte.miracle.network;

import static com.vkontakte.miracle.engine.util.DeviceUtil.getDeviceName;
import static com.vkontakte.miracle.engine.util.DeviceUtil.getUserAgent;

import android.os.Build;

import java.util.HashMap;

public class Constants {

    public static final String app_version_code = "12338";
    public static final String app_version_name = "7.26";
    public static final Integer client_id = 2274003;
    public static final String client_secret = "hHbZxrka2uZ6jB1inYsH";
    public static final String sig = "b238fdd4d2681fe69dddffd9eb1000cb";
    public static final String fake_receipt = "xTdgMap-WI%3AAPA91bHrZ0atGrdMY25OP_KfQtVlWHySYYoHjovrukLsRu-wpLMTJVwc4NThRwXkAdSTmKY_594D8s7LDwV28HWjMnkzfz37OkPFZkK2pthd1fyU3iewwakN5mISTno3BLHAntpKKKw5";

    public static final String latest_api_v = "5.183";
    public static final String current_api_v = "5.131";

    public static final String base_fields ="photo_100,photo_200";

    public static final String groups_min_fields = base_fields + ",screen_name,activity,verified";
    public static final String groups_all_fields = groups_min_fields +
            ",status,photo_max,description,counters,cover,links,members_count,contacts";

    public static final String users_min_fields = base_fields + ",screen_name,verified,online,last_seen,friend_status";
    public static final String users_all_fields = users_min_fields +
            ",status,photo_max,bdate,counters,home_town,sex";

    public static final String wall_fields = "video_files,photo_100,photo_200,sex,verified";
    public static final String message_fields = wall_fields+",online,last_seen";

    public static HashMap<String,String> defaultLoginHeaders(){
        HashMap<String, String> map = new HashMap<>();
        map.put("User-Agent", getUserAgent());
        return map;
    }

    public static HashMap<String, Object> defaultWallFields(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("extended", 1);
        map.put("filters", "post");
        map.put("fields", wall_fields);
        return map;
    }

    public static HashMap<String, Object> defaultMessagesFields(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("extended", 1);
        map.put("fields", message_fields);
        return map;
    }

    public static HashMap<String, Object> defaultLoginFields(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("grant_type", "password");
        map.put("client_id", client_id);
        map.put("client_secret", client_secret);
        map.put("scope", "all");
        map.put("2fa_supported", 1);
        map.put("libverify_support", 0);
        map.put("v", latest_api_v);
        return map;
    }

    public static HashMap<String, Object> defaultValidatePhoneFields(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("api_id", client_id);
        map.put("client_id", client_id);
        map.put("client_secret", client_secret);
        map.put("v", latest_api_v);
        return map;
    }

    public static HashMap<String, Object> defaultTokenRefreshFields2(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("v", latest_api_v);
        return map;
    }

    public static HashMap<String, Object> defaultTokenRefreshFields(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("client_id", client_id);
        map.put("client_secret", client_secret);
        map.put("https", 1);
        map.put("lang", System.getProperty("user.language"));
        map.put("sig", sig);
        map.put("v", latest_api_v);
        return map;
    }

    public static HashMap<String, Object> defaultRegisterDeviceFields(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_version", app_version_code);
        map.put("push_provider", "fcm");
        map.put("companion_apps", "vk_client");
        map.put("type", 4);
        map.put("device_model", getDeviceName());
        map.put("system_version", Build.VERSION.RELEASE);
        map.put("v", latest_api_v);
        return map;
    }
}
