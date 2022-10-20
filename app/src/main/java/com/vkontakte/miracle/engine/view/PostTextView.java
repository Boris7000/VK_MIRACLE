package com.vkontakte.miracle.engine.view;

import android.content.Context;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.vkontakte.miracle.engine.view.textView.MiracleTextView;

public class PostTextView extends LinearLayout {

    private boolean initialized =false;
    private MiracleTextView postTextView;
    private TextView more;
    private ViewGroup parent;

    public PostTextView(Context context) { super(context); }
    public PostTextView(@NonNull Context context, @Nullable AttributeSet attrs) { super(context, attrs); }

    @Override
    protected void onFinishInflate() { super.onFinishInflate();  if(!initialized)init();}

    public void init(){
        initialized=true;
        postTextView = (MiracleTextView) getChildAt(0);
        more =  (TextView) getChildAt(1);
        more.setOnClickListener(v -> {
            TransitionManager.beginDelayedTransition(parent==null?(ViewGroup) getParent().getParent():parent);
            postTextView.setMaxLines(Integer.MAX_VALUE); more.setVisibility(GONE);
        });
    }

    public void setParent(ViewGroup parent) {
        this.parent = parent;
    }

    public void setText(String text, boolean big_text) {
        if (initialized) {
            init();
        }

        TransitionManager.endTransitions(parent == null ? (ViewGroup) getParent().getParent() : parent);

        postTextView.setText(text);
        //postTextView.post(() -> {
            int lines = postTextView.getLineCount();

            if (big_text) {
                postTextView.setTextSize(18);
                more.setVisibility(GONE);
            } else {
                postTextView.setTextSize(14);
                if (lines > 16 || (text.length() > 500 && lines < 6)) {
                    postTextView.setMaxLines(6);
                    more.setVisibility(VISIBLE);
                } else {
                    postTextView.setMaxLines(500);
                    more.setVisibility(GONE);
                }
            }
       // });
    }

    public MiracleTextView getPostTextView() {
        return postTextView;
    }
}