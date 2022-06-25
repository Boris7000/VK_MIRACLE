package com.vkontakte.miracle.model.wall;

import static com.vkontakte.miracle.engine.adapter.holder.ViewHolderTypes.TYPE_POST;

import com.vkontakte.miracle.engine.adapter.holder.ItemDataHolder;
import com.vkontakte.miracle.model.Attachments;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.catalog.CatalogExtendedArrays;

import org.json.JSONException;
import org.json.JSONObject;

public class PostItem implements ItemDataHolder {

    private String id;
    private long date;
    private Owner owner;
    private Owner source;
    private Owner from;
    private Owner to;
    private Attachments attachments;
    private String text;
    private boolean zoom_text = false;
    private boolean userLikes = false;
    private boolean canPostComments = true;
    private boolean canViewComments = true;
    private int likesCount = 0;
    private int commentsCount = 0;
    private int repostsCount = 0;
    private int viewsCount = 1;

    public String getId() {
        return id;
    }
    public Owner getOwner() {
        return owner;
    }
    public Owner getSource() {
        return source;
    }
    public Owner getFrom() {
        return from;
    }
    public Owner getTo() {
        return to;
    }
    public long getDate() {
        return date;
    }
    public String getText() {
        return text;
    }
    public boolean getZoomText() {
        return zoom_text;
    }
    public boolean getUserLikes() {
        return userLikes;
    }
    public boolean getCanPostComments() {
        return canPostComments;
    }
    public boolean getCanViewComments() {
        return canViewComments;
    }
    public int getLikesCount() {
        return likesCount;
    }
    public int getCommentsCount() {
        return commentsCount;
    }
    public int getRepostsCount() {
        return repostsCount;
    }
    public int getViewsCount() {
        return viewsCount;
    }
    public Attachments getAttachments() {
        return attachments;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    public void setUserLikes(boolean userLikes) {
        this.userLikes = userLikes;
    }

    public PostItem(JSONObject jsonObject, CatalogExtendedArrays catalogExtendedArrays) throws JSONException {

        if(jsonObject.has("post_id")) {
            id = jsonObject.getString("post_id");
        } else {
            if(jsonObject.has("id")) {
                id = jsonObject.getString("id");
            }else {
                return;
            }
        }

        if(jsonObject.has("owner_id")){
            owner = new Owner(jsonObject.getString("owner_id"), catalogExtendedArrays);
        }

        if(jsonObject.has("source_id")){
            source = new Owner(jsonObject.getString("source_id"), catalogExtendedArrays);
        }

        if(jsonObject.has("from_id")){
            from = new Owner(jsonObject.getString("from_id"), catalogExtendedArrays);
        }

        if(jsonObject.has("to_id")){
            to = new Owner(jsonObject.getString("to_id"), catalogExtendedArrays);
        }

        date = jsonObject.getLong("date");

        if(jsonObject.has("text")){
            text = jsonObject.getString("text");

            if(jsonObject.has("zoom_text")) {
                zoom_text = jsonObject.getBoolean("zoom_text");
            }

        }else {
            text = "";
        }

        if(jsonObject.has("attachments")){
            attachments = new Attachments(jsonObject.getJSONArray("attachments"));
        }

        if(jsonObject.has("likes")) {
            JSONObject jo_likes = jsonObject.getJSONObject("likes");
            likesCount = jo_likes.getInt("count");
            userLikes = jo_likes.getInt("user_likes") == 1;
        }
        if(jsonObject.has("reposts")){
            JSONObject jo_reposts = jsonObject.getJSONObject("reposts");
            repostsCount = jo_reposts.getInt("count");
        }
        if(jsonObject.has("comments")){
            JSONObject jo_comments = jsonObject.getJSONObject("comments");
            commentsCount = jo_comments.getInt("count");
            canPostComments = jo_comments.getInt("can_post")==1;
            canViewComments = jo_comments.getInt("can_view")==1;
        }

        if (jsonObject.has("views")){
            JSONObject jo_views = jsonObject.getJSONObject("views");
            viewsCount = jo_views.getInt("count");
        }

    }

    @Override
    public int getViewHolderType() {
        return TYPE_POST;
    }
}
