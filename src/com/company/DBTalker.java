package com.company;

import java.util.ArrayList;

/**
 * Created by Daniel on 2016-07-19.
 */
public class DBTalker {

    public static ArrayList<String> getListings(ListingFilter filter) {
        return new ArrayList<>();
    }

    public static String createListing(Listing listing){
        return "";
    }

    public static String deleteListing(String listingID){
        return "";
    }

    public static ArrayList<String> getListingCharacteristics(){
        ArrayList<String> temp = new ArrayList<>();
        temp.add("windows");
        temp.add("kitchen");
        temp.add("air conditioning");
        temp.add("free food");
        temp.add("pizza");
        return temp;
    }

    public static ArrayList<String> getHostListings(String userID){
        return new ArrayList<>();
    }

    public static String createNewUser(User user) {
        try { return DBHelper.createNewUser(user); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getAllHostsNamesAndIDs() {
        try { return DBHelper.getAllHostsNamesAndIDs(); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getAllRentersNamesAndIDs() {
        try { return DBHelper.getAllRentersNamesAndIDs(); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static String deleteUser(String userID) {
        try { return DBHelper.deleteUser(userID); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static String deleteBooking(String bookingID) {
        try { return null; }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getMyBookings(String userID) {
        try { return new ArrayList<>(); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static Listing getListingInfo(String listingID) {
        try { return null; }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    private static ArrayList<String> getReviewableHosts(String userID){
        return new ArrayList<>();
    }

    private static ArrayList<String> getReviewableRenters(String userID){
        return new ArrayList<>();
    }

    private static void errorOccurred(Exception e){
        System.out.println("An error has occurred");
        e.printStackTrace();
    }

}
