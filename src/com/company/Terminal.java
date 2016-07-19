package com.company;

import java.util.ArrayList;
import java.util.Scanner;

import com.company.Validators.ValidatorKeys;

/**
 * Created by Daniel on 2016-07-16.
 */
public class Terminal {

    Scanner sc;
    IOHelper helper;
    InfoHelper asker;

    private String userID = "";
    private String userName = "";
    private boolean isHost;

    public Terminal(){
        sc = new Scanner(System.in);
        helper = new IOHelper(sc);
        asker = new InfoHelper(sc);
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
            helper.askQuestions();
        }
    }

    private void tryToLogin(){
        helper.add("Register", this::register);
        helper.add("Login", this::login);
        helper.askQuestions();
    }

    private void register() {
        asker.add(new Info("name", "Name:", Info.DataType.STRING));
        asker.add(new Info("occupation", "Occupation:", Info.DataType.STRING));
        asker.add(new Info("DOB", "Date of Birth (yyyy-mm-dd):", Info.DataType.DATE, ValidatorKeys.IS_ADULT));
        asker.add(new Info("SIN", "Social Insurance Number:", Info.DataType.STRING, ValidatorKeys.ALL_NUMBERS));
        asker.add(new Info("isRenter", "Will you be a renter? (y/n):", Info.DataType.BOOLEAN));
        User user = new User(asker.askQuestions());

        if (user.isRenter()) {
            asker.add(new Info("number", "Credit Card Number:", Info.DataType.STRING, ValidatorKeys.ALL_NUMBERS));
            asker.add(new Info("expiryDate", "Credit Card Expiry Date:", Info.DataType.STRING));
            asker.add(new Info("CCV", "Credit Card CCV:", Info.DataType.STRING, ValidatorKeys.ALL_NUMBERS));
            asker.add(new Info("holderName", "Credit Card Holder Name:", Info.DataType.STRING));
            user.setCreditCard(new CreditCard(asker.askQuestions()));
        }

        String response = DBHelper.createNewUser(user);
        if (!response.equals("error")){
            userID = response;
            userName = user.getName();
            System.out.println("Welcome, " + userName);
            startSession();
        }
    }

    private void login(){
        helper.add("login as host");
        helper.add("login as renter");
        String type = helper.askQuestions();
        showUsers(type);
    }

    private void showUsers(String type){
        isHost = type.equals("login as host");
        ArrayList<String> users = isHost ? DBHelper.getAllHostsNamesAndIDs() : DBHelper.getAllRentersNamesAndIDs();
        helper.setQuestions(users);

        String selected = helper.askQuestions();
        String[] parts = selected.split(":");

        userName = parts[0];
        userID = parts[1];
        System.out.println("Welcome back, " + userName);
    }

    private void deleteConfirmation(){
        helper.add("Permanently delete " + userName + "'s account", this::delete);
        helper.add("No, I've changed my mind!", ()->{});
        helper.askQuestions();
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
