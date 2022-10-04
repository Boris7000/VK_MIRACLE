package com.vkontakte.miracle.fragment.base;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.side.SideListFragment;

public class FragmentTest extends SideListFragment {

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        EditText editText = rootView.findViewById(R.id.et);

        Resources.Theme theme = getMiracleActivity().getTheme();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int color = (getColorByAttributeId(theme, R.attr.colorPrimary_10));
            String c10 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_20));
            String c20 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_25));
            String c25 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_30));
            String c30 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_35));
            String c35 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_40));
            String c40 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_50));
            String c50 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_60));
            String c60 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_70));
            String c70 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_80));
            String c80 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_90));
            String c90 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_95));
            String c95 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_98));
            String c98 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorPrimary_99));
            String c99 = String.format("#%06X", (0xFFFFFF & color));

            color = (getColorByAttributeId(theme, R.attr.colorSecondary_10));
            String cs10 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_20));
            String cs20 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_25));
            String cs25 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_30));
            String cs30 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_35));
            String cs35 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_40));
            String cs40 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_50));
            String cs50 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_60));
            String cs60 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_70));
            String cs70 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_80));
            String cs80 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_90));
            String cs90 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_95));
            String cs95 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_98));
            String cs98 = String.format("#%06X", (0xFFFFFF & color));
            color = (getColorByAttributeId(theme, R.attr.colorSecondary_99));
            String cs99 = String.format("#%06X", (0xFFFFFF & color));

            editText.setText("" +
                "<color name=\"_10\">"+c10+"</color>\n" +
                "    <color name=\"_20\">"+c20+"</color>\n" +
                "    <color name=\"_25\">"+c25+"</color>\n" +
                "    <color name=\"_30\">"+c30+"</color>\n" +
                "    <color name=\"_35\">"+c35+"</color>\n" +
                "    <color name=\"_40\">"+c40+"</color>\n" +
                "    <color name=\"_50\">"+c50+"</color>\n" +
                "    <color name=\"_60\">"+c60+"</color>\n" +
                "    <color name=\"_70\">"+c70+"</color>\n" +
                "    <color name=\"_80\">"+c80+"</color>\n" +
                "    <color name=\"_90\">"+c90+"</color>\n" +
                "    <color name=\"_95\">"+c95+"</color>\n" +
                "    <color name=\"_98\">"+c98+"</color>\n" +
                "    <color name=\"_99\">"+c99+"</color>\n" +
                "    <color name=\"_secondary_10\">"+cs10+"</color>\n" +
                "    <color name=\"_secondary_20\">"+cs20+"</color>\n" +
                "    <color name=\"_secondary_25\">"+cs25+"</color>\n" +
                "    <color name=\"_secondary_30\">"+cs30+"</color>\n" +
                "    <color name=\"_secondary_35\">"+cs35+"</color>\n" +
                "    <color name=\"_secondary_40\">"+cs40+"</color>\n" +
                "    <color name=\"_secondary_50\">"+cs50+"</color>\n" +
                "    <color name=\"_secondary_60\">"+cs60+"</color>\n" +
                "    <color name=\"_secondary_70\">"+cs70+"</color>\n" +
                "    <color name=\"_secondary_80\">"+cs80+"</color>\n" +
                "    <color name=\"_secondary_90\">"+cs90+"</color>\n" +
                "    <color name=\"_secondary_95\">"+cs95+"</color>\n" +
                "    <color name=\"_secondary_98\">"+cs98+"</color>\n" +
                "    <color name=\"_secondary_99\">"+cs99+"</color>");
        }
        return rootView;
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_test, container, false);
    }
}
