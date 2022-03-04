package com.vkontakte.miracle.network;

import com.vkontakte.miracle.network.converter.JSONConverterFactory;
import com.vkontakte.miracle.network.methods.apis.IAccount;
import com.vkontakte.miracle.network.methods.apis.IAudio;
import com.vkontakte.miracle.network.methods.apis.ICatalog;
import com.vkontakte.miracle.network.methods.apis.IExecute;
import com.vkontakte.miracle.network.methods.apis.IFriends;
import com.vkontakte.miracle.network.methods.apis.IGroups;
import com.vkontakte.miracle.network.methods.apis.ILikes;
import com.vkontakte.miracle.network.methods.apis.IMessage;
import com.vkontakte.miracle.network.methods.apis.IOauth;
import com.vkontakte.miracle.network.methods.apis.IPhotos;
import com.vkontakte.miracle.network.methods.apis.ILongPoll;
import com.vkontakte.miracle.network.methods.apis.IUsers;
import com.vkontakte.miracle.network.methods.apis.IWall;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class Creator {

    public static Retrofit getBuilder(){
        return new Retrofit.Builder().baseUrl("https://api.vk.com/method/").addConverterFactory(JSONConverterFactory.create()).build();
    }

    public static IOauth oauth(){
        return new Retrofit.Builder().baseUrl("https://oauth.vk.com/").addConverterFactory(JSONConverterFactory.create()).build().create(IOauth.class);
    }

    public static ILongPoll longPoll(){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(35, TimeUnit.SECONDS)
                .connectTimeout(35, TimeUnit.SECONDS)
                .build();
        return new Retrofit.Builder().baseUrl("https://api.vk.com/").addConverterFactory(JSONConverterFactory.create()).client(okHttpClient).build().create(ILongPoll.class);
    }

    public static IOauth oauthMethod(){
        return getBuilder().create(IOauth.class);
    }

    public static IAccount account(){
        return getBuilder().create(IAccount.class);
    }
    public static IAudio audio(){
        return getBuilder().create(IAudio.class);
    }
    public static ICatalog catalog(){
        return getBuilder().create(ICatalog.class);
    }
    public static IExecute execute(){
        return getBuilder().create(IExecute.class);
    }
    public static IFriends friends(){
        return getBuilder().create(IFriends.class);
    }
    public static IGroups groups(){
        return getBuilder().create(IGroups.class);
    }
    public static ILikes likes(){
        return getBuilder().create(ILikes.class);
    }
    public static IMessage message(){
        return getBuilder().create(IMessage.class);
    }
    public static IUsers users(){
        return getBuilder().create(IUsers.class);
    }
    public static IWall wall(){
        return getBuilder().create(IWall.class);
    }
    public static IPhotos photos(){
        return getBuilder().create(IPhotos.class);
    }
}
