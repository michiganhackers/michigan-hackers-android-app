package org.michiganhackers.michiganhackers;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.Map;

public class DirectoryViewModel extends ViewModel{
    DirectoryRepository directoryRepository;
    private MutableLiveData<Map<String, Team>> teamsByName;
    public DirectoryViewModel(DirectoryRepository directoryRepository_in){
        if(this.directoryRepository != null){
            return;
        }
        if(directoryRepository_in != null){
            this.directoryRepository = directoryRepository_in;
        }
    }

}
