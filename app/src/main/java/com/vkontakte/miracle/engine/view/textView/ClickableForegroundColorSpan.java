package com.vkontakte.miracle.engine.view.textView;

import android.graphics.Typeface;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;

public class ClickableForegroundColorSpan extends ClickableSpan {

    private final int color;
    private final OnClickListener onClickListener;

    public ClickableForegroundColorSpan(@ColorInt int color, OnClickListener onClickListener) {
        this.color = color;
        this.onClickListener = onClickListener;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    @Override
    public void onClick(View widget) {

        CharSequence text = ((TextView) widget).getText();

        Spanned s = (Spanned) text;
        int start = s.getSpanStart(this);
        int end = s.getSpanEnd(this);

        if (onClickListener != null) {
            onClickListener.onClick(text.subSequence(start, end).toString());
        }
    }

    public interface OnClickListener {
        void onClick(String s);
    }
}