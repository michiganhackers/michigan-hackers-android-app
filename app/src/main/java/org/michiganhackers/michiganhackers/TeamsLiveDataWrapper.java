package org.michiganhackers.michiganhackers;

import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.michiganhackers.michiganhackers.directory.Member;
import org.michiganhackers.michiganhackers.directory.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class TeamsLiveDataWrapper {
    private static final String TAG = "TeamsLiveDataWrapper";

    // Note: This livedata is not updated in realtime
    private MutableLiveData<Map<String, Team>> teams;

    private LiveData<List<String>> teamNames;

    public TeamsLiveDataWrapper(String uid) {
        CollectionReference teamsRef = FirebaseFirestore.getInstance().collection("Teams");

        if (this.teams == null) {
            teams = new MutableLiveData<>();
            teamNames = Transformations.map(teams, new Function<Map<String, Team>, List<String>>() {
                @Override
                public List<String> apply(Map<String, Team> input) {
                    return new ArrayList<>(input.keySet());
                }
            });

            teamsRef.get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            Map<String, Team> teamsLocal = new TreeMap<>();
                            for (DocumentSnapshot documentSnapshot : documentSnapshots) {
                                Team team = documentSnapshot.toObject(Team.class);
                                if (team != null) {
                                    teamsLocal.put(team.getName(), team);
                                }
                            }
                            teams.setValue(teamsLocal);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Failed to get teams documents: ", e);
                        }
                    });
        }
    }

    public LiveData<List<String>> getTeamNames() {
        return teamNames;
    }
}




