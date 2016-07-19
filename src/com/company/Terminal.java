package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.company.Validators.ValidatorKeys;
import com.company.Info.DataType;

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
        asker.add(new Info("name", "Name:", DataType.STRING));
        asker.add(new Info("occupation", "Occupation:", DataType.STRING));
        asker.add(new Info("DOB", "Date of Birth (yyyy-mm-dd):", DataType.DATE, ValidatorKeys.IS_ADULT));
        asker.add(new Info("SIN", "Social Insurance Number:", DataType.STRING, ValidatorKeys.ALL_NUMBERS));
        asker.add(new Info("isRenter", "Will you be a renter? (y/n):", DataType.BOOLEAN));
        User user = new User(asker.askQuestions());

        if (user.isRenter()) {
            asker.add(new Info("number", "Credit Card Number:", DataType.STRING, ValidatorKeys.ALL_NUMBERS));
            asker.add(new Info("expiryDate", "Credit Card Expiry Date:", DataType.STRING));
            asker.add(new Info("CCV", "Credit Card CCV:", DataType.STRING, ValidatorKeys.ALL_NUMBERS));
            asker.add(new Info("holderName", "Credit Card Holder Name:", DataType.STRING));
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
        asker.add(new Info("country", "Country:", DataType.STRING));
        asker.add(new Info("city", "City:", DataType.STRING));
        asker.add(new Info("postalCode", "Postal Code:", DataType.STRING, ValidatorKeys.POSTAL_CODE));
        asker.add(new Info("longitude", "Longitude:", DataType.DOUBLE));
        asker.add(new Info("latitude", "Latitude:", DataType.DOUBLE));
        Listing listing = new Listing(asker.askQuestions());

        helper.add("windows");
        helper.add("kitchen");
        helper.add("air conditioning");
        helper.add("free food");
        helper.add("pizza");
        listing.setCharacteristics(helper.askQuestionsWithMultipleInput());
    }

    private void showListingInfo(){

    }

    private void deleteListing(){

    }
}
