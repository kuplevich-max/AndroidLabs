package com.example.seafight;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.seafight.game.FieldValidator;
import com.example.seafight.game.GameManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainViewModel extends ViewModel {
    private MutableLiveData<GameManager.GameStatus> currentGameState;
    private MutableLiveData<GameManager.AccountInfo> currentAccountInfo;
    private MutableLiveData<GameManager.UnitState[][]> currentUserField;
    private MutableLiveData<DatabaseReference> currentGameNodeRef;
    @SuppressLint("StaticFieldLeak")
    public Activity activityContext;

    public void changeCurrentGameState(String code) {
        setCurrentGameNodeRef(FirebaseDatabase.getInstance().getReference("processes").child(code));
        changeCurrentGameState();
    }

    public void initiateGameState(String code, Activity activity) {
        setCurrentGameNodeRef(FirebaseDatabase.getInstance().getReference("processes").child(code));
        setCurrentGameState(new GameManager.GameStatus(
            getCurrentUserField().getValue(), GameManager.getEmptyField(), FirebaseAuth.getInstance().getCurrentUser().getUid(),
            null, GameManager.GameState.Waiting
        ));
        changeCurrentGameState();

        getCurrentGameNodeRef().getValue().child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GameManager.GameStatus gs = new GameManager.GameStatus();
                gs.hostState = getCurrentUserField().getValue();
                gs.userState = GameManager.getEmptyField();
                gs.hostId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                gs.state = GameManager.GameState.hostTurn;
                setCurrentGameState(gs);
                changeCurrentGameState(code);
                setGameStateListener();

                toGameScreen(activity);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast("Что-то пошло не так при создании игры. Error - " + error.getCode());
            }
        });
    }

    private void toGameScreen(Activity activity) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
        navController.navigate(R.id.nav_game);
    }

    public void connectToGame(String code, Activity activity) {
        setCurrentGameNodeRef(FirebaseDatabase.getInstance().getReference("processes").child(code));
        DatabaseReference dbr = getCurrentGameNodeRef().getValue();
        String s = dbr.getKey();
        dbr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String userId = snapshot.child("userId").getValue(String.class);
                String hostId = snapshot.child("hostId").getValue(String.class);
                if (hostId == null) {
                    toast("Не существует такой игры ");
                    return;
                }
                GameManager.GameState state = snapshot.child("state").getValue(GameManager.GameState.class);
                String cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (state != GameManager.GameState.Ended) {
                    if (userId != null) {
                        if (userId.equals(cuid)) {
                            setGameStateFromSnapshot(snapshot);
                            toast("Вы успешно переподключились к игре");
                            toGameScreen(activity);
                        } else {
                            toast("Кто-то уже занял это место в игре");
                        }
                    } else if (hostId.equals(cuid)) {
                        setGameStateFromSnapshot(snapshot);
                        toast("Вы зашли как создатель игры");
                        toGameScreen(activity);
                    } else {
                        Map<String, Object> connectUpdate = new HashMap<>();
                        connectUpdate.put("/userState", GameManager.fieldToString(Objects.requireNonNull(getCurrentUserField().getValue())));
                        connectUpdate.put("/userId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        if (state == GameManager.GameState.Waiting) connectUpdate.put("/state", GameManager.GameState.hostTurn.toString());
                        getCurrentGameNodeRef().getValue().updateChildren(connectUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                toast("Вы подключились к игре");
                                toGameScreen(activity);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                toast("Что-то пошло не так при подключении к игре");
                            }
                        });
                    }
                } else if (state == GameManager.GameState.Ended) {
                    toast("Игра уже закончена!");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast("Что-то не пошло при подключении. Error - " + error.getCode());
            }
        });
        setGameStateListener();
    }

    public void changeCurrentGameState() {
        GameManager.GameStatus gs = getCurrentGameState().getValue();
        GameStateForSend gfs = new GameStateForSend(
                gs.state.name(), gs.hostId, gs.userId,
                GameManager.fieldToString(gs.hostState),
                GameManager.fieldToString(gs.userState)
        );
        getCurrentGameNodeRef().getValue().setValue(gfs).addOnCompleteListener(command -> {
            if (command.isSuccessful()) {
                if (gs.state == GameManager.GameState.Ended) {
                    toast("Игра закончена!");
                } else if (gs.state == GameManager.GameState.Waiting) {
                    toast("Игра создана!");
                }
            } else {
                toast("Что-то ошло не так при создании игры");
            }
        });
    }

    static class GameStateForSend {
        public String state = "";
        public String hostId = "";
        public String userId = "";
        public String hostState = "";
        public String userState = "";
        public GameStateForSend(String state, String hostId, String userId, String hostState, String userState) {
            this.state = state; this.hostId = hostId; this.userId = userId; this.hostState = hostState; this.userState = userState;
        }
    }

    // set listener by current code
    public void setGameStateListener() {
        ValueEventListener vel = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                setGameStateFromSnapshot(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                toast("Что-то пошло не так при изменения состояния игры. Error - " + error.getCode());
            }
        };
        getCurrentGameNodeRef().getValue().addValueEventListener(vel);
    }

    private static GameManager.UnitState[][] getState(DataSnapshot snapshot, String child) throws JsonProcessingException {
        DataSnapshot stateRaw = snapshot.child(child);
        ObjectMapper mapper = new ObjectMapper();
        String content = (String)stateRaw.getValue(String.class);
        return content == null ? GameManager.getEmptyField() : mapper.readValue(content, GameManager.UnitState[][].class);
    }

    public static GameManager.GameStatus parseGSSnapshot(DataSnapshot snapshot) {
        GameManager.GameStatus gs = new GameManager.GameStatus();
        gs.state = snapshot.child("state").getValue(GameManager.GameState.class);
        gs.hostId = snapshot.child("hostId").getValue(String.class);
        gs.userId = snapshot.child("userId").getValue(String.class);
        try {
            gs.hostState = getState(snapshot, "hostState");
            gs.userState = getState(snapshot, "userState");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return gs;
    }


    public void setGameStateFromSnapshot(DataSnapshot snapshot) {
        GameManager.GameStatus gs = parseGSSnapshot(snapshot);

        if(gs.userState != null & gs.hostState != null) {
            setCurrentGameState(gs);
            if (isCurrentUserTurn()) {
                toast("Ваш ход!");
            }
        }
    }

    public boolean isCurrentUserTurn() {
        GameManager.GameStatus gs = getCurrentGameState().getValue();
        if (gs.state == GameManager.GameState.Ended) {
            return true;
        } else if (
            gs.userId != null && gs.hostId != null &&
            (gs.userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && gs.state == GameManager.GameState.userTurn ||
            gs.hostId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()) && gs.state == GameManager.GameState.hostTurn)
        ) {
            return true;
        } else {
            return false;
        }
    }

    public void setCurrentGameState(GameManager.GameStatus currentGameState) {
        if (this.currentGameState == null) {
            this.currentGameState = new MutableLiveData<GameManager.GameStatus>(new GameManager.GameStatus(
                    GameManager.getEmptyField(), GameManager.getEmptyField(),
                    null, null, GameManager.GameState.Waiting
            ));
        }
        this.currentGameState.setValue(currentGameState);
    }

    public LiveData<GameManager.GameStatus> getCurrentGameState() {
        if (currentGameState == null) {
            currentGameState = new MutableLiveData<>(new GameManager.GameStatus(
                GameManager.getEmptyField(), GameManager.getEmptyField(),
                null, null, GameManager.GameState.Waiting
            ));
        }
        return currentGameState;
    }

    public void setCurrentAccountInfo(GameManager.AccountInfo value) {
        if (this.currentAccountInfo == null) {
            this.currentAccountInfo = new MutableLiveData<>(new GameManager.AccountInfo());
        }
        this.currentAccountInfo.setValue(value);
    }

    public LiveData<GameManager.AccountInfo> getCurrentAccountInfo() {
        if (this.currentAccountInfo == null) {
            this.currentAccountInfo = new MutableLiveData<>(new GameManager.AccountInfo());
        }
        return this.currentAccountInfo;
    }

    public void setCurrentUserField(GameManager.UnitState[][] field) {
        if (currentUserField == null) { currentUserField = new MutableLiveData<>(GameManager.getEmptyField()); }
        currentUserField.setValue(field);
    }

    public LiveData<GameManager.UnitState[][]> getCurrentUserField() {
        if (currentUserField == null) { currentUserField = new MutableLiveData<>(GameManager.getEmptyField()); }
        return currentUserField;
    }

    public void setCurrentGameNodeRef(DatabaseReference ref) {
        if (currentGameNodeRef == null) { currentGameNodeRef = new MutableLiveData<>(FirebaseDatabase.getInstance().getReference("processes/test")); }
        currentGameNodeRef.setValue(ref);
    }

    public LiveData<DatabaseReference> getCurrentGameNodeRef() {
        if (currentGameNodeRef == null) { currentGameNodeRef = new MutableLiveData<>(FirebaseDatabase.getInstance().getReference("processes/test")); }
        return currentGameNodeRef;
    }

    public void toast(String text) {
        if (activityContext != null) {
            Toast.makeText(activityContext, text, Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Error", "Активити не выставлено");
        }
    }

    public void setFieldClickListenerCreate(TableLayout field) {
        int size = 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int i_c = i, j_c = j;
                Button cell = (Button)((TableRow)field.getChildAt(i)).getChildAt(j);
                cell.setOnClickListener(v -> {
                    GameManager.UnitState[][] states = getCurrentUserField().getValue();
                    FieldValidator fieldValidator = new FieldValidator(states);
                    if (!fieldValidator.CheckShipCellsInAngles(i_c, j_c) &&
                        states[i_c][j_c] != GameManager.UnitState.ALIVE
                    ) {
                        states[i_c][j_c] = GameManager.UnitState.ALIVE;
                    } else {
                        states[i_c][j_c] = GameManager.UnitState.EMPTY;
                    }
                    fieldValidator = new FieldValidator(states);
                    fieldValidator.Check();
                    setCurrentUserField(states);
                });
            }
        }
    }

    public void setFieldClickListenerAttack(TableLayout field, String fieldName) {
        int size = 10;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int i_c = i, j_c = j;
                Button cell = (Button)((TableRow)field.getChildAt(i)).getChildAt(j);
                cell.setOnClickListener(v -> {
                    String cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    GameManager.GameStatus gameState = this.currentGameState.getValue();
                    GameManager.UnitState[][] states = GameManager.getEmptyField();
                    states = fieldName.equals("USER_FIELD") ? gameState.userState : gameState.hostState;
                    if(gameState.state != GameManager.GameState.Waiting) {
                        if (gameState.hostId.equals(cuid) && gameState.state == GameManager.GameState.hostTurn) {
                            gameState.state = states[i_c][j_c] == GameManager.UnitState.ALIVE ?
                                GameManager.GameState.hostTurn : GameManager.GameState.userTurn;
                        } else if (gameState.userId.equals(cuid) && gameState.state == GameManager.GameState.userTurn) {
                            gameState.state = states[i_c][j_c] == GameManager.UnitState.ALIVE ?
                                GameManager.GameState.userTurn : GameManager.GameState.hostTurn;
                        } else {
                            if (gameState.state != GameManager.GameState.Ended) {
                                toast("Не ваш ход. Подождите");
                            } else {
                                toast("Игра уже закончена");
                            }
                            return;
                        }
                    } else {
                        toast("Не ваш ход. Подождите.");
                    }
                    if (states[i_c][j_c] == GameManager.UnitState.EMPTY) {
                        states[i_c][j_c] = GameManager.UnitState.MISSED;
                    } else if (states[i_c][j_c] == GameManager.UnitState.ALIVE) {
                        states[i_c][j_c] = GameManager.UnitState.DESTROYED;
                    }

                    FieldValidator hostFieldValidator = new FieldValidator(gameState.hostState),
                                   userFieldValidator = new FieldValidator(gameState.userState);
                    if (hostFieldValidator.isDefeat()) {
                        gameState.state = GameManager.GameState.Ended;
                        gameState.userId = GameManager.GameStatus.winCondition + gameState.userId;
                        toast("Игра закончена, игрок победил!");
                    } else if (userFieldValidator.isDefeat()) {
                        gameState.state = GameManager.GameState.Ended;
                        gameState.hostId = GameManager.GameStatus.winCondition + gameState.hostId;
                        toast("Игра закончена, создатель победил!");
                    }
                    setCurrentGameState(gameState);
                    changeCurrentGameState();
                });
            }
        }
    }
}