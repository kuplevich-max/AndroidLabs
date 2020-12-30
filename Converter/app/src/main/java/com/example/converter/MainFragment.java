package com.example.converter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class MainFragment extends Fragment {

    MainViewModel mainViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        EditText input = (EditText) view.findViewById(R.id.tv_input);
        EditText output = (EditText) view.findViewById(R.id.tv_output);
        Button changeFieldsButton = (Button) view.findViewById(R.id.change);
        Button convert = (Button) view.findViewById(R.id.convert);
        ImageView copyFirst = (ImageView) view.findViewById(R.id.copy_input);
        ImageView copySecond = (ImageView) view.findViewById(R.id.copy_output);
        input.setInputType(InputType.TYPE_NULL);
        output.setInputType(InputType.TYPE_NULL);
        Spinner convertCategorySpinner = (Spinner) view.findViewById(R.id.spinner_category);
        Spinner inputCategorySpinner = (Spinner) view.findViewById(R.id.spinner_input);
        Spinner outputCategorySpinner = (Spinner) view.findViewById(R.id.spinner_output);

        mainViewModel.getInput().observe(getViewLifecycleOwner(), val -> {
            input.setText(val);
        });

        mainViewModel.getOutput().observe(getViewLifecycleOwner(), val -> {
            output.setText(val);
        });

        changeFieldsButton.setOnClickListener(view1 -> {
            mainViewModel.ChangeFields();
        });

        copyFirst.setOnClickListener(view1 -> {
            CopyValue(input);
        });

        copySecond.setOnClickListener(view1 -> {
            CopyValue(output);
        });

        ArrayAdapter<String> mainAdapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_item, mainViewModel.getMainCategories());
        convertCategorySpinner.setAdapter(mainAdapter);

        convertCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainViewModel.setCategory(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mainViewModel.getCategory().observe(getViewLifecycleOwner(), val->{
            List<String> categories = mainViewModel.getFieldsCategories();
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, categories);
            inputCategorySpinner.setAdapter(adapter);
            outputCategorySpinner.setAdapter(adapter);
            inputCategorySpinner.setSelection(mainViewModel.getFieldsCategories().indexOf(mainViewModel.getInputConverterCategory().getValue()));
            outputCategorySpinner.setSelection(mainViewModel.getFieldsCategories().indexOf(mainViewModel.getOutputConverterCategory().getValue()));
        });

        inputCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainViewModel.setInputConverterCategory(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mainViewModel.getInputConverterCategory().observe(getViewLifecycleOwner(), val->{
            inputCategorySpinner.setSelection(mainViewModel.getFieldsCategories().indexOf(val));
        });

        outputCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainViewModel.setOutputConverterCategory(adapterView.getItemAtPosition(i).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mainViewModel.getOutputConverterCategory().observe(getViewLifecycleOwner(), val->{
            outputCategorySpinner.setSelection(mainViewModel.getFieldsCategories().indexOf(val));
        });

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainViewModel.ConvertValue();
            }
        });

        return view;
    }

    private void CopyValue(EditText et){
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", et.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext().getApplicationContext(), "Скопировано",Toast.LENGTH_SHORT).show();
    }
}