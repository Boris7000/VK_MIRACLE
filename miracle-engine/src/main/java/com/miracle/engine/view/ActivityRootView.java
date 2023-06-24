package com.miracle.engine.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.miracle.engine.R;

public class ActivityRootView extends CoordinatorLayout {

    public static final int TYPE_PORTRAIT = 0;
    public static final int TYPE_LAND = 1;
    public static final int TYPE_TABLET = 2;

    public static final int STATE_CLEAR = 0;
    public static final int STATE_STANDARD = 1;


    private final int type;
    private int state = STATE_STANDARD;

    public ActivityRootView(@NonNull Context context) {
        this(context, null);
    }

    public ActivityRootView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivityRootView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ActivityRootView, 0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveAttributeDataForStyleable(context, R.styleable.ActivityRootView, attrs, a, 0, 0);
        }

        type = a.getInt(R.styleable.ActivityRootView_type, 0);

    }

    public int getType() {
        return type;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
