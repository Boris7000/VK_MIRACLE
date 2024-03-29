package com.vkontakte.miracle.fragment.messages;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.miracle.engine.util.ColorUtil.getColorByAttributeId;
import static com.vkontakte.miracle.engine.util.APIUtil.createOwnersMap;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.StringsUtil.getMembersDeclensions;
import static com.vkontakte.miracle.engine.util.StringsUtil.getMessageTypingDeclensions;
import static com.vkontakte.miracle.engine.util.StringsUtil.getTrimmed;
import static com.vkontakte.miracle.engine.util.StringsUtil.getWordFirstChar;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.message;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.async.AsyncExecutor;
import com.miracle.engine.async.ExecutorConveyor;
import com.miracle.engine.fragment.base.templates.BaseRecyclerFragment;
import com.squareup.picasso.Picasso;
import com.vkontakte.miracle.MainActivity;
import com.vkontakte.miracle.MainApp;
import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.messages.ChatAdapter;
import com.vkontakte.miracle.adapter.messages.ReplySwipeCallback;
import com.vkontakte.miracle.engine.context.ContextExtractor;
import com.vkontakte.miracle.engine.util.LargeDataStorage;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.engine.util.TimeUtil;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;
import com.vkontakte.miracle.model.messages.fields.ChatSettings;
import com.vkontakte.miracle.model.messages.fields.PushSettings;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.model.users.fields.LastSeen;
import com.vkontakte.miracle.network.api.Message;
import com.vkontakte.miracle.network.api.Users;
import com.vkontakte.miracle.service.longpoll.LongPollServiceController;
import com.vkontakte.miracle.service.longpoll.listeners.OnMessageTypingUpdateListener;
import com.vkontakte.miracle.service.longpoll.listeners.OnUserOnlineUpdateListener;
import com.vkontakte.miracle.service.longpoll.model.MessageTypingUpdate;
import com.vkontakte.miracle.service.longpoll.model.UserOnlineUpdate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class FragmentChat extends BaseRecyclerFragment {

    private View rootView;
    private MainApp mainApp;
    private User user;

    private AppCompatEditText messageEditText;
    private ImageView sendButton;

    private ImageView imageView;
    private TextView status;

    private ViewStub mutedStub;
    private ImageView muted;
    private ViewStub verifiedStub;
    private ImageView verified;
    private ViewStub onlineStatusStub;
    private ImageView onlineStatus;
    private ViewStub imageTextStub;
    private TextView imageText;

    private ConversationItem conversationItem;

    private MessageTypingUpdates executor;
    private OnMessageTypingUpdateListener onMessageTypingUpdateListener;
    private OnUserOnlineUpdateListener onUserOnlineUpdateListener;
    private final ArrayMap<String,Owner> ownerArrayMap = new ArrayMap<>();
    private final ExecutorConveyor<Boolean> executorConveyor = new ExecutorConveyor<>();

    private boolean voiceSendButtonMode = true;

    public void setConversationItem(ConversationItem conversationItem) {
        this.conversationItem = conversationItem;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MainActivity mainActivity = ContextExtractor.extractMainActivity(getContext());
        if(mainActivity!=null) {
            mainActivity.hideNavigationBars();
        }

        mainApp = MainApp.getInstance();

        rootView = super.onCreateView(inflater, container, savedInstanceState);


        user = StorageUtil.get().currentUser();
        ownerArrayMap.put(user.getId(), new Owner(user));


        onMessageTypingUpdateListener = messageTypingUpdates -> {
            ArrayList<ArrayList<MessageTypingUpdate>> arrayLists = new ArrayList<>();
            if (messageTypingUpdates.size() > 1) {
                ArrayMap<String, String> arrayMap = new ArrayMap<>();
                for (int j = 0; j < messageTypingUpdates.size(); j++) {
                    MessageTypingUpdate messageTypingUpdate = messageTypingUpdates.get(j);
                    if (!arrayMap.containsKey(messageTypingUpdate.getPeerId())) {
                        arrayMap.put(messageTypingUpdate.getPeerId(), messageTypingUpdate.getPeerId());
                        if(conversationItem.getPeer().getLocalId().equals(messageTypingUpdate.getPeerId())){
                            ArrayList<MessageTypingUpdate> arrayList = new ArrayList<>();
                            arrayList.add(messageTypingUpdate);
                            for (int i = j + 1; i < messageTypingUpdates.size(); i++) {
                                MessageTypingUpdate messageTypingUpdate1 = messageTypingUpdates.get(i);
                                if (messageTypingUpdate1.getPeerId().equals(messageTypingUpdate.getPeerId())) {
                                    arrayList.add(messageTypingUpdate1);
                                }
                            }
                            arrayLists.add(arrayList);
                        }
                    }
                }
            } else {
                if (messageTypingUpdates.size() == 1) {
                    ArrayList<MessageTypingUpdate> arrayList = new ArrayList<>();
                    arrayList.add(messageTypingUpdates.get(0));
                    arrayLists.add(arrayList);
                }
            }

            for (ArrayList<MessageTypingUpdate> list:arrayLists) {
                MessageTypingUpdate messageTypingUpdate = list.get(0);
                ArrayList<String> typingIds = new ArrayList<>();
                for (int i=0;i<list.size();i++){
                    typingIds.addAll(messageTypingUpdate.getFromIds());
                }
                loadTyping(typingIds, messageTypingUpdate.isText(), conversationItem);
            }

        };

        LongPollServiceController.get().addOnMessageTypingListener(onMessageTypingUpdateListener);

        return rootView;
    }

    @Override
    public void findViews(@NonNull View rootView) {
        super.findViews(rootView);
        imageView = rootView.findViewById(R.id.photo);
        status = rootView.findViewById(R.id.chatStatus);
        mutedStub = rootView.findViewById(R.id.mutedStub);
        verifiedStub = rootView.findViewById(R.id.verifiedStub);
        onlineStatusStub = rootView.findViewById(R.id.onlineStatusStub);
        imageTextStub = rootView.findViewById(R.id.imageTextStub);
        messageEditText = rootView.findViewById(R.id.messageEditText);
        sendButton = rootView.findViewById(R.id.sendButton);
    }

    @CallSuper
    @Override
    public void initViews(@NonNull View rootView, @Nullable Bundle savedInstanceState){
        super.initViews(rootView, savedInstanceState);

        switch (conversationItem.getPeer().getType()){
            case "user":{
                updateAndSetFromUser();
                break;
            }
            case "group":{
                updateAndSetFromGroup();
                break;
            }
            case "chat": {
                updateAndSetFromChat();
                break;
            }
        }

        new ItemTouchHelper(new ReplySwipeCallback(mainApp, position -> Log.d("ABOBA","SWIPED")))
                .attachToRecyclerView(getRecyclerView());

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                if(getTrimmed(messageEditText).length()>0){
                    if (voiceSendButtonMode) {
                        voiceSendButtonMode = false;
                        if(sendButton.getAnimation()!=null) {
                            sendButton.getAnimation().cancel();
                        }

                        animateSendButton(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_send_28, mainApp.getTheme()));

                        sendButton.setOnClickListener(view -> {
                            final String message = getTrimmed(messageEditText);
                            executorConveyor.addAsyncExecutor(new AsyncExecutor<Boolean>() {
                                @Override
                                public Boolean inBackground() {
                                    try {
                                        Response<JSONObject> response =  message().send(conversationItem.getPeer().getId(),
                                                message, null,null,
                                                System.currentTimeMillis(), user.getAccessToken(),latest_api_v).execute();
                                        JSONObject jsonObject = validateBody(response);
                                        int message_id = jsonObject.getInt("response");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return true;
                                }
                                @Override
                                public void onExecute(Boolean object) {

                                }
                            });
                            messageEditText.setText("");
                        });
                    }
                } else {
                    if (!voiceSendButtonMode) {
                        voiceSendButtonMode = true;
                        if(sendButton.getAnimation()!=null) {
                            sendButton.getAnimation().cancel();
                        }

                        animateSendButton(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_voice_28, mainApp.getTheme()));

                        sendButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
                    }
                }
            }
        };
        messageEditText.addTextChangedListener(textWatcher);
    }



    @Override
    public boolean scrollAndElevateEnabled() {
        return false;
    }

    @Override
    public boolean reverseRecyclerAdapter() {
        return true;
    }

    @Override
    public RecyclerView.Adapter<?> onCreateRecyclerAdapter() {
        ChatAdapter chatAdapter = new ChatAdapter(conversationItem);
        chatAdapter.setMessageAddedListener(messageItem -> addTask(new Task() {
            @Override
            public void func() {
                updateConversationLastMessage(messageItem);
                onComplete();
            }
        }));
        return chatAdapter;
    }

    @Override
    public String requestTitleText() {
        switch (conversationItem.getPeer().getType()){
            case "user":
            case "group":{
                Owner owner = conversationItem.getOwner();
                return owner.getName();
            }
            case "chat": {
                ChatSettings chatSettings = conversationItem.getChatSettings();
                return chatSettings.getTitle();
            }
        }
        return super.requestTitleText();
    }

    @NonNull
    @Override
    public View inflateRootView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    private void loadTyping(ArrayList<String> typingIds, boolean isText, ConversationItem conversationItem){

        String ownerId = typingIds.get(0);

        switch (conversationItem.getPeer().getType()) {
            case "user":
            case "group": {
                addTask(new Task() {
                    @Override
                    public void func() {
                        if(isText) {
                            updateConversationMessageTyping(mainApp.getString(R.string.formatted_typing), typingIds, true);
                        } else {
                            updateConversationMessageTyping(mainApp.getString(R.string.formatted_typing_audio), typingIds, false);
                        }
                        onComplete();
                    }
                });
                break;
            }
            case "chat": {
                Owner owner = ownerArrayMap.get(ownerId);
                if(owner==null){
                    addTask(new Task() {
                        @Override
                        public void func() {
                            new AsyncExecutor<ProfileItem>(){
                                @Override
                                public ProfileItem inBackground() {
                                    try {
                                        Response<JSONObject> response = Users.getWithMessageFields(ownerId, user.getAccessToken()).execute();
                                        JSONObject jo_response = validateBody(response);
                                        JSONArray profiles = jo_response.getJSONArray("response");

                                        if(profiles.length()>0){
                                            return new ProfileItem(profiles.getJSONObject(0));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                public void onExecute(ProfileItem profileItem) {
                                    if(profileItem!=null){
                                        Owner owner = new Owner(profileItem);
                                        ownerArrayMap.put(ownerId,owner);
                                        updateConversationMessageTyping(getMessageTypingDeclensions(getContext(), owner, isText,
                                                typingIds.size()-1), typingIds, isText);
                                    }
                                    onComplete();
                                }
                            }.start();
                        }
                    });
                } else {
                    addTask(new Task() {
                        @Override
                        public void func() {
                            updateConversationMessageTyping(getMessageTypingDeclensions(getContext(),owner, isText,
                                    typingIds.size()-1), typingIds, isText);
                            onComplete();
                        }
                    });
                }
            }
        }
    }

    private void updateConversationMessageTyping(String ownersString, ArrayList<String> typingIds, boolean isText){
        if(executor!=null){
            executor.setIsTyping(ownersString, typingIds, isText);
        } else {
            executor = new MessageTypingUpdates(ownersString, typingIds, isText);
            executor.start();
        }
    }

    private void updateConversationLastMessage(MessageItem messageItem){
        if (conversationItem != null) {
            conversationItem.setLastMessage(messageItem);

            if(executor!=null){
                ArrayList<String> typingIds = executor.getTypingIds();
                typingIds.remove(messageItem.getOwner().getId());
                if(!typingIds.isEmpty()){
                    loadTyping(typingIds, executor.isText, conversationItem);
                } else {
                    setNotTyping();
                    executor.cancel();
                    executor = null;
                }
            } else {
                setNotTyping();
            }

            ChatAdapter chatAdapter = (ChatAdapter) getRecyclerView().getAdapter();

            if(chatAdapter!=null){
                if (!chatAdapter.getRecyclerView().canScrollVertically(1)) {
                    chatAdapter.getRecyclerView().scrollToPosition(0);
                }
            }
        }
    }

    private void updateConversation() throws Exception {
        Response<JSONObject> response =  Message.getConversationById(conversationItem.getPeer().getId(),
                user.getAccessToken()).execute();

        JSONObject jsonObject = validateBody(response).getJSONObject("response");

        ArrayMap<String,Owner> ownerArrayMap = createOwnersMap(jsonObject);

        this.ownerArrayMap.putAll(ownerArrayMap);

        JSONArray jsonArray = jsonObject.getJSONArray("items");

        conversationItem = new ConversationItem(jsonArray.getJSONObject(0), ownerArrayMap);
    }

    //
    private void updateAndSetFromChat(){
        if(onlineStatus !=null&& onlineStatus.getVisibility()!=GONE){
            onlineStatus.setVisibility(GONE);
        }

        if(verified !=null&& verified.getVisibility()!=GONE){
            verified.setVisibility(GONE);
        }

        setInfoFromChat(conversationItem.getChatSettings());
        setStatusFromChat(conversationItem.getChatSettings());

        new AsyncExecutor<Boolean>(){
            @Override
            public Boolean inBackground() {
                try {
                    updateConversation();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            public void onExecute(Boolean object) {
                if(object){
                    setInfoFromChat(conversationItem.getChatSettings());
                    setStatusFromChat(conversationItem.getChatSettings());
                }
            }
        }.start();
    }

    private void setInfoFromChat(ChatSettings chatSettings){
        if(chatSettings.getPhoto200().isEmpty()){
            ColorDrawable colorDrawable = new ColorDrawable(getColorByAttributeId(mainApp, R.attr.colorSecondary));
            imageView.setImageDrawable(colorDrawable);

            if(imageText ==null) {
                if(imageTextStub !=null) {
                    imageText = (TextView) imageTextStub.inflate();
                } else {
                    imageText = rootView.findViewById(R.id.imageText);
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
    }

    private void setStatusFromChat(ChatSettings chatSettings){
        setStatus(getMembersDeclensions(mainApp, chatSettings.getMembersCount()));
    }

    //
    private void updateAndSetFromGroup(){

        if(onlineStatus !=null&& onlineStatus.getVisibility()!=GONE){
            onlineStatus.setVisibility(GONE);
        }

        if(imageText !=null&& imageText.getVisibility()!=GONE){
            imageText.setVisibility(GONE);
        }

        updateInfoFromOwner(conversationItem.getOwner());
        setStatusFromGroup();

        new AsyncExecutor<Boolean>(){
            @Override
            public Boolean inBackground() {
                try {
                    updateConversation();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onExecute(Boolean object) {
                if(object){
                    updateInfoFromOwner(conversationItem.getOwner());
                }
            }
        }.start();

    }

    private void setStatusFromGroup(){
        setStatus(mainApp.getString(R.string.group));
    }

    //
    private void updateAndSetFromUser(){

        if(imageText !=null&& imageText.getVisibility()!=GONE){
            imageText.setVisibility(GONE);
        }

        updateInfoFromOwner(conversationItem.getOwner());
        setStatusFromUser(conversationItem.getOwner().getProfileItem());
        updateOnline(conversationItem.getOwner().getProfileItem());

        new AsyncExecutor<Boolean>(){
            @Override
            public Boolean inBackground() {
                try {
                    updateConversation();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public void onExecute(Boolean object) {
                if(object){
                    updateInfoFromOwner(conversationItem.getOwner());
                    setStatusFromUser(conversationItem.getOwner().getProfileItem());
                    updateOnline(conversationItem.getOwner().getProfileItem());
                }
            }
        }.start();

        onUserOnlineUpdateListener = userOnlineUpdates -> {
            ProfileItem profileItem = conversationItem.getOwner().getProfileItem();
            UserOnlineUpdate userOnlineUpdate = userOnlineUpdates.get(profileItem.getId());
            if (userOnlineUpdate != null) {
                profileItem.setOnline(userOnlineUpdate.isOnline());
                profileItem.setLastSeen(new LastSeen(userOnlineUpdate.getTs(), userOnlineUpdate.isOnline()?
                        userOnlineUpdate.getExtra() % 256 : profileItem.getLastSeen().getPlatform()));
                updateOnline(profileItem);
                if (executor == null || executor.canceled) {
                    setStatusFromUser(profileItem);
                }
            }
        };
        LongPollServiceController.get().addOnUserOnlineListener(onUserOnlineUpdateListener);

    }

    private void setStatusFromUser(ProfileItem profileItem){
        if(profileItem.isOnline()){
            setStatus(mainApp.getString(R.string.online));
        } else {
            if(profileItem.getLastSeen()!=null) {
                LastSeen lastSeen = profileItem.getLastSeen();
                setStatus(TimeUtil.getOnlineDateString(mainApp, lastSeen.getTime(), profileItem.getSex()));
            }
        }
    }

    private void updateOnline(ProfileItem profileItem){
        if(profileItem.isOnline()){
            if(onlineStatus==null) {
                if(onlineStatusStub!=null) {
                    onlineStatus = (ImageView) onlineStatusStub.inflate();
                } else {
                    onlineStatus = rootView.findViewById(R.id.onlineStatus);
                }
            }
            if(onlineStatus.getVisibility()!=VISIBLE) {
                onlineStatus.setVisibility(VISIBLE);
            }

            int platform = profileItem.getLastSeen().getPlatform();

            onlineStatus.setImageResource(platform==7?R.drawable.ic_online_16
                    :R.drawable.ic_online_mobile_16);
            onlineStatus.setBackgroundResource(platform==7?R.drawable.ic_online_subtract_16
                    :R.drawable.ic_online_mobile_subtract_16);
        } else {
            if(onlineStatus!=null&&onlineStatus.getVisibility()!=GONE){
                onlineStatus.setVisibility(GONE);
            }
        }
    }

    private void updateInfoFromOwner(Owner owner){
        if(!owner.getPhoto200().isEmpty()) {
            Picasso.get().load(owner.getPhoto200()).into(imageView);
        }

        if(owner.isVerified()){
            if(verified==null) {
                if(verifiedStub!=null) {
                    verified = (ImageView) verifiedStub.inflate();
                } else {
                    verified = rootView.findViewById(R.id.verified);
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

    private void setStatus(String text){
        status.setText(text);
    }

    private void updateMuted(PushSettings pushSettings){
        if(pushSettings.isDisabledForever()||pushSettings.isDisabledMentions()
                ||pushSettings.isDisabledMassMentions()||pushSettings.isNoSound()){
            if(muted ==null) {
                if(mutedStub !=null) {
                    muted = (ImageView) mutedStub.inflate();
                } else {
                    muted = rootView.findViewById(R.id.muted);
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
    }

    private void animateSendButton(Drawable drawable){
        final float old = sendButton.getScaleX();
        ValueAnimator animator = ValueAnimator.ofFloat(old, 0.1f);
        animator.addUpdateListener(valueAnimator -> {
            sendButton.setScaleX((Float) valueAnimator.getAnimatedValue());
            sendButton.setScaleY((Float) valueAnimator.getAnimatedValue());
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                sendButton.setImageDrawable(drawable);
                final float old = sendButton.getScaleX();
                ValueAnimator animator = ValueAnimator.ofFloat(old, 1);
                animator.addUpdateListener(valueAnimator -> {
                    sendButton.setScaleX((Float) valueAnimator.getAnimatedValue());
                    sendButton.setScaleY((Float) valueAnimator.getAnimatedValue());
                });
                animator.setDuration(150);
                animator.start();
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(150);
        animator.start();
    }

    @Override
    public void onDestroy() {
        MainActivity mainActivity = ContextExtractor.extractMainActivity(getContext());
        if(mainActivity!=null) {
            mainActivity.showNavigationBars();
        }
        LongPollServiceController longPollServiceController = LongPollServiceController.get();
        longPollServiceController.removeOnMessageTypingListener(onMessageTypingUpdateListener);
        if(onUserOnlineUpdateListener!=null){
            longPollServiceController.removeOnUserOnlineListener(onUserOnlineUpdateListener);
        }
        if(executor!=null){
            executor.cancel();
        }

        super.onDestroy();
    }

    @Override
    public void readSavedInstance(Bundle savedInstanceState) {
        super.readSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("conversationItem");
        if(key!=null){
            conversationItem = (ConversationItem) LargeDataStorage.get().getLargeData(key);
            savedInstanceState.remove("conversationItem");
        }
    }

    @Override
    public void onClearSavedInstance(@NonNull Bundle savedInstanceState) {
        super.onClearSavedInstance(savedInstanceState);
        String key = savedInstanceState.getString("conversationItem");
        if (key != null) {
            LargeDataStorage.get().removeLargeData(key);
            savedInstanceState.remove("conversationItem");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(conversationItem !=null){
            outState.putString("conversationItem", LargeDataStorage.get().storeLargeData(conversationItem));
        }
    }


    private class MessageTypingUpdates extends AsyncExecutor<Boolean> {

        private ArrayList<String> typingIds;
        private boolean isText;

        private final int allTime = 6000;
        private int time = allTime;
        private boolean canceled = false;

        public MessageTypingUpdates(String ownersString, ArrayList<String> typingIds, boolean isText){
            this.typingIds = typingIds;
            this.isText = isText;
            setIsTypingViewHolder(ownersString);
        }

        @Override
        public Boolean inBackground() {
            while (time>0){
                int sleepTime = 500;
                time-= sleepTime;
                if(!canceled) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public void onExecute(Boolean object) {
            if(object) {
                executor = null;
                setNotTypingViewHolder();
                cancel();
            }
        }

        private void resetTime(){
            time = allTime;
        }

        public void cancel() {
            canceled = true;
        }

        private void setNotTypingViewHolder(){
            if(!canceled) {
                addTask(new Task() {
                    @Override
                    public void func() {
                        setNotTyping();
                        onComplete();
                    }
                });
            }
        }

        private void setIsTypingViewHolder(String ownersString){
            if(!canceled) {
                setStatus(ownersString);
            }
        }

        public void setIsTyping(String ownersString, ArrayList<String> typingIds, boolean isText){
            this.typingIds = typingIds;
            this.isText = isText;
            resetTime();
            setIsTypingViewHolder(ownersString);
        }

        public ArrayList<String> getTypingIds() {
            return typingIds;
        }

        public boolean isText() {
            return isText;
        }
    }

    private void setNotTyping(){
        switch (conversationItem.getPeer().getType()){
            case "user":{
                setStatusFromUser(conversationItem.getOwner().getProfileItem());
                break;
            }
            case "group":{
                setStatusFromGroup();
                break;
            }
            case "chat": {
                setStatusFromChat(conversationItem.getChatSettings());
                break;
            }
        }
    }

    private final ArrayList<Task> tasks = new ArrayList<>();

    private void addTask(Task task){
        tasks.add(task);
        if(tasks.size()==1){
            task.func();
        }
    }

    private abstract class Task{
        public abstract void func();
        public void onComplete(){
            tasks.remove(this);
            if(!tasks.isEmpty()){
                Task task = tasks.get(0);
                if(task!=null&&task!=this) {
                    task.func();
                }
            }
        }
    }

}