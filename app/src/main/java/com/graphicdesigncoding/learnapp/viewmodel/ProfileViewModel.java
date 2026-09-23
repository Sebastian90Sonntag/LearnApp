package com.graphicdesigncoding.learnapp.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.graphicdesigncoding.learnapp.api.SessionManager;
import com.graphicdesigncoding.learnapp.image.ImageResize;
import com.graphicdesigncoding.learnapp.repository.ProfileRepository;
import com.graphicdesigncoding.learnapp.repository.Resource;

public class ProfileViewModel extends AndroidViewModel {

    private final ProfileRepository profileRepository;
    private final MutableLiveData<Resource<String>> uploadResult = new MutableLiveData<>();

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.profileRepository = new ProfileRepository(application);
    }

    public SessionManager getSessionManager() {
        return profileRepository.getSessionManager();
    }

    public LiveData<Resource<String>> getUploadResult() {
        return uploadResult;
    }

    public LiveData<Resource<SessionManager>> fetchProfile() {
        return profileRepository.fetchProfile();
    }

    public void uploadAvatar(ImageResize resizedBMP) {
        profileRepository.uploadAvatar(resizedBMP).observeForever(uploadResult::setValue);
    }
}
