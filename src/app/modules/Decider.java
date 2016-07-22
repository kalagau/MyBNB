package app.modules;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-16.
 */
public class Decider {

    private ArrayList<String> options;
    private ArrayList<Runnable> actions;
    private Scanner sc;

    public Decider(Scanner sc) {
        this.sc = sc;
        options = new ArrayList<>();
        actions = new ArrayList<>();
    }

    public String displayOptions(){
        if(options.isEmpty())
            return "";
        for(int i = 0; i< options.size(); i++)
            System.out.println("(" + (i+1) + ") " + options.get(i));
        System.out.println();

        int selectedNumber = getInputedInt(sc.nextLine());
        if(inputIsValid(selectedNumber)){
            String selected = options.get(selectedNumber);
            Runnable action = actions.isEmpty() ? null : actions.get(selectedNumber);
            this.clear();
            if(action != null)
                action.run();
            return selected;
        }
        return displayOptions();
    }

    public ArrayList<String> displayOptionsWithMultipleInput(){
        ArrayList<String> selections = new ArrayList<>();
        for(int i = 0; i< options.size(); i++)
            System.out.println("(" + (i+1) + ") " + options.get(i));

        String[] inputs = sc.nextLine().split(" *, *");
        for(String input : inputs){
            int selected = getInputedInt(input);
            if(inputIsValid(selected))
                selections.add(options.get(selected));
            else
                return displayOptionsWithMultipleInput();
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
        options.add(question);
        actions.add(action);
    }

    public void add(String question){
        options.add(question);
    }

    public void setOptions(ArrayList<String> options){
        this.options = options;
    }

    public void clear(){
        options.clear();
        actions.clear();
    }

    private boolean inputIsValid(int selectedNumber){
        return selectedNumber >= 0 && selectedNumber < options.size();
    }

}
