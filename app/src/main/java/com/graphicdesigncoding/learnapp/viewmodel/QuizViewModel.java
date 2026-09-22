package com.graphicdesigncoding.learnapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.graphicdesigncoding.learnapp.repository.QuizRepository;
import com.graphicdesigncoding.learnapp.repository.Resource;

public class QuizViewModel extends AndroidViewModel {

    private final QuizRepository quizRepository;
    private final MutableLiveData<Resource<QuizRepository.QuizQuestion>> currentQuestion = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> answerResult = new MutableLiveData<>();

    public QuizViewModel(@NonNull Application application) {
        super(application);
        this.quizRepository = new QuizRepository(application);
    }

    public LiveData<Resource<QuizRepository.QuizQuestion>> getCurrentQuestion() {
        return currentQuestion;
    }

    public LiveData<Resource<Boolean>> getAnswerResult() {
        return answerResult;
    }

    public void loadNextQuestion() {
        quizRepository.fetchCurrentQuestion().observeForever(currentQuestion::setValue);
    }

    public void submitRating(String questionId, int statusId) {
        quizRepository.submitAnswerRating(questionId, statusId).observeForever(res -> {
            answerResult.setValue(res);
            if (res.status == Resource.Status.SUCCESS) {
                loadNextQuestion();
            }
        });
    }
}
