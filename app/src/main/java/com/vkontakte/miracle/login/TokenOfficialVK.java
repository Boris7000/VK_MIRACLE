package com.vkontakte.miracle.login;

import static android.util.Base64.NO_WRAP;

import static com.vkontakte.miracle.login.TokenRefresh.LOGIN_TAG;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import com.vkontakte.miracle.network.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class TokenOfficialVK {

    private final int rid = 1;
    private KeyPair pair = null;

    private String userAgent(){
        return "Android-GCM/1.5 (" + Build.DEVICE + ' ' + Build.ID + ')';
    }

    public String getNonce(Long timestamp) {
        String valueOf = timestamp.toString();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[24];
        new Random().nextBytes(bArr);
        try {
            byteArrayOutputStream.write(bArr);
            byteArrayOutputStream.write(valueOf.getBytes());
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), NO_WRAP);
        } catch (IOException unused){
            return null;
        }
    }

    private String receipt(String auth, Boolean clear) throws IOException{
        String str;
        String str2;
        String genNewKey = genNewKey();
        String sig = getSig(genNewKey);
        byte[] encoded = pair.getPublic().getEncoded();
        try {
            encoded = MessageDigest.getInstance("SHA1").digest(encoded);
            str = null;
        } catch (NoSuchAlgorithmException unused) {
            str = "";
        }

        if(str==null){
            encoded[0] = (byte) ((encoded[0] & 15) + 112 & 255);
            str2 = Base64.encodeToString(encoded, Base64.NO_WRAP).substring(0, 11);
        } else {
            return str;
        }

        ArrayList<String> arrayList = new ArrayList<>();
        fillParams(
                arrayList,
                sig,
                genNewKey,
                str2,
                auth.split(" ")[1].split(":")[0],
                clear
        );
        return doRequest("https://android.clients.google.com/c2dm/register3", arrayList, auth);
    }

    public List<String> requestToken() {
        ArrayList<String> ret = new ArrayList<>(2);

        try {
            String[] strArr = new String[]{
                    "4537286713832810256:3813922857350986999",
                    "4607161437294568617:4436643741345745345",
                    "4031819488942003867:1675892049294949499",
                    "3665846370517392830:3012248377502379040"
            };

            String str3 = "AidLogin " + strArr[new Random().nextInt(strArr.length - 1)];
            String  sb3 = receipt(str3, false);
            if (sb3.contains("REGISTRATION_ERROR")) {
                Log.d(LOGIN_TAG,"Token register fail");
                return null;
            }
            ret.add(sb3.split("\\|ID\\|"+rid+"\\|:")[1]);
            sb3 = receipt(str3, true);
            if (sb3.contains("REGISTRATION_ERROR")) {
                Log.d(LOGIN_TAG,"Token register fail");
                return null;
            }
            ret.add(sb3.split("\\|ID\\|"+rid+"\\|:")[1]);
            Log.d(LOGIN_TAG,"Token register ok");
            return ret;
        } catch (Exception unused){
            Log.d(LOGIN_TAG,"Token register fail");
            Log.d(LOGIN_TAG,unused.getMessage());
            return null;
        }
    }

    private void fillParams( ArrayList<String> list, String str,
                             String str2, String str3, String device, Boolean clear) {
        if (clear) {
            list.add("X-scope=GCM");
            list.add("X-delete=1");
            list.add("X-X-delete=1");
        } else {
            list.add("X-scope=*");
            list.add("X-X-subscription=841415684880");
            list.add("X-gmp_app_id=1:841415684880:android:632f429381141121");
        }
        list.add("X-subtype=841415684880");
        list.add("X-X-subtype=841415684880");
        list.add("X-app_ver="+ Constants.app_version_code);
        list.add("X-kid=|ID|"+rid+"|");
        list.add("X-X-kid=|ID|"+rid+"|");
        list.add("X-osv=" + Build.VERSION.SDK_INT);
        list.add("X-sig="+str);
        list.add("X-cliv=fiid-9877000");
        list.add("X-gmsv=200313005");
        list.add("X-pub2="+str2);
        list.add("X-appid="+str3);
        list.add("X-subscription=841415684880");
        list.add("X-app_ver_name="+Constants.app_version_name);
        list.add("app=com.vkontakte.android");
        list.add("sender=841415684880");
        list.add("device="+device);
        list.add("cert=48761eef50ee53afc4cc9c5f10e6bde7f8f5b82f");
        list.add("app_ver="+Constants.app_version_code);
        list.add("gcm_ver=200313005");
    }

    private String doRequest(String str, List<String> list, String str3) throws IOException{

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder().
                        addHeader("User-Agent", userAgent())
                        .addHeader("Authorization", str3)
                        .addHeader("app", "com.vkontakte.android")
                        .addHeader("Gcm-ver", "200313005")
                        .addHeader("Gcm-cert", "48761eef50ee53afc4cc9c5f10e6bde7f8f5b82f")
                        .build()));

        FormBody.Builder formBody = new FormBody.Builder();

        for (String i: list) {
            String[] v = i.split("=");
            formBody.add(v[0], v[1]);
        }

        Request request = new Request.Builder()
                .url(str)
                .post(formBody.build())
                .build();
        return builder.build().newCall(request).execute().body().string();
    }

    private String genNewKey() {
        try {
            KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA");
            instance.initialize(2048);
            pair = instance.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(pair.getPublic().getEncoded(), Base64.URL_SAFE | Base64.NO_WRAP);
    }

    private String getSig(String str) {

        try {

            if(pair == null) return null;

            PrivateKey privateKey = pair.getPrivate();
            Signature instance = Signature.getInstance(privateKey instanceof RSAPrivateKey?
                    "SHA256withRSA":"SHA256withECDSA");
            instance.initSign(privateKey);
            instance.update(
                    join("\n", new String[]{"com.vkontakte.android", str}).getBytes(StandardCharsets.UTF_8)
            );
            return Base64.encodeToString(instance.sign(), Base64.URL_SAFE | NO_WRAP);
        } catch (Exception e){
            return null;
        }
    }

    private String join(String str, Iterable<String> iterable) {
        StringBuilder str2 = new StringBuilder();

        for (String next: iterable) {
            if (str2.length()==0) {
                str2 = new StringBuilder(next);
            } else {
                str2.append(str).append(next);
            }
        }

        return str2.toString();
    }

    private String join(String str, String[] strArr) {
        return join(str, Arrays.asList(strArr));
    }
}
