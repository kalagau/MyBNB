package com.company;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-16.
 */
public class Terminal {

    Scanner sc;
    IOHelper helper;


    private String userID = "";
    private String userName = "";
    private boolean isRenter;

    public Terminal(){
        sc = new Scanner(System.in);
        helper = new IOHelper(sc);
        startSession();
    }

    private void startSession(){
        if(userID.isEmpty())
            tryToLogin();

        String input = "";
        do {

        } while(!input.equals("exit"));
    }

    private void tryToLogin(){
        helper.add("Register", this::register);
        helper.add("Login", this::login);
        helper.askQuestion();
    }

    private void register() {
        System.out.println("Name:");
        String name = sc.next();

        System.out.println("Occupation:");
        String occupation = sc.next();

        System.out.println("Date of Birth:");
        String DOB = sc.next();

        System.out.println("SIN:");
        String SIN = sc.next();

        System.out.println("Will you be a host? (y/n):");
        boolean isRenter = !sc.next().equals("y");

        User user = new User(name, occupation, DOB, SIN, isRenter);

        if (isRenter) {
            System.out.println("Credit Card Number:");
            String number = sc.next();

            System.out.println("Credit Card Expiry Date:");
            String expiryDate = sc.next();

            System.out.println("Credit Card CCV:");
            String CCV = sc.next();

            System.out.println("Credit Card Holder Name:");
            String holderName = sc.next();

            user.setCreditCard(new CreditCard(number, expiryDate, CCV, holderName));
        }

        String response = DBHelper.createNewUser(user);
        if (!response.equals("error")){
            userID = response;
            userName = name;
            System.out.println("Welcome, " + userName);
        }
    }

    private void login(){
        helper.clear();
        helper.add("login as host");
        helper.add("login as renter");
        String type = helper.askQuestion();
        displayLoginDataOfType(type);
    }

    private void displayLoginDataOfType(String type){
        helper.clear();
        if(type.equals("login as host"))
            helper.setQuestions(DBHelper.getAllHostsNamesAndIDs());
        else
            helper.setQuestions(DBHelper.getAllRentersNamesAndIDs());

        String selected = helper.askQuestion();
        String[] parts = selected.split(":");

        userName = parts[0];
        userID = parts[1];
        System.out.println("Welcome back, " + userName);
    }

}
