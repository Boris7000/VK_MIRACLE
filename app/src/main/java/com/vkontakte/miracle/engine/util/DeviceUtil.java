package com.vkontakte.miracle.engine.util;

import static android.content.Context.WINDOW_SERVICE;
import static com.vkontakte.miracle.network.Constants.app_version_code;
import static com.vkontakte.miracle.network.Constants.app_version_name;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

public class DeviceUtil {
    public static String getUserAgent(){
        return String.format(Locale.US,
                "VKAndroidApp/%s-%s (Android %s; SDK %d; %s; %s; %s; %s)",
                app_version_name,
                app_version_code,
                Build.VERSION.RELEASE,
                Build.VERSION.SDK_INT,
                Build.SUPPORTED_ABIS[0],
                getDeviceName(),
                System.getProperty("user.language"),
                SCREEN_RESOLUTION());
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

    public static String SCREEN_RESOLUTION() {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        if (metrics == null) {
            return "1920x1080";
        }
        return metrics.heightPixels + "x" + metrics.widthPixels;
    }

    public static int getDisplayHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int getWindowHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getWindowWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int[] getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getRealMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }
    public static int[] getWindowSize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        return new int[]{outMetrics.widthPixels, outMetrics.heightPixels};
    }

    public boolean showNavigationBar(Resources resources)
    {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }


    public static int getStatusBarHeight(Window window){
        Rect rectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    private int getNavigationBarHeight(WindowManager wm) {
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        wm.getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

}
