package com.example.converter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.converter.Helpers.ConverterClass;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel
{
    private MutableLiveData<String> input = new MutableLiveData<String>(""); //input
    private MutableLiveData<String> output = new MutableLiveData<String>("");
    private MutableLiveData<String> category = new MutableLiveData<String>("");
    private MutableLiveData<String> inputConverterCategory = new MutableLiveData<String>("");
    private MutableLiveData<String> outputConverterCategory = new MutableLiveData<String>("");
    private ConverterClass converterClass = new ConverterClass();

    public LiveData<String> getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input.setValue(input);
    }

    public LiveData<String> getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output.setValue(output);
    }

    public LiveData<String> getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category.setValue(category);
    }

    public LiveData<String> getInputConverterCategory() {
        return inputConverterCategory;
    }

    public void setInputConverterCategory(String inputConverterCategory) {
        this.inputConverterCategory.setValue(inputConverterCategory);
    }

    public LiveData<String> getOutputConverterCategory() {
        return outputConverterCategory;
    }

    public void setOutputConverterCategory(String outputConverterCategory) {
        this.outputConverterCategory.setValue(outputConverterCategory);
    }

    public List<String> getFieldsCategories(){
        switch (category.getValue()){
            case "Масса":
                return converterClass.getWeightCategories();
            case "Время":
                return converterClass.getTimeCategories();
            case "Расстояние":
                return converterClass.getDistanceCategories();
        }
        return new ArrayList<>();
    }

    public List<String> getMainCategories(){
        return converterClass.getCategories();
    }

    public void ConvertValue(){
        output.setValue(converterClass.Calculate(input.getValue(), category.getValue(), inputConverterCategory.getValue(), outputConverterCategory.getValue()));
    }

    public void ChangeFields(){
        String temp = inputConverterCategory.getValue();
        inputConverterCategory.setValue(outputConverterCategory.getValue());
        outputConverterCategory.setValue(temp);
        input.setValue(output.getValue());
        ConvertValue();
    }
}
