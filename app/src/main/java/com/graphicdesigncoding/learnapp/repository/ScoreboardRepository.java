package com.graphicdesigncoding.learnapp.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.graphicdesigncoding.learnapp.api.ApiConfig;
import com.graphicdesigncoding.learnapp.api.CallAPI;
import com.graphicdesigncoding.learnapp.api.Callback;
import com.graphicdesigncoding.learnapp.api.ContentType;
import com.graphicdesigncoding.learnapp.api.Crypt;
import com.graphicdesigncoding.learnapp.api.SessionManager;
import com.graphicdesigncoding.learnapp.api.SimpleJson;
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.user.User;
import com.graphicdesigncoding.learnapp.user.UserItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreboardRepository {

    private final SessionManager sessionManager;
    private final Crypt crypt = new Crypt();
    private final SimpleJson simpleJson = new SimpleJson();

    public ScoreboardRepository(Context context) {
        this.sessionManager = new SessionManager(context);
    }

    public LiveData<Resource<List<User>>> fetchScoreboard() {
        MutableLiveData<Resource<List<User>>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String token = sessionManager.getToken();
        String jsonBody = "{\"token\":\"" + token + "\",\"t\":\"" + token + "\"}";

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/scoreboard",
                jsonBody,
                ContentType.APPLICATION_JSON,
                TransferMethod.POST,
                token,
                new Callback() {
                    @Override
                    public void finished(Object responseMsg) {
                        try {
                            JSONObject obj = simpleJson.Decode(responseMsg.toString());
                            JSONArray dataArray = null;

                            if (obj.has("data")) {
                                dataArray = obj.getJSONArray("data");
                            } else {
                                String dataKey = crypt.md5("data");
                                if (obj.has(dataKey)) {
                                    dataArray = simpleJson.GetArray(obj, dataKey);
                                }
                            }

                            if (dataArray != null) {
                                List<User> userList = new ArrayList<>();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject item = dataArray.getJSONObject(i);
                                    String name = item.optString("username", item.optString("name", ""));
                                    String score = item.optString("score", "0");
                                    userList.add(new User(null, name, score));
                                }

                                userList.sort(Collections.reverseOrder());
                                result.postValue(Resource.success(userList));
                            } else {
                                result.postValue(Resource.error("Service error"));
                            }
                        } catch (Exception e) {
                            result.postValue(Resource.error("Parsing error: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void canceled(Object responseMsg) {
                        result.postValue(Resource.error(responseMsg != null ? responseMsg : "No connection to server"));
                    }
                }
        );

        return result;
    }
}
