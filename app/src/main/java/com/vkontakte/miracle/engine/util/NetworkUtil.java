package com.vkontakte.miracle.engine.util;

import static com.vkontakte.miracle.engine.util.NavigationUtil.hardResolveVKURL;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;

import retrofit2.Response;

public class NetworkUtil {

    public static void openURLInBrowser(String url, Context context){
        if(!hardResolveVKURL(url, context)){
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(browserIntent);
        }
    }

    public static void CheckConnection(int timeOutMs) throws Exception {
        Socket socket = new Socket();
        SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
        socket.connect(socketAddress, timeOutMs);
        socket.close();
    }

    private static final String unknownError = "Error without description.\nhahaha, VK moment...\n\uD83D\uDE02\uD83E\uDD23\uD83D\uDE06\uD83D\uDE02\uD83E\uDD23\uD83D\uDE06\uD83D\uDE02\uD83E\uDD23\uD83D\uDE06";

    @NonNull
    public static JSONObject validateBody(Response<JSONObject> response) throws Exception {
        if(response.errorBody()==null&&response.body()!=null) {
            JSONObject jsonObject = response.body();
            if (jsonObject.has("response")) {
                if((jsonObject.get("response") instanceof JSONObject)) {
                    JSONObject jo_response = jsonObject.getJSONObject("response");
                    if (jo_response.has("error")) {
                        if (jsonObject.has("error_description")) {
                            throwError(jsonObject.getString("error_description"));
                        } else {
                            throwError(unknownError);
                        }
                    }
                }
                return jsonObject;
            } else {
                if (jsonObject.has("error") && (jsonObject.get("error") instanceof JSONObject)) {
                    JSONObject jo_error = jsonObject.getJSONObject("error");
                    if (jo_error.has("error_msg")) {
                        throwError(jo_error.getString("error_msg"));
                    } else {
                        throwError(unknownError);
                    }
                }
            }
        } else {
            if(response.errorBody()!=null){
                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                if (jsonObject.has("error_description")) {
                    throwError(jsonObject.getString("error_description"));
                } else {
                    throwError(unknownError);
                }
            }
        }
        return new JSONObject();
    }


    public static void throwError(String errorMsg) throws Exception{
        throw new Exception(errorMsg);
    }

    public static void logError(Exception e){ Log.i("API_error", e.toString()); }

    public static void logError(String error){ Log.i("API_error", error); }

    public static int[] parseIntArray(JSONArray array) throws JSONException {
        int[] list = new int[array.length()];
        for (int i = 0; i < array.length(); i++) {
            list[i] = array.getInt(i);
        }
        return list;
    }

    public static String[] parseStringArray(JSONArray array) throws JSONException {
        String[] list = new String[array.length()];
        for (int i = 0; i < array.length(); i++) {
            list[i] = array.getString(i);
        }
        return list;
    }

    public static ArrayList<String> parseStringArrayList(JSONArray array) throws JSONException {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            list.add(array.getString(i));
        }
        return list;
    }

    public static boolean hasFlag(int mask, int flag) {
        return (mask & flag) != 0;
    }

}
