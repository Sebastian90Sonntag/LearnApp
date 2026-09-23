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

import org.json.JSONObject;

public class QuizRepository {

    public static class QuizQuestion {
        public String id;
        public String title;
        public String question;
        public String answer;

        public QuizQuestion(String id, String title, String question, String answer) {
            this.id = id;
            this.title = title;
            this.question = question;
            this.answer = answer;
        }
    }

    private final SessionManager sessionManager;
    private final Crypt crypt = new Crypt();

    public QuizRepository(Context context) {
        this.sessionManager = new SessionManager(context);
    }

    public LiveData<Resource<QuizQuestion>> fetchCurrentQuestion() {
        MutableLiveData<Resource<QuizQuestion>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String token = sessionManager.getToken();

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/quiz/question",
                null,
                ContentType.APPLICATION_JSON,
                TransferMethod.GET,
                token,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        try {
                            JSONObject jobj = new JSONObject(obj.toString());

                            String idKey = crypt.md5("questionID");
                            String titleKey = crypt.md5("title");
                            String questionKey = crypt.md5("question");
                            String answerKey = crypt.md5("answer");

                            String id = jobj.optString("questionId", jobj.optString(idKey, "1"));
                            String title = jobj.optString("title", jobj.optString(titleKey, "Quiz"));
                            String question = jobj.optString("question", jobj.optString(questionKey, ""));
                            String answer = jobj.optString("answer", jobj.optString(answerKey, ""));

                            QuizQuestion q = new QuizQuestion(id, title, question, answer);
                            result.postValue(Resource.success(q));

                        } catch (Exception e) {
                            result.postValue(Resource.error("JSON parsing error: " + e.getMessage()));
                        }
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Failed to fetch question"));
                    }
                }
        );

        return result;
    }

    public LiveData<Resource<Boolean>> submitAnswerRating(String questionId, int statusId) {
        MutableLiveData<Resource<Boolean>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        String token = sessionManager.getToken();
        String jsonBody = "{\"questionId\":\"" + questionId + "\",\"rating\":\"" + statusId + "\"}";

        new CallAPI(
                ApiConfig.BASE_URL + "/api/v1/quiz/question",
                jsonBody,
                ContentType.APPLICATION_JSON,
                TransferMethod.POST,
                token,
                new Callback() {
                    @Override
                    public void finished(Object obj) {
                        result.postValue(Resource.success(true));
                    }

                    @Override
                    public void canceled(Object obj) {
                        result.postValue(Resource.error(obj != null ? obj : "Failed to record answer"));
                    }
                }
        );

        return result;
    }
}
