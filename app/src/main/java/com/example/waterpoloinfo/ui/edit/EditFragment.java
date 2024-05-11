package com.example.waterpoloinfo.ui.edit;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.waterpoloinfo.R;
import com.example.waterpoloinfo.Team;
import com.example.waterpoloinfo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditFragment extends Fragment {
    private static final String LOG_TAG = EditFragment.class.getName();
    private Spinner teamsSpinner;
    private Spinner deleteTeamssSpinner;

    private Spinner playersSpinner;
    private Spinner playersToTeamSpinner;

    private EditText createTeamEditText;
    private EditText updateTeamNameEditText;
    private List<String> playerNames;
    private List<String> teamNames;
    // Firebase Firestore inicializálása
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String USERS_COLLECTION = "Users";
    private static final String TEAMS_COLLECTION = "Teams";
    private static final String FIELD_SELECTED_ACCOUNT_TYPE =  "selected_account_type";
    private static final String PLAYER_SELECTED_ACCOUNT_TYPE =  "player";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit, container, false);

        teamsSpinner = root.findViewById(R.id.teamsSpinner);
        teamNames = new ArrayList<>();

        deleteTeamssSpinner = root.findViewById(R.id.deleteTeamssSpinner);

        playersSpinner = root.findViewById(R.id.playersSpinner);
        playerNames = new ArrayList<>();
        playersToTeamSpinner = root.findViewById(R.id.playersToTeamSpinner);

        // Firestore-ból lekérjük az "players" típusú felhasználók neveit
        loadPlayers();

        // Firestore-ból lekérjük az csapatokat
        loadTeams();

        //Melyik csapathoz adjuk hozzá a játékost
        Button addPlayerToTeamButton = root.findViewById(R.id.playerToTeamButton);

        loadTeamsToUpdateWithPlayer(addPlayerToTeamButton);


        createTeamEditText = root.findViewById(R.id.createTeamEditText);
        Button createTeamButton = root.findViewById(R.id.createTeamButton);
        createTeam(createTeamButton);


        /************************************** Csapat tölrése ************************************/
        Button deleteTeamButton = root.findViewById(R.id.deleteTeamButton);
        deleteTeam(deleteTeamButton);

        /*Csapat nevének beállítása*/
        updateTeamNameEditText = root.findViewById(R.id.updateTeamNameEditText);
        Button updateTeamNameButton = root.findViewById(R.id.updateTeamNameButton);
        setTeamName(updateTeamNameEditText, updateTeamNameButton);


        return root;
    }

    public void loadTeams(){
        db.collection(TEAMS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Minden csapat nevét hozzáadjuk a listához
                                String teamName = document.getString("team_name");
                                teamNames.add(teamName);
                            }
                            // Spinner feltöltése
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_item, teamNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            updateTeamSpinner();
                        } else {
                            Toast.makeText(getContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }    /*Csapatok betöltése*/

    private void loadTeamsToUpdateWithPlayer(Button addPlayerToTeamButton) {
        addPlayerToTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Firestore referenciák
                CollectionReference teamsRef = db.collection(TEAMS_COLLECTION);
                CollectionReference usersRef = db.collection(USERS_COLLECTION);

                String teamName = playersToTeamSpinner.getSelectedItem().toString();
                String playerName = playersSpinner.getSelectedItem().toString();

                // A játékos lekérése a név alapján
                usersRef.whereEqualTo("user_name", playerName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot userDocument : task.getResult()) {
                                        // A felhasználó dokumentumának lekérése
                                        String userEmail = userDocument.getString("user_email");

                                        // A csapat lekérése a név alapján
                                        teamsRef.whereEqualTo("team_name", teamName)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot teamDocument : task.getResult()) {
                                                                // A csapat dokumentumának frissítése
                                                                DocumentReference teamRef = teamsRef.document(teamDocument.getId());
                                                                CollectionReference playersRef = teamRef.collection("Players");

                                                                // Új játékos létrehozása
                                                                Map<String, Object> playerData = new HashMap<>();
                                                                playerData.put("user_name", playerName);
                                                                playerData.put("user_email", userEmail);
                                                                playerData.put("selected_account_type", "player");
                                                                playersRef.document(playerName).set(playerData)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                // Sikeres hozzáadás esetén értesítés
                                                                                Toast.makeText(getContext(), "A " + playerName + " játékost hozzáadtuk a " + teamName + " csapathoz", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                // Sikertelen hozzáadás esetén hibaüzenet
                                                                                Toast.makeText(getContext(), "Hiba történt a játékos hozzáadásakor: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    Log.d(LOG_TAG, "Error getting documents: ", task.getException());
                                }
                            }
                        });
            }
        });


    }


    public void createTeam(Button createTeamButton) {

        createTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Csapat létrehozása
                String teamName = createTeamEditText.getText().toString();
                String teamId = createTeamEditText.getText().toString();

                Team team = new Team(teamName, teamId);

                // Firestore kollekció referenciájának hozzáférése
                CollectionReference teamsRef = db.collection(TEAMS_COLLECTION);

                // Új csapat hozzáadása a "Teams" kollekcióhoz
                teamsRef.add(team)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(LOG_TAG, "Csapat sikeresen létrehozva, id: " + documentReference.getId());
                                // Sikeres csapatlétrehozás esetén további műveletek végrehajtása
                                Toast.makeText(getContext(), teamName +" csapat sikeresen létrehozva.", Toast.LENGTH_LONG).show();
                                updateTeamSpinner();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(LOG_TAG, "Csapat létrehozása sikertelen", e);
                                // Csapat létrehozása sikertelen esetén kezelés

                            }
                        });
            }
        });


    } /*Csapat létrehozása*/ // - where

    private void deleteTeam(Button deleteTeamButton) {

        // Csapat törlése gomb eseménykezelője
        deleteTeamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiválasztott csapat lekérése a Spinnerből
                String selectedTeamName = deleteTeamssSpinner.getSelectedItem().toString();
                // Csapat törlése

                db.collection(TEAMS_COLLECTION)
                        .whereEqualTo("team_name", selectedTeamName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // Csapat törlése Firestore-ból
                                        document.getReference().delete();
                                        // Sikeres törlés után értesítés
                                        Toast.makeText(getContext(), "A " + selectedTeamName + " csapat törölve lett", Toast.LENGTH_SHORT).show();
                                        // A Spinnter frissítése az új adatokkal
                                        updateTeamSpinner();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Hiba történt a csapat törlése közben: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                updateTeamSpinner();
            }
        });



    }     /*Csapat törlése*/ // - where

    public void setTeamName(EditText editText, Button button){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiválasztott csapat nevének lekérése a Spinner-ből
                String selectedTeamName = teamsSpinner.getSelectedItem().toString();

                // Új csapatnév lekérése az EditText-ből
                String newTeamName = editText.getText().toString();

                // Firestore referenciája a Teams kollekcióhoz
                CollectionReference teamsRef = db.collection(TEAMS_COLLECTION);

                // Kiválasztott csapat frissítése a Firestore-ban
                teamsRef.whereEqualTo("team_name", selectedTeamName)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        // A kiválasztott csapat dokumentumának azonosítója
                                        String teamId = document.getId();

                                        // Kiválasztott csapat dokumentumának frissítése az új névvel
                                        teamsRef.document(teamId).update("team_name", newTeamName)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Csapatnév frissítve", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Hiba történt a csapatnév frissítése közben: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                    updateTeamSpinner();


                                } else {
                                    Toast.makeText(getContext(), "Hiba történt a csapat keresése közben: " + task.getException(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }     /*Csapat nevének beállítása*/

    private void updateTeamSpinner() {
        db.collection(TEAMS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> updatedTeamNames = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Minden csapat nevét hozzáadjuk az új listához
                                String teamName = document.getString("team_name");
                                updatedTeamNames.add(teamName);
                            }
                            // Az adapter frissítése az új csapatnevekkel
                            ArrayAdapter<String> updatedAdapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_item, updatedTeamNames);
                            updatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            teamsSpinner.setAdapter(updatedAdapter);
                            deleteTeamssSpinner.setAdapter(updatedAdapter);
                            playersToTeamSpinner.setAdapter(updatedAdapter);
                        } else {
                            Toast.makeText(getContext(), "Hiba történt az adatok lekérdezése közben: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }     /*Spinner frissítése az új adatokkal*/

    private void loadPlayers(){
        db.collection(USERS_COLLECTION)
                .whereEqualTo(FIELD_SELECTED_ACCOUNT_TYPE, PLAYER_SELECTED_ACCOUNT_TYPE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Minden "player" típusú felhasználó nevét hozzáadjuk a listához
                                String userName = document.getString("user_name");
                                playerNames.add(userName);
                            }
                            // Spinner feltöltése az "Editor" típusú felhasználók neveivel
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                                    android.R.layout.simple_spinner_item, playerNames);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            playersSpinner.setAdapter(adapter);
                        } else {
                            Toast.makeText(getContext(), "Error getting documents: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




}
