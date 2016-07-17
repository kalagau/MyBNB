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
    private boolean isHost;

    public Terminal(){
        sc = new Scanner(System.in);
        helper = new IOHelper(sc);
        startSession();
    }

    private void startSession(){
        if(userID.isEmpty())
            tryToLogin();

        while(true){
            helper.add("Logout", this::logout);
            helper.add("Delete current user", this::deleteConfirmation);
            if(isHost){
                helper.add("Create new listing", this::createListing);
                helper.add("Show listing information", this::showListingInfo);
                helper.add("Delete a listing", this::deleteListing);
            }else{

            }
            helper.askQuestion();
        }
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
            startSession();
        }
    }

    private void login(){
        helper.add("login as host");
        helper.add("login as renter");
        String type = helper.askQuestion();
        showUsers(type);
    }

    private void showUsers(String type){
        isHost = type.equals("login as host");
        ArrayList<String> users = isHost ? DBHelper.getAllHostsNamesAndIDs() : DBHelper.getAllRentersNamesAndIDs();
        helper.setQuestions(users);

        String selected = helper.askQuestion();
        String[] parts = selected.split(":");

        userName = parts[0];
        userID = parts[1];
        System.out.println("Welcome back, " + userName);
    }

    private void deleteConfirmation(){
        helper.add("Permanently delete " + userName + "'s account", this::delete);
        helper.add("No, I've changed my mind!", ()->{});
        helper.askQuestion();
    }

    private void delete(){
        DBHelper.deleteUser(userID);
        logout();
    }

    private void logout(){
        System.out.println("Goodbye, " + userName);
        userID = "";
        userName = "";
        startSession();
    }

    private void createListing(){

    }

    private void showListingInfo(){

    }

    private void deleteListing(){

    }
}
