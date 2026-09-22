package com.graphicdesigncoding.learnapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.graphicdesigncoding.learnapp.repository.AuthRepository;
import com.graphicdesigncoding.learnapp.repository.Resource;

public class RegisterViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<Boolean>> registerResult = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<Resource<Boolean>> getRegisterResult() {
        return registerResult;
    }

    public void register(String username, String email, String password, String repeatPassword) {
        authRepository.register(username, email, password, repeatPassword).observeForever(registerResult::setValue);
    }
}
