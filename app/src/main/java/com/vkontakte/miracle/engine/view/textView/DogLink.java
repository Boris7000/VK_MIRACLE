package com.vkontakte.miracle.engine.view.textView;

public class DogLink extends AbsInternalLink {

    public static String TYPE_ALL = "all";
    public static String TYPE_ONLINE = "online";

    public String type;

    public DogLink(int start, int end, String type, String targetLine) {
        this.start = start;
        this.end = end;
        this.type = type;
        this.targetLine = targetLine;
    }

}
