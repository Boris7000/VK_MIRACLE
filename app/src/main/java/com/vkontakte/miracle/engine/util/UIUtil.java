package com.vkontakte.miracle.engine.util;

import android.view.View;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;

public class UIUtil {

    public static int THEME_BLUE = 0;
    public static int THEME_EMERALD = 1;
    public static int THEME_VIOLET = 2;
    public static int THEME_ORANGE = 3;
    public static int THEME_CRIMSON = 4;
    public static int THEME_CARROT = 5;
    public static int THEME_CYBERPUNK = 6;
    public static int THEME_CONTRAST = 7;
    public static int THEME_STAR_NIGHT = 8;
    public static int THEME_FIJI = 9;
    public static int THEME_SAILOR = 10;
    public static int THEME_FOREST = 11;
    public static int THEME_SUNSET = 12;
    public static int THEME_NEON = 13;
    public static int THEME_SYSTEM1 = 14;
    public static int THEME_SYSTEM2 = 15;

    public static int[] THEME_RECOURSE_IDS = new int[]{R.style.BlueTheme, R.style.EmeraldTheme, R.style.VioletTheme,
            R.style.OrangeTheme, R.style.CrimsonTheme, R.style.CarrotTheme, R.style.CyberpunkTheme, R.style.ContrastTheme,
            R.style.StarNightTheme, R.style.FijiTheme, R.style.SailorTheme, R.style.ForestTheme,R.style.SunsetTheme,
            R.style.NeonTheme, R.style.Sys1Theme, R.style.Sys2Theme};

    public static int getThemeRecourseId(int themeId){
        return THEME_RECOURSE_IDS[themeId];
    }

    public static void setLightStatusBar(@NonNull View view) {
        int flags = view.getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
    }

    public static void clearLightStatusBar(@NonNull View view) {
        int flags = view.getSystemUiVisibility();
        flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        view.setSystemUiVisibility(flags);
    }
}
