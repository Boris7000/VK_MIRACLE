package com.miracle.engine.view;

import static androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.miracle.engine.R;

public class VP2NestedRecyclerView extends RecyclerView {

    private final int touchSlop;
    private float initialX = 0f;
    private float initialY = 0f;
    private boolean scrolled = false;
    private ViewPager2 viewPager2;

    private final int orientation;

    public VP2NestedRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public VP2NestedRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null){
            @SuppressLint("CustomViewStyleable")
            final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerView, 0, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveAttributeDataForStyleable(context, R.styleable.RecyclerView, attrs, a, 0, 0);
            }

            orientation = a.getInt(R.styleable.RecyclerView_android_orientation, 0);

        } else {
            orientation = RecyclerView.VERTICAL;
        }

        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();


        if(orientation==RecyclerView.HORIZONTAL) {
            addOnItemTouchListener(new SimpleOnItemTouchListener() {
                @Override
                public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent ev) {
                    return handleChild(ev);
                }
            });
        }
    }


    private boolean handleChild(MotionEvent ev){
        if (viewPager2 == null) {
            findViewPager2();
            if(viewPager2==null){
                return false;
            }
        }

        int orientation = viewPager2.getOrientation();

        if (!canScroll(orientation, -1f) && !canScroll(orientation, 1f)) {
            return true;
        }
        if (ev.getAction() == MotionEvent.ACTION_UP||ev.getAction() == MotionEvent.ACTION_CANCEL) {
            scrolled = false;
            viewPager2.setUserInputEnabled(true);
            getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            initialX = ev.getX();
            initialY = ev.getY();
            getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (!scrolled) {
                float dx = ev.getX() - initialX;
                float dy = ev.getY() - initialY;
                boolean isVpHorizontal = orientation == ORIENTATION_HORIZONTAL;

                float absDX = Math.abs(dx);
                float absDY = Math.abs(dy);
                // assuming ViewPager2 touch-slop is 2x touch-slop of child
                float scaledDx = absDX * (isVpHorizontal ? .7f : 1f);
                float scaledDy = absDY * (isVpHorizontal ? 1f : .7f);

                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (isVpHorizontal == (scaledDy > scaledDx)) {// Gesture is perpendicular
                        scrolled = true;
                        viewPager2.setUserInputEnabled(false);
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false;
                    } else {// Gesture is parallel
                        boolean canScroll = canScroll(orientation, isVpHorizontal ? dx : dy);
                        scrolled = canScroll;
                        viewPager2.setUserInputEnabled(!canScroll);
                        getParent().requestDisallowInterceptTouchEvent(canScroll);
                        return !canScroll;
                    }
                }
            }
        }
        return false;
    }

    private boolean canScroll(int orientation, float delta){
        int direction = delta>0?-1:1;

        switch (orientation){
            case 0:{
                return canScrollHorizontally(direction);
            }
            case 1:{
                return canScrollVertically(direction);
            }
        }
        return false;
    }

    private void findViewPager2(){
        ViewParent viewParent = getParent();

        while (!(viewParent instanceof ViewPager2)&&viewParent!=null) {
            viewParent = viewParent.getParent();
        }
        viewPager2 = (ViewPager2) viewParent;
    }

    private void handle(MotionEvent ev){

        if (viewPager2 == null) {
            findViewPager2();
            if(viewPager2==null){
                return;
            }
        }

        int orientation = viewPager2.getOrientation();

        if(this.orientation==RecyclerView.HORIZONTAL) {

            if (!canScroll(this.orientation, -1f) && !canScroll(this.orientation, 1f)) {
                return;
            }
            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                scrolled = false;
                viewPager2.setUserInputEnabled(true);
                getParent().requestDisallowInterceptTouchEvent(false);
            } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                initialX = ev.getX();
                initialY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = ev.getX() - initialX;
                float dy = ev.getY() - initialY;
                boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

                float absDX = Math.abs(dx);
                float absDY = Math.abs(dy);
                // assuming ViewPager2 touch-slop is 2x touch-slop of child
                float scaledDx = absDX * (isVpHorizontal ? .5f : 1);
                float scaledDy = absDY * (isVpHorizontal ? 1 : .5f);

                if (scaledDx > touchSlop || scaledDy > touchSlop) {
                    if (isVpHorizontal == (scaledDy > scaledDx)) {// Gesture is perpendicular
                        scrolled = true;
                        viewPager2.setUserInputEnabled(false);
                        getParent().requestDisallowInterceptTouchEvent(false);
                    } else {// Gesture is parallel
                        boolean canScroll = canScroll(orientation, isVpHorizontal ? dx : dy);
                        scrolled = canScroll;
                        viewPager2.setUserInputEnabled(!canScroll);
                        getParent().requestDisallowInterceptTouchEvent(canScroll);
                    }
                }
            }
        } else {

            if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
                scrolled = false;
                getParent().requestDisallowInterceptTouchEvent(false);
            } else if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                initialX = ev.getX();
                initialY = ev.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
            } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                if(!scrolled) {
                    float dx = ev.getX() - initialX;
                    float dy = ev.getY() - initialY;
                    boolean isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL;

                    float absDX = Math.abs(dx);
                    float absDY = Math.abs(dy);
                    // assuming ViewPager2 touch-slop is 2x touch-slop of child
                    float scaledDx = absDX * (isVpHorizontal ? .5f : 1f);
                    float scaledDy = absDY * (isVpHorizontal ? 1f : .5f);

                    if (scaledDx > touchSlop || scaledDy > touchSlop) {
                        if (isVpHorizontal == (scaledDy > scaledDx)) {// Gesture is perpendicular
                            scrolled = true;
                            getParent().requestDisallowInterceptTouchEvent(true);
                        } else {// Gesture is parallel
                            scrolled = false;
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        handle(e);
        return super.onTouchEvent(e);
    }

}



