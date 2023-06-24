package com.miracle.engine.util;

import android.view.View;

import androidx.annotation.NonNull;

import com.miracle.engine.R;

public class UIUtil {

    public static final int THEME_BLUE = 0;
    public static final int THEME_EMERALD = 1;
    public static final int THEME_VIOLET = 2;
    public static final int THEME_ORANGE = 3;
    public static final int THEME_CRIMSON = 4;
    public static final int THEME_CARROT = 5;
    public static final int THEME_NIGHT = 6;
    public static final int THEME_SAND = 7;
    public static final int THEME_ULTRAMARINE = 8;
    public static final int THEME_MONO = 9;
    public static final int THEME_EVA01 = 10;
    public static final int THEME_EVA02 = 11;
    public static final int THEME_SYSTEM1 = 12;

    public static final int[] THEME_RECOURSE_IDS = new int[]{R.style.BlueTheme, R.style.EmeraldTheme, R.style.VioletTheme,
            R.style.OrangeTheme, R.style.CrimsonTheme, R.style.CarrotTheme, R.style.NightTheme, R.style.SandTheme,
            R.style.UltramarineTheme, R.style.MonoTheme, R.style.EVA01Theme, R.style.EVA02Theme, R.style.Sys1Theme};

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