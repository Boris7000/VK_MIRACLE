package com.vkontakte.miracle.engine.view;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

public class NestedScrollableHost extends FrameLayout {

    private final int touchSlop;
    private float initialX = 0f;
    private float initialY = 0f;
    private ViewPager2 viewPager2;
    private View child;

    public NestedScrollableHost(@NonNull Context context) {
        this(context, null);
    }

    public NestedScrollableHost(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private void findChild(){
        if(getChildCount()>0){
            child = getChildAt(0);
        } else {
            child = null;
        }
    }

    private void findViewPager2(){
        ViewParent viewParent = getParent();

        while (!(viewParent instanceof ViewPager2)&&viewParent!=null) {
            viewParent = viewParent.getParent();
        }
        viewPager2 = (ViewPager2) viewParent;
    }

    private boolean canChildScroll(Integer orientation, Float delta) {

        if (child == null) {
            findChild();
            if(child==null){
                return false;
            }
        }

        int direction = delta>0?-1:1;
        switch (orientation){
            case 0:{
                return child.canScrollHorizontally(direction);
            }
            case 1:{
                return child.canScrollVertically(direction);
            }
        }
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        handleInterceptTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }


    private void handleInterceptTouchEvent(MotionEvent ev){

        if (viewPager2 == null) {
            findViewPager2();
            if(viewPager2==null){
                return;
            }
        }

        int orientation = viewPager2.getOrientation();

        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return;
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = ev.getX();
            initialY = ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = ev.getX() - initialX;
            float dy = ev.getY() - initialY;
            boolean isVpHorizontal = orientation == ORIENTATION_HORIZONTAL;

            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            float scaledDx = Math.abs(dx) * (isVpHorizontal ? .5f : 1f);
            float scaledDy = Math.abs(dy) * (isVpHorizontal ? 1f : .5f);

            if (scaledDx > touchSlop || scaledDy > touchSlop) {
                if (isVpHorizontal == (scaledDy > scaledDx)) {
                    // Gesture is perpendicular, allow all parents to intercept
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    // Child can scroll, disallow all parents to intercept
                    // Child cannot scroll, allow all parents to intercept
                    getParent().requestDisallowInterceptTouchEvent(canChildScroll(orientation, isVpHorizontal ? dx : dy));
                }
            }
        }

    }

}
