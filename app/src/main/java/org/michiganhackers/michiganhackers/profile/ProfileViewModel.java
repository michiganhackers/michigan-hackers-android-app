package org.michiganhackers.michiganhackers.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.net.Uri;

import org.michiganhackers.michiganhackers.MemberLiveDataWrapper;
import org.michiganhackers.michiganhackers.TeamsLiveDataWrapper;
import org.michiganhackers.michiganhackers.directory.Member;

import java.util.List;


public class ProfileViewModel extends ViewModel {
    private static final String TAG = "ProfileViewModel";

    private MemberLiveDataWrapper memberLiveDataWrapper;
    private TeamsLiveDataWrapper teamsLiveDataWrapper;

    ProfileViewModel(String uid) {
        memberLiveDataWrapper = new MemberLiveDataWrapper(uid);
        teamsLiveDataWrapper = new TeamsLiveDataWrapper(uid);
    }

    public LiveData<List<String>> getTeamNames() {
        return teamsLiveDataWrapper.getTeamNames();
    }

    public MutableLiveData<Member> getMember() {
        return memberLiveDataWrapper.getMember();
    }

    public void setMember(Member newMember, Uri filePath) {
        memberLiveDataWrapper.setMember(newMember, filePath);
    }
}