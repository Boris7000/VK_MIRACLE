package com.vkontakte.miracle.engine.view.textView;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.vkontakte.miracle.R;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiracleTextView extends androidx.appcompat.widget.AppCompatTextView {

    private List<Character> mAdditionalHashTagChars;
    private OnHashTagClickListener onHashTagClickListener;
    private OnOwnerClickListener onOwnerClickListener;
    private OnTopicClickListener onTopicClickListener;
    private OnOtherClickListener onOtherClickListener;
    private OnURLClickListener onURLClickListener;
    private final int highlightColor;

    private static final Pattern URL_PATTERN = Pattern.compile("((http|https|rstp)://\\S*)");
    private static final Pattern ownerPattern = Pattern.compile("\\[(id|club)(\\d+)\\|([^]]+)]");
    private static final Pattern topicCommentPattern = Pattern.compile("\\[(id|club)(\\d*):bp(-\\d*)_(\\d*)\\|([^]]+)]");
    private static final Pattern otherLinkPattern = Pattern.compile("\\[(https:[^]]+)\\|([^]]+)]");

    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    //getContext().startActivity(browserIntent);

    public MiracleTextView(Context context) {
        this(context, null);
    }

    public MiracleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mAdditionalHashTagChars = new ArrayList<>();
        this.mAdditionalHashTagChars.add('_');
        this.mAdditionalHashTagChars.add('@');

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MiracleTextView);
            try {
                highlightColor = a.getColor(R.styleable.MiracleTextView_highlightColor, getColorByAttributeId(context,R.attr.colorPrimary));
                if(a.getBoolean(R.styleable.MiracleTextView_enableClicking, false)){
                    setMovementMethod(LinkMovementMethod.getInstance());
                }
            } finally {
                a.recycle();
            }
        } else {
            highlightColor = getColorByAttributeId(context,R.attr.colorPrimary);
        }

        setText(getText());
    }

    private void setColorForHashTagToTheEnd(Spannable originalText, int startIndex, int nextNotLetterDigitCharIndex) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, s -> {
            Log.d("rijgijrgjrigj",s);
            if(onHashTagClickListener!=null){
                onHashTagClickListener.onHashTagClicked(s);
            }
        });

        originalText.setSpan(span, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColorForOwner(Spannable originalText, OwnerLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, s -> {
            Log.d("rijgijrgjrigj",link.ownerId);
            if(onOwnerClickListener!=null){
                onOwnerClickListener.onOwnerLinkClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColorForTopic(Spannable originalText, TopicLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, s -> {
            Log.d("rijgijrgjrigj",link.replyToOwner);
            if(onTopicClickListener!=null){
                onTopicClickListener.onTopicClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void setColorForOther(Spannable originalText, OtherLink link) {
        CharacterStyle span = new ClickableForegroundColorSpan(highlightColor, s -> {
            Log.d("rijgijrgjrigj",link.Link);
            if(onOtherClickListener!=null){
                onOtherClickListener.onOtherClicked(link);
            }
        });
        originalText.setSpan(span, link.start, link.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void setText(CharSequence originalText, BufferType type) {
        if (originalText != null && originalText.length() > 0) {

            Spannable spannable = SpannableStringBuilder.valueOf(originalText);

            List<OwnerLink> ownerLinks = findOwnersLinks(spannable);
            List<TopicLink> topicLinks = findTopicLinks(spannable);
            List<OtherLink> othersLinks = findOthersLinks(spannable);
            List<AbsInternalLink> all = new ArrayList<>();
            all.addAll(ownerLinks);
            all.addAll(topicLinks);
            all.addAll(othersLinks);

            all.sort(LINK_COMPARATOR);

            spannable = Spannable.Factory.getInstance().newSpannable(replace(spannable, all));

            for (OwnerLink link : ownerLinks) {
                setColorForOwner(spannable,link);
            }
            for (TopicLink link : topicLinks) {
                setColorForTopic(spannable,link);
            }
            for (OtherLink link : othersLinks) {
                setColorForOther(spannable,link);
            }

            setColorsToAllHashTags(spannable);

            linkifyUrl(spannable);

            super.setText(spannable, type);

            if(getParent()!=null){
                View view = (View) getParent();
                view.requestLayout();
            }

        } else {
            super.setText(originalText, type);
        }

    }

    private void setColorsToAllHashTags(Spannable text) {
        int startIndexOfNextHashSign;

        int index = 0;
        while (index < text.length() - 1) {
            char sign = text.charAt(index);
            int nextNotLetterDigitCharIndex = index + 1; // we assume it is next. if if was not changed by findNextValidHashTagChar then index will be incremented by 1
            if (sign == '#') {
                startIndexOfNextHashSign = index;

                nextNotLetterDigitCharIndex = findNextValidHashTagChar(text, startIndexOfNextHashSign);

                setColorForHashTagToTheEnd(text, startIndexOfNextHashSign, nextNotLetterDigitCharIndex);
            }

            index = nextNotLetterDigitCharIndex;
        }
    }

    public void linkifyUrl(Spannable originalText) {
        Matcher m = URL_PATTERN.matcher(originalText);

        while (m.find()) {
            ClickableSpan span = new ClickableForegroundColorSpan(highlightColor, s -> {
                Log.d("rijgijrgjrigj",s);
                if(onURLClickListener!=null){
                    onURLClickListener.onURLClicked(s);
                }
            });
            originalText.setSpan(span, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private List<OwnerLink> findOwnersLinks(CharSequence input) {
        List<OwnerLink> links  = new ArrayList<>();
        Matcher matcher = ownerPattern.matcher(input);
        while (matcher.find()) {
            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            String group3 = matcher.group(3);

            if(group1!=null&&group2!=null&&group3!=null){
                String ownerId = "club".equals(group1) ? "-"+group2 : group2;
                links.add(new OwnerLink(matcher.start(), matcher.end(), ownerId, group3));
            }
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

    private int findNextValidHashTagChar(CharSequence text, int start) {
        int nonLetterDigitCharIndex = -1; // skip first sign '#"
        for (int index = start + 1; index < text.length(); index++) {
            char sign = text.charAt(index);
            boolean isValidSign = Character.isLetterOrDigit(sign) || mAdditionalHashTagChars.contains(sign);
            if (!isValidSign) {
                nonLetterDigitCharIndex = index;
                break;
            }
        }

        if (nonLetterDigitCharIndex == -1) {
            // we didn't find non-letter. We are at the end of text
            nonLetterDigitCharIndex = text.length();
        }

        return nonLetterDigitCharIndex;
    }

    private static final Comparator<AbsInternalLink> LINK_COMPARATOR = Comparator.comparingInt(link -> link.start);

    private static CharSequence replace(CharSequence input, List<? extends AbsInternalLink> links) {
        if (links == null || links.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder(input);
        for (int y = 0; y < links.size(); y++) {
            AbsInternalLink link = links.get(y);
            int origLength = link.end - link.start;
            int newLength = link.targetLine.length();
            shiftLinks(links, link, origLength - newLength);
            result.replace(link.start, link.end, link.targetLine);
            link.end = link.end - (origLength - newLength);
        }

        return result;
    }

    private static void shiftLinks(List<? extends AbsInternalLink> links, AbsInternalLink after, int count) {
        boolean shiftAllowed = false;
        for (AbsInternalLink link : links) {
            if (shiftAllowed) {
                link.start = link.start - count;
                link.end = link.end - count;
            }

            if (link == after) {
                shiftAllowed = true;
            }
        }
    }

    public void setAdditionalHashTagChars(List<Character> additionalHashTagChars) {
        this.mAdditionalHashTagChars = additionalHashTagChars;
    }

    public void setOnHashTagClickListener(OnHashTagClickListener mOnHashTagClickListener) {
        this.onHashTagClickListener = mOnHashTagClickListener;
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
        void onHashTagClicked(String hashTag);
    }

    public interface OnOwnerClickListener {
        void onOwnerLinkClicked(OwnerLink ownerLink);
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
