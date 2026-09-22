package com.graphicdesigncoding.learnapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.graphicdesigncoding.learnapp.repository.AuthRepository;
import com.graphicdesigncoding.learnapp.repository.Resource;

public class RecoverViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final MutableLiveData<Resource<Boolean>> resetRequestResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> resetConfirmResult = new MutableLiveData<>();

    public RecoverViewModel(@NonNull Application application) {
        super(application);
        this.authRepository = new AuthRepository(application);
    }

    public LiveData<Resource<Boolean>> getResetRequestResult() {
        return resetRequestResult;
    }

    public LiveData<Resource<Boolean>> getResetConfirmResult() {
        return resetConfirmResult;
    }

    public void requestReset(String email) {
        authRepository.requestPasswordReset(email).observeForever(resetRequestResult::setValue);
    }

    public void confirmReset(String code, String password, String repeatPassword) {
        authRepository.confirmPasswordReset(code, password, repeatPassword).observeForever(resetConfirmResult::setValue);
    }
}
