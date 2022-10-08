package com.vkontakte.miracle.fragment.settings;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideListFragment;

import java.util.Locale;

public class FragmentThemeExtractor extends SideListFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        EditText editText = rootView.findViewById(R.id.et);

        Context context = getContext();
        if(context!=null) {
            Resources.Theme theme = context.getTheme();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

                int c10 = (getColorByAttributeId(theme, R.attr.colorPrimary_10));
                int c20 = (getColorByAttributeId(theme, R.attr.colorPrimary_20));
                int c25 = (getColorByAttributeId(theme, R.attr.colorPrimary_25));
                int c30 = (getColorByAttributeId(theme, R.attr.colorPrimary_30));
                int c35 = (getColorByAttributeId(theme, R.attr.colorPrimary_35));
                int c40 = (getColorByAttributeId(theme, R.attr.colorPrimary_40));
                int c50 = (getColorByAttributeId(theme, R.attr.colorPrimary_50));
                int c60 = (getColorByAttributeId(theme, R.attr.colorPrimary_60));
                int c70 = (getColorByAttributeId(theme, R.attr.colorPrimary_70));
                int c80 = (getColorByAttributeId(theme, R.attr.colorPrimary_80));
                int c90 = (getColorByAttributeId(theme, R.attr.colorPrimary_90));
                int c95 = (getColorByAttributeId(theme, R.attr.colorPrimary_95));
                int c98 = (getColorByAttributeId(theme, R.attr.colorPrimary_98));
                int c99 = (getColorByAttributeId(theme, R.attr.colorPrimary_99));

                int cs10 = (getColorByAttributeId(theme, R.attr.colorSecondary_10));
                int cs20 = (getColorByAttributeId(theme, R.attr.colorSecondary_20));
                int cs25 = (getColorByAttributeId(theme, R.attr.colorSecondary_25));
                int cs30 = (getColorByAttributeId(theme, R.attr.colorSecondary_30));
                int cs35 = (getColorByAttributeId(theme, R.attr.colorSecondary_35));
                int cs40 = (getColorByAttributeId(theme, R.attr.colorSecondary_40));
                int cs50 = (getColorByAttributeId(theme, R.attr.colorSecondary_50));
                int cs60 = (getColorByAttributeId(theme, R.attr.colorSecondary_60));
                int cs70 = (getColorByAttributeId(theme, R.attr.colorSecondary_70));
                int cs80 = (getColorByAttributeId(theme, R.attr.colorSecondary_80));
                int cs90 = (getColorByAttributeId(theme, R.attr.colorSecondary_90));
                int cs95 = (getColorByAttributeId(theme, R.attr.colorSecondary_95));
                int cs98 = (getColorByAttributeId(theme, R.attr.colorSecondary_98));
                int cs99 = (getColorByAttributeId(theme, R.attr.colorSecondary_99));

                String string = String.format(Locale.getDefault(),
                        "<color name=\"_10\">#%06X</color>\n" +
                        "    <color name=\"_20\">#%06X</color>\n" +
                        "    <color name=\"_25\">#%06X</color>\n" +
                        "    <color name=\"_30\">#%06X</color>\n" +
                        "    <color name=\"_35\">#%06X</color>\n" +
                        "    <color name=\"_40\">#%06X</color>\n" +
                        "    <color name=\"_50\">#%06X</color>\n" +
                        "    <color name=\"_60\">#%06X</color>\n" +
                        "    <color name=\"_70\">#%06X</color>\n" +
                        "    <color name=\"_80\">#%06X</color>\n" +
                        "    <color name=\"_90\">#%06X</color>\n" +
                        "    <color name=\"_95\">#%06X</color>\n" +
                        "    <color name=\"_98\">#%06X</color>\n" +
                        "    <color name=\"_99\">#%06X</color>\n" +
                        "    <color name=\"_secondary_10\">#%06X</color>\n" +
                        "    <color name=\"_secondary_20\">#%06X</color>\n" +
                        "    <color name=\"_secondary_25\">#%06X</color>\n" +
                        "    <color name=\"_secondary_30\">#%06X</color>\n" +
                        "    <color name=\"_secondary_35\">#%06X</color>\n" +
                        "    <color name=\"_secondary_40\">#%06X</color>\n" +
                        "    <color name=\"_secondary_50\">#%06X</color>\n" +
                        "    <color name=\"_secondary_60\">#%06X</color>\n" +
                        "    <color name=\"_secondary_70\">#%06X</color>\n" +
                        "    <color name=\"_secondary_80\">#%06X</color>\n" +
                        "    <color name=\"_secondary_90\">#%06X</color>\n" +
                        "    <color name=\"_secondary_95\">#%06X</color>\n" +
                        "    <color name=\"_secondary_98\">#%06X</color>\n" +
                        "    <color name=\"_secondary_99\">#%06X</color>",
                        c10, c20,c25, c30, c35, c40, c50, c60, c70, c80, c90, c95, c98, c99,
                        cs10, cs20, cs25, cs30, cs35, cs40, cs50, cs60, cs70, cs80, cs90, cs95, cs98, cs99);

                editText.setText(string);
            }
        }
        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_theme_extractor, container, false);
    }
}
