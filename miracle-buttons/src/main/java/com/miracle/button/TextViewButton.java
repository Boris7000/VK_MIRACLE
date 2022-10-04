package com.miracle.button;

import static com.miracle.button.Utils.adjustAlpha;
import static com.miracle.button.Utils.createRippleDrawable;
import static com.miracle.button.Utils.dpToPx;
import static com.miracle.button.Utils.getColorByAttributeId;
import static com.miracle.button.Utils.getDrawableFromAttributes;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.TextViewCompat;

import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;

public class TextViewButton extends AppCompatTextView {

    public static final int ICON_GRAVITY_START = 0x1;
    public static final int ICON_GRAVITY_TEXT_START = 0x2;
    public static final int ICON_GRAVITY_END = 0x3;
    public static final int ICON_GRAVITY_TEXT_END = 0x4;
    private static final int DEFAULT_ANIMATION_DURATION = 400;
    private static final int DEFAULT_ICON_SPACING_DP = 12;
    private static final float DEFAULT_RIPPLE_ALPHA = 0.27f;

    private static final ArgbEvaluator colorEvaluator = new ArgbEvaluator();

    private Drawable iconStart;
    private Drawable iconEnd;
    private Drawable background;

    private int iconStartGravity;
    private int iconEndGravity;
    private int iconStartSize;
    private int iconEndSize;
    private int iconStartColor;
    private int iconEndColor;
    private int disabledIconStartColor;
    private int disabledIconEndColor;
    private int iconStartLeft;
    private int iconStartTop;
    private int iconEndLeft;
    private int iconEndTop;
    private int iconSpacing;

    private final float strokeWidth;

    private int backgroundColor;
    private int disabledBackgroundColor;
    private int textColor;
    private int disabledTextColor;
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

    public TextViewButton(@NonNull Context context) {
        this(context, null);
    }

    public TextViewButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextViewButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Resources.Theme theme = context.getTheme();

        TypedArray array = theme.obtainStyledAttributes(attrs, R.styleable.TextViewButton, defStyleAttr, 0);

        ShapeAppearanceModel shapeAppearanceModel;
        int gravity;
        boolean disableAutoBackground;

