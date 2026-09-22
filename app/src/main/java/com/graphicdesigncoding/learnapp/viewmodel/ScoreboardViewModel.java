package com.graphicdesigncoding.learnapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.graphicdesigncoding.learnapp.repository.Resource;
import com.graphicdesigncoding.learnapp.repository.ScoreboardRepository;
import com.graphicdesigncoding.learnapp.user.User;

import java.util.List;

public class ScoreboardViewModel extends AndroidViewModel {

    private final ScoreboardRepository scoreboardRepository;
    private final MutableLiveData<Resource<List<User>>> scoreboardResult = new MutableLiveData<>();

    public ScoreboardViewModel(@NonNull Application application) {
        super(application);
        this.scoreboardRepository = new ScoreboardRepository(application);
    }

    public LiveData<Resource<List<User>>> getScoreboardResult() {
        return scoreboardResult;
    }

    public void loadScoreboard() {
        scoreboardRepository.fetchScoreboard().observeForever(scoreboardResult::setValue);
    }
}
