package com.vkontakte.miracle.engine.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;

import java.util.Locale;
import java.util.Objects;

import static com.vkontakte.miracle.network.Constants.app_version_code;
import static com.vkontakte.miracle.network.Constants.app_version_name;

public class DeviceUtil {
    public static String getUserAgent(){
        return String.format(Locale.US, "VKAndroidApp/%s-%s (Android %s; SDK %d; %s; %s; %s; %s)", app_version_name, app_version_code, Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Build.SUPPORTED_ABIS[0], getDeviceName(), "ru", SCREEN_RESOLUTION());
  }

    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private static String SCREEN_RESOLUTION() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        if (metrics == null) {
            return "1920x1080";
        }
        return metrics.heightPixels + "x" + metrics.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getStatusBarHeight(Context context){

        Resources resources = Objects.requireNonNull(context).getResources();

        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) { return resources.getDimensionPixelSize(resourceId); }
        else return 0;
    }

    public static int getNavigationBarHeight(Context context){

        Resources resources = Objects.requireNonNull(context).getResources();

        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        int resourceId1 = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (resourceId > 0) {
            if(resourceId1>0){
                if(!resources.getBoolean(resourceId1)) return 0;
            }
            return resources.getDimensionPixelSize(resourceId);
        }
        else return 0;
    }

}
