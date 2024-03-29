package com.vkontakte.miracle.adapter.messages;

import static com.vkontakte.miracle.engine.util.APIUtil.createOwnersMap;
import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_MESSAGE_IN;
import static com.vkontakte.miracle.engine.util.ViewHolderTypes.TYPE_MESSAGE_OUT;

import android.util.ArrayMap;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.miracle.engine.adapter.MiracleAsyncLoadAdapter;
import com.miracle.engine.adapter.holder.ItemDataHolder;
import com.miracle.engine.adapter.holder.ViewHolderFabric;
import com.vkontakte.miracle.adapter.messages.holders.MessageInChatViewHolder;
import com.vkontakte.miracle.adapter.messages.holders.MessageInViewHolder;
import com.vkontakte.miracle.adapter.messages.holders.MessageOutViewHolder;
import com.vkontakte.miracle.adapter.messages.holders.MessageViewHolder;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.messages.ConversationItem;
import com.vkontakte.miracle.model.messages.MessageItem;
import com.vkontakte.miracle.model.messages.fields.Peer;
import com.vkontakte.miracle.model.users.User;
import com.vkontakte.miracle.network.api.Message;
import com.vkontakte.miracle.service.longpoll.LongPollServiceController;
import com.vkontakte.miracle.service.longpoll.listeners.OnMessageAddedUpdateListener;
import com.vkontakte.miracle.service.longpoll.listeners.OnMessageReadUpdateListener;
import com.vkontakte.miracle.service.longpoll.model.MessageAddedUpdate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Response;

public class ChatAdapter extends MiracleAsyncLoadAdapter {

    private final ConversationItem conversationItem;
    private OnMessageReadUpdateListener onMessageReadUpdateListener;
    private OnMessageAddedUpdateListener onMessageAddedUpdateListener;
    private OnMessageAddedListener messageAddedListener;

    public ChatAdapter(ConversationItem conversationItem){
        this.conversationItem = conversationItem;
    }

    private final ArrayList<MessageOutViewHolder> messageOutViewHolders = new ArrayList<>();

    @Override
    public void onLoading() throws Exception {

        User user = StorageUtil.get().currentUser();
        ArrayList<ItemDataHolder> holders = getItemDataHolders();

        MessageItem messageItem = conversationItem.getLastMessage();
        Peer peer = conversationItem.getPeer();

        setTimeStump(System.currentTimeMillis()/1000);

        Response<JSONObject> response =  Message.getHistory(peer.getId(), messageItem!=null?messageItem.getId():"-1",
                holders.size(), getStep(), user.getAccessToken()).execute();

        JSONObject jsonObject = validateBody(response).getJSONObject("response");

        setTotalCount(jsonObject.getInt("count"));

        ArrayMap<String, Owner> ownerArrayMap = createOwnersMap(jsonObject);

        JSONArray jsonArray = jsonObject.getJSONArray("items");

        ArrayList<MessageItem> messageItems = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            messageItems.add(new MessageItem(jsonArray.getJSONObject(i), ownerArrayMap));
        }

        setAddedCount(messageItems.size());
        holders.addAll(messageItems);

