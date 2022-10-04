package com.miracle.button;

import static com.miracle.button.Utils.adjustAlpha;
import static com.miracle.button.Utils.createRippleDrawable;
import static com.miracle.button.Utils.getColorByAttributeId;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class SwitchButton extends AppCompatImageView {

    private OnSwitchListener onSwitchListener;

    private static final int DEFAULT_ANIMATION_DURATION = 400;
    private static final float DASH_THICKNESS_PART = 1f / 12f;
    private static final float SIN_45 = (float) Math.sin(Math.toRadians(45));
    private static final float DEFAULT_RIPPLE_ALPHA = 0.27f;

    private final ArgbEvaluator colorEvaluator = new ArgbEvaluator();
    private ValueAnimator fractionAnimator;

    private Drawable icon;
    private Drawable background;

    private int dashThickness;
    private int dashLengthXProjection;
    private int dashLengthYProjection;
    private final int dashXStart;
    private final int dashYStart;

    @FloatRange(from = 0f, to = 1f)
    private float disabledAlpha;
    private final float strokeWidth;

    private int backgroundColor;
    private int disabledBackgroundColor;
    private int iconColor;
    private int disabledIconColor;
    private int strokeColor;
    private int disabledStrokeColor;
    private int rippleColor;
    private int disabledRippleColor;
    private int dashColor;
    @FloatRange(from = 0f, to = 1f)
    private float rippleAlpha = DEFAULT_RIPPLE_ALPHA;

    private final static int[][] states = new int[][] {new int[]{}};

    private long animationDuration;
    @FloatRange(from = 0f, to = 1f)
    private float fraction = 0f;
    private boolean dashEnabled;
    private boolean autoClickSwitch;
    private boolean enabled;
    private boolean saveColors;
    private boolean disableAutoRipple;

    @NonNull
    private final Path clipPath;
    @NonNull
    private final Paint dashPaint;
    @NonNull
    private final Point dashStart = new Point();
    @NonNull
    private final Point dashEnd = new Point();

    public SwitchButton(@NonNull Context context) {
        this(context, null);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources.Theme theme = context.getTheme();

        TypedArray array = theme.obtainStyledAttributes(attrs, R.styleable.SwitchButton, 0, 0);

        ShapeAppearanceModel shapeAppearanceModel;
        boolean disableAutoBackground;

        try {
            disableAutoBackground = array.getBoolean(R.styleable.SwitchButton_disable_auto_background, false);
            disableAutoRipple = array.getBoolean(R.styleable.SwitchButton_disable_auto_ripple, false);
            saveColors = array.getBoolean(R.styleable.SwitchButton_save_colors, false);
            animationDuration = array.getInteger(R.styleable.SwitchButton_animation_duration, DEFAULT_ANIMATION_DURATION);
            enabled = array.getBoolean(R.styleable.SwitchButton_android_enabled, true);
            //////////////////////////////////////////////////////////////////////////////////////////
            backgroundColor = array.getColor(R.styleable.SwitchButton_background_color, getColorByAttributeId(theme, R.attr.colorPrimary));
            disabledBackgroundColor = array.getColor(R.styleable.SwitchButton_disabled_background_color, backgroundColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            strokeColor = array.getColor(R.styleable.SwitchButton_stroke_color, backgroundColor);
            disabledStrokeColor = array.getColor(R.styleable.SwitchButton_disabled_stroke_color, strokeColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            iconColor = array.getColor(R.styleable.SwitchButton_icon_color, 0);
            disabledIconColor = array.getColor(R.styleable.SwitchButton_disabled_icon_color, iconColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            rippleColor = array.getColor(R.styleable.SwitchButton_ripple_color, 0);
            disabledRippleColor = array.getColor(R.styleable.SwitchButton_disabled_ripple_color, disabledIconColor);
            rippleAlpha = array.getFloat(R.styleable.SwitchButton_ripple_alpha, rippleAlpha);
            //////////////////////////////////////////////////////////////////////////////////////////
            dashColor = array.getColor(R.styleable.SwitchButton_dash_color, disabledIconColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            strokeWidth = array.getDimensionPixelSize(R.styleable.SwitchButton_stroke_width, 0);
            shapeAppearanceModel = ShapeAppearanceModel.builder(context, attrs, defStyleAttr, 0).build();
            dashEnabled = array.getBoolean(R.styleable.SwitchButton_dash_enabled, false);
            disabledAlpha = array.getFloat(R.styleable.SwitchButton_disabled_alpha, 1);
            autoClickSwitch = array.getBoolean(R.styleable.SwitchButton_auto_click_switch, true);
        } finally {
            array.recycle();
        }

        if (disabledAlpha < 0f || disabledAlpha > 1f) {
            throw new IllegalArgumentException("Wrong value for disabled_alpha [" + disabledAlpha + "]. "
                    + "Must be value from range [0, 1]");
        }

        if(autoClickSwitch){
            setOnClickListener(view -> {
                setChecked(!isChecked(), true);
                if (onSwitchListener != null) {
                    onSwitchListener.onSwitch(enabled);

                }
            });
        }

        if(rippleColor==0){
            if (iconColor == 0) {
                iconColor = getColorByAttributeId(theme, R.attr.colorOnPrimary);
                disabledIconColor = iconColor;
            }
            rippleColor = iconColor;
            disabledRippleColor = disabledIconColor;
        }

        if(getBackground() == null) {
            if(!disableAutoBackground) {
                MaterialShapeDrawable materialShapeDrawable =
                        new AlwaysStatefulMaterialShapeDrawable(shapeAppearanceModel);
                if (strokeWidth > 0) {
                    materialShapeDrawable.setStrokeWidth(strokeWidth);
                }
                setBackground(materialShapeDrawable);
            }
        }

        dashXStart = getPaddingLeft();
        dashYStart = getPaddingTop();
        dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setColor(dashColor);
        clipPath = new Path();
        initDashCoordinates();

        setFraction(enabled ? 0f : 1f);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dashEnabled) {
            drawDash(canvas);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                canvas.clipOutPath(clipPath);
            } else {
                canvas.clipPath(clipPath, Region.Op.XOR);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        dashLengthXProjection = width - getPaddingLeft() - getPaddingRight();
        dashLengthYProjection = height - getPaddingTop() - getPaddingBottom();
        dashThickness = (int) (DASH_THICKNESS_PART * (dashLengthXProjection + dashLengthYProjection) / 2f);
        dashPaint.setStrokeWidth(dashThickness);
        initDashCoordinates();
        updateClipPath();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        if(drawable!=null) {
            this.icon = DrawableCompat.wrap(drawable).mutate();
            updateIconColor(fraction);
            updateIconAlpha(fraction);
        } else {
            this.icon = null;
        }
        super.setImageDrawable(this.icon);
    }

    @Override
    public void setBackground(Drawable background) {
        if(background!=null) {
            if(disableAutoRipple){
                this.background = DrawableCompat.wrap(background).mutate();
            } else {
                if(background instanceof RippleDrawable){
                    this.background = DrawableCompat.wrap(background).mutate();
                } else {
                    if(background instanceof MaterialShapeDrawable){
                        this.background = createRippleDrawable(background, states, rippleColor);
                    } else {
                        Drawable content = DrawableCompat.wrap(background).mutate();
                        this.background = createRippleDrawable(content, states, rippleColor);
                    }
                }
            }
            super.setBackground(this.background);
            updateBackgroundColor(fraction);
        } else {
            super.setBackground(null);
        }
    }

    public boolean isChecked() {
        return enabled;
    }

    public void setChecked(boolean checked){
        setChecked(checked, false);
    }

    public void setChecked(boolean checked, boolean animate){
        this.enabled = checked;

        float newFraction;
        if (enabled) {
            newFraction = 0f;
        } else {
            newFraction = 1f;
        }
        if (animate) {
            animateToFraction(newFraction);
        } else {
            setFraction(newFraction);
        }
    }

    private void animateToFraction(float toFraction) {
        if(fractionAnimator!=null){
            if(fractionAnimator.isRunning()){
                fractionAnimator.removeAllUpdateListeners();
                fractionAnimator.end();
            }
        }
        fractionAnimator = ValueAnimator.ofFloat(fraction, toFraction);
        fractionAnimator.addUpdateListener(animation -> setFraction((float) animation.getAnimatedValue()));
        fractionAnimator.setInterpolator(new DecelerateInterpolator());
        fractionAnimator.setDuration(animationDuration);
        fractionAnimator.start();
    }

    private void setFraction(float fraction) {
        this.fraction = fraction;
        updateBackgroundColor(fraction);
        updateIconColor(fraction);
        updateIconAlpha(fraction);
        if(dashEnabled) {
            updateDashAlpha(fraction);
            updateClipPath();
        }
    }

    private void updateIconAlpha(float fraction){
        int alpha = (int) ((disabledAlpha + (1f - fraction) * (1f - disabledAlpha)) * 255);
        setImageAlpha(alpha);
    }

    private void updateDashAlpha(float fraction){
        int alpha = (int) ((disabledAlpha + (1f - fraction) * (1f - disabledAlpha)) * 255);
        dashPaint.setAlpha(alpha);
    }

    private void updateIconColor(float fraction){
        if(icon!=null){
            final int color;
            if (iconColor != disabledIconColor) {
                color = (int) colorEvaluator.evaluate(fraction, iconColor, disabledIconColor);
            } else {
                color = iconColor;
            }
            setDrawableColorFilter(color, icon);
        }
    }

    private void updateBackgroundColor(float fraction){
        if(background!=null) {
            final int color;
            if(backgroundColor!=disabledBackgroundColor){
                color = (int) colorEvaluator.evaluate(fraction, backgroundColor, disabledBackgroundColor);
            } else {
                color = backgroundColor;
            }

            if(background instanceof RippleDrawable){

                RippleDrawable rippleDrawable = (RippleDrawable) background;

                final int color1;
                final int color2;
                if (rippleColor != disabledRippleColor) {
                    color2 = (int) colorEvaluator.evaluate(fraction, rippleColor, disabledRippleColor);
                } else {
                    color2 = rippleColor;
                }
                color1 = adjustAlpha(color2, rippleAlpha);

                rippleDrawable.setColor(new ColorStateList(states, new int[]{color1}));

                Drawable contentDrawable = rippleDrawable.getDrawable(0);

                if(contentDrawable instanceof MaterialShapeDrawable) {

                    MaterialShapeDrawable materialShapeDrawable = (MaterialShapeDrawable) contentDrawable;

                    materialShapeDrawable.setFillColor(new ColorStateList(states, new int[]{color}));

                    if (strokeWidth > 0) {
                        final int color3;
                        if (strokeColor != disabledStrokeColor) {
                            color3 = (int) colorEvaluator.evaluate(fraction, strokeColor, disabledStrokeColor);
                        } else {
                            color3 = strokeColor;
                        }
                        materialShapeDrawable.setStrokeColor(new ColorStateList(states, new int[]{color3}));
                    }
                } else {
                    if(contentDrawable!=null) {
                        setDrawableColorFilter(color, contentDrawable);
                    }
                }
            } else {
                setDrawableColorFilter(color, background);
            }
        }
    }

    private void setDrawableColorFilter(int color , Drawable drawable){
        DrawableCompat.setTintList(drawable, new ColorStateList(states, new int[]{color}));
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
    }

    private void initDashCoordinates() {
        float delta1 = 1.5f * SIN_45 * dashThickness;
        float delta2 = 0.5f * SIN_45 * dashThickness;
        dashStart.x = (int) (dashXStart + delta2);
        dashStart.y = dashYStart + (int) (delta1);
        dashEnd.x = (int) (dashXStart + dashLengthXProjection - delta1);
        dashEnd.y = (int) (dashYStart + dashLengthYProjection - delta2);
    }

    private void updateClipPath() {
        float delta = dashThickness / SIN_45;
        clipPath.reset();
        clipPath.moveTo(dashXStart, dashYStart + delta);
        clipPath.lineTo(dashXStart + delta, dashYStart);
        clipPath.lineTo(dashXStart + dashLengthXProjection * fraction, dashYStart + dashLengthYProjection * fraction - delta);
        clipPath.lineTo(dashXStart + dashLengthXProjection * fraction - delta, dashYStart + dashLengthYProjection * fraction);
    }

    private void drawDash(Canvas canvas) {
        float x = fraction * (dashEnd.x - dashStart.x) + dashStart.x;
        float y = fraction * (dashEnd.y - dashStart.y) + dashStart.y;
        canvas.drawLine(dashStart.x, dashStart.y, x, y, dashPaint);
    }

    public void setDisableAutoRipple(boolean disableAutoRipple) {
        this.disableAutoRipple = disableAutoRipple;
    }

    public void setAnimationDuration(long duration){
        animationDuration = duration;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    @Override
    public void setBackgroundColor(int color){
        if(backgroundColor!=color) {
            if (backgroundColor == disabledBackgroundColor) {
                disabledBackgroundColor = color;
            }
            backgroundColor = color;
            updateBackgroundColor(fraction);
        }
    }

    public void setDisabledBackgroundColor(int color){
        if(disabledBackgroundColor!=color) {
            disabledBackgroundColor = color;
            updateBackgroundColor(fraction);
        }
    }

    public void setIconColor(int color){
        if(iconColor!=color) {
            if (iconColor == disabledIconColor) {
                disabledIconColor = color;
            }
            iconColor = color;
            updateIconColor(fraction);
        }
    }

    public void setDisabledIconColor(int color){
        if(disabledIconColor!=color) {
            disabledIconColor = color;
            updateIconColor(fraction);
        }
    }

    public void setRippleColor(int color) {
        if(rippleColor!=color) {
            if (rippleColor == disabledRippleColor) {
                disabledRippleColor = color;
            }
            rippleColor = color;
            updateBackgroundColor(fraction);
        }
    }

    public void setDisabledRippleColor(int color) {
        if(disabledRippleColor!=color) {
            disabledRippleColor = color;
            updateBackgroundColor(fraction);
        }
    }

    public void setRippleAlpha(float rippleAlpha) {
        if(this.rippleAlpha!=rippleAlpha) {
            this.rippleAlpha = rippleAlpha;
            updateBackgroundColor(fraction);
        }
    }

    public void setStrokeColor(int color) {
        if(strokeColor!=color) {
            if (strokeColor == disabledStrokeColor) {
                disabledStrokeColor = color;
            }
            strokeColor = color;
            updateBackgroundColor(fraction);
        }
    }

    public void setDisabledStrokeColor(int color) {
        if(disabledStrokeColor!=color) {
            disabledStrokeColor = color;
            updateBackgroundColor(fraction);
        }
    }

    public void setDisabledAlpha(float alpha){
        if(disabledAlpha!=alpha) {
            this.disabledAlpha = alpha;
            updateIconAlpha(fraction);
            updateDashAlpha(fraction);
        }
    }

    public void setDashColor(int color) {
        if(dashColor!=color) {
            dashColor = color;
            dashPaint.setColor(color);
            if(dashEnabled){
                invalidate();
            }
        }
    }

    public void setAutoClickSwitch(boolean autoClickSwitch) {
        this.autoClickSwitch = autoClickSwitch;
    }

    public void setDashEnabled(boolean enabled){
        if(dashEnabled!=enabled) {
            dashEnabled = enabled;
            invalidate();
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SwitchButtonSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SwitchButtonSavedState savedState = (SwitchButtonSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        enabled = savedState.buttonEnabled;
        dashEnabled = savedState.dashEnabled;
        animationDuration = savedState.animationDuration;
        disabledAlpha = savedState.disabledAlpha;
        rippleAlpha = savedState.rippleAlpha;
        saveColors = savedState.saveColors;
        if(saveColors){
            backgroundColor = savedState.backgroundColor;
            disabledBackgroundColor = savedState.backgroundColor;
            dashColor = savedState.dashColor;
            iconColor = savedState.backgroundColor;
            disabledIconColor = savedState.backgroundColor;
            strokeColor = savedState.backgroundColor;
            disabledStrokeColor = savedState.backgroundColor;
            rippleColor = savedState.backgroundColor;
            disabledRippleColor = savedState.backgroundColor;
        }
        setFraction(enabled ? 0f : 1f);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SwitchButtonSavedState savedState = new SwitchButtonSavedState(superState);
        savedState.buttonEnabled = enabled;
        savedState.dashEnabled = dashEnabled;
        savedState.animationDuration = animationDuration;
        savedState.disabledAlpha = disabledAlpha;
        savedState.rippleAlpha = rippleAlpha;
        savedState.saveColors = saveColors;
        if(saveColors){
            savedState.backgroundColor = backgroundColor;
            savedState.disabledBackgroundColor = backgroundColor;
            savedState.dashColor = dashColor;
            savedState.iconColor = backgroundColor;
            savedState.disabledIconColor = backgroundColor;
            savedState.strokeColor = backgroundColor;
            savedState.disabledStrokeColor = backgroundColor;
            savedState.rippleColor = backgroundColor;
            savedState.disabledRippleColor = backgroundColor;
        }
        return savedState;
    }

    static class SwitchButtonSavedState extends BaseSavedState {
        boolean saveColors = false;
        boolean buttonEnabled;
        boolean dashEnabled;
        long animationDuration;
        @FloatRange(from = 0f, to = 1f)
        private float disabledAlpha;
        @FloatRange(from = 0f, to = 1f)
        private float rippleAlpha;
        int backgroundColor;
        int disabledBackgroundColor;
        int dashColor;
        int iconColor;
        int disabledIconColor;
        int strokeColor;
        int disabledStrokeColor;
        int rippleColor;
        int disabledRippleColor;

        SwitchButtonSavedState(Parcelable superState) {
            super(superState);
        }

        private SwitchButtonSavedState(Parcel in) {
            super(in);
            saveColors = in.readInt() == 1;
            buttonEnabled = in.readInt() == 1;
            dashEnabled = in.readInt() == 1;
            animationDuration = in.readLong();
            disabledAlpha = in.readFloat();
            rippleAlpha = in.readFloat();
            if(saveColors){
                backgroundColor = in.readInt();
                disabledBackgroundColor = in.readInt();
                dashColor = in.readInt();
                iconColor = in.readInt();
                disabledIconColor = in.readInt();
                strokeColor = in.readInt();
                disabledStrokeColor = in.readInt();
                rippleColor = in.readInt();
                disabledRippleColor = in.readInt();
            }
        }
        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(saveColors ? 1 : 0);
            out.writeInt(buttonEnabled ? 1 : 0);
            out.writeInt(dashEnabled ? 1 : 0);
            out.writeLong(animationDuration);
            out.writeFloat(disabledAlpha);
            out.writeFloat(rippleAlpha);
            if(saveColors){
                out.writeInt(backgroundColor);
                out.writeInt(disabledBackgroundColor);
                out.writeInt(dashColor);
                out.writeInt(iconColor);
                out.writeInt(disabledIconColor);
                out.writeInt(strokeColor);
                out.writeInt(disabledStrokeColor);
                out.writeInt(rippleColor);
                out.writeInt(disabledRippleColor);
            }
        }
        public static final Parcelable.Creator<SwitchButtonSavedState> CREATOR =
                new Parcelable.Creator<SwitchButtonSavedState>() {
                    public SwitchButtonSavedState createFromParcel(Parcel in) {
                        return new SwitchButtonSavedState(in);
                    }
                    public SwitchButtonSavedState[] newArray(int size) {
                        return new SwitchButtonSavedState[size];
                    }
                };
    }

    public interface OnSwitchListener{
        void onSwitch(boolean checked);
    }

    public void setOnSwitchListener(OnSwitchListener onSwitchListener){
        this.onSwitchListener = onSwitchListener;
    }
}
