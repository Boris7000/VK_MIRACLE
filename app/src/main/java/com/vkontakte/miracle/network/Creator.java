package com.vkontakte.miracle.network;

import com.vkontakte.miracle.network.converter.JSONConverterFactory;
import com.vkontakte.miracle.network.api.services.IAccount;
import com.vkontakte.miracle.network.api.services.IAudio;
import com.vkontakte.miracle.network.api.services.ICatalog;
import com.vkontakte.miracle.network.api.services.IExecute;
import com.vkontakte.miracle.network.api.services.IFriends;
import com.vkontakte.miracle.network.api.services.IGroups;
import com.vkontakte.miracle.network.api.services.ILikes;
import com.vkontakte.miracle.network.api.services.ILongPoll;
import com.vkontakte.miracle.network.api.services.IMessage;
import com.vkontakte.miracle.network.api.services.IOauth;
import com.vkontakte.miracle.network.api.services.IPhotos;
import com.vkontakte.miracle.network.api.services.IStats;
import com.vkontakte.miracle.network.api.services.IUsers;
import com.vkontakte.miracle.network.api.services.IUtils;
import com.vkontakte.miracle.network.api.services.IWall;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class Creator {

    public static OkHttpClient createClient(){
        return new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new UserAgentInterceptor())
                .build();
    }

    public static Retrofit getBuilder(){
        /*return new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(JSONConverterFactory.create())
                .build();

         */
        return new Retrofit.Builder()
                .baseUrl("https://api.vk.com/method/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createClient())
                .build();
    }

    public static IOauth oauth(){
        return new Retrofit.Builder()
                .baseUrl("https://oauth.vk.com/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createClient())
                .build().create(IOauth.class);
    }

    public static ILongPoll longPoll(){
        return new Retrofit.Builder()
                .baseUrl("https://api.vk.com/")
                .addConverterFactory(JSONConverterFactory.create())
                .client(createClient())
                .build()
                .create(ILongPoll.class);

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
    public static IUtils utils(){
        return getBuilder().create(IUtils.class);
    }
    public static IStats stats(){
        return getBuilder().create(IStats.class);
    }
}
