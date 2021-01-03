package com.example.seafight.userinterface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.seafight.R;
import com.example.seafight.game.GameManager;
import com.example.seafight.MainViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserAccountFragment extends Fragment {

    public final int ImagePickRequest = 1;
    public final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 23;

    public Uri imageUri;
    public FirebaseAuth auth;
    public Button logoutButton;
    public ImageView imageView;
    public StorageTask uploadTask;
    public EditText nicknameTextView;
    public MainViewModel mainViewModel;
    public DatabaseReference databaseRef;
    public TextView winsTextView, lossesTextView, winrateTextView;
    public DatabaseReference databaseRefStat;
    public Button chooseImageButton, uploadImageButton, saveImageButton;

    private void filePick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, ImagePickRequest);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        auth = FirebaseAuth.getInstance();
        winsTextView = (TextView) view.findViewById(R.id.wins);
        lossesTextView = (TextView) view.findViewById(R.id.losses);
        winrateTextView = (TextView) view.findViewById(R.id.winrate);
        saveImageButton = (Button) view.findViewById(R.id.save);
        imageView = (ImageView) view.findViewById(R.id.user_image);
        nicknameTextView = (EditText) view.findViewById(R.id.name);
        uploadImageButton = (Button) view.findViewById(R.id.upload);
        logoutButton = (Button) view.findViewById(R.id.logoutButton);
        chooseImageButton = (Button) view.findViewById(R.id.choose_file);
        databaseRef = FirebaseDatabase.getInstance().getReference("profiles");
        databaseRefStat = FirebaseDatabase.getInstance().getReference("processes");
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { filePick(); }
        });


        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseRef.child(Objects.requireNonNull(auth.getUid())).child("nickname").setValue(
                    nicknameTextView.getText().toString().trim()
                ).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(requireActivity(), "Данные изменены", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        databaseRefStat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }

            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (auth.getCurrentUser() != null) {
                    Integer wins = 0, total = 0, losses = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String hostId = dataSnapshot.child("hostId").getValue(String.class),
                               userId = dataSnapshot.child("userId").getValue(String.class);
                        GameManager.GameState currentGameState = dataSnapshot.child("state").getValue(GameManager.GameState.class);
                        if (currentGameState != null &&
                                currentGameState.equals(GameManager.GameState.Ended)
                                && userId != null && hostId != null
                        ) {
                            total++;
                            String currentId = auth.getCurrentUser().getUid();
                            if (hostId.contains(currentId) || userId.contains(currentId)) {
                                if (hostId.contains(currentId) && hostId.charAt(0) == GameManager.GameStatus.winCondition ) {
                                    wins++;
                                }
                                if (userId.contains(currentId) && userId.charAt(0) == GameManager.GameStatus.winCondition) {
                                    wins++;
                                }
                            }
                        }
                    }
                    losses = total - wins;
                    lossesTextView.setText(losses.toString());
                    winsTextView.setText(wins.toString());
                    winrateTextView.setText(Double.toString(Math.floor((double)wins / (double)total * 10000)/100).concat("%"));
                }
            }
        });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(requireActivity(),
                            "Загрузка в процессе", Toast.LENGTH_SHORT).show();
                } else {
                    uploadTheFile();
                }
            }
        });

        String uniqueId = auth.getUid();
        if (uniqueId != null) {
            databaseRef.child(uniqueId).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    GameManager.AccountInfo accountInfo = mainViewModel.getCurrentAccountInfo().getValue();
                    if (accountInfo != null && snapshot.getValue() != null && auth.getCurrentUser() != null) {
                        if (snapshot.getKey() != null && snapshot.getKey().equals("nickname")){
                            String name = snapshot.getValue().toString();
                            nicknameTextView.setText(name);
                            accountInfo.accountName = name;
                        }
                        accountInfo.accountEmail = auth.getCurrentUser().getEmail();
                        if (snapshot.getKey().equals("ImagePath")){
                            String urlImage = snapshot.getValue().toString();
                            Picasso.get().load(urlImage).into(imageView);

                            accountInfo.accountImage = urlImage;
                            mainViewModel.setCurrentAccountInfo(accountInfo);
                        }
                        mainViewModel.setCurrentAccountInfo(accountInfo);
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }
            });
        }


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePickRequest && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Picasso.get().load(data.getData()).into(imageView);
        }
    }

    private String extensionOfFile(Uri uri) {
        ContentResolver cR = requireActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadTheFile(){
        if (imageUri != null) {
            StorageReference fileReference = FirebaseStorage.getInstance().getReference("uploads").child(System.currentTimeMillis() + "." + extensionOfFile(imageUri));
            uploadTask = fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String upload = Objects.requireNonNull(uri).toString();
                            String uniqueId = auth.getUid();
                            assert uniqueId != null;
                            databaseRef.child(uniqueId).child("ImagePath").setValue(upload);
                            Toast.makeText(requireActivity(), "Uploaded", Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .addOnFailureListener(e ->
                    Toast.makeText(requireActivity(), e.getMessage(), Toast.LENGTH_SHORT).show()
                );
        }
    }
}