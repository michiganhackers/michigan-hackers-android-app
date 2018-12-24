package org.michiganhackers.michiganhackers.profile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class ProfileViewModelFactory implements ViewModelProvider.Factory{
    private String uid;
    public ProfileViewModelFactory(String uid){
        this.uid = uid;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(uid);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
