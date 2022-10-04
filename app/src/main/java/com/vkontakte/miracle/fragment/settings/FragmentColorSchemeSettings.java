package com.vkontakte.miracle.fragment.settings;

import static com.vkontakte.miracle.engine.util.UIUtil.THEME_BLUE;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_CARROT;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_CONTRAST;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_CRIMSON;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_EMERALD;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_EVA01;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_EVA02;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_ORANGE;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_SYSTEM1;
import static com.vkontakte.miracle.engine.util.UIUtil.THEME_VIOLET;

import android.os.Bundle;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.MiracleApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideListFragment;
import com.vkontakte.miracle.engine.util.SettingsUtil;
import com.vkontakte.miracle.engine.view.radioGroup.MiracleRadioGroup;

public class FragmentColorSchemeSettings extends SideListFragment {

    private MainActivity mainActivity;
    private LinearLayout blockScreen;
    private final ArrayMap<Integer, Integer> themes = new ArrayMap<>();
    private final ArrayMap<Integer, Integer> buttons = new ArrayMap<>();

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        themes.put(R.id.rb_blue, THEME_BLUE);
        themes.put(R.id.rb_emerald, THEME_EMERALD);
        themes.put(R.id.rb_orange, THEME_ORANGE);
        themes.put(R.id.rb_crimson, THEME_CRIMSON);
        themes.put(R.id.rb_violet, THEME_VIOLET);
        themes.put(R.id.rb_carrot, THEME_CARROT);
        themes.put(R.id.rb_eva01, THEME_EVA01);
        themes.put(R.id.rb_eva02, THEME_EVA02);
        themes.put(R.id.rb_contrast, THEME_CONTRAST);
        themes.put(R.id.rb_system1, THEME_SYSTEM1);

        buttons.put(THEME_BLUE, R.id.rb_blue);
        buttons.put(THEME_EMERALD, R.id.rb_emerald);
        buttons.put(THEME_ORANGE, R.id.rb_orange);
        buttons.put(THEME_CRIMSON, R.id.rb_crimson);
        buttons.put(THEME_VIOLET, R.id.rb_violet);
        buttons.put(THEME_CARROT, R.id.rb_carrot);
        buttons.put(THEME_EVA01, R.id.rb_eva01);
        buttons.put(THEME_EVA02, R.id.rb_eva02);
        buttons.put(THEME_CONTRAST, R.id.rb_contrast);
        buttons.put(THEME_SYSTEM1, R.id.rb_system1);

        mainActivity = getMiracleActivity();

        if(savedInstanceState!=null){
            block();
        }

        MiracleRadioGroup radioGroup = rootView.findViewById(R.id.light_r);

        Integer rbId = buttons.get(SettingsUtil.get().themeId());
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
            MiracleApp.getInstance().updateThemeRecourseId(themeId);
            mainActivity.recreate();
        });

        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_settings_color_scheme, container, false);
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        blockScreen = rootView.findViewById(R.id.fragmentBlockScreen);
    }

    private void unblock(){blockScreen.setClickable(false);}

    private void block(){blockScreen.setClickable(true);}

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        unblock();
    }

}
