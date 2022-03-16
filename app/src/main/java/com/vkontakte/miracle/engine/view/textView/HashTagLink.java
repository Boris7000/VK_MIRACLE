package com.vkontakte.miracle.engine.view.textView;

public class HashTagLink extends AbsInternalLink{

    public String name;

    public HashTagLink(int start, int end, String name, String targetLine) {
        this.start = start;
        this.end = end;
        this.name = name;
        this.targetLine = targetLine;
    }

}
