package com.vkontakte.miracle.adapter.messages;

import static com.vkontakte.miracle.engine.util.NetworkUtil.createOwnersMap;
import static com.vkontakte.miracle.engine.util.NetworkUtil.loadOwners;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.StringsUtil.getMessageTypingDeclensions;

import android.util.ArrayMap;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.adapter.messages.holders.ConversationViewHolder;
import com.vkontakte.miracle.engine.adapter.MiracleLoadableAdapter;
import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.longpoll.listeners.OnMessageAddedUpdateListener;
import com.vkontakte.miracle.longpoll.listeners.OnMessageReadUpdateListener;
import com.vkontakte.miracle.longpoll.listeners.OnMessageTypingUpdateListener;
import com.vkontakte.miracle.longpoll.listeners.OnUserOnlineUpdateListener;
import com.vkontakte.miracle.longpoll.model.MessageAddedUpdate;
import com.vkontakte.miracle.longpoll.model.MessageReadUpdate;
import com.vkontakte.miracle.longpoll.model.MessageTypingUpdate;
import com.vkontakte.miracle.longpoll.model.UserOnlineUpdate;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.users.fields.LastSeen;
import com.vkontakte.miracle.network.methods.Message;
import com.vkontakte.miracle.network.methods.Users;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import retrofit2.Response;

public class ConversationsAdapter extends MiracleLoadableAdapter {


    private final ArrayMap<String,ConversationItem> conversationItemArrayMap = new ArrayMap<>();
    private final ArrayMap<String,Owner> ownerArrayMap = new ArrayMap<>();
    private final ArrayMap<String, MessageTypingUpdates> messageTypingUpdatesArrayMap = new ArrayMap<>();
    private OnMessageAddedUpdateListener onMessageAddedUpdateListener;
    private OnMessageReadUpdateListener onMessageReadUpdateListener;
    private OnMessageTypingUpdateListener onMessageTypingUpdateListener;
    private OnUserOnlineUpdateListener onUserOnlineUpdateListener;

    @Override
    public void onLoading() throws Exception {

        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        setTimeStump(System.currentTimeMillis()/1000);

        Response<JSONObject> response =  Message.getConversations(holders.size(), getStep(),
                "all", getUserItem().getAccessToken()).execute();

        JSONObject jsonObject = validateBody(response);

        jsonObject = jsonObject.getJSONObject("response");

        setTotalCount(jsonObject.getInt("count"));

        ArrayMap<String, Owner> ownerArrayMap = createOwnersMap(jsonObject);

        this.ownerArrayMap.putAll(ownerArrayMap);

        JSONArray jsonArray = jsonObject.getJSONArray("items");
        setAddedCount(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject iJsonObject = jsonArray.getJSONObject(i);

            ConversationItem conversationItem = new ConversationItem(iJsonObject.getJSONObject("conversation"),ownerArrayMap);

            if(iJsonObject.has("last_message")){
                conversationItem.setLastMessage(new MessageItem(iJsonObject.getJSONObject("last_message"),ownerArrayMap));
            }

            holders.add(conversationItem);
            conversationItemArrayMap.put(conversationItem.getPeer().getLocalId(),conversationItem);
        }

        if (holders.size()==getTotalCount()||jsonArray.length()<getStep()) {
            setFinallyLoaded(true);
        }
    }

