package com.vkontakte.miracle.engine.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LockableSheetBehavior<V extends View> extends BottomSheetBehavior<V> {

    boolean scrollEnabled = true;

    public void setScrollEnabled(boolean scrollEnabled) {
        this.scrollEnabled = scrollEnabled;
    }

    public boolean scrollEnabled() {
        return scrollEnabled;
    }

    public LockableSheetBehavior() {
        super();
    }

    public LockableSheetBehavior(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull MotionEvent event) {
        if(scrollEnabled) {
            return super.onInterceptTouchEvent(parent, child, event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull V child, @NonNull MotionEvent event) {
        if(scrollEnabled) {
            return super.onTouchEvent(parent, child, event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        if(scrollEnabled) {
            return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type);
        } else {
            return false;
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
         if(scrollEnabled) {
             super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
         }
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int type) {
        if(scrollEnabled) {
            super.onStopNestedScroll(coordinatorLayout, child, target, type);
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        if(scrollEnabled) {
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull V child, @NonNull View target, float velocityX, float velocityY) {
        if(scrollEnabled) {
            return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
        } else {
            return false;
        }
    }
}
