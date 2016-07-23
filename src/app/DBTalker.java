package app;

import app.objects.*;

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

    public static String createListing(String userID, Listing listing){
        try { return DBHelper.createListing(listing, userID); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getListings(ListingFilter filter) {
        try { return null; }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static Listing getListingInfo(String listingID) {
        try { return DBHelper.getListingInfo(listingID); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static String deleteListing(String listingID){
        try { return DBHelper.deleteListing(listingID); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getListingCharacteristics(){
        return new ArrayList<>(Arrays.asList(characteristics));
    }

    public static ArrayList<String> getHostListings(String userID){
        try { return DBHelper.getHostListings(userID); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
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

    public static String createBooking(String userID, String listingID, Rental rental){
        try { return null; }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getMyBookings(String userID, boolean isHost) {
        try { return DBHelper.getMyBookings(userID, isHost); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static String deleteBooking(String bookingID, boolean isHost) {
        try { return DBHelper.deleteBooking(bookingID, isHost); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static Listing setListingPrice(String listingID, CalendarEntryRange calendarEntryRange) {
        try { return null; }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static Listing setListingAvailability(String listingID, CalendarEntryRange calendarEntryRange) {
        try { return null; }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static String createReview(String userID, Review review){
        try { return DBHelper.createReview(userID, review); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getReviewableHosts(String userID){
        try { return DBHelper.getReviewableHosts(userID); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getReviewableRenters(String userID){
        try { return DBHelper.getReviewableRenters(userID); }
        catch (Exception e) {
            errorOccurred(e);
            return null;
        }
    }

    public static ArrayList<String> getReviewableListings(String userID){
        try { return DBHelper.getReviewableListings(userID); }
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
