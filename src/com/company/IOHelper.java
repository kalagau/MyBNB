package com.company;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-16.
 */
public class IOHelper {

    private ArrayList<String> questions;
    private ArrayList<Runnable> actions;
    private Scanner sc;

    public IOHelper(Scanner sc) {
        this.sc = sc;
        questions = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public String askQuestion(){
        String selected = "";
        for(int i = 0; i< questions.size(); i++)
            System.out.println("(" + i + ") " + questions.get(i));

        int selectedNumber = getInputedInt();
        if(inputIsValid(selectedNumber)){
            selected = questions.get(selectedNumber);
            if(!actions.isEmpty())
                actions.get(selectedNumber).run();
        }else{
            return askQuestion();
        }

        return selected;
    }

    private int getInputedInt(){
        int selectedNumber = -1;

        try { selectedNumber = Integer.parseInt(sc.next());
        }catch (NumberFormatException e){
            askQuestion();
        }

        return selectedNumber;
    }

    public void add(String question, Runnable action){
        questions.add(question);
        actions.add(action);
    }

    public void add(String question){
        questions.add(question);
    }

    public void setQuestions(ArrayList<String> questions){
        this.questions = questions;
    }

    public void clear(){
        questions.clear();
        actions.clear();
    }

    private boolean inputIsValid(int selectedNumber){
        return selectedNumber >= 0 && selectedNumber < questions.size();
    }

}
