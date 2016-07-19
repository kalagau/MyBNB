package com.company;

import sun.util.calendar.BaseCalendar;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        this.questions = new ArrayList<>();
        this.answers = new HashMap();
    }

    public Map askQuestions(){
        questions.forEach((info)->askQuestion(info));
        Map answersCopy = new HashMap();
        answersCopy.putAll(answers);
        this.clear();
        return answersCopy;
    }

    private void askQuestion(Info info){
        System.out.println(info.text);
        String input = sc.next();
        switch (info.type){
            case BOOLEAN:
                int boolVal = getBoolean(input);
                if(boolVal == -1 || !(Boolean) info.isValid.apply(boolVal)) askQuestion(info);
                else answers.put(info.key, boolVal == 1);
                break;
            case INTEGER:
                int intVal = getInt(input);
                if(intVal == -1 || !(Boolean) info.isValid.apply(intVal)) askQuestion(info);
                else answers.put(info.key, intVal);
                break;
            case DOUBLE:
                double doubleVal = getDouble(input);
                if(doubleVal == -1 || !(Boolean) info.isValid.apply(doubleVal)) askQuestion(info);
                else answers.put(info.key, doubleVal);
                break;
            case DATE:
                Date dateVal = getDate(input);
                if(dateVal == null || !(Boolean) info.isValid.apply(dateVal)) askQuestion(info);
                else answers.put(info.key, dateVal);
                break;
            case STRING:
                if(!(Boolean) info.isValid.apply(input)) askQuestion(info);
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

    private Date getDate(String input){
        try {
            return Date.valueOf(input);
        } catch(IllegalArgumentException e){
            return null;
        }
    }
}
