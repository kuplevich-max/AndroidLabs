package com.example.seafight.userinterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.seafight.MainActivity;
import com.example.seafight.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final EditText usernameEditText = view.findViewById(R.id.username);
        final EditText passwordEditText = view.findViewById(R.id.password);
        final Button loginButton = view.findViewById(R.id.login);

        loginButton.setOnClickListener(v -> {
            String login = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireActivity(), "Поля должны быть заполненными", Toast.LENGTH_SHORT).show();
            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(login, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(requireActivity(), "Вы успешно вошли", Toast.LENGTH_SHORT).show();
                            toMainIfAuthenticated(requireActivity());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(login, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(requireActivity(), "Вы успешно зарегистрировались", Toast.LENGTH_SHORT).show();
                                        toMainIfAuthenticated(requireActivity());
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(requireActivity(), "Ошибка при попытке регистрации", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    });
            }
        });
    }

    public static void toMainIfAuthenticated(Activity activity) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent i = new Intent(activity, MainActivity.class);
            i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            activity.startActivity(i);
            activity.finish();
        }
    }
}