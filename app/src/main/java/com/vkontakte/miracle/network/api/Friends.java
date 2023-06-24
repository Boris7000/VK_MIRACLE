package com.vkontakte.miracle.network.api;

import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Constants.users_min_fields;
import static com.vkontakte.miracle.network.Creator.execute;
import static com.vkontakte.miracle.network.Creator.friends;

import org.json.JSONObject;

import retrofit2.Call;

public class Friends {

    public static Call<JSONObject> get(int count, int offset, String order, String user_id, String access_token){
        return friends().get(count, offset, order, user_id, users_min_fields, access_token,latest_api_v);
    }

    public static Call<JSONObject> getOnline(String order, String user_id, String access_token){

        String targetOrder = (order == null)? null : toQuotes(order);
        String targetFields = toQuotes(users_min_fields);

        String code = "var user_id = %s;\n" +
                "var fields = %s;\n" +
                "\n" +
                "var user = API.users.get({\"v\":\"" + latest_api_v + "\",\n" +
                "    \"user_ids\":user_id, \n" +
                "    \"fields\":\"counters\"\n" +
                "})[0];\n" +
                "\n" +
                "var total_count = user.counters.online_friends;\n" +
                "\n" +
                "var uids = API.friends.getOnline({\"v\":\"" + latest_api_v + "\",\n" +
                "    \"user_id\":user_id, \n" +
                "    \"count\":total_count, \n" +
                "    \"offset\":0,\n" +
                "    \"order\":%s\n" +
                "});\n" +
                "\n" +
                "var profiles = API.users.get({\"v\":\"" + latest_api_v + "\",\"user_ids\":uids, \"fields\":fields});\n" +
                "\n" +
                "return {\"count\":total_count, \"items\":profiles};";

        String formattedCode = String.format(code, user_id, targetFields, targetOrder);


        return execute().execute(formattedCode, access_token, latest_api_v);
    }

    public static Call<JSONObject> getMutual(String user_id, String target_id, String access_token){

        String targetFields = toQuotes(users_min_fields);

        String code = "var source_uid = %s;\n" +
                "var target_uid = %s;\n" +
                "var fields = %s;\n" +
                "\n" +
                "var user = API.users.get({\"v\":\"" + latest_api_v + "\",\n" +
                "    \"user_ids\":target_uid, \n" +
                "    \"fields\":\"counters\"\n" +
                "})[0];\n" +
                "\n" +
                "var total_count = user.counters.mutual_friends;\n" +
                "\n" +
                "var uids = API.friends.getMutual({\"v\":\"" + latest_api_v + "\",\n" +
                "    \"source_uid\":source_uid, \n" +
                "    \"target_uid\":target_uid, \n" +
                "    \"count\":total_count, \n" +
                "    \"offset\":0\n" +
                "});\n" +
                "\n" +
                "var profiles = API.users.get({\"v\":\"" + latest_api_v + "\",\"user_ids\":uids, \"fields\":fields});\n" +
                "\n" +
                "return {\"count\":total_count, \"items\":profiles};";

        String formattedCode = String.format(code, user_id, target_id, targetFields);


        return execute().execute(formattedCode, access_token, latest_api_v);
    }


    static String toQuotes(String word) {
        if (word == null) {
           return null;
        } else {
            return "\"" + word + "\"";
        }
    }

}
