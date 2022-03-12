package com.vkontakte.miracle.fragment.settings;

import static com.vkontakte.miracle.engine.fragment.ScrollAndElevate.scrollAndElevate;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_BLUE;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_CARROT;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_CONTRAST;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_CRIMSON;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_CYBERPUNK;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_EMERALD;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_FIJI;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_FOREST;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_NEON;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_ORANGE;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_SAILOR;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_STAR_NIGHT;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_SUNSET;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_SYSTEM1;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_SYSTEM2;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_VIOLET;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vkontakte.miracle.MiracleActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.view.radioGroup.MiracleRadioGroup;

public class FragmentColorSchemeSettings extends SimpleMiracleFragment {
    private MiracleActivity miracleActivity;
    private final ArrayMap<Integer, Integer> themes = new ArrayMap<>();
    private final ArrayMap<Integer, Integer> buttons = new ArrayMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        themes.put(R.id.rb_blue, THEME_BLUE);
        themes.put(R.id.rb_emerald, THEME_EMERALD);
        themes.put(R.id.rb_orange, THEME_ORANGE);
        themes.put(R.id.rb_crimson, THEME_CRIMSON);
        themes.put(R.id.rb_violet, THEME_VIOLET);
        themes.put(R.id.rb_carrot, THEME_CARROT);
        themes.put(R.id.rb_cyberpunk, THEME_CYBERPUNK);
        themes.put(R.id.rb_contrast, THEME_CONTRAST);
        themes.put(R.id.rb_startNight, THEME_STAR_NIGHT);
        themes.put(R.id.rb_fuji, THEME_FIJI);
        themes.put(R.id.rb_forest, THEME_FOREST);
        themes.put(R.id.rb_sailor, THEME_SAILOR);
        themes.put(R.id.rb_sunset, THEME_SUNSET);
        themes.put(R.id.rb_neon, THEME_NEON);
        themes.put(R.id.rb_system1, THEME_SYSTEM1);
        themes.put(R.id.rb_system2, THEME_SYSTEM2);

        buttons.put(THEME_BLUE, R.id.rb_blue);
        buttons.put(THEME_EMERALD, R.id.rb_emerald);
        buttons.put(THEME_ORANGE, R.id.rb_orange);
        buttons.put(THEME_CRIMSON, R.id.rb_crimson);
        buttons.put(THEME_VIOLET, R.id.rb_violet);
        buttons.put(THEME_CARROT, R.id.rb_carrot);
        buttons.put(THEME_CYBERPUNK, R.id.rb_cyberpunk);
        buttons.put(THEME_CONTRAST, R.id.rb_contrast);
        buttons.put(THEME_STAR_NIGHT, R.id.rb_startNight);
        buttons.put(THEME_FIJI, R.id.rb_fuji);
        buttons.put(THEME_FOREST, R.id.rb_forest);
        buttons.put(THEME_SAILOR, R.id.rb_sailor);
        buttons.put(THEME_SUNSET, R.id.rb_sunset);
        buttons.put(THEME_NEON, R.id.rb_neon);
        buttons.put(THEME_SYSTEM1, R.id.rb_system1);
        buttons.put(THEME_SYSTEM2, R.id.rb_system2);

        iniContext();

        miracleActivity = getMiracleActivity();

        View rootView = inflater.inflate(R.layout.fragment_settings_color_scheme, container, false);

        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));
        setBackClick(rootView.findViewById(R.id.backButton));
        setScrollView(rootView.findViewById(R.id.scrollView));
        scrollAndElevate(getScrollView(),getAppBarLayout(),miracleActivity);


        MiracleRadioGroup radioGroup1 = rootView.findViewById(R.id.light_r);

        Integer rbId = buttons.get(SettingsUtil.get().themeId());
        if(rbId==null){
            rbId = R.id.rb_blue;
        }
        radioGroup1.setCheckedId(rbId);


        radioGroup1.setOnRadioCheckedListener(radioView -> {
            Integer themeId = themes.get(radioView.getId());
            if (themeId == null) {
                themeId = THEME_BLUE;
            }
            MiracleApp.getInstance().updateThemeRecourseId(themeId);
            miracleActivity.recreate();
        });

        return rootView;
    }

}