        try {
            disableAutoBackground = array.getBoolean(R.styleable.TextViewButton_disable_auto_background, false);
            disableAutoRipple = array.getBoolean(R.styleable.TextViewButton_disable_auto_ripple, false);
            saveColors = array.getBoolean(R.styleable.TextViewButton_save_colors, false);
            animationDuration = array.getInteger(R.styleable.TextViewButton_animation_duration, DEFAULT_ANIMATION_DURATION);
            enabled = array.getBoolean(R.styleable.TextViewButton_android_enabled, true);
            //////////////////////////////////////////////////////////////////////////////////////////
            backgroundColor = array.getColor(R.styleable.TextViewButton_background_color, getColorByAttributeId(theme, R.attr.colorPrimary));
            disabledBackgroundColor = array.getColor(R.styleable.TextViewButton_disabled_background_color, backgroundColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            strokeColor = array.getColor(R.styleable.TextViewButton_stroke_color, backgroundColor);
            disabledStrokeColor = array.getColor(R.styleable.TextViewButton_disabled_stroke_color, strokeColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            textColor = array.getColor(R.styleable.TextViewButton_text_color, 0);
            disabledTextColor = array.getColor(R.styleable.TextViewButton_disabled_text_color, textColor);
            //////////////////////////////////////////////////////////////////////////////////////////
            rippleColor = array.getColor(R.styleable.TextViewButton_ripple_color, 0);
            disabledRippleColor = array.getColor(R.styleable.TextViewButton_disabled_ripple_color, rippleColor);
            rippleAlpha = array.getFloat(R.styleable.TextViewButton_ripple_alpha, rippleAlpha);
            //////////////////////////////////////////////////////////////////////////////////////////
            iconStartColor = array.getColor(R.styleable.TextViewButton_icon_start_color, 0);
            iconEndColor = array.getColor(R.styleable.TextViewButton_icon_end_color, textColor);
            disabledIconStartColor = array.getColor(R.styleable.TextViewButton_disabled_icon_start_color, 0);
            disabledIconEndColor = array.getColor(R.styleable.TextViewButton_disabled_icon_end_color, textColor);
            iconStartGravity = array.getInteger(R.styleable.TextViewButton_icon_start_gravity, ICON_GRAVITY_TEXT_START);
            iconEndGravity = array.getInteger(R.styleable.TextViewButton_icon_end_gravity, ICON_GRAVITY_TEXT_END);
            iconSpacing = array.getDimensionPixelSize(R.styleable.TextViewButton_icon_spacing,
                    (int)dpToPx(context, DEFAULT_ICON_SPACING_DP));
            iconStartSize = array.getDimensionPixelSize(R.styleable.TextViewButton_icon_start_size, 0);
            iconEndSize = array.getDimensionPixelSize(R.styleable.TextViewButton_icon_end_size, 0);
            iconStart = getDrawableFromAttributes(context, array, R.styleable.TextViewButton_icon_start);
            iconEnd = getDrawableFromAttributes(context, array, R.styleable.TextViewButton_icon_end);
            //////////////////////////////////////////////////////////////////////////////////////////
            strokeWidth = array.getDimensionPixelSize(R.styleable.TextViewButton_stroke_width, 0);
            shapeAppearanceModel = ShapeAppearanceModel.builder(context, attrs, defStyleAttr, 0).build();
            gravity = array.getInteger(R.styleable.TextViewButton_android_gravity, Gravity.CENTER);
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
                if(iconStartColor == 0 && iconEndColor == 0 && disabledIconStartColor == 0 && disabledIconEndColor == 0){
                    rippleColor = textColor;
                    disabledRippleColor = disabledTextColor;
                    iconStartColor = textColor;
                    disabledIconStartColor = iconStartColor;
                    iconEndColor = textColor;
                    disabledIconEndColor = iconEndColor;
                } else {
                    if(iconStartColor != 0){
                        rippleColor = iconStartColor;
                        disabledRippleColor = disabledIconStartColor;
                        iconEndColor = iconStartColor;
                        disabledIconEndColor = disabledIconStartColor;
                    } else {
                        if(iconEndColor != 0){
                            rippleColor = iconEndColor;
                            disabledRippleColor = disabledIconEndColor;
                            iconStartColor = iconEndColor;
                            disabledIconStartColor = disabledIconEndColor;
                        }
                    }
                }
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

        setCompoundDrawablePadding(iconSpacing);

        updateIconStart(iconStart != null);
        updateIconEnd(iconEnd != null);

        setFraction(enabled ? 0f : 1f);

    }

    private void updateIconStart(boolean needsIconReset){
        if (iconStart != null) {
            iconStart = DrawableCompat.wrap(iconStart).mutate();
            updateIconStartColor(fraction);

            int width = iconStartSize != 0 ? iconStartSize : iconStart.getIntrinsicWidth();
            int height = iconStartSize != 0 ? iconStartSize : iconStart.getIntrinsicHeight();

            iconStart.setBounds(iconStartLeft, iconStartTop, iconStartLeft + width, iconStartTop + height);
            iconStart.setVisible(true, needsIconReset);
        }

        // Forced icon update
        if (needsIconReset) {
            resetIconDrawable();
        }

        // Otherwise only update if the icon or the position has changed
        Drawable[] existingDrawables = TextViewCompat.getCompoundDrawablesRelative(this);
        Drawable drawableStart = existingDrawables[0];
        Drawable drawableEnd = existingDrawables[2];
        boolean hasIconChanged = drawableStart != iconStart || drawableEnd != iconEnd;

        if (hasIconChanged) {
            resetIconDrawable();
        }

    }

    private void updateIconStartPosition(int buttonWidth) {
        if (iconStart == null || getLayout() == null) {
            return;
        }

        iconStartTop = 0;

        Layout.Alignment textAlignment = getActualTextAlignment();
        if (iconStartGravity == ICON_GRAVITY_START
                || (iconStartGravity == ICON_GRAVITY_TEXT_START && textAlignment == Layout.Alignment.ALIGN_NORMAL)) {
            iconStartLeft = 0;
            updateIconStart(/* needsIconReset = */ false);
            return;
        }

        int localIconSize = iconStartSize == 0 ? iconStart.getIntrinsicWidth() : iconStartSize;
        int textWidth = getTextWidth();
        int availableWidth = buttonWidth
                - textWidth
                - ViewCompat.getPaddingEnd(this)
                - localIconSize
                - (textWidth>0?iconSpacing:0)
                - (iconEnd!=null?(iconEndSize != 0 ? iconEndSize : iconEnd.getIntrinsicWidth()):0)
                - ViewCompat.getPaddingStart(this);
        int newIconLeft =
                textAlignment == Layout.Alignment.ALIGN_CENTER ? availableWidth / 2 : availableWidth;

        // Only flip the bound value if either isLayoutRTL() or iconGravity is textEnd, but not both
        if (isLayoutRTL()) {
            newIconLeft = -newIconLeft;
        }

        if (iconStartLeft != newIconLeft) {
            iconStartLeft = newIconLeft;
            updateIconStart(/* needsIconReset = */ false);
        }

    }

    private void updateIconEnd(boolean needsIconReset){
        if (iconEnd != null) {
            iconEnd = DrawableCompat.wrap(iconEnd).mutate();
            updateIconEndColor(fraction);

            int width = iconEndSize != 0 ? iconEndSize : iconEnd.getIntrinsicWidth();
            int height = iconEndSize != 0 ? iconEndSize : iconEnd.getIntrinsicHeight();

            iconEnd.setBounds(iconEndLeft, iconEndTop, iconEndLeft + width, iconEndTop + height);
            iconEnd.setVisible(true, needsIconReset);
        }

        // Forced icon update
        if (needsIconReset) {
            resetIconDrawable();
            return;
        }

        // Otherwise only update if the icon or the position has changed
        Drawable[] existingDrawables = TextViewCompat.getCompoundDrawablesRelative(this);
        Drawable drawableStart = existingDrawables[0];
        Drawable drawableEnd = existingDrawables[2];
        boolean hasIconChanged = drawableStart != iconStart || drawableEnd != iconEnd;

        if (hasIconChanged) {
            resetIconDrawable();
        }
    }

    private void updateIconEndPosition(int buttonWidth) {
        if (iconEnd == null || getLayout() == null) {
            return;
        }

        iconEndTop = 0;

        Layout.Alignment textAlignment = getActualTextAlignment();
        if (iconEndGravity == ICON_GRAVITY_END
                || (iconEndGravity == ICON_GRAVITY_TEXT_END && textAlignment == Layout.Alignment.ALIGN_OPPOSITE)) {
            iconEndLeft = 0;
            updateIconEnd(/* needsIconReset = */ false);
            return;
        }

        int localIconSize = iconEndSize == 0 ? iconEnd.getIntrinsicWidth() : iconEndSize;
        int textWidth = getTextWidth();
        int availableWidth = buttonWidth
                - getTextWidth()
                - ViewCompat.getPaddingEnd(this)
                - localIconSize
                - (textWidth>0?iconSpacing:0)
                - (iconStart!=null?(iconStartSize != 0 ? iconStartSize : iconStart.getIntrinsicWidth()):0)
                - ViewCompat.getPaddingStart(this);
        int newIconLeft =
                textAlignment == Layout.Alignment.ALIGN_CENTER ? availableWidth / 2 : availableWidth;

        // Only flip the bound value if either isLayoutRTL() or iconGravity is textEnd, but not both
        if (isLayoutRTL() != (iconEndGravity == ICON_GRAVITY_TEXT_END)) {
            newIconLeft = -newIconLeft;
        }

        if (iconEndLeft != newIconLeft) {
            iconEndLeft = newIconLeft;
            updateIconEnd(/* needsIconReset = */ false);
        }
    }

    private void resetIconDrawable() {
        TextViewCompat.setCompoundDrawablesRelative(this, iconStart, null, iconEnd, null);
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
        updateIconStartColor(fraction);
        updateIconEndColor(fraction);
        updateTextColor(fraction);
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

    private void updateIconStartColor(float fraction){
        if(iconStart!=null){
            final int color;
            if (iconStartColor != disabledIconStartColor) {
                color = (int) colorEvaluator.evaluate(fraction, iconStartColor, disabledIconStartColor);
            } else {
                color = iconStartColor;
            }
            setDrawableColorFilter(color, iconStart);
        }
    }

    private void updateIconEndColor(float fraction){
        if(iconEnd!=null){
            final int color;
            if (iconEndColor != disabledIconEndColor) {
                color = (int) colorEvaluator.evaluate(fraction, iconEndColor, disabledIconEndColor);
            } else {
                color = iconEndColor;
            }
            setDrawableColorFilter(color, iconEnd);
        }
    }

    private void updateTextColor(float fraction){
        final int color;
        if (textColor != disabledTextColor) {
            color = (int) colorEvaluator.evaluate(fraction, textColor, disabledTextColor);
        } else {
            color = textColor;
        }
        super.setTextColor(color);
    }

    private void setDrawableColorFilter(int color , Drawable drawable){
        DrawableCompat.setTintList(drawable, new ColorStateList(states, new int[]{color}));
        DrawableCompat.setTintMode(drawable, PorterDuff.Mode.SRC_IN);
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



    public void setIconStartColor(int color){
        if(iconStartColor!=color) {
            if (iconStartColor == disabledIconStartColor) {
                disabledIconStartColor = color;
            }
            this.iconStartColor = color;
            updateIconStartColor(fraction);
        }
    }

    public void setDisabledIconStartColor(int color) {
        if(disabledIconStartColor!=color) {
            this.disabledIconStartColor = color;
            updateIconStartColor(fraction);
        }
    }

    public void setIconEndColor(int color){
        if(iconEndColor!=color) {
            if (iconEndColor == disabledIconEndColor) {
                disabledIconEndColor = color;
            }
            this.iconEndColor = color;
            updateIconEndColor(fraction);
        }
    }

    public void setDisabledIconEndColor(int color) {
        if(disabledIconEndColor!=color) {
            this.disabledIconEndColor = color;
            updateIconEndColor(fraction);
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

    public void setIconStartImageResource(@DrawableRes int resourceId) {
        if (resourceId != 0) {
            Drawable value = AppCompatResources.getDrawable(getContext(), resourceId);
            setIconStart(value);
        } else {
            setIconStart(null);
        }
    }

    public void setIconStart(Drawable iconStart) {
        this.iconStart = iconStart;
        updateIconStart(true);
        updateIconEnd(false);
    }

    public void setIconEndImageResource(@DrawableRes int resourceId) {
        if (resourceId != 0) {
            Drawable value = AppCompatResources.getDrawable(getContext(), resourceId);
            setIconEnd(value);
        } else {
            setIconEnd(null);
        }
    }

    public void setIconEnd(Drawable iconEnd) {
        this.iconEnd = iconEnd;
        updateIconEnd(true);
        updateIconStart(false);
    }

    public void setIconStartGravity(int iconStartGravity) {
        this.iconStartGravity = iconStartGravity;
        updateIconStartPosition(getMeasuredWidth());
        updateIconEndPosition(getMeasuredWidth());
    }

    public void setIconEndGravity(int iconEndGravity) {
        this.iconEndGravity = iconEndGravity;
        updateIconEndPosition(getMeasuredWidth());
        updateIconStartPosition(getMeasuredWidth());
    }

    public void setIconSpacing(int iconSpacing) {
        this.iconSpacing = iconSpacing;
        updateIconStartPosition(getMeasuredWidth());
        updateIconEndPosition(getMeasuredWidth());
    }

    public void setIconStartSize(int iconStartSize) {
        if (this.iconStartSize != iconStartSize) {
            this.iconStartSize = iconStartSize;
            updateIconStart(true);
            updateIconEnd(true);
        }
    }

    public int getIconStartSize() {
        return iconStartSize;
    }

    public void setIconEndSize(int iconEndSize) {
        if (this.iconEndSize != iconEndSize) {
            this.iconEndSize = iconEndSize;
            updateIconEnd(true);
            updateIconStart(true);
        }
    }

    public int getIconEndSize() {
        return iconEndSize;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        updateIconStartPosition(getMeasuredWidth());
        updateIconEndPosition(getMeasuredWidth());
    }

    @Override
    protected void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        super.onTextChanged(charSequence, i, i1, i2);
        updateIconStartPosition(getMeasuredWidth());
        updateIconEndPosition(getMeasuredWidth());
    }

    @Override
    public void setTextAlignment(int textAlignment) {
        super.setTextAlignment(textAlignment);
        updateIconStartPosition(getMeasuredWidth());
        updateIconEndPosition(getMeasuredWidth());
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
            strokeColor = savedState.backgroundColor;
            disabledStrokeColor = savedState.backgroundColor;
            rippleColor = savedState.backgroundColor;
            disabledRippleColor = savedState.backgroundColor;
            iconStartColor = savedState.iconStartColor;
            disabledIconStartColor = savedState.disabledIconStartColor;
            iconEndColor = savedState.iconEndColor;
            disabledIconEndColor = savedState.disabledIconEndColor;
        }
        setFraction(enabled ? 0f : 1f);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        ButtonSavedState savedState = new ButtonSavedState(superState);
        savedState.buttonEnabled = enabled;
        savedState.animationDuration = animationDuration;
        savedState.rippleAlpha = rippleAlpha;
        savedState.saveColors = saveColors;
        if(saveColors){
            savedState.backgroundColor = backgroundColor;
            savedState.disabledBackgroundColor = backgroundColor;
            savedState.textColor = backgroundColor;
            savedState.disabledTextColor = backgroundColor;
            savedState.strokeColor = backgroundColor;
            savedState.disabledStrokeColor = backgroundColor;
            savedState.rippleColor = backgroundColor;
            savedState.disabledRippleColor = backgroundColor;
            savedState.iconStartColor = iconStartColor;
            savedState.disabledIconStartColor = disabledIconStartColor;
            savedState.iconEndColor = iconEndColor;
            savedState.disabledIconEndColor = disabledIconEndColor;
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
        int strokeColor;
        int disabledStrokeColor;
        int rippleColor;
        int disabledRippleColor;
        int iconStartColor;
        int disabledIconStartColor;
        int iconEndColor;
        int disabledIconEndColor;

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
                strokeColor = in.readInt();
                disabledStrokeColor = in.readInt();
                rippleColor = in.readInt();
                disabledRippleColor = in.readInt();
                iconStartColor = in.readInt();
                disabledIconStartColor = in.readInt();
                iconEndColor = in.readInt();
                disabledIconEndColor = in.readInt();
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
                out.writeInt(strokeColor);
                out.writeInt(disabledStrokeColor);
                out.writeInt(rippleColor);
                out.writeInt(disabledRippleColor);
                out.writeInt(iconStartColor);
                out.writeInt(disabledIconStartColor);
                out.writeInt(iconEndColor);
                out.writeInt(disabledIconEndColor);
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

    private int getTextWidth() {
        Paint textPaint = getPaint();
        String buttonText = getText().toString();
        if (getTransformationMethod() != null) {
            // if text is transformed, add that transformation to to ensure correct calculation
            // of icon padding.
            buttonText = getTransformationMethod().getTransformation(buttonText, this).toString();
        }

        return Math.min((int) textPaint.measureText(buttonText), getLayout().getEllipsizedWidth());
    }

    private Layout.Alignment getGravityTextAlignment() {
        switch (getGravity() & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) {
            case Gravity.CENTER_HORIZONTAL:
                return Layout.Alignment.ALIGN_CENTER;
            case Gravity.END:
            case Gravity.RIGHT:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case Gravity.START:
            case Gravity.LEFT:
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    private Layout.Alignment getActualTextAlignment() {
        switch (getTextAlignment()) {
            case TEXT_ALIGNMENT_GRAVITY:
                return getGravityTextAlignment();
            case TEXT_ALIGNMENT_CENTER:
                return Layout.Alignment.ALIGN_CENTER;
            case TEXT_ALIGNMENT_TEXT_END:
            case TEXT_ALIGNMENT_VIEW_END:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case TEXT_ALIGNMENT_TEXT_START:
            case TEXT_ALIGNMENT_VIEW_START:
            case TEXT_ALIGNMENT_INHERIT:
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    private boolean isLayoutRTL() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

}

