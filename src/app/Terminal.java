package app;

import app.handlers.BookingHandler;
import app.handlers.ListingHandler;
import app.handlers.ReviewHandler;
import app.handlers.UserHandler;
import app.modules.Asker;
import app.modules.Decider;

import java.util.*;

/**
 * Created by Daniel on 2016-07-16.
 */
public class Terminal {

    private Decider decider;
    private UserHandler userHandler;
    private ListingHandler listingHandler;
    private BookingHandler bookingHandler;
    private ReviewHandler reviewHandler;

    public Terminal(){
        Scanner sc = new Scanner(System.in);
        Asker asker = new Asker(sc);
        decider = new Decider(sc);
        userHandler = new UserHandler(decider, asker, "", sc, this);
        listingHandler = new ListingHandler(decider, asker, userHandler.getUserID(), sc, this);
        reviewHandler = new ReviewHandler(decider, asker, userHandler.getUserID(), sc, this);
        bookingHandler = new BookingHandler(decider, asker, userHandler.getUserID(), sc, this);
        startSession();
    }

    public void setUserID(String userID){
        listingHandler.setUserID(userID);
        reviewHandler.setUserID(userID);
        bookingHandler.setUserID(userID);
    }

    public void startSession(){
        if(userHandler.getUserID().isEmpty())
            userHandler.tryToLogin();

        while(true){
            if(userHandler.isHost()){
                decider.add("Create new listing", listingHandler::createListing);
                decider.add("Show listing information", listingHandler::showMyListingInfo);
                decider.add("Delete a listing", listingHandler::deleteListing);
                decider.add("Edit listing price over date range", listingHandler::setListingPrice);
                decider.add("Edit listing availability", listingHandler::setListingAvailability);
                decider.add("Review a renter", reviewHandler::reviewRenter);
            }else{
                decider.add("Book a listing", bookingHandler::selectListingToBook);
                decider.add("Show my bookings", bookingHandler::showBooking);
                decider.add("Review a listing", reviewHandler::reviewListing);
                decider.add("Review a host", reviewHandler::reviewHost);
            }
            decider.add("Cancel a Booking", bookingHandler::deleteBooking);
            decider.add("Logout", userHandler::logout);
            decider.add("Delete current user", userHandler::deleteUserConfirmation);
            decider.displayOptions();
        }
    }
    public UserHandler getUserHandler() {
        return userHandler;
    }

    public ListingHandler getListingHandler() {
        return listingHandler;
    }

    public ReviewHandler getReviewHandler() {
        return reviewHandler;
    }

    public BookingHandler getBookingHandler() {
        return bookingHandler;
    }

    public static void printNotFound(String items){
        System.out.println("...");
        System.out.println("No " + items + " could be found!");
    }
}
