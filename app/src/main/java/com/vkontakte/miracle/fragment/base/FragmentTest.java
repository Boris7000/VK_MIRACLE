package com.vkontakte.miracle.fragment.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.engine.view.radioGroup.MiracleRadioGroup;

public class FragmentTest extends SimpleMiracleFragment {

    private View rootView;

    private View mouseView;
    private ViewGroup currentOwner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_test, container, false);

        setAppBarLayout(rootView.findViewById(R.id.appbarlayout));
        setToolBar(getAppBarLayout().findViewById(R.id.toolbar));
        setAppbarClickToTop();
        setBackClick();

        LinearLayout linearLayout = rootView.findViewById(R.id.layout);

        mouseView = inflater.inflate(R.layout.view_photo_grid_item, linearLayout, false);


        MiracleRadioGroup radioGroup1 = rootView.findViewById(R.id.light_r);

        radioGroup1.setOnRadioCheckedListener(radioView -> {
            if(currentOwner!=null) {
                currentOwner.removeView(mouseView);
            }
            currentOwner = radioView;
            currentOwner.addView(mouseView);
        });

        return rootView;
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentTest();
        }
    }
}