        if (holders.size()==getTotalCount()||jsonArray.length()<getStep()) {
            setFinallyLoaded(true);
        }

    }

    @Override
    public void onComplete() {

        if(!loaded()) {
            ArrayList<MessageAddedUpdate> messageAddedUpdates = StorageUtil.get().loadMessageAddedLongPollUpdates();
            ArrayList<MessageAddedUpdate> missedMessageAddedUpdates = new ArrayList<>();

            androidx.collection.ArrayMap<String,MessageItem> messageItemArrayMap = new androidx.collection.ArrayMap<>();

            for (ItemDataHolder itemDataHolder : getItemDataHolders()) {
                if(itemDataHolder instanceof MessageItem){
                    MessageItem messageItem = (MessageItem) itemDataHolder;
                    messageItemArrayMap.put(messageItem.getId(),messageItem);
                }
            }

            for (MessageAddedUpdate messageAddedUpdate : messageAddedUpdates) {
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
        setStep(75);
        /*
        LongPollServiceController longPollServiceController = LongPollServiceController.get();

        onMessageReadUpdateListener = messageReadUpdates -> {
            if(loaded()){
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


                    if(messageReadUpdateOut!=null){
                        conversationItem.setOutRead(messageReadUpdateOut.getLocalId());
                        int newOutReadId = Integer.parseInt(messageReadUpdateOut.getLocalId());

                        for (MessageOutViewHolder messageOutViewHolder : messageOutViewHolders) {
                            MessageItem messageItem = messageOutViewHolder.getMessageItem();
                            int messageId = Integer.parseInt(messageItem.getId());
                            if(messageId<=newOutReadId) {
                                messageOutViewHolder.hideReadIcon();
                            }
                        }
                    }
                    if(messageReadUpdateIn!=null){
                        conversationItem.setInRead(messageReadUpdateIn.getLocalId());
                    }
                }
            }
        };
        longPollServiceController.addOnMessageReadUpdateListener(onMessageReadUpdateListener);

        onMessageAddedUpdateListener = messageAddedUpdates -> {
            if(loaded()) {
                ArrayList<String> newMessagesIds = new ArrayList<>();

                for (MessageAddedUpdate messageAddedUpdate : messageAddedUpdates) {
                    if (messageAddedUpdate.getPeerId().equals(conversationItem.getPeer().getLocalId())) {
                        newMessagesIds.add(messageAddedUpdate.getMessageId());
                    }
                }

                if (!newMessagesIds.isEmpty()) {
                    addTask(new Task() {
                        @Override
                        public void func() {
                            new AsyncExecutor<ArrayList<MessageItem>>() {
                                @Override
                                public ArrayList<MessageItem> inBackground() {
                                    try {
                                        ProfileItem userItem = getUserItem();
                                        Response<JSONObject> response = Message.getById(stringFromArrayList(newMessagesIds, ","),
                                                userItem.getAccessToken()).execute();
                                        JSONObject jo_response = validateBody(response).getJSONObject("response");
                                        ArrayMap<String, Owner> ownerArrayMap = createOwnersMap(jo_response);
                                        JSONArray items = jo_response.getJSONArray("items");

                                        ArrayList<MessageItem> messageItems = new ArrayList<>();

                                        for (int i = 0; i < items.length(); i++) {
                                            messageItems.add(new MessageItem(items.getJSONObject(i), ownerArrayMap));
                                        }

                                        return messageItems;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                public void onExecute(ArrayList<MessageItem> messageItems) {

                                    if(!messageItems.isEmpty()){
                                        for (MessageItem messageItem:messageItems){
                                            addNewMessage(messageItem);
                                        }
                                    }

                                    onComplete();
                                }
                            }.start();
                        }
                    });
                }
            }
        };
        longPollServiceController.addOnMessageAddedUpdateListener(onMessageAddedUpdateListener);


         */
    }

    private void addNewMessage(MessageItem messageItem){
        getItemDataHolders().add(0, messageItem);
        setAddedCount(1);
        notifyItemInserted(0);
        if(messageAddedListener!=null){
            messageAddedListener.onMessageAdded(messageItem);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = super.onCreateViewHolder(parent, viewType);
        if(viewHolder instanceof MessageViewHolder){
            MessageViewHolder messageViewHolder = ((MessageViewHolder)viewHolder);
            messageViewHolder.setConversationItem(conversationItem);

            if(viewHolder instanceof MessageOutViewHolder) {
                MessageOutViewHolder messageOutViewHolder = ((MessageOutViewHolder) viewHolder);
                messageOutViewHolders.add(messageOutViewHolder);
            }
        }
        return viewHolder;
    }

    @Override
    public void setRecyclerView(RecyclerView recyclerView) {
        super.setRecyclerView(recyclerView);
        if(recyclerView==null){
            LongPollServiceController longPollServiceController = LongPollServiceController.get();
            longPollServiceController.removeOnMessageReadUpdateListener(onMessageReadUpdateListener);
            longPollServiceController.removeOnMessageAddedUpdateListener(onMessageAddedUpdateListener);
        }
    }

    @Override
    public ArrayMap<Integer, ViewHolderFabric> getViewHolderFabricMap() {
        ArrayMap<Integer, ViewHolderFabric> arrayMap = super.getViewHolderFabricMap();
        arrayMap.put(TYPE_MESSAGE_OUT, new MessageOutViewHolder.Fabric());
        if(conversationItem.getPeer().getType().equals("chat")){
            arrayMap.put(TYPE_MESSAGE_IN, new MessageInChatViewHolder.Fabric());
        } else {
            arrayMap.put(TYPE_MESSAGE_IN, new MessageInViewHolder.Fabric());
        }
        return arrayMap;
    }

    public void setMessageAddedListener(OnMessageAddedListener messageAddedListener) {
        this.messageAddedListener = messageAddedListener;
    }
}
