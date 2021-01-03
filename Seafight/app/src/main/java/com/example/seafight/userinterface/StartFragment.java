package com.example.seafight.userinterface;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.seafight.R;
import com.example.seafight.game.FieldValidator;
import com.example.seafight.MainViewModel;
import com.example.seafight.game.GameManager;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class StartFragment extends Fragment {

    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainViewModel =
                new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        GameManager.createField(requireActivity(), view, R.id.field,
            mainViewModel.getCurrentUserField().getValue());
        TableLayout field = (TableLayout) view.findViewById(R.id.field);
        mainViewModel.setFieldClickListenerCreate(field);

        EditText textView = view.findViewById(R.id.createGameCodeInput);

        ((Button) view.findViewById(R.id.createGameCodeGenerate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 10;
                Random random = new Random();
                StringBuilder buffer = new StringBuilder(targetStringLength);
                for (int i = 0; i < targetStringLength; i++) {
                    int randomLimitedInt = leftLimit + (int)
                            (random.nextFloat() * (rightLimit - leftLimit + 1));
                    buffer.append((char) randomLimitedInt);
                }
                String generatedString = buffer.toString();

                textView.setText(generatedString);
            }
        });

        ((Button) view.findViewById(R.id.createGameCodeCopy)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) requireActivity()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Generated code copied",
                        textView.getText().toString());
                clipboard.setPrimaryClip(clip);
            }
        });

        Button createGameCodeCreate = (Button) view.findViewById(R.id.createGameCodeCreate);
        createGameCodeCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.initiateGameState(textView.getText().toString(), requireActivity());
            }
        });

        mainViewModel.getCurrentUserField().observe(getViewLifecycleOwner(), state -> {
            GameManager.fillField(requireActivity(), field, mainViewModel.getCurrentUserField().getValue(), false);
            createGameCodeCreate.setEnabled(new FieldValidator(state).Check());
        });

        ((Button) view.findViewById(R.id.createGameCodeConnect)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainViewModel.connectToGame(textView.getText().toString(), requireActivity());
            }
        });
        return view;
    }
}