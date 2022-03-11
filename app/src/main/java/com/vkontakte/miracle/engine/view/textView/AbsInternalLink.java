package com.vkontakte.miracle.engine.view.textView;

import androidx.annotation.NonNull;

public class AbsInternalLink {

    public int start;
    public int end;

    public String targetLine;

    @NonNull
    @Override
    public String toString() {
        return "AbsInternalLink{" +
                "start=" + start +
                ", end=" + end +
                ", targetLine='" + targetLine + '\'' +
                '}';
    }
}