    @Override
    public void onComplete() {

        if(!hasData()) {
            ArrayList<MessageAddedUpdate> storageMessageAddedUpdates = StorageUtil.loadMessageAddedLongPollUpdates(getMiracleApp());
            ArrayList<MessageAddedUpdate> missedMessageAddedUpdates = new ArrayList<>();

            androidx.collection.ArrayMap<String,MessageItem> messageItemArrayMap = new androidx.collection.ArrayMap<>();

            for (ItemDataHolder itemDataHolder : getItemDataHolders()) {
                if(itemDataHolder instanceof ConversationItem){
                    ConversationItem conversationItem = (ConversationItem) itemDataHolder;
                    messageItemArrayMap.put(conversationItem.getLastMessage().getId(),conversationItem.getLastMessage());
                }
            }

            for (int j = 0; j < storageMessageAddedUpdates.size(); j++) {
                MessageAddedUpdate messageAddedUpdate = storageMessageAddedUpdates.get(j);
                if (messageAddedUpdate.getTs() > getTimeStump()){
                    if(!messageItemArrayMap.containsKey(messageAddedUpdate.getMessageId())) {
                        missedMessageAddedUpdates.add(messageAddedUpdate);
                    }
                } else {
                    break;
                }
            }

            super.onComplete();

            if (!missedMessageAddedUpdates.isEmpty()) {
                onMessageAddedUpdateListener.onMessageAddedUpdate(missedMessageAddedUpdates);
            }
        } else {
            super.onComplete();
        }
    }

