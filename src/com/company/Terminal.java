package com.company;

import java.util.*;

import com.company.Validators.ValidatorKeys;
import com.company.Info.DataType;
import com.company.Review.RevieweeType;

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
                helper.add("Review a renter", this::reviewRenter);
            }else{
                helper.add("Book a listing", this::selectListingToBook);
                helper.add("Show my bookings", this::showBooking);
                helper.add("Delete a booking", this::deleteBooking);
                helper.add("Review a listing", this::reviewListing);
                helper.add("Review a host", this::reviewHost);
            }
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
        if (response != null){
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
        if(!listingID.isEmpty()){

        }else
            printNotFound("listings");

    }

    private void deleteListing(){
        String listingID = getIDFromMySelectedListing();
        if(!listingID.isEmpty()){
            if(DBTalker.deleteListing(listingID) != null)
                System.out.println("Successfully deleted!");
        }else
            printNotFound("Listings");
    }

    private String getIDFromMySelectedListing(){
        System.out.println("Select a listing:");
        ArrayList<String> listings = DBTalker.getHostListings(userID);
        listings.forEach(l->helper.add(l));
        String listingText = helper.askQuestions();
        return getListingID(listingText);
    }

    private void selectListingToBook(){
        System.out.println("Select a listing to book:");
        ArrayList<String> filteredListings = getFilteredListings();
        if(!filteredListings.isEmpty()) {
            helper.setQuestions(filteredListings);
            String listingID = getListingID(helper.askQuestions());
            printListingInfo(listingID);
            helper.add("Book this listing", () -> bookListing(listingID));
            helper.add("Go back to results", this::selectListingToBook);
            helper.add("Go to main menu");
        } else
            printNotFound("listings");
    }

    private void showListingInfo(String listingID){

    }

    private void bookListing(String listingID){
    }

    private void showBooking(){
        String bookingID = selectFromMyBookings();
        if(!bookingID.isEmpty())
            showListingInfo(bookingID);
        else
            printNotFound("bookings");
    }

    private void deleteBooking(){
        String bookingID = selectFromMyBookings();
        if(!bookingID.isEmpty())
            DBTalker.deleteBooking(bookingID);
        else
            printNotFound("bookings");
    }

    private String selectFromMyBookings(){
        System.out.println("Select a booking:");
        helper.setQuestions(DBTalker.getMyBookings(userID));
        return getBookingID(helper.askQuestions());
    }

    private String getListingID(String listingText){
        return "";
//        return listingText.split(":")[0];
    }

    private String getUserID(String userText){
        return userText.split(":")[1];
    }

    private String getBookingID(String bookingText){
        return "";
//        return bookingText.split(":")[0];
    }

    private void printListingInfo(String listingID){
        Listing listing = DBTalker.getListingInfo(listingID);
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
        ListingFilter listingsFilter = new ListingFilter(asker.askQuestions());
        return DBTalker.getListings(listingsFilter);
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

    private void reviewHost(){
        System.out.println("Select a Host:");
        ArrayList<String> reviewees = DBTalker.getReviewableHosts(userID);
        if(!reviewees.isEmpty()){
            helper.setQuestions(reviewees);
            String selectedRevieweeID = getUserID(helper.askQuestions());
            createReview(selectedRevieweeID, RevieweeType.HOST);
        }else
            printNotFound("reviewable hosts");
    }

    private void reviewRenter(){
        System.out.println("Select a Renter:");
        ArrayList<String> reviewees = DBTalker.getReviewableRenters(userID);
        if(!reviewees.isEmpty()){
            helper.setQuestions(reviewees);
            String selectedRevieweeID = getUserID(helper.askQuestions());
            createReview(selectedRevieweeID, RevieweeType.RENTER);
        }else
            printNotFound("reviewable renters");
    }

    private void reviewListing(){
        System.out.println("Select a Listing:");
        ArrayList<String> reviewees = DBTalker.getReviewableListings(userID);
        if(!reviewees.isEmpty()){
            helper.setQuestions(reviewees);
            String selectedRevieweeID = getUserID(helper.askQuestions());
            createReview(selectedRevieweeID, RevieweeType.LISTING);
        }else
            printNotFound("reviewable listings");
    }

    private void createReview(String revieweeID, RevieweeType type){
        System.out.println("Description:");
        String description = sc.nextLine();

        System.out.println("Rating:");
        helper.add("1 star");
        helper.add("2 stars");
        helper.add("3 stars");
        helper.add("4 stars");
        helper.add("5 stars");
        int rating = Integer.parseInt(helper.askQuestions());

        Review review = new Review(description, revieweeID, rating, type);
        DBTalker.createReview(userID, review);
    }

    private void printNotFound(String items){
        System.out.println("...");
        System.out.println("No " + items + " could be found!");
    }
}
