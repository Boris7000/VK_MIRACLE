package com.vkontakte.miracle.adapter.messages.holders;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.vkontakte.miracle.engine.util.ColorUtil.getColorByAttributeId;
import static com.vkontakte.miracle.engine.util.StringsUtil.getWordFirstChar;
import static com.vkontakte.miracle.engine.util.TimeUtil.getMessageDateString;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.adapter.holder.MiracleViewHolder;
import com.vkontakte.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.engine.util.NavigationUtil;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;
import com.vkontakte.miracle.model.messages.fields.ChatSettings;
import com.vkontakte.miracle.model.messages.fields.Peer;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.LastSeen;

public class ConversationViewHolder extends MiracleViewHolder {

    private boolean isMessageTyping = false;
    private String ownersString;

    private final ImageView imageView;
    private final TextView body;
    private final TextView from;
    private final TextView title;
    private final TextView unreadCount;
    private final TextView date;
    private final View unreadDot;
    private final ViewStub mutedStub;
    private ImageView muted;
    private final ViewStub verifiedStub;
    private ImageView verified;
    private final ViewStub onlineStatusStub;
    private ImageView onlineStatus;
    private final ViewStub imageTextStub;
    private TextView imageText;
    ///////////////////////////
    private ColorDrawable colorDrawable;
    private int colorGrayDarker = -1;
    private int colorPrimary = -1;

