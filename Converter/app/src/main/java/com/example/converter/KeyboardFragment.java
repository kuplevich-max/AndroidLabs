package com.example.converter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class KeyboardFragment extends Fragment implements View.OnClickListener{

    MainViewModel mainViewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keyboard, container, false);
        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        Button button0 = (Button) view.findViewById(R.id.button_0);
        Button button1 = (Button) view.findViewById(R.id.button_1);
        Button button2 = (Button) view.findViewById(R.id.button_2);
        Button button3 = (Button) view.findViewById(R.id.button_3);
        Button button4 = (Button) view.findViewById(R.id.button_4);
        Button button5 = (Button) view.findViewById(R.id.button_5);
        Button button6 = (Button) view.findViewById(R.id.button_6);
        Button button7 = (Button) view.findViewById(R.id.button_7);
        Button button8 = (Button) view.findViewById(R.id.button_8);
        Button button9 = (Button) view.findViewById(R.id.button_9);
        Button buttonDot = (Button) view.findViewById(R.id.button_dot);
        Button buttonDel = (Button) view.findViewById(R.id.button_del);
        button0.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        buttonDot.setOnClickListener(this);
        buttonDel.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        Button b = (Button)view;

        String str = mainViewModel.getInput().getValue();

        if (b.getText().charAt(0) == '⌫'){
            if (str.length() > 0)
                mainViewModel.setInput(str.substring(0, str.length() - 1));
        }
        else{
            mainViewModel.setInput(mainViewModel.getInput().getValue() + b.getText());
        }
    }
}