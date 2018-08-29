package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ProfileViewModelFactory implements ViewModelProvider.Factory{
    private String uid;
    ProfileViewModelFactory(String uid){
        this.uid = uid;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(uid);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
