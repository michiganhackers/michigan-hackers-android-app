package org.michiganhackers.michiganhackers.settings;

import org.michiganhackers.michiganhackers.profile.ProfileViewModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SettingsViewModelFactory implements ViewModelProvider.Factory {
    private String uid;

    public SettingsViewModelFactory(String uid) {
        this.uid = uid;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(uid);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
