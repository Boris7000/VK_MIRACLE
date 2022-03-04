package com.vkontakte.miracle.engine.view.textView;

public class OwnerLink extends AbsInternalLink {

    public String ownerId;

    public OwnerLink(int start, int end, String ownerId, String name) {
        this.start = start;
        this.end = end;
        this.ownerId = ownerId;
        targetLine = name;
    }
}