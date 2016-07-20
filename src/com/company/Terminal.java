package com.company;

import java.util.ArrayList;
import java.util.Scanner;

import com.company.Validators.ValidatorKeys;
import com.company.Info.DataType;

/**
 * Created by Daniel on 2016-07-16.
 */
public class Terminal {

    private Scanner sc;
    private IOHelper helper;
    private InfoHelper asker;

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
            if(isHost){
                helper.add("Create new listing", this::createListing);
                helper.add("Show listing information", this::showMyListingInfo);
                helper.add("Delete a listing", this::deleteListing);
            }else{
                helper.add("Book a listing", this::selectListingToBook);
            }
            helper.add("Create a Review", this::createReview);
            helper.add("Logout", this::logout);
            helper.add("Delete current user", this::deleteUserConfirmation);
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

        String response = DBTalker.createNewUser(user);
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
        ArrayList<String> users = isHost ? DBTalker.getAllHostsNamesAndIDs() : DBTalker.getAllRentersNamesAndIDs();
        helper.setQuestions(users);

        String selected = helper.askQuestions();
        String[] parts = selected.split(":");

        userName = parts[0];
        userID = parts[1];
        System.out.println("Welcome back, " + userName);
    }

    private void deleteUserConfirmation(){
        helper.add("Permanently delete " + userName + "'s account", this::deleteUser);
        helper.add("No, I've changed my mind!");
        helper.askQuestions();
    }

    private void deleteUser(){
        DBTalker.deleteUser(userID);
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
        asker.add(new Info("mainPrice", "Price:", DataType.DOUBLE));
        Listing listing = new Listing(asker.askQuestions());

        helper.setQuestions(DBTalker.getListingCharacteristics());
        listing.setCharacteristics(helper.askQuestionsWithMultipleInput());
        DBTalker.createListing(listing);
    }

    private void showMyListingInfo(){
        String listingID = getIDFromMySelectedListing();

    }

    private void deleteListing(){
        String listingID = getIDFromMySelectedListing();

        if(DBTalker.deleteListing(listingID) != "error")
            System.out.println("Successfully deleted!");
    }

    private String getIDFromMySelectedListing(){
        System.out.println("Select a listing:");
        ArrayList<String> listings = DBTalker.getHostListings(userID);
        listings.forEach(l->helper.add(l));
        String listingText = helper.askQuestions();
        return listingText.split(":")[0];
    }

    private void selectListingToBook(){
        System.out.println("Select a listing to book:");
        helper.setQuestions(getFilteredListings());
        String listingID = helper.askQuestions().split(":")[2];
        printListingInfo(listingID);
//      TODO  helper.add("Book this listing" ()->bookListing(listingID));
        helper.add("Go back to results", this::selectListingToBook);
        helper.add("Go to main menu");
    }

    private void bookListing(String listingID){

    }

    private void printListingInfo(String listingID){

    }

    private ArrayList<String> getFilteredListings(){
        ArrayList<String> filters = getFilters();
        if(filters.contains("Distance from Location")){
            asker.add(new Info("longitude", "Longitude:", DataType.DOUBLE));
            asker.add(new Info("latitude", "Latitude:", DataType.DOUBLE));
            asker.add(new Info("maxDistance", "Max Distance (km):", DataType.DOUBLE));
        }
        if(filters.contains("Postal Code")){
            asker.add(new Info("postalCode", "Postal Code:", DataType.STRING, ValidatorKeys.POSTAL_CODE));
        }
        if(filters.contains("Price Range")){
            asker.add(new Info("mainPrice", "Price:", DataType.DOUBLE));
        }
        if(filters.contains("Address")){
            asker.add(new Info("country", "Country:", DataType.STRING));
            asker.add(new Info("city", "City:", DataType.STRING));
        }
        if(filters.contains("Available Date Range")){
            asker.add(new Info("firstDate", "First Date:", DataType.STRING));
            asker.add(new Info("lastDate", "Last Date:", DataType.STRING));
        }
        ListingFilter listsingsFilter = new ListingFilter(asker.askQuestions());
        return DBTalker.getListings(listsingsFilter);
    }

    private ArrayList<String> getFilters(){
        System.out.println("Select filters:");
        helper.add("Distance from Location");
        helper.add("Postal Code");
        helper.add("Price Range");
        helper.add("Address");
        helper.add("Available Date Range");
        return helper.askQuestionsWithMultipleInput();
    }

    private void createReview(){

    }
}
