package com.vkontakte.miracle.engine.view.textView;

import androidx.annotation.NonNull;

public class TopicLink extends AbsInternalLink {

    public String replyToOwner;
    public String topicOwnerId;
    public String replyToCommentId;

    @NonNull
    @Override
    public String toString() {
        return "TopicLink{" +
                "replyToOwner=" + replyToOwner +
                ", topicOwnerId=" + topicOwnerId +
                ", replyToCommentId=" + replyToCommentId +
                "} " + super.toString();
    }
}