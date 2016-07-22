package app;

import app.objects.Listing;
import app.objects.ListingFilter;
import app.objects.Review;
import app.objects.User;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Daniel on 2016-07-19.
 */
public class DBTalker {

    private static String[] characteristics = { "Kitchen", "Internet", "TV", "Essentials", "Shampoo",
            "Heating", "Air Conditioning", "Washer", "Dryer", "Free Parking on Premises",
            "Wireless Internet", "Cable TV", "Breakfast", "Pets Allowed", "Family/Kid Friendly",
            "Suitable for Events", "Smoking Allowed", "Wheelchair Accessible", "Elevator in Building",
            "Indoor Fireplace", "Buzzer/Wireless Intercom", "Doorman", "Pool", "Hot Tub", "Gym",
            "24-Hour Check-in", "Hangers", "Iron", "Hair Dryer", "Laptop Friendly Workspace" };

    public static ArrayList<String> getListings(ListingFilter filter) {
        return new ArrayList<>();
    }

    public static String createListing(String userID, Listing listing){
        return "";
    }

    public static String deleteListing(String listingID){
        return "";
    }

    public static ArrayList<String> getListingCharacteristics(){
        return new ArrayList<>(Arrays.asList(characteristics));
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

    public static ArrayList<String> getReviewableHosts(String userID){
        return new ArrayList<>();
    }

    public static ArrayList<String> getReviewableRenters(String userID){
        return new ArrayList<>();
    }

    public static ArrayList<String> getReviewableListings(String userID){
        return new ArrayList<>();
    }

    public static String createReview(String userID, Review review){
        try { return null; }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    private static void errorOccurred(Exception e){
        System.out.println("An error has occurred");
        e.printStackTrace();
    }

}
