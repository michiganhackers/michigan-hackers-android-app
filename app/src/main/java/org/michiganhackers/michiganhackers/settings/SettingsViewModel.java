package org.michiganhackers.michiganhackers.settings;

import android.net.Uri;

import org.michiganhackers.michiganhackers.MemberLiveDataWrapper;
import org.michiganhackers.michiganhackers.directory.Member;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {
    private final String TAG = getClass().getCanonicalName();

    private MemberLiveDataWrapper memberLiveDataWrapper;

    SettingsViewModel(String uid) {
        memberLiveDataWrapper = new MemberLiveDataWrapper(uid);
    }

    public MutableLiveData<Member> getMember() {
        return memberLiveDataWrapper.getMember();
    }

    public void setMember(Member newMember, Uri filePath) {
        memberLiveDataWrapper.setMember(newMember, filePath);
    }

    public void removeMember(String uid) {
        memberLiveDataWrapper.removeMember(uid);
    }
}