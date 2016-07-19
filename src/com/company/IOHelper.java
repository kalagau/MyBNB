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

    public String askQuestions(){
        for(int i = 0; i< questions.size(); i++)
            System.out.println("(" + (i+1) + ") " + questions.get(i));

        int selectedNumber = getInputedInt(sc.nextLine());
        if(inputIsValid(selectedNumber)){
            String selected = questions.get(selectedNumber);
            Runnable action = actions.isEmpty() ? null : actions.get(selectedNumber);
            this.clear();
            if(action != null)
                action.run();
            return selected;
        }
        return askQuestions();
    }

    public ArrayList<String> askQuestionsWithMultipleInput(){
        ArrayList<String> selections = new ArrayList<>();
        for(int i = 0; i< questions.size(); i++)
            System.out.println("(" + (i+1) + ") " + questions.get(i));

        String[] inputs = sc.nextLine().split(" *, *");
        for(String input : inputs){
            int selected = getInputedInt(input);
            if(inputIsValid(selected))
                selections.add(questions.get(selected));
            else
                return askQuestionsWithMultipleInput();
        }
        this.clear();
        return selections;
    }

    private int getInputedInt(String input){
        try {
            return Integer.parseInt(input) - 1;
        }catch (NumberFormatException e){
            return -1;
        }
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