    public ConversationViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.photo);
        body = itemView.findViewById(R.id.body);
        from = itemView.findViewById(R.id.message_text_from);
        title = itemView.findViewById(R.id.title);
        unreadCount = itemView.findViewById(R.id.unreadCount);
        date = itemView.findViewById(R.id.date);
        mutedStub = itemView.findViewById(R.id.mutedStub);
        unreadDot = itemView.findViewById(R.id.unreadDot);
        verifiedStub = itemView.findViewById(R.id.verifiedStub);
        onlineStatusStub = itemView.findViewById(R.id.onlineStatusStub);
        imageTextStub = itemView.findViewById(R.id.imageTextStub);
    }

    @Override
    public void bind(ItemDataHolder itemDataHolder) {

        Context context = itemView.getContext();

        ConversationItem conversationItem = (ConversationItem) itemDataHolder;

        if(conversationItem.getPushSettings()!=null){
            if(muted ==null) {
                if(mutedStub !=null) {
                    muted = (ImageView) mutedStub.inflate();
                } else {
                    muted = itemView.findViewById(R.id.muted);
                }
            }
            if(muted.getVisibility()!=VISIBLE) {
                muted.setVisibility(VISIBLE);
            }
        } else {
            if(muted !=null&& muted.getVisibility()!=GONE){
                muted.setVisibility(GONE);
            }
        }

        Picasso.get().cancelRequest(imageView);

        Peer peer = conversationItem.getPeer();

        switch (peer.getType()){
            case "user":{
                setFromUser(conversationItem.getOwner());
                break;
            }
            case "group":{
                setFromGroup(conversationItem.getOwner());
                break;
            }

            case "chat": {
                setFromChat(context, conversationItem.getChatSettings());
                break;
            }
        }

        if(conversationItem.getUnreadCount()>0){
            unreadCount.setText(String.valueOf(conversationItem.getUnreadCount()));
            unreadCount.setVisibility(VISIBLE);
        } else {
            unreadCount.setVisibility(GONE);
        }

        if(isMessageTyping) {
            isMessageTyping = false;
            date.setText("");
            from.setText("");
            if(colorGrayDarker==-1){
                colorGrayDarker = getColorByAttributeId(context, R.attr.colorEmphasis_70);
            }
            body.setTextColor(colorGrayDarker);
            body.setText(ownersString);
        } else {
            MessageItem messageItem = conversationItem.getLastMessage();
            if(messageItem!=null){
                date.setText(getMessageDateString(context, messageItem.getDate()));

                if(messageItem.isOut()){
                    from.setText("Вы: ");
                } else {
                    if(peer.getType().equals("chat")){
                        Owner owner = messageItem.getOwner();
                        from.setText(String.format("%1$s: ",owner.getShortName()));
                    } else {
                        from.setText("");
                    }
                }

                if(messageItem.getText().isEmpty()){
                    if(colorPrimary==-1){
                        colorPrimary = getColorByAttributeId(context, R.attr.colorPrimary);
                    }
                    body.setTextColor(colorPrimary);
                    //TODO доделать
                    body.setText("Вложения");
                } else {
                    if(colorGrayDarker==-1){
                        colorGrayDarker = getColorByAttributeId(context, R.attr.colorEmphasis_70);
                    }
                    body.setTextColor(colorGrayDarker);
                    body.setText(messageItem.getText());
                }

                if(messageItem.isOut()&&!conversationItem.getOutRead().equals(messageItem.getId())){
                    if(unreadDot.getVisibility()!=VISIBLE){
                        unreadDot.setVisibility(VISIBLE);
                    }
                } else {
                    if(unreadDot.getVisibility()!=GONE){
                        unreadDot.setVisibility(GONE);
                    }
                }
            }
        }

        itemView.setOnClickListener(view -> NavigationUtil.goToChat(conversationItem, getContext()));

    }

    private void setFromChat(Context context, ChatSettings chatSettings){
        title.setText(chatSettings.getTitle());

        if(chatSettings.getPhoto200().isEmpty()){

            if(colorDrawable==null){
                colorDrawable = new ColorDrawable(getColorByAttributeId(context, R.attr.colorPrimary));
            }

            imageView.setImageDrawable(colorDrawable);

            if(imageText ==null) {
                if(imageTextStub !=null) {
                    imageText = (TextView) imageTextStub.inflate();
                } else {
                    imageText = itemView.findViewById(R.id.imageText);
                }
            }
            if(imageText.getVisibility()!=VISIBLE) {
                imageText.setVisibility(VISIBLE);
            }
            imageText.setText(getWordFirstChar(chatSettings.getTitle()));
        } else {
            if(imageText !=null&& imageText.getVisibility()!=GONE){
                imageText.setVisibility(GONE);
            }
            Picasso.get().load(chatSettings.getPhoto200()).into(imageView);
        }

        if(onlineStatus !=null&& onlineStatus.getVisibility()!=GONE){
            onlineStatus.setVisibility(GONE);
        }

        if(verified !=null&& verified.getVisibility()!=GONE){
            verified.setVisibility(GONE);
        }
    }

    private void setFromGroup(Owner owner){
        setFromOwner(owner);

        if(onlineStatus !=null&& onlineStatus.getVisibility()!=GONE){
            onlineStatus.setVisibility(GONE);
        }

        if(imageText !=null&& imageText.getVisibility()!=GONE){
            imageText.setVisibility(GONE);
        }
    }

    private void setFromUser(Owner owner){
        setFromOwner(owner);
        ProfileItem profileItem = owner.getProfileItem();

        if(profileItem.isOnline()) {
            if(onlineStatus ==null) {
                if(onlineStatusStub !=null) {
                    onlineStatus = (ImageView) onlineStatusStub.inflate();
                } else {
                    onlineStatus = itemView.findViewById(R.id.onlineStatus);
                }
            }
            if(onlineStatus.getVisibility()!=VISIBLE) {
                onlineStatus.setVisibility(VISIBLE);
            }

            LastSeen lastSeen = profileItem.getLastSeen();
            onlineStatus.setImageResource(lastSeen.getPlatform()==7?
                    R.drawable.ic_online_16:R.drawable.ic_online_mobile_16);
            onlineStatus.setBackgroundResource(lastSeen.getPlatform()==7?
                    R.drawable.ic_online_subtract_16 :R.drawable.ic_online_mobile_subtract_16);

        } else {
            if(onlineStatus !=null&& onlineStatus.getVisibility()!=GONE){
                onlineStatus.setVisibility(GONE);
            }
        }

        if(imageText !=null&& imageText.getVisibility()!=GONE){
            imageText.setVisibility(GONE);
        }
    }

    private void setFromOwner(Owner owner){
        title.setText(owner.getName());

        if(!owner.getPhoto200().isEmpty()) {
            Picasso.get().load(owner.getPhoto200()).into(imageView);
        }

        if(owner.isVerified()){
            if(verified==null) {
                if(verifiedStub!=null) {
                    verified = (ImageView) verifiedStub.inflate();
                } else {
                    verified = itemView.findViewById(R.id.verified);
                }
            }
            if(verified.getVisibility()!=VISIBLE) {
                verified.setVisibility(VISIBLE);
            }
        } else {
            if(verified!=null&&verified.getVisibility()!=GONE){
                verified.setVisibility(GONE);
            }
        }
    }

    public static class Fabric implements ViewHolderFabric {
        @Override
        public MiracleViewHolder create(LayoutInflater inflater, ViewGroup viewGroup) {
            return new ConversationViewHolder(inflater.inflate(R.layout.view_conversation_item, viewGroup, false));
        }
    }

    public void setIsTyping(boolean isWriting) {
        this.isMessageTyping = isWriting;
    }

    public void setOwnersString(String ownersString) {
        this.ownersString = ownersString;
    }
}
