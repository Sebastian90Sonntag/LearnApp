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
import com.graphicdesigncoding.learnapp.api.TransferMethod;
import com.graphicdesigncoding.learnapp.image.ImageResize;
import com.graphicdesigncoding.learnapp.image.PrepareImageToBase64;

import org.json.JSONObject;

public class ProfileRepository {

    private final SessionManager sessionManager;
    private final Crypt crypt = new Crypt();

    public ProfileRepository(Context context) {
        this.sessionManager = new SessionManager(context);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public LiveData<Resource<String>> uploadAvatar(ImageResize resizedBMP) {
        MutableLiveData<Resource<String>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String token = sessionManager.getToken();
        String base64Img = new PrepareImageToBase64().Convert(resizedBMP);

        String postBody = "token=" + token + "&imageBase64=" + base64Img + "&" +
                crypt.md5("token") + "=" + token + "&" + crypt.md5("image") + "=" + base64Img;

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/profile/avatar",
                postBody,
                ContentType.TEXT_PLAIN,
                TransferMethod.POST,
                token,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        try {
                            JSONObject jobj = new JSONObject(obj.toString());
                            String imgLink = jobj.optString("image_link", jobj.optString(crypt.md5("image_link"), ""));
                            if (!imgLink.isEmpty()) {
                                sessionManager.saveSession(token, sessionManager.getUsername(), sessionManager.getEmail(), sessionManager.getPassword(), imgLink);
                                result.postValue(Resource.success(imgLink));
                            } else {
                                result.postValue(Resource.error("Invalid image link returned"));
                            }
                        } catch (Exception e) {
                            result.postValue(Resource.error("Parsing error: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Avatar upload failed"));
                    }
                }
        );

        return result;
    }

    public LiveData<Resource<SessionManager>> fetchProfile() {
        MutableLiveData<Resource<SessionManager>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String token = sessionManager.getToken();

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/profile",
                null,
                ContentType.APPLICATION_JSON,
                TransferMethod.GET,
                token,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        try {
                            JSONObject jobj = new JSONObject(obj.toString());
                            String username = jobj.optString("username", sessionManager.getUsername());
                            String email = jobj.optString("email", sessionManager.getEmail());
                            String imgLink = jobj.optString("image_link", sessionManager.getImage());

                            sessionManager.saveSession(token, username, email, sessionManager.getPassword(), imgLink);
                            result.postValue(Resource.success(sessionManager));
                        } catch (Exception e) {
                            result.postValue(Resource.error("Parsing error: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Failed to fetch profile"));
                    }
                }
        );

        return result;
    }
}
