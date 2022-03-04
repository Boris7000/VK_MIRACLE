package com.vkontakte.miracle.engine.view.bottomNavigation;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class SimpleMiracleBottomNavigationItem extends AppCompatImageView implements MiracleBottomNavigationItem {


    public SimpleMiracleBottomNavigationItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void select(int color) {
        setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void unselect(int color) {
        setColorFilter(color, PorterDuff.Mode.SRC_IN);
    }
}
