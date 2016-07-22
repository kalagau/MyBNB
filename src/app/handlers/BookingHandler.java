package app.handlers;

import app.modules.Asker;
import app.DBTalker;
import app.modules.Decider;
import app.Terminal;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public class BookingHandler extends BaseHandler {

    private ListingHandler listingHandler;

    public BookingHandler(Decider decider, Asker asker, String userID, Scanner sc, ListingHandler listingHandler) {
        super(decider, asker, userID, sc);
        this.listingHandler = listingHandler;
    }

    public void bookListing(String listingID){
    }

    public void showBooking(){
        String bookingID = selectFromMyBookings();
        if(!bookingID.isEmpty())
            ListingHandler.showListingInfo(bookingID);
        else
            Terminal.printNotFound("bookings");
    }

    public void deleteBooking(){
        String bookingID = selectFromMyBookings();
        if(!bookingID.isEmpty())
            DBTalker.deleteBooking(bookingID);
        else
            Terminal.printNotFound("bookings");
    }

    public String selectFromMyBookings(){
        System.out.println("Select a booking:");
        decider.setOptions(DBTalker.getMyBookings(userID));
        return getBookingID(decider.displayOptions());
    }

    public void selectListingToBook(){
        ArrayList<String> filteredListings = listingHandler.getFilteredListings();
        if(!filteredListings.isEmpty()) {
            System.out.println("Select a listing to book:");
            decider.setOptions(filteredListings);
            String listingID = ListingHandler.getListingID(decider.displayOptions());
            listingHandler.printListingInfo(listingID);
            decider.add("Book this listing", () -> bookListing(listingID));
            decider.add("Go back to results", this::selectListingToBook);
            decider.add("Go to app menu");
        } else
            Terminal.printNotFound("listings");
    }

    public static String getBookingID(String bookingText){
        return "";
//        return bookingText.split(":")[0];
    }



}
