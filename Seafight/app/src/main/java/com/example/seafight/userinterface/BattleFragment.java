package com.example.seafight.userinterface;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.seafight.R;
import com.example.seafight.game.GameManager;
import com.example.seafight.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;

public class BattleFragment extends Fragment {

    private MainViewModel mainViewModel;

    @SuppressLint("UseCompatLoadingForDrawables")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        GameManager.GameStatus gs = mainViewModel.getCurrentGameState().getValue();
        TableLayout fieldUser =
            GameManager.createField(requireActivity(), view, R.id.fieldUser, gs.userState);
        TableLayout fieldHost =
            GameManager.createField(requireActivity(), view, R.id.fieldHost, gs.hostState);

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            String currentUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (gs.hostId != null && gs.hostId.equals(currentUid)){
                mainViewModel.setFieldClickListenerAttack(fieldUser, "USER_FIELD");
            } else if (gs.userId != null && gs.userId.equals(currentUid)){
                mainViewModel.setFieldClickListenerAttack(fieldHost, "HOST_FIELD");
            }
        }

        mainViewModel.getCurrentGameState().observe(getViewLifecycleOwner(), state -> {
            GameManager.GameStatus gstate = mainViewModel.getCurrentGameState().getValue();
            String cuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            boolean isCurrentHost = gstate.hostId != null && gstate.hostId.equals(cuid),
                    isCurrentUser = gstate.userId != null && gstate.userId.equals(cuid);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                GameManager.fillField(requireActivity(), fieldHost, gstate.hostState, !isCurrentHost);
                GameManager.fillField(requireActivity(), fieldUser, gstate.userState, !isCurrentUser);
            }
        });

        return view;
    }
}