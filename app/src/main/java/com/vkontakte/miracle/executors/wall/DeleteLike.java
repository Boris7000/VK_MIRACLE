package com.vkontakte.miracle.executors.wall;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;

import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.util.StorageUtil;
import com.vkontakte.miracle.model.Owner;
import com.vkontakte.miracle.model.users.ProfileItem;
import com.vkontakte.miracle.model.wall.PostItem;
import com.vkontakte.miracle.network.methods.Likes;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class DeleteLike extends AsyncExecutor<Boolean> {

    private final PostItem postItem;
    private final ProfileItem profileItem;
    private int newLikesCount;

    public DeleteLike(PostItem postItem){
        this.postItem = postItem;
        profileItem = StorageUtil.get().currentUser();
    }

    @Override
    public Boolean inBackground() {
        try {
            if(profileItem!=null) {

                Owner owner = postItem.getFrom();
                if(owner==null){
                    owner = postItem.getSource();
                }
                if(owner==null){
                    owner = postItem.getOwner();
                }

                Call<JSONObject> call = Likes.delete(
                        "post",
                        postItem.getId(),
                        owner.getId(),
                        profileItem.getAccessToken());

                Response<JSONObject> response = call.execute();
                JSONObject jo_response = validateBody(response).getJSONObject("response");
                newLikesCount = jo_response.getInt("likes");
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onExecute(Boolean object) {
        if(object){
            postItem.setLikesCount(newLikesCount);
        }
    }

}
