package com.vkontakte.miracle.fragment.base;

import static com.vkontakte.miracle.engine.util.NetworkUtil.validateBody;
import static com.vkontakte.miracle.network.Constants.latest_api_v;
import static com.vkontakte.miracle.network.Creator.longPoll;
import static com.vkontakte.miracle.network.Creator.message;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.vkontakte.miracle.R;
import com.vkontakte.miracle.engine.async.AsyncExecutor;
import com.vkontakte.miracle.engine.fragment.FragmentFabric;
import com.vkontakte.miracle.engine.fragment.MiracleFragment;
import com.vkontakte.miracle.engine.fragment.SimpleMiracleFragment;
import com.vkontakte.miracle.model.users.ProfileItem;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Response;

public class FragmentTest extends SimpleMiracleFragment {

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        iniContext();

        rootView = inflater.inflate(R.layout.fragment_test, container, false);
        setTopBar(rootView.findViewById(R.id.appbarLinear));
        setAppBarLayout(rootView.findViewById(R.id.appbar));

        LinearLayout linearLayout = rootView.findViewById(R.id.layout);
        EditText editText = (EditText) linearLayout.getChildAt(0);

        new AsyncExecutor<String>() {
            @Override
            public String inBackground() {
                try {
                    ProfileItem profileItem = getMiracleActivity().getUserItem();
                    Response<JSONObject> response = message().getLongPollServer(1,3,profileItem.getAccessToken(),latest_api_v).execute();
                    JSONObject jsonObject = validateBody(response).getJSONObject("response");

                    String server = jsonObject.getString("server");

                    Call<JSONObject> call = longPoll().request(server.substring(11),"a_check",jsonObject.getString("key"),jsonObject.getString("ts"),30,2 + 8 + 64 + 128,3);

                    response = call.execute();

                    jsonObject = validateBody(response);

                    return jsonObject.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                    return e.toString();
                }
            }
            @Override
            public void onExecute(String object) {
                editText.setText(object);
            }
        }.start();
        return rootView;
    }

    public static class Fabric implements FragmentFabric {
        @NonNull
        @Override
        public MiracleFragment createFragment() {
            return new FragmentTest();
        }
    }
}