    @Override
    public void ini() {
        super.ini();

        setStep(50);

        onMessageAddedUpdateListener = messageAddedUpdates -> {

            if(hasData()){
                ArrayList<ArrayList<MessageAddedUpdate>> arrayLists = new ArrayList<>();
                if(messageAddedUpdates.size()>1) {
                    ArrayMap<String, String> arrayMap = new ArrayMap<>();
                    for (int j = 0; j < messageAddedUpdates.size(); j++) {
                        MessageAddedUpdate messageAddedUpdate = messageAddedUpdates.get(j);
                        if (!arrayMap.containsKey(messageAddedUpdate.getPeerId())) {
                            arrayMap.put(messageAddedUpdate.getPeerId(), messageAddedUpdate.getPeerId());
                            ArrayList<MessageAddedUpdate> arrayList = new ArrayList<>();
                            arrayList.add(messageAddedUpdate);
                            for (int i = j + 1; i < messageAddedUpdates.size(); i++) {
                                MessageAddedUpdate messageAddedUpdate1 = messageAddedUpdates.get(i);
                                if (messageAddedUpdate1.getPeerId().equals(messageAddedUpdate.getPeerId())) {
                                    arrayList.add(messageAddedUpdate1);
                                }
                            }
                            arrayLists.add(arrayList);
                        }
                    }
                } else {
                    if(messageAddedUpdates.size()==1){
                        ArrayList<MessageAddedUpdate> arrayList = new ArrayList<>();
                        arrayList.add(messageAddedUpdates.get(0));
                        arrayLists.add(arrayList);
                    }
                }
                for (ArrayList<MessageAddedUpdate> list:arrayLists) {
                    int count = list.size();
                    MessageAddedUpdate messageAddedUpdate = list.get(list.size()-1);
                    String key = messageAddedUpdate.getPeerId();
                    ConversationItem conversationItem = conversationItemArrayMap.get(key);
                    if (conversationItem != null) {
                        if(messageAddedUpdate.isOut()){
                            conversationItem.setUnreadCount(0);
                        } else {
                            conversationItem.setUnreadCount(conversationItem.getUnreadCount()+count);
                        }

                        if(messageAddedUpdate.hasAttachments()){
                            addTask(new Task() {
                                @Override
                                public void func() {
                                    new AsyncExecutor<MessageItem>() {
                                        @Override
                                        public MessageItem inBackground() {
                                            try {
                                                ProfileItem userItem = getUserItem();
                                                Response<JSONObject> response = Message.getById(messageAddedUpdate.getMessageId(),
                                                        userItem.getAccessToken()).execute();
                                                JSONObject jo_response = validateBody(response).getJSONObject("response");
                                                ArrayMap<String, Owner> ownerArrayMap = createOwnersMap(jo_response);
                                                JSONArray items = jo_response.getJSONArray("items");
                                                MessageItem messageItem = new MessageItem(items.getJSONObject(0), ownerArrayMap);
                                                ConversationsAdapter.this.ownerArrayMap.putAll(ownerArrayMap);
                                                return messageItem;
                                            } catch (Exception e) {
                                                Log.d("eifiejfiejfi",e.getMessage());
                                                e.printStackTrace();
                                            }
                                            return null;
                                        }
                                        @Override
                                        public void onExecute(MessageItem messageItem) {
                                            if (messageItem != null) {
                                                updateConversationLastMessage(messageItem, key);
                                            }
                                            onComplete();
                                        }
                                    }.start();
                                }
                            });
                        } else {
                            Owner owner;
                            if (messageAddedUpdate.isOut()) {
                                owner = ownerArrayMap.get(messageAddedUpdate.getFromId());
                            } else {
                                if (conversationItem.getPeer().getType().equals("chat")) {
                                    owner = ownerArrayMap.get(messageAddedUpdate.getFromId());
                                } else {
                                    owner = conversationItem.getOwner();
                                }
                            }
                            if(owner==null){
                                addTask(new Task() {
                                    @Override
                                    public void func() {
                                        new AsyncExecutor<Owner>() {
                                            @Override
                                            public Owner inBackground() {
                                                try {
                                                    ProfileItem userItem = getUserItem();
                                                    ArrayList<String> arrayList = new ArrayList<>();
                                                    arrayList.add(messageAddedUpdate.getFromId());
                                                    ArrayMap<String, Owner> ownerArrayMap = loadOwners(arrayList, userItem.getAccessToken());
                                                    ConversationsAdapter.this.ownerArrayMap.putAll(ownerArrayMap);
                                                    return ownerArrayMap.get(messageAddedUpdate.getFromId());
                                                } catch (Exception e) {
                                                    Log.d("eifiejfiejfi",e.getMessage());
                                                    e.printStackTrace();
                                                }
                                                return null;
                                            }
                                            @Override
                                            public void onExecute(Owner object) {
                                                if (object != null) {
                                                    updateConversationLastMessage(new MessageItem(messageAddedUpdate, object), key);
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
                                        updateConversationLastMessage(new MessageItem(messageAddedUpdate, owner), key);
                                        onComplete();
                                    }
                                });
                            }
                        }
                    }
                }
            }
        };

        onMessageReadUpdateListener = messageReadUpdates -> {
            if(hasData()){
                ArrayList<ArrayList<MessageReadUpdate>> arrayLists = new ArrayList<>();
                if(messageReadUpdates.size()>1) {
                    ArrayMap<String, String> arrayMap = new ArrayMap<>();
                    for (int j = 0; j < messageReadUpdates.size(); j++) {
                        MessageReadUpdate messageReadUpdate = messageReadUpdates.get(j);
                        if (!arrayMap.containsKey(messageReadUpdate.getPeerId())) {
                            arrayMap.put(messageReadUpdate.getPeerId(), messageReadUpdate.getPeerId());
                            ArrayList<MessageReadUpdate> arrayList = new ArrayList<>();
                            arrayList.add(messageReadUpdate);
                            for (int i = j + 1; i < messageReadUpdates.size(); i++) {
                                MessageReadUpdate messageReadUpdate1 = messageReadUpdates.get(i);
                                if (messageReadUpdate1.getPeerId().equals(messageReadUpdate.getPeerId())) {
                                    arrayList.add(messageReadUpdate1);
                                }
                            }
                            arrayLists.add(arrayList);
                        }
                    }
                } else {
                    if(messageReadUpdates.size()==1){
                        ArrayList<MessageReadUpdate> arrayList = new ArrayList<>();
                        arrayList.add(messageReadUpdates.get(0));
                        arrayLists.add(arrayList);
                    }
                }
                for (ArrayList<MessageReadUpdate> list:arrayLists) {

                    MessageReadUpdate messageReadUpdateIn = null;
                    MessageReadUpdate messageReadUpdateOut = null;

                    for (int i = list.size() - 1; i >= 0; i--) {
                        MessageReadUpdate messageReadUpdate = messageReadUpdates.get(i);
                        if (messageReadUpdate.isOut()) {
                            if (messageReadUpdateOut == null) {
                                messageReadUpdateOut = messageReadUpdate;
                            }
                        } else {
                            if (messageReadUpdateIn == null) {
                                messageReadUpdateIn = messageReadUpdate;
                            }
                        }
                        if (messageReadUpdateIn != null && messageReadUpdateOut != null) {
                            break;
                        }
                    }


                    String key = messageReadUpdates.get(0).getPeerId();
                    ConversationItem conversationItem = conversationItemArrayMap.get(key);

                    if (conversationItem != null) {
                        if(messageReadUpdateOut!=null){
                            conversationItem.setOutRead(messageReadUpdateOut.getLocalId());
                        }
                        if(messageReadUpdateIn!=null){
                            conversationItem.setOutRead(messageReadUpdateIn.getLocalId());
                        }
                    }

                    addTask(new Task() {
                        @Override
                        public void func() {
                            int position = getItemDataHolders().indexOf(conversationItem);
                            if (position > -1) {
                                ConversationViewHolder conversationViewHolder = getConversationViewHolderAt(position);
                                if (conversationViewHolder != null) {
                                    conversationViewHolder.bind(conversationItem);
                                }
                            }
                            onComplete();
                        }
                    });

                }
            }
        };

        onMessageTypingUpdateListener = messageTypingUpdates -> {
            if(hasData()) {
                ArrayList<ArrayList<MessageTypingUpdate>> arrayLists = new ArrayList<>();
                if (messageTypingUpdates.size() > 1) {
                    ArrayMap<String, String> arrayMap = new ArrayMap<>();
                    for (int j = 0; j < messageTypingUpdates.size(); j++) {
                        MessageTypingUpdate messageTypingUpdate = messageTypingUpdates.get(j);
                        if (!arrayMap.containsKey(messageTypingUpdate.getPeerId())) {
                            arrayMap.put(messageTypingUpdate.getPeerId(), messageTypingUpdate.getPeerId());
                            if(conversationItemArrayMap.containsKey(messageTypingUpdate.getPeerId())){
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
                    ConversationItem conversationItem = conversationItemArrayMap.get(messageTypingUpdate.getPeerId());
                    if(conversationItem!=null){
                        ArrayList<String> typingIds = new ArrayList<>();
                        for (int i=0;i<list.size();i++){
                            typingIds.addAll(messageTypingUpdate.getFromIds());
                        }
                        loadTyping(typingIds, messageTypingUpdate.isText(), conversationItem);
                    }
                }
            }
        };

        onUserOnlineUpdateListener = userOnlineUpdates -> {
            if(hasData()) {
                for(Map.Entry<String,UserOnlineUpdate>entry:userOnlineUpdates.entrySet()) {
                    String key = entry.getKey();
                    ConversationItem conversationItem = conversationItemArrayMap.get(key);
                    if (conversationItem != null) {
                        addTask(new Task() {
                            @Override
                            public void func() {
                                UserOnlineUpdate userOnlineUpdate = entry.getValue();
                                updateUserOnline(userOnlineUpdate,key);
                                onComplete();
                            }
                        });
                    }
                }
            }
        };

        getMiracleApp().getLongPollServiceController().addOnMessageAddedUpdateListener(onMessageAddedUpdateListener);
        getMiracleApp().getLongPollServiceController().addOnMessageReadUpdateListener(onMessageReadUpdateListener);
        getMiracleApp().getLongPollServiceController().addOnMessageTypingListener(onMessageTypingUpdateListener);
        getMiracleApp().getLongPollServiceController().addOnUserOnlineListener(onUserOnlineUpdateListener);

    }

    private void loadTyping(ArrayList<String> typingIds, boolean isText, ConversationItem conversationItem){

        String ownerId = typingIds.get(0);
        String peerId = conversationItem.getPeer().getLocalId();

        switch (conversationItem.getPeer().getType()) {
            case "user":
            case "group": {
                addTask(new Task() {
                    @Override
                    public void func() {
                        MessageTypingUpdates executor = messageTypingUpdatesArrayMap.get(peerId);
                        if(isText) {
                            updateConversationMessageTyping(executor,
                                    getMiracleApp().getString(R.string.formatted_typing), typingIds, true, peerId);
                        } else {
                            updateConversationMessageTyping(executor,
                                    getMiracleApp().getString(R.string.formatted_typing_audio), typingIds, false, peerId);
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
                                        ProfileItem userItem = getUserItem();
                                        Response<JSONObject> response = Users.getWithMessageFields(ownerId, userItem.getAccessToken()).execute();
                                        JSONObject jo_response = validateBody(response);
                                        JSONArray profiles = jo_response.getJSONArray("response");

                                        if(profiles.length()>0){
                                            return new ProfileItem(profiles.getJSONObject(0));
                                        }
                                    } catch (Exception e) {
                                        Log.d("eifiejfiejfi",e.getMessage());
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                public void onExecute(ProfileItem profileItem) {
                                    if(profileItem!=null){
                                        Owner owner = new Owner(profileItem);
                                        ownerArrayMap.put(ownerId,owner);
                                        MessageTypingUpdates executor = messageTypingUpdatesArrayMap.get(peerId);
                                        updateConversationMessageTyping(executor,
                                                getMessageTypingDeclensions(owner, isText, typingIds.size()-1,getMiracleApp()),
                                                typingIds, isText, peerId);
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
                            MessageTypingUpdates executor = messageTypingUpdatesArrayMap.get(peerId);
                            updateConversationMessageTyping(executor,
                                    getMessageTypingDeclensions(owner, isText, typingIds.size()-1, getMiracleApp()),
                                    typingIds, isText, peerId);
                            onComplete();
                        }
                    });
                }
            }
        }
    }

    private void updateConversationLastMessage(MessageItem messageItem, String key){
        ConversationItem conversationItem = conversationItemArrayMap.get(key);
        if (conversationItem != null) {
            conversationItem.setLastMessage(messageItem);
            int position = getItemDataHolders().indexOf(conversationItem);
            if (position > -1) {
                ConversationViewHolder conversationViewHolder = getConversationViewHolderAt(position);
                if (conversationViewHolder != null) {

                    MessageTypingUpdates executor = messageTypingUpdatesArrayMap.get(key);
                    if(executor!=null){
                        ArrayList<String> typingIds = executor.getTypingIds();
                        typingIds.remove(messageItem.getOwner().getId());
                        if(!typingIds.isEmpty()){
                            loadTyping(typingIds, executor.isText, conversationItem);
                        } else {
                            conversationViewHolder.setIsTyping(false);
                            executor.cancel();
                        }
                    } else {
                        conversationViewHolder.setIsTyping(false);
                    }

                    if(position!=0){
                        ArrayList<ItemDataHolder> itemDataHolders = getItemDataHolders();
                        ItemDataHolder itemDataHolder1 = itemDataHolders.get(0);
                        ItemDataHolder itemDataHolder2 = itemDataHolders.get(position);
                        getItemDataHolders().set(0,itemDataHolder2);
                        getItemDataHolders().set(position,itemDataHolder1);
                        notifyItemMoved(position,0);
                    }
                    conversationViewHolder.bind(conversationItem);
                }
            }
        }
    }

    private void updateConversationMessageTyping(MessageTypingUpdates executor, String ownersString, ArrayList<String> typingIds, boolean isText, String key){
        if(executor!=null){
            executor.setIsTyping(ownersString, typingIds, isText);
        } else {
            executor = new MessageTypingUpdates(ownersString, typingIds, isText, key);
            messageTypingUpdatesArrayMap.put(key,executor);
            executor.start();
        }
    }

    private void updateUserOnline(UserOnlineUpdate userOnlineUpdate, String key){
        ConversationItem conversationItem = conversationItemArrayMap.get(key);
        if (conversationItem != null) {
            int position = getItemDataHolders().indexOf(conversationItem);
            if (position > -1) {
                ConversationViewHolder conversationViewHolder = getConversationViewHolderAt(position);
                if (conversationViewHolder != null) {
                    ProfileItem profileItem = conversationItem.getOwner().getProfileItem();
                    profileItem.setOnline(userOnlineUpdate.isOnline());
                    profileItem.setLastSeen(new LastSeen(userOnlineUpdate.getTs(), userOnlineUpdate.isOnline()?
                            userOnlineUpdate.getExtra() % 256 : profileItem.getLastSeen().getPlatform()));
                    conversationViewHolder.bind(conversationItem);
                }
            }
        }
    }

    @Override
    public void setDetached(boolean detached) {
        if(detached){
            getMiracleApp().getLongPollServiceController().removeOnMessageAddedUpdateListener(onMessageAddedUpdateListener);
            getMiracleApp().getLongPollServiceController().removeOnMessageReadUpdateListener(onMessageReadUpdateListener);
            getMiracleApp().getLongPollServiceController().removeOnMessageTypingListener(onMessageTypingUpdateListener);
            getMiracleApp().getLongPollServiceController().removeOnUserOnlineListener(onUserOnlineUpdateListener);
            for (Map.Entry<String, MessageTypingUpdates> etr: messageTypingUpdatesArrayMap.entrySet()) {
                etr.getValue().cancel();
            }
        }
        super.setDetached(detached);
    }

    private class MessageTypingUpdates extends AsyncExecutor<Boolean> {

        private final String key;
        private ArrayList<String> typingIds;
        private boolean isText;

        private final int allTime = 6000;
        private int time = allTime;
        private boolean canceled = false;

        public MessageTypingUpdates(String ownersString, ArrayList<String> typingIds, boolean isText, String key){
            this.key = key;
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
                        Log.d("eifiejfiejfi",e.getMessage());
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
                messageTypingUpdatesArrayMap.remove(key);
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
                        ConversationItem conversationItem = conversationItemArrayMap.get(key);
                        if (conversationItem != null) {
                            int position = getItemDataHolders().indexOf(conversationItem);
                            if (position > -1) {
                                ConversationViewHolder conversationViewHolder = getConversationViewHolderAt(position);
                                if (conversationViewHolder != null) {
                                    conversationViewHolder.setIsTyping(false);
                                    conversationViewHolder.bind(conversationItem);
                                }
                            }
                        }
                        onComplete();
                    }
                });
            }
        }

        private void setIsTypingViewHolder(String ownersString){
            if(!canceled) {
                ConversationItem conversationItem = conversationItemArrayMap.get(key);
                if (conversationItem != null) {
                    int position = getItemDataHolders().indexOf(conversationItem);
                    if (position > -1) {
                        ConversationViewHolder conversationViewHolder = getConversationViewHolderAt(position);
                        if (conversationViewHolder != null) {
                            conversationViewHolder.setIsTyping(true);
                            conversationViewHolder.setOwnersString(ownersString);
                            conversationViewHolder.bind(conversationItem);
                        }
                    }
                }
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

    private ConversationViewHolder getConversationViewHolderAt(int position){
        RecyclerView.ViewHolder viewHolder = getRecyclerView().findViewHolderForAdapterPosition(position);
        if(viewHolder!=null) {
            if (viewHolder instanceof ConversationViewHolder) {
                return  (ConversationViewHolder) viewHolder;
            }
        }
        return null;
    }
}


