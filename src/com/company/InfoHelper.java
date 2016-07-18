package com.company;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-18.
 */
public class InfoHelper {

    private Scanner sc;
    private ArrayList<Info> questions;
    private Map answers;

    public InfoHelper(Scanner sc) {
        this.sc = sc;
        this.answers = new HashMap();
    }

    public Map askQuestions(){
        questions.forEach((info)->askQuestion(info));
        Map answersCopy = answers;
        this.clear();
        return answersCopy;
    }

    private void askQuestion(Info info){
        System.out.println(info.text);
        String input = sc.next();
        switch (info.type){
            case BOOLEAN:
                int boolVal = getBoolean(input);
                if(boolVal == -1) askQuestion(info);
                else answers.put(info.key, boolVal);
                break;
            case INTEGER:
                int intVal = getInt(input);
                if(intVal == -1) askQuestion(info);
                else answers.put(info.key, intVal);
                break;
            case DOUBLE:
                double doubleVal = getDouble(input);
                if(doubleVal == -1) askQuestion(info);
                else answers.put(info.key, doubleVal);
                break;
            case DATE:
            case STRING:
                answers.put(info.key, input);
                break;
        }
    }

    public void add(Info info){
        questions.add(info);
    }

    public void clear(){
        questions.clear();
        answers.clear();
    }

    private int getBoolean(String input){
        switch(input){
            case "n": return 0;
            case "y": return 1;
            default: return -1;
        }
    }

    private int getInt(String input) {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double getDouble(String input) {
        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }


}
