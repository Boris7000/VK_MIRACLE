package com.miracle.button;

import static com.miracle.button.Utils.adjustAlpha;
import static com.miracle.button.Utils.createRippleDrawable;
import static com.miracle.button.Utils.dpToPx;
import static com.miracle.button.Utils.getColorByAttributeId;
import static com.miracle.button.Utils.getDrawableByResourceId;
import static com.miracle.button.Utils.getDrawableFromAttributes;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class Button extends LinearLayout {

    private static final int DEFAULT_ANIMATION_DURATION = 400;
    private static final int DEFAULT_ICON_SPACING_DP = 12;
    private static final int ICON_GRAVITY_START = 0;
    private static final int ICON_GRAVITY_END = 1;
    private static final float DEFAULT_RIPPLE_ALPHA = 0.27f;

    private final LayoutInflater inflater;
    private static final ArgbEvaluator colorEvaluator = new ArgbEvaluator();

    private Drawable icon;
    private Drawable background;

    @Nullable
    private TextView textView;
    @Nullable
    private String text;
    private float textSize;
    private final int textAppearanceResId;

    @Nullable
    private ImageView iconView;
    private final int iconGravity;
    private float iconSpacing;

    private final float strokeWidth;

    private int backgroundColor;
    private int disabledBackgroundColor;
    private int textColor;
    private int disabledTextColor;
    private int iconColor;
    private int disabledIconColor;
    private int strokeColor;
    private int disabledStrokeColor;
    private int rippleColor;
    private int disabledRippleColor;
    @FloatRange(from = 0f, to = 1f)
    private float rippleAlpha = DEFAULT_RIPPLE_ALPHA;

    private final static int[][] states = new int[][] {new int[]{}};

    private long animationDuration;
    @FloatRange(from = 0f, to = 1f)
    private float fraction = 0f;
    private boolean enabled;
    private boolean saveColors;
    private boolean disableAutoRipple;

    public Button(Context context) {
        this(context, null);
    }

    public Button(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Button(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public Button(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflater = LayoutInflater.from(context);

        Resources.Theme theme = context.getTheme();

        TypedArray array = theme.obtainStyledAttributes(attrs, R.styleable.Button, defStyleAttr, defStyleRes);

        ShapeAppearanceModel shapeAppearanceModel;
        int gravity;
        boolean disableAutoBackground;

        try {
            disableAutoBackground = array.getBoolean(R.styleable.Button_disable_auto_background, false);
            disableAutoRipple = array.getBoolean(R.styleable.Button_disable_auto_ripple, false);
            saveColors = array.getBoolean(R.styleable.Button_save_colors, false);
            animationDuration = array.getInteger(R.styleable.Button_animation_duration, DEFAULT_ANIMATION_DURATION);
            enabled = array.getBoolean(R.styleable.Button_android_enabled, true);
            //////////////////////////////////////////////////////////////////////////////////////////
            backgroundColor = array.getColor(R.styleable.Button_background_color, getColorByAttributeId(theme, R.attr.colorPrimary));
            disabledBackgroundColor = array.getColor(R.styleable.Button_disabled_background_color, backgroundColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            strokeColor = array.getColor(R.styleable.Button_stroke_color, backgroundColor);
            disabledStrokeColor = array.getColor(R.styleable.Button_disabled_stroke_color, strokeColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            textColor = array.getColor(R.styleable.Button_text_color, 0);
            disabledTextColor = array.getColor(R.styleable.Button_disabled_text_color, textColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            iconColor = array.getColor(R.styleable.Button_icon_color, 0);
            disabledIconColor = array.getColor(R.styleable.Button_disabled_icon_color, iconColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            rippleColor = array.getColor(R.styleable.Button_ripple_color, 0);
            disabledRippleColor = array.getColor(R.styleable.Button_disabled_ripple_color, disabledIconColor);
            rippleAlpha = array.getFloat(R.styleable.Button_ripple_alpha, rippleAlpha);
            //////////////////////////////////////////////////////////////////////////////////////////
            text = array.getString(R.styleable.Button_android_text);
            textSize = array.getDimensionPixelSize(R.styleable.Button_android_textSize, -1);
            textAppearanceResId = array.getResourceId(R.styleable.Button_android_textAppearance, 0);
            icon = getDrawableFromAttributes(context, array, R.styleable.Button_android_src);
            iconGravity = array.getInt(R.styleable.Button_icon_gravity, 0);
            iconSpacing = array.getDimensionPixelSize(R.styleable.Button_icon_spacing,
                    (int)dpToPx(context, DEFAULT_ICON_SPACING_DP));
            strokeWidth = array.getDimensionPixelSize(R.styleable.Button_stroke_width, 0);
            shapeAppearanceModel = ShapeAppearanceModel.builder(context, attrs, defStyleAttr, defStyleRes).build();
            gravity = array.getInteger(R.styleable.Button_android_gravity,Gravity.CENTER);
        } finally {
            array.recycle();
        }

        if(rippleColor==0){
            if(textColor!=0){
                rippleColor = textColor;
                disabledRippleColor = disabledTextColor;
            } else {
                textColor = getColorByAttributeId(theme, R.attr.colorOnPrimary);
                disabledTextColor = textColor;
                if (iconColor == 0) {
                    iconColor = getColorByAttributeId(theme, R.attr.colorOnPrimary);
                    disabledIconColor = iconColor;
                }
                rippleColor = iconColor;
                disabledRippleColor = disabledIconColor;
            }
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

        setGravity(gravity);

        setIconImageDrawable(icon, false);

        setText(text);

        setFraction(enabled ? 0f : 1f);
    }

    private void inflateTextView(){
        textView = (TextView) inflater.inflate(R.layout.text_view, this, false);
        if(textAppearanceResId!=0) {
            textView.setTextAppearance(textAppearanceResId);
        }
        if(textSize>-1) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }

        updateTextColor(fraction);

        switch (iconGravity){
            case ICON_GRAVITY_START:{
                addView(textView);
                break;
            }
            case ICON_GRAVITY_END:{
                addView(textView, 0);
                break;
            }
        }
    }

    private void inflateIconView(){
        iconView = (ImageView) inflater.inflate(R.layout.icon_view, this, false);
        switch (iconGravity){
            case ICON_GRAVITY_START:{
                addView(iconView, 0);
                break;
            }
            case ICON_GRAVITY_END:{
                addView(iconView);
                break;
            }
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        updateSpacing();
        super.addView(child, index, params);
    }

    private void updateSpacing(){
        switch (iconGravity){
            case ICON_GRAVITY_START:{
                if(iconView!=null&&iconView.getVisibility()==VISIBLE) {
                    if (textView != null && textView.getVisibility() == VISIBLE) {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iconView.getLayoutParams();
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = (int) iconSpacing;
                    } else {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iconView.getLayoutParams();
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 0;
                    }
                }
                break;
            }
            case ICON_GRAVITY_END:{
                if(iconView!=null&&iconView.getVisibility()==VISIBLE) {
                    if (textView != null && textView.getVisibility() == VISIBLE) {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iconView.getLayoutParams();
                        layoutParams.leftMargin = (int) iconSpacing;
                        layoutParams.rightMargin = 0;
                    } else {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) iconView.getLayoutParams();
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 0;
                    }
                }
                break;
            }
        }
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

    public void setText(String text) {
        this.text = text;
        if (text != null && !text.isEmpty()) {
            if (textView == null) {
                inflateTextView();
                textView.setText(text);
                updateTextColor(fraction);
            }
        } else {
            if (textView != null && textView.getVisibility() != VISIBLE) {
                textView.setVisibility(GONE);
            }
        }
    }

    public void setIconImageResource(@DrawableRes int iconImageResource) {
        setIconImageResource(iconImageResource, false);
    }

    public void setIconImageResource(@DrawableRes int iconImageResource, boolean animate) {
        if(iconImageResource!=0) {
            setIconImageDrawable(getDrawableByResourceId(getContext(), iconImageResource), animate);
        } else {
            setIconImageDrawable(null, animate);
        }
    }

    public void setIconImageDrawable(@Nullable Drawable drawable) {
        setIconImageDrawable(drawable, false);
    }

    public void setIconImageDrawable(@Nullable Drawable drawable , boolean animate) {
        if(drawable!=null) {
            this.icon = DrawableCompat.wrap(drawable).mutate();
            if (iconView == null) {
                inflateIconView();
            }
            if(animate){
                ValueAnimator animator = ValueAnimator.ofFloat(iconView.getScaleX(), 0);
                animator.addUpdateListener(animation -> {
                    float value = (Float) animation.getAnimatedValue();
                    iconView.setScaleX(value);
                    iconView.setScaleY(value);
                });
                animator.setInterpolator(new DecelerateInterpolator());
                animator.setDuration(animationDuration/2);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        iconView.setImageDrawable(icon);
                        updateIconColor(fraction);
                        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                        animator.addUpdateListener(animation1 -> {
                            float value = (Float) animation1.getAnimatedValue();
                            iconView.setScaleX(value);
                            iconView.setScaleY(value);
                        });
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.setDuration(animationDuration/2);
                        animator.start();
                        super.onAnimationEnd(animation);
                    }
                });
                animator.start();
            } else {
                iconView.setImageDrawable(icon);
                updateIconColor(fraction);
            }
        } else {
            this.icon = null;
            if (iconView != null && iconView.getVisibility() != VISIBLE) {
                iconView.setVisibility(GONE);
            }
        }
    }

    @Override
    public void setEnabled(boolean checked){
        setEnabled(checked, false);
    }

    public void setEnabled(boolean enabled, boolean animate){
        this.enabled = enabled;

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

    public boolean isEnabled() {
        return enabled;
    }

    private void animateToFraction(float toFraction) {
        ValueAnimator animator = ValueAnimator.ofFloat(fraction, toFraction);
        animator.addUpdateListener(animation -> setFraction((float) animation.getAnimatedValue()));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(animationDuration);
        animator.start();
    }

    private void setFraction(float fraction) {
        this.fraction = fraction;
        updateBackgroundColor(fraction);
        updateIconColor(fraction);
        updateTextColor(fraction);
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

    private void updateTextColor(float fraction){
        if(textView!=null) {
            final int color;
            if (textColor != disabledTextColor) {
                color = (int) colorEvaluator.evaluate(fraction, textColor, disabledTextColor);
            } else {
                color = textColor;
            }
            textView.setTextColor(color);
        }
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

    public void setTextColor(int color) {
        if(textColor!=color) {
            if (textColor == disabledTextColor) {
                disabledTextColor = color;
            }
            this.textColor = color;
            updateTextColor(fraction);
        }
    }

    public void setDisabledTextColor(int color) {
        if(disabledTextColor!=color) {
            this.disabledTextColor = color;
            updateTextColor(fraction);
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

    public void setDisableAutoRipple(boolean disableAutoRipple) {
        this.disableAutoRipple = disableAutoRipple;
    }

    public long getAnimationDuration() {
        return animationDuration;
    }

    public void setAnimationDuration(long duration){
        animationDuration = duration;
    }

    public void setIconSpacing(float iconSpacing) {
        this.iconSpacing = iconSpacing;
        updateSpacing();
    }

    public void setTextSize(float textSize) {
        if(this.textSize!=textSize) {
            this.textSize = textSize;
            if (textView != null) {
                textView.setTextSize(textSize);
            }
        }
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof ButtonSavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        ButtonSavedState savedState = (ButtonSavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        enabled = savedState.buttonEnabled;
        animationDuration = savedState.animationDuration;
        rippleAlpha = savedState.rippleAlpha;
        saveColors = savedState.saveColors;
        if(saveColors){
            backgroundColor = savedState.backgroundColor;
            disabledBackgroundColor = savedState.backgroundColor;
            textColor = savedState.backgroundColor;
            disabledTextColor = savedState.backgroundColor;
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
        ButtonSavedState savedState = new ButtonSavedState(superState);
        savedState.buttonEnabled = enabled;
        savedState.animationDuration = animationDuration;
        savedState.saveColors = saveColors;
        savedState.rippleAlpha = rippleAlpha;
        if(saveColors){
            savedState.backgroundColor = backgroundColor;
            savedState.disabledBackgroundColor = backgroundColor;
            savedState.textColor = backgroundColor;
            savedState.disabledTextColor = backgroundColor;
            savedState.iconColor = backgroundColor;
            savedState.disabledIconColor = backgroundColor;
            savedState.strokeColor = backgroundColor;
            savedState.disabledStrokeColor = backgroundColor;
            savedState.rippleColor = backgroundColor;
            savedState.disabledRippleColor = backgroundColor;
        }
        return savedState;
    }

    static class ButtonSavedState extends BaseSavedState {
        boolean saveColors = false;
        boolean buttonEnabled;
        long animationDuration;
        @FloatRange(from = 0f, to = 1f)
        private float rippleAlpha;
        int backgroundColor;
        int disabledBackgroundColor;
        int textColor;
        int disabledTextColor;
        int iconColor;
        int disabledIconColor;
        int strokeColor;
        int disabledStrokeColor;
        int rippleColor;
        int disabledRippleColor;

        ButtonSavedState(Parcelable superState) {
            super(superState);
        }

        private ButtonSavedState(Parcel in) {
            super(in);
            saveColors = in.readInt() == 1;
            buttonEnabled = in.readInt() == 1;
            animationDuration = in.readLong();
            rippleAlpha = in.readFloat();
            if(saveColors){
                backgroundColor = in.readInt();
                disabledBackgroundColor = in.readInt();
                textColor = in.readInt();
                disabledTextColor = in.readInt();
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
            out.writeLong(animationDuration);
            out.writeFloat(rippleAlpha);
            if(saveColors){
                out.writeInt(backgroundColor);
                out.writeInt(disabledBackgroundColor);
                out.writeInt(textColor);
                out.writeInt(disabledTextColor);
                out.writeInt(iconColor);
                out.writeInt(disabledIconColor);
                out.writeInt(strokeColor);
                out.writeInt(disabledStrokeColor);
                out.writeInt(rippleColor);
                out.writeInt(disabledRippleColor);
            }
        }
        public static final Parcelable.Creator<ButtonSavedState> CREATOR =
                new Parcelable.Creator<ButtonSavedState>() {
                    public ButtonSavedState createFromParcel(Parcel in) {
                        return new ButtonSavedState(in);
                    }
                    public ButtonSavedState[] newArray(int size) {
                        return new ButtonSavedState[size];
                    }
                };
    }
}



