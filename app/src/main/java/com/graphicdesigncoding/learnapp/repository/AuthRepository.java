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

import org.json.JSONObject;

public class AuthRepository {

    private final SessionManager sessionManager;
    private final Crypt crypt = new Crypt();
    private final SimpleJson simpleJson = new SimpleJson();

    public AuthRepository(Context context) {
        this.sessionManager = new SessionManager(context);
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }

    public LiveData<Resource<Boolean>> login(String email, String password) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String jsonBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"e\":\"" + email + "\",\"p\":\"" + password + "\"}";

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/auth/login",
                jsonBody,
                ContentType.APPLICATION_JSON,
                TransferMethod.POST,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        try {
                            JSONObject json = new JSONObject(obj.toString());
                            String tokenKey = crypt.md5("token");
                            String userKey = crypt.md5("username");
                            String imgKey = crypt.md5("image_link");

                            String token = json.optString("token", json.optString(tokenKey, ""));
                            String username = "";
                            String imageLink = json.optString("image_link", json.optString(imgKey, ""));

                            if (json.has("user")) {
                                JSONObject userObj = json.getJSONObject("user");
                                username = userObj.optString("username", "");
                                imageLink = userObj.optString("image_link", imageLink);
                            } else if (json.has(userKey)) {
                                username = json.optString(userKey, "");
                            }

                            if (!token.isEmpty()) {
                                sessionManager.saveSession(token, username, email, password, imageLink);
                                result.postValue(Resource.success(true));
                            } else {
                                result.postValue(Resource.error("Invalid token response"));
                            }
                        } catch (Exception e) {
                            result.postValue(Resource.error("Parsing error: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Login failed"));
                    }
                }
        );

        return result;
    }

    public LiveData<Resource<Boolean>> register(String username, String email, String password, String repeatPassword) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String jsonBody = "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"repeatPassword\":\"" + repeatPassword + "\"," +
                "\"u\":\"" + username + "\",\"e\":\"" + email + "\",\"p\":\"" + password + "\",\"rp\":\"" + repeatPassword + "\"}";

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/auth/register",
                jsonBody,
                ContentType.APPLICATION_JSON,
                TransferMethod.POST,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        result.postValue(Resource.success(true));
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Registration failed"));
                    }
                }
        );

        return result;
    }

    public LiveData<Resource<Boolean>> requestPasswordReset(String email) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String jsonBody = "{\"email\":\"" + email + "\",\"e\":\"" + email + "\",\"r\":\"" + crypt.md5("recover") + "\"}";

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/auth/forgot-password",
                jsonBody,
                ContentType.APPLICATION_JSON,
                TransferMethod.POST,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        result.postValue(Resource.success(true));
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Reset request failed"));
                    }
                }
        );

        return result;
    }

    public LiveData<Resource<Boolean>> confirmPasswordReset(String code, String password, String repeatPassword) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String jsonBody = "{\"code\":\"" + code + "\",\"password\":\"" + password + "\",\"repeatPassword\":\"" + repeatPassword + "\"," +
                "\"rt\":\"" + code + "\",\"r\":\"" + crypt.md5("recoverToken") + "\",\"p\":\"" + password + "\",\"rp\":\"" + repeatPassword + "\"}";

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/auth/reset-password",
                jsonBody,
                ContentType.APPLICATION_JSON,
                TransferMethod.POST,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        result.postValue(Resource.success(true));
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Password reset failed"));
                    }
                }
        );

        return result;
    }
}
