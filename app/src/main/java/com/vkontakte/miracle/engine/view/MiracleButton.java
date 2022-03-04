package com.vkontakte.miracle.engine.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.vkontakte.miracle.R;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;
import static com.vkontakte.miracle.engine.util.DimensionsUtil.dpToPx;

public class MiracleButton extends LinearLayout {

    private final LayoutInflater inflater;
    private boolean active;
    private boolean outline;
    private boolean transparent;
    private boolean secondary;
    private int activeTextColor;
    private int disabledTextColor;
    private int activeBackgroundColor;
    private int disabledBackgroundColor;
    private int activeImageColor;
    private int disabledImageColor;
    private int imageGravity;
    private int imageSpacing;
    private static final int[] ACTIVE_STATE_SET = {
            android.R.attr.state_active
    };

    private TextView textView;
    private ImageView imageView;

    @FloatRange(from = 0f, to = 1f)
    private float fraction = 0f;
    private final ArgbEvaluator colorEvaluator = new ArgbEvaluator();

    public MiracleButton(Context context) {
        this(context,null);
    }

    public MiracleButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MiracleButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs,defStyleAttr,0);
    }

    public MiracleButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MiracleButton, 0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveAttributeDataForStyleable(context, R.styleable.MiracleButton, attrs, a, 0, 0);
        }

        active = a.getBoolean(R.styleable.MiracleButton_active, true);
        secondary = a.getBoolean(R.styleable.MiracleButton_secondary, false);
        transparent = a.getBoolean(R.styleable.MiracleButton_transparent, false);
        outline = a.getBoolean(R.styleable.MiracleButton_outline, false);
        imageGravity = a.getInt(R.styleable.MiracleButton_imageGravity, 0);
        imageSpacing = (int) a.getDimension(R.styleable.MiracleButton_imageSpacing, dpToPx(9, getContext()));

        fraction = active?1f:0f;

        if(transparent){
            activeTextColor = a.getInt(R.styleable.MiracleButton_activeTextColor,
                    getColorByAttributeId(getContext(),
                            secondary ?R.attr.colorSecondary:R.attr.colorPrimary));
            disabledTextColor = a.getInt(R.styleable.MiracleButton_disabledTextColor,
                    getColorByAttributeId(getContext(),
                            secondary ?R.attr.colorSecondaryNeutral_60 :R.attr.colorPrimaryNeutral_60));
            activeImageColor = a.getInt(R.styleable.MiracleButton_activeImageColor,
                    getColorByAttributeId(getContext(),
                            secondary ?R.attr.colorSecondary:R.attr.colorPrimary));
            disabledImageColor = a.getInt(R.styleable.MiracleButton_disabledImageColor,
                    getColorByAttributeId(getContext(),
                            secondary ?R.attr.colorSecondaryNeutral_60 :R.attr.colorPrimaryNeutral_60));
        } else {
            if(outline){
                activeBackgroundColor = a.getInt(R.styleable.MiracleButton_activeBackgroundColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSecondary:R.attr.colorPrimary));
                disabledBackgroundColor = a.getInt(R.styleable.MiracleButton_disabledBackgroundColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSecondaryNeutral_60:R.attr.colorPrimaryNeutral_60));

                activeTextColor = a.getInt(R.styleable.MiracleButton_activeTextColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSecondary:R.attr.colorPrimary));
                disabledTextColor = a.getInt(R.styleable.MiracleButton_disabledTextColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSecondaryNeutral_60 :R.attr.colorPrimaryNeutral_60));

                activeImageColor = a.getInt(R.styleable.MiracleButton_activeImageColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSecondary:R.attr.colorPrimary));
                disabledImageColor = a.getInt(R.styleable.MiracleButton_disabledImageColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSecondaryNeutral_60 :R.attr.colorPrimaryNeutral_60));

                if (getBackground() == null) {
                    setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.rounded_button_bg_outline_white, getContext().getTheme()));
                }

            } else {

                activeBackgroundColor = a.getInt(R.styleable.MiracleButton_activeBackgroundColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSecondary:R.attr.colorPrimary));
                disabledBackgroundColor = a.getInt(R.styleable.MiracleButton_disabledBackgroundColor,
                        getColorByAttributeId(getContext(),
                                secondary ?R.attr.colorSurfaceSecondary:R.attr.colorSurfacePrimary));

                activeTextColor = a.getInt(R.styleable.MiracleButton_activeTextColor,
                        getColorByAttributeId(getContext(), R.attr.colorOnPrimary));
                disabledTextColor = a.getInt(R.styleable.MiracleButton_disabledTextColor,
                        getColorByAttributeId(getContext(),
                                secondary ? R.attr.colorSecondary : R.attr.colorPrimary));
                activeImageColor = a.getInt(R.styleable.MiracleButton_activeImageColor,
                        getColorByAttributeId(getContext(), R.attr.colorOnPrimary));
                disabledImageColor = a.getInt(R.styleable.MiracleButton_disabledImageColor,
                        getColorByAttributeId(getContext(),
                                secondary ? R.attr.colorSecondary : R.attr.colorPrimary));

                if (getBackground() == null) {
                    setBackground(ResourcesCompat.getDrawable(getResources(),
                            R.drawable.rounded_button_bg_white, getContext().getTheme()));
                }
            }
        }

        inflater = LayoutInflater.from(getContext());

        String text = a.getString(R.styleable.MiracleButton_text);
        if(text!=null&&!text.isEmpty()){
            setText(text);
        }

        int imageResourceId = a.getResourceId(R.styleable.MiracleButton_imageResource,0);
        if(imageResourceId>0){
            setImageResource(imageResourceId);
        }

        a.recycle();

    }


    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {

        if(child instanceof ImageView){
            imageView = (ImageView) child;
        } else if(child instanceof TextView){
            textView = (TextView) child;
        }

        updateMargins();
        super.addView(child, index, params);

    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (active) {
            mergeDrawableStates(drawableState, ACTIVE_STATE_SET);
        }
        return drawableState;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        final Drawable buttonDrawable = getBackground();
        if (buttonDrawable != null && buttonDrawable.isStateful()
                && buttonDrawable.setState(getDrawableState())) {
            invalidateDrawable(buttonDrawable);
        }
    }

    ////////////////////////////////

    public void setActive(boolean active) {
        setActive(active, true);
    }

    public void setActive(boolean active,boolean animate) {
        if(active!=this.active) {
            float newFraction;
            if (active) {
                newFraction = 1f;
            } else {
                newFraction = 0f;
            }
            this.active = active;
            if (animate) {
                animateToFraction(newFraction);
            } else {
                setFraction(newFraction);
                invalidate();
            }
        }
    }

    private void animateToFraction(float toFraction) {
        ValueAnimator animator = ValueAnimator.ofFloat(fraction, toFraction);
        animator.addUpdateListener(animation -> setFraction((float) animation.getAnimatedValue()));
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();
    }

    private void setFraction(float fraction) {
        this.fraction = fraction;
        updateColor(fraction);
    }

    private void updateColor(float fraction) {
        if(getBackground()!=null) {
            setBackgroundColor((int) colorEvaluator.evaluate(fraction, disabledBackgroundColor, activeBackgroundColor));
        }
        if(imageView!=null){
            setImageColor((int) colorEvaluator.evaluate(fraction, disabledImageColor, activeImageColor));
        }
        if(textView!=null) {
            setTextColor((int) colorEvaluator.evaluate(fraction, disabledTextColor, activeTextColor));
        }
    }

    @Override
    public void setBackgroundColor(int backgroundColor){
        getBackground().setColorFilter(new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.SRC_IN));
    }

    private void setImageColor(int imageColor){
        imageView.setColorFilter(new PorterDuffColorFilter(imageColor, PorterDuff.Mode.SRC_IN));
    }

    private void setTextColor(int textColor){
        textView.setTextColor(textColor);
    }

    ////////////////////////////////

    private void updateMargins(){

        switch (imageGravity){
            case 0:{
                if (textView != null && imageView != null) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = imageSpacing;
                    layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                } else {
                    if (textView != null) {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 0;
                    }else if(imageView != null){
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 0;
                    }
                }
                break;
            }
            case 1:{
                if (textView != null && imageView != null) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    layoutParams.leftMargin = imageSpacing;
                    layoutParams.rightMargin = 0;
                    layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                    layoutParams.leftMargin = 0;
                    layoutParams.rightMargin = 0;
                } else {
                    if (textView != null) {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 0;
                    }else if(imageView != null){
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                        layoutParams.leftMargin = 0;
                        layoutParams.rightMargin = 0;
                    }
                }
                break;
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public ImageView getImageView() {
        return imageView;
    }

    @Override
    public void setBackground(Drawable background) {
        super.setBackground(background);
        setBackgroundColor((int) colorEvaluator.evaluate(fraction, disabledBackgroundColor, activeBackgroundColor));
    }

    public void setText(String text){
        if(text==null||text.isEmpty()){
            if(textView!=null){
                if(textView.getVisibility()!=GONE) {
                    textView.setVisibility(GONE);
                }
            }
        }else {
            if(textView==null){
                TextView textView = (TextView) inflater.inflate(R.layout.miracle_button_text, this, false);

                switch (imageGravity){
                    case 0:{
                        addView(textView);
                        break;
                    }
                    case 1:{
                        addView(textView, 0);
                        break;
                    }
                }
                setTextColor((int) colorEvaluator.evaluate(fraction, disabledTextColor, activeTextColor));
            }
            textView.setText(text);
            if(textView.getVisibility()!=VISIBLE) {
                textView.setVisibility(VISIBLE);
            }
        }
    }

    public void setImageResource(Drawable drawable){
        if(drawable==null){
            if(imageView!=null){
                if(imageView.getVisibility()!=GONE) imageView.setVisibility(GONE);
            }
        }else {
            if(imageView==null){
                ImageView imageView = (ImageView) inflater.inflate(R.layout.miracle_button_image, this, false);

                switch (imageGravity){
                    case 0:{
                        addView(imageView,0);
                        break;
                    }
                    case 1:{
                        addView(imageView);
                        break;
                    }
                }
                setImageColor((int) colorEvaluator.evaluate(fraction, disabledImageColor, activeImageColor));
            }
            imageView.setImageDrawable(drawable);
            if(imageView.getVisibility()!=VISIBLE) imageView.setVisibility(VISIBLE);
        }
    }

    public void setImageResource(int imageResourceId){
        if(imageResourceId>0){
            setImageResource(ResourcesCompat.getDrawable(getContext().getResources(), imageResourceId, getContext().getTheme()));
        }else {
            setImageResource(null);
        }
    }

}
