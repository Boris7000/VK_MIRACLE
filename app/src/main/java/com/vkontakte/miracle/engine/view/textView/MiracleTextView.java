package com.vkontakte.miracle.engine.view.textView;

import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;

import com.vkontakte.miracle.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MiracleTextView extends androidx.appcompat.widget.AppCompatTextView implements ClickableForegroundColorSpan.OnHashTagClickListener,ClickableForegroundColorSpan.OnOwnerLinkClickListener {


    private List<Character> mAdditionalHashTagChars;
    private OnHashTagClickListener mOnHashTagClickListener;
    private OnOwnerLinkClickListener mOnOwnerLinkClickListener;
    private boolean mDisplayHashTags, mDisplayOwnerLinks;
    private int mHashTagWordColor;

    private static final Pattern URL_PATTERN = Pattern.compile("((http|https|rstp)://\\S*)");
    private static final Pattern  ownerPattern = Pattern.compile("\\[(((id|club)(\\d+))|((http|https|rstp)://\\S*))\\|([^]]+)]");

    public MiracleTextView(Context context) {
        this(context, null);
    }

    public MiracleTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mAdditionalHashTagChars = new ArrayList<>(2);
        this.mAdditionalHashTagChars.add('_');
        this.mAdditionalHashTagChars.add('@');

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MiracleTextView);
            try {
                mHashTagWordColor = a.getColor(R.styleable.MiracleTextView_hashTagColor, getColorByAttributeId(context,R.attr.colorPrimary));
                mDisplayHashTags = a.getBoolean(R.styleable.MiracleTextView_displayHashTags, false);
                mDisplayOwnerLinks = a.getBoolean(R.styleable.MiracleTextView_displayOwnerLinks, false);
            } finally {
                a.recycle();
            }
        }

        setText(getText());
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

    private void setColorForHashTagToTheEnd(Spannable s, int startIndex, int nextNotLetterDigitCharIndex) {
        CharacterStyle span;

        if (mOnHashTagClickListener != null) {
            span = new ClickableForegroundColorSpan(mHashTagWordColor, this);
        } else {
            // no need for clickable span because it is messing with selection when click
            span = new ForegroundColorSpan(mHashTagWordColor);
        }

        s.setSpan(span, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private Spannable setColorsToAllOwnerLinks(CharSequence originalText) {
        List<OwnerLink> ownerLinks = findOwnersLinks(originalText);
        int count = safeCountOfMultiple(ownerLinks);
        if(count > 0){
            List<AbsInternalLink> all = new ArrayList<>(count);

            if(!ownerLinks.isEmpty()){
                all.addAll(ownerLinks);
            }

            all.sort(LINK_COMPARATOR);

            Spannable result = Spannable.Factory.getInstance().newSpannable(replace(originalText, all));
            for (final AbsInternalLink link : all) {
                setColorForOwnerLinkToTheEnd(result, link.start, link.end);
            }

            return result;
        }else return  SpannableStringBuilder.valueOf(originalText);
    }

    private void setColorForOwnerLinkToTheEnd(Spannable s, int startIndex, int nextNotLetterDigitCharIndex) {
        CharacterStyle span;

        if (mOnOwnerLinkClickListener != null) {
            span = new ClickableForegroundColorSpan(mHashTagWordColor, this::onOwnerLinkClicked);
        } else {
            // no need for clickable span because it is messing with selection when click
            span = new ForegroundColorSpan(mHashTagWordColor);
        }

        s.setSpan(span, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void setText(CharSequence originalText, BufferType type) {
        if (originalText != null && originalText.length() > 0) {

            Spannable spannable;

            if (mDisplayOwnerLinks) {
                spannable =  setColorsToAllOwnerLinks(originalText);
            } else {
                spannable = SpannableStringBuilder.valueOf(originalText);
            }

            if (mDisplayHashTags) {
                setColorsToAllHashTags(spannable);
            }

            linkifyUrl(spannable);

            super.setText(spannable, type);
        } else {
            super.setText(originalText, type);
        }
    }

    public void linkifyUrl(Spannable spannable) {
        Matcher m = URL_PATTERN.matcher(spannable);

        while (m.find()) {

            String url = spannable.toString().substring(m.start(), m.end());

            ClickableSpan urlSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    widget.getContext().startActivity(browserIntent);
                }
            };
            spannable.setSpan(urlSpan, m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    /*
    private void eraseAndColorizeAllText(Spannable text) {
        if (getText() instanceof Spannable) {
            Spannable spannable = ((Spannable) getText());

            CharacterStyle[] spans = spannable.getSpans(0, text.length(), CharacterStyle.class);
            for (CharacterStyle span : spans) {
                spannable.removeSpan(span);
            }
        }

        setColorsToAllHashTags(text);
    }*/

    @Override
    public void onHashTagClicked(String hashTag) {
        mOnHashTagClickListener.onHashTagClicked(hashTag);
    }

    public List<String> getAllHashTags(boolean withHashes) {
        String text = getText().toString();
        Spannable spannable = (Spannable) getText();

        // use set to exclude duplicates
        Set<String> hashTags = new LinkedHashSet<>();

        for (CharacterStyle span : spannable.getSpans(0, text.length(), CharacterStyle.class)) {
            hashTags.add(text.substring(!withHashes ? spannable.getSpanStart(span) + 1 :
                    spannable.getSpanStart(span), spannable.getSpanEnd(span)));
        }

        return new ArrayList<>(hashTags);
    }

    private static List<OwnerLink> findOwnersLinks(CharSequence input) {
        List<OwnerLink> links = null;

        Matcher matcher = ownerPattern.matcher(input);
        while (matcher.find()) {
            if (links == null) {
                links = new ArrayList<>(1);
            }

            String group1 = matcher.group(1);
            String group2 = matcher.group(2);
            String group3 = matcher.group(3);
            String group7 = matcher.group(7);

            if(group1!=null&&(group1.equals("club")||group1.equals("id"))){
                boolean club = group1.equals("club");
                String ownerId = group2;
                if(club) ownerId = "-"+ownerId;
                links.add(new OwnerLink(matcher.start(), matcher.end(), ownerId, group3));}
            else {
                links.add(new OwnerLink(matcher.start(), matcher.end(), group1, group7));
            }
        }

        return links;
    }

    private static final Comparator<AbsInternalLink> LINK_COMPARATOR = Comparator.comparingInt(link -> link.start);

    private static CharSequence replace(CharSequence input, List<? extends AbsInternalLink> links) {
        if (safeIsEmpty(links)) {
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

    public static int safeCountOfMultiple(Collection<?>... collections) {
        if (collections == null) {
            return 0;
        }

        int count = 0;
        for (Collection<?> collection : collections) {
            count = count + safeCountOf(collection);
        }

        return count;
    }

    public static int safeCountOf(Collection<?> collection) {
        return collection == null ? 0 : collection.size();
    }

    public static boolean safeIsEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public void setOnHashTagClickListener(OnHashTagClickListener onHashTagClickListener) {
        this.mOnHashTagClickListener = onHashTagClickListener;
    }

    public void setOnOwnerLinkClickListener(OnOwnerLinkClickListener onOwnerLinkClickListener) {
        this.mOnOwnerLinkClickListener = onOwnerLinkClickListener;
    }

    public void setAdditionalHashTagChars(List<Character> additionalHashTagChars) {
        this.mAdditionalHashTagChars = additionalHashTagChars;
    }

    public List<String> getAllHashTags() {
        return getAllHashTags(false);
    }

    @Override
    public void onOwnerLinkClicked(String hashTag) {
        mOnOwnerLinkClickListener.onOwnerLinkClicked(hashTag);
    }

    public interface OnHashTagClickListener {
        void onHashTagClicked(String hashTag);
    }
    public interface OnOwnerLinkClickListener {
        void onOwnerLinkClicked(String ownerLink);
    }

    @Override
    public void scrollTo(int x, int y) {
        // disable scroll
        // super.scrollTo(x, y);
    }
}
