package com.example.converter.Helpers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterClass {
    public Map<String, Double> distance = new HashMap<String, Double>();
    public Map<String, Double> time = new HashMap<String, Double>();
    public Map<String, Double> weight = new HashMap<String, Double>();

    public ConverterClass() {
        InitDistanceCat();
        InitTimeCat();
        InitWeightCat();
    }

    private void InitTimeCat(){
        time.put("Миллисекунды", 1000.);
        time.put("Секунды", 1.);
        time.put("Часы", 1./3600);
        time.put("Дни", 1./86400);
        time.put("Года", 1./31536000);
    }

    private void InitDistanceCat(){
        distance.put("Миллиметры", 1000.);
        distance.put("Сантиметры", 100.);
        distance.put("Дециметры", 10.);
        distance.put("Метры", 1.);
        distance.put("Километры", 1./1000);
    }

    private void InitWeightCat(){
        weight.put("Тонны",1./1000);
        weight.put("Центнеры",1./100);
        weight.put("Килограммы",1.);
        weight.put("Граммы",1000.);
        weight.put("Милиграммы",1000000.);
    }

    public List<String> getCategories(){
        return Arrays.asList("Расстояние", "Время", "Масса");
    }

    public List<String> getDistanceCategories(){
        return new ArrayList<String>(distance.keySet());
    }

    public List<String> getTimeCategories(){
        return new ArrayList<String>(time.keySet());
    }

    public List<String> getWeightCategories(){
        return new ArrayList<String>(weight.keySet());
    }


    public String Calculate(String input, String category, String inputCategory, String outputCategory){
        if (input.isEmpty())
            return "";

        double data;

        try{
            data = Double.parseDouble(input);
        }
        catch (Exception e){
            return "Error";
        }

        double fCoefficient = 1., sCoefficient=1.;

        switch (category){
            case "Масса":
                fCoefficient = weight.get(inputCategory);
                sCoefficient = weight.get(outputCategory);
                break;
            case "Время":
                fCoefficient = time.get(inputCategory);
                sCoefficient = time.get(outputCategory);
                break;
            case "Расстояние":
                fCoefficient = distance.get(inputCategory);
                sCoefficient = distance.get(outputCategory);
                break;
        }

        return String.valueOf(data * sCoefficient / fCoefficient);
    }
}
