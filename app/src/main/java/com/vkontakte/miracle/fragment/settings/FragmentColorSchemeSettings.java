package com.vkontakte.miracle.fragment.settings;

import static com.miracle.engine.util.UIUtil.THEME_BLUE;
import static com.miracle.engine.util.UIUtil.THEME_CARROT;
import static com.miracle.engine.util.UIUtil.THEME_MONO;
import static com.miracle.engine.util.UIUtil.THEME_CRIMSON;
import static com.miracle.engine.util.UIUtil.THEME_EMERALD;
import static com.miracle.engine.util.UIUtil.THEME_EVA01;
import static com.miracle.engine.util.UIUtil.THEME_EVA02;
import static com.miracle.engine.util.UIUtil.THEME_NIGHT;
import static com.miracle.engine.util.UIUtil.THEME_ORANGE;
import static com.miracle.engine.util.UIUtil.THEME_SAND;
import static com.miracle.engine.util.UIUtil.THEME_SYSTEM1;
import static com.miracle.engine.util.UIUtil.THEME_ULTRAMARINE;
import static com.miracle.engine.util.UIUtil.THEME_VIOLET;

import android.app.Activity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.miracle.engine.fragment.base.templates.BaseListFragment;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.view.radioGroup.MiracleRadioGroup;

public class FragmentColorSchemeSettings extends BaseListFragment {

    private LinearLayout blockScreen;
    private MiracleRadioGroup radioGroup;
    private final ArrayMap<Integer, Integer> themes = new ArrayMap<>();
    private final ArrayMap<Integer, Integer> buttons = new ArrayMap<>();

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings_color_scheme, container, false);
    }

    @Override
    public void inflateContent(@NonNull LayoutInflater inflater, @NonNull LinearLayout container) {
        inflater.inflate(R.layout.fragment_content_settings_color_scheme, container, true);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        blockScreen = rootView.findViewById(R.id.fragmentBlockScreen);
        radioGroup = rootView.findViewById(R.id.theme_rg);
    }

    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        themes.put(R.id.rb_blue, THEME_BLUE);
        themes.put(R.id.rb_emerald, THEME_EMERALD);
        themes.put(R.id.rb_orange, THEME_ORANGE);
        themes.put(R.id.rb_crimson, THEME_CRIMSON);
        themes.put(R.id.rb_violet, THEME_VIOLET);
        themes.put(R.id.rb_carrot, THEME_CARROT);
        themes.put(R.id.rb_night, THEME_NIGHT);
        themes.put(R.id.rb_sand, THEME_SAND);
        themes.put(R.id.rb_ultramarine, THEME_ULTRAMARINE);
        themes.put(R.id.rb_mono, THEME_MONO);
        themes.put(R.id.rb_eva01, THEME_EVA01);
        themes.put(R.id.rb_eva02, THEME_EVA02);
        themes.put(R.id.rb_system1, THEME_SYSTEM1);

        buttons.put(THEME_BLUE, R.id.rb_blue);
        buttons.put(THEME_EMERALD, R.id.rb_emerald);
        buttons.put(THEME_ORANGE, R.id.rb_orange);
        buttons.put(THEME_CRIMSON, R.id.rb_crimson);
        buttons.put(THEME_VIOLET, R.id.rb_violet);
        buttons.put(THEME_CARROT, R.id.rb_carrot);
        buttons.put(THEME_NIGHT, R.id.rb_night);
        buttons.put(THEME_SAND, R.id.rb_sand);
        buttons.put(THEME_ULTRAMARINE, R.id.rb_ultramarine);
        buttons.put(THEME_MONO, R.id.rb_mono);
        buttons.put(THEME_EVA01, R.id.rb_eva01);
        buttons.put(THEME_EVA02, R.id.rb_eva02);
        buttons.put(THEME_SYSTEM1, R.id.rb_system1);

        if(savedInstanceState!=null){
            block();
        }

        Integer rbId = buttons.get(MainApp.getInstance().getThemeId());
        if(rbId==null){
            rbId = R.id.rb_blue;
        }
        radioGroup.setCheckedId(rbId);


        radioGroup.setOnRadioCheckedListener(radioView -> {
            radioGroup.setOnRadioCheckedListener(null);
            block();
            Integer themeId = themes.get(radioView.getId());
            if (themeId == null) {
                themeId = THEME_BLUE;
            }
            MainApp.getInstance().updateThemeRecourseId(themeId);
            Activity activity = getActivity();
            if(activity!=null) {
                activity.recreate();
            }
        });

    }

    @Override
    public int requestTitleTextResId() {
        return R.string.settings_color_scheme;
    }

    private void unblock(){blockScreen.setClickable(false);}

    private void block(){blockScreen.setClickable(true);}

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        unblock();
    }

}
