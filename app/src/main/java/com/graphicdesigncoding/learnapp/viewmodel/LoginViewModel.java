package com.graphicdesigncoding.learnapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.graphicdesigncoding.learnapp.repository.AuthRepository;
import com.graphicdesigncoding.learnapp.repository.Resource;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<Boolean>> loginResult = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<Resource<Boolean>> getLoginResult() {
        return loginResult;
    }

    public void login(String email, String password) {
        authRepository.login(email, password).observeForever(loginResult::setValue);
    }
}
