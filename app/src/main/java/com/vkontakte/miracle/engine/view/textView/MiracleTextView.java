package com.vkontakte.miracle.engine.view.textView;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;
import static com.vkontakte.miracle.engine.util.NetworkUtil.openURLInBrowser;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;

import com.vkontakte.miracle.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiracleTextView extends androidx.appcompat.widget.AppCompatTextView {

    private OnHashTagClickListener onHashTagClickListener;
    private OnOwnerClickListener onOwnerClickListener;
    private OnDogClickListener onDogClickListener;
    private OnTopicClickListener onTopicClickListener;
    private OnOtherClickListener onOtherClickListener;
    private OnURLClickListener onURLClickListener;
    private final int highlightColor;
    private final boolean highlightUnderline;

    private static final Pattern urlPattern = Pattern.compile("((http|https|rstp)://\\S+)");
    private static final Pattern hashTagPattern = Pattern.compile("(#)([а-яА-Я\\w][а-яА-Я@\\w]+)");
    private static final Pattern dogPatter = Pattern.compile("(@)(all|online)");
    private static final Pattern ownerPattern = Pattern.compile("\\[(id|club)(\\d+)\\|([^]]+)]");
    private static final Pattern topicCommentPattern = Pattern.compile("\\[(id|club)(\\d*):bp(-\\d*)_(\\d*)\\|([^]]+)]");
    private static final Pattern otherLinkPattern = Pattern.compile("\\[(https:[^]]+)\\|([^]]+)]");

    public MiracleTextView(Context context) {
        this(context, null);
    }

    public MiracleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MiracleTextView);
            try {
                highlightColor = a.getColor(R.styleable.MiracleTextView_highlightColor,
                        getColorByAttributeId(context, R.attr.colorPrimary));
                highlightUnderline = a.getBoolean(R.styleable.MiracleTextView_highlightUnderline, false);
                if(a.getBoolean(R.styleable.MiracleTextView_enableClicking, false)){
                    setMovementMethod(LinkMovementMethod.getInstance());
                }
            } finally {
                a.recycle();
            }
        } else {
            highlightColor = getColorByAttributeId(context, R.attr.colorPrimary);
            highlightUnderline = false;
        }

        setText(getText(),BufferType.SPANNABLE);
    }


    private void setColorForHashTag(Spannable originalText, HashTagLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, highlightUnderline, s -> {
            if(onHashTagClickListener!=null){
                onHashTagClickListener.onHashTagClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColorForDog(Spannable originalText, DogLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, highlightUnderline, s -> {
            if(onDogClickListener!=null){
                onDogClickListener.onDogClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColorForOwner(Spannable originalText, OwnerLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, highlightUnderline, s -> {
            if(onOwnerClickListener!=null){
                onOwnerClickListener.onOwnerClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColorForTopic(Spannable originalText, TopicLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, highlightUnderline, s -> {
            if(onTopicClickListener!=null){
                onTopicClickListener.onTopicClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColorForOther(Spannable originalText, OtherLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, highlightUnderline, s -> {
            if(onOtherClickListener!=null){
                onOtherClickListener.onOtherClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void setText(CharSequence originalText, BufferType type) {
        if (originalText != null && originalText.length() > 0) {

            List<OwnerLink> ownerLinks = findOwnersLinks(originalText);
            List<TopicLink> topicLinks = findTopicLinks(originalText);
            List<OtherLink> othersLinks = findOthersLinks(originalText);
            List<AbsInternalLink> all = new ArrayList<>();
            all.addAll(ownerLinks);
            all.addAll(topicLinks);
            all.addAll(othersLinks);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                all.sort(Comparator.comparingInt(link -> link.start));
            } else {
                Collections.sort(all, (element, element1) -> {
                    if(element.start == element1.start) {
                        return 0;
                    } else {
                        return element.start < element1.start ? -1 : 1;
                    }
                });
            }

            SpannableStringBuilder spannable = replace(SpannableStringBuilder.valueOf(originalText), all);

            for (OwnerLink link : ownerLinks) {
                setColorForOwner(spannable,link);
            }
            for (OwnerLink link : ownerLinks) {
                setColorForOwner(spannable,link);
            }
            for (TopicLink link : topicLinks) {
                setColorForTopic(spannable,link);
            }
            for (OtherLink link : othersLinks) {
                setColorForOther(spannable,link);
            }

            List<HashTagLink> hashTagLinks = findHashTagLinks(spannable);
            List<DogLink> dogLinks = findDogLinks(spannable);

            for (HashTagLink link : hashTagLinks) {
                setColorForHashTag(spannable,link);
            }
            for (DogLink link : dogLinks) {
                setColorForDog(spannable,link);
            }

            linkifyUrl(spannable);

            super.setText(spannable, type);

        } else {
            super.setText(originalText, type);
        }

    }

    public void linkifyUrl(Spannable originalText) {
        Matcher m = urlPattern.matcher(originalText);

        while (m.find()) {
            ClickableSpan span = new ClickableForegroundColorSpan(highlightColor, highlightUnderline, s -> {
                if(onURLClickListener!=null){
                    onURLClickListener.onURLClicked(s);
                } else {
                    openURLInBrowser(s,getContext());
                }
            });
            originalText.setSpan(span, m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private List<HashTagLink> findHashTagLinks(CharSequence input) {
        List<HashTagLink> links  = new ArrayList<>();
        Matcher matcher = hashTagPattern.matcher(input);
        while (matcher.find()) {
            links.add(new HashTagLink(matcher.start(), matcher.end(), matcher.group(2), matcher.group(0)));
        }
        return links;
    }

    private List<DogLink> findDogLinks(CharSequence input) {
        List<DogLink> links  = new ArrayList<>();
        Matcher matcher = dogPatter.matcher(input);
        while (matcher.find()) {
            links.add(new DogLink(matcher.start(), matcher.end(), matcher.group(2), matcher.group(0)));
        }
        return links;
    }

    private List<OwnerLink> findOwnersLinks(CharSequence input) {
        List<OwnerLink> links  = new ArrayList<>();
        Matcher matcher = ownerPattern.matcher(input);
        while (matcher.find()) {
            String group2 = matcher.group(2);
            String ownerId = "club".equals(matcher.group(1)) ? "-"+group2 : group2;
            links.add(new OwnerLink(matcher.start(), matcher.end(), ownerId, matcher.group(3)));
        }
        return links;
    }

    private static List<TopicLink> findTopicLinks(CharSequence input) {
        Matcher matcher = topicCommentPattern.matcher(input);
        List<TopicLink> links = new ArrayList<>();
        while (matcher.find()) {
            TopicLink link = new TopicLink();
            link.start = matcher.start();
            link.end = matcher.end();
            link.replyToOwner = "club".equals(matcher.group(1)) ? "-"+matcher.group(2) : matcher.group(2);
            link.topicOwnerId = matcher.group(3);
            link.replyToCommentId = matcher.group(4);
            link.targetLine = matcher.group(5);
            links.add(link);

        }
        return links;
    }

    private static List<OtherLink> findOthersLinks(CharSequence input) {
        List<OtherLink> links = new ArrayList<>();
        Matcher matcher = otherLinkPattern.matcher(input);
        while (matcher.find()) {
            links.add(new OtherLink(matcher.start(), matcher.end(), matcher.group(1), matcher.group(2)));
        }
        return links;
    }

    private static SpannableStringBuilder replace(SpannableStringBuilder input, List<? extends AbsInternalLink> links) {
        if (links == null || links.isEmpty()) {
            return input;
        }
        for (int y = 0; y < links.size(); y++) {
            AbsInternalLink link = links.get(y);
            int origLength = link.end - link.start;
            int newLength = link.targetLine.length();
            int count = origLength - newLength;
            for (int x = y+1; x < links.size(); x++){
                AbsInternalLink nextLink = links.get(x);
                nextLink.start-=count;
                nextLink.end-=count;
            }
            input.replace(link.start, link.end, link.targetLine);
            link.end-=count;
        }

        return input;
    }


    public void setOnHashTagClickListener(OnHashTagClickListener mOnHashTagClickListener) {
        this.onHashTagClickListener = mOnHashTagClickListener;
    }

    public void setOnDogClickListener(OnDogClickListener onDogClickListener) {
        this.onDogClickListener = onDogClickListener;
    }

    public void setOnOwnerClickListener(OnOwnerClickListener mOnOwnerClickListener) {
        this.onOwnerClickListener = mOnOwnerClickListener;
    }

    public void setOnTopicClickListener(OnTopicClickListener onTopicClickListener) {
        this.onTopicClickListener = onTopicClickListener;
    }

    public void setOnOtherClickListener(OnOtherClickListener onOtherClickListener) {
        this.onOtherClickListener = onOtherClickListener;
    }

    public void setOnURLClickListener(OnURLClickListener onURLClickListener) {
        this.onURLClickListener = onURLClickListener;
    }

    public interface OnHashTagClickListener {
        void onHashTagClicked(HashTagLink hashTagLink);
    }

    public interface OnDogClickListener {
        void onDogClicked(DogLink dogLink);
    }

    public interface OnOwnerClickListener {
        void onOwnerClicked(OwnerLink ownerLink);
    }

    public interface OnTopicClickListener {
        void onTopicClicked(TopicLink ownerLink);
    }

    public interface OnOtherClickListener {
        void onOtherClicked(OtherLink otherLink);
    }

    public interface OnURLClickListener {
        void onURLClicked(String url);
    }

    @Override
    public void scrollTo(int x, int y) {
        // disable scroll
        // super.scrollTo(x, y);
    }

}
