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

    private Scanner sc;
    private Decider decider;
    private Asker asker;

    private UserHandler userHandler;
    private ListingHandler listingHandler;
    private ReviewHandler reviewHandler;
    private BookingHandler bookingHandler;

    public Terminal(){
        sc = new Scanner(System.in);
        decider = new Decider(sc);
        asker = new Asker(sc);
        userHandler = new UserHandler(decider, asker, "", sc, this);
        listingHandler = new ListingHandler(decider, asker, userHandler.getUserID(), sc);
        reviewHandler = new ReviewHandler(decider, asker, userHandler.getUserID(), sc);
        bookingHandler = new BookingHandler(decider, asker, userHandler.getUserID(), sc, listingHandler);
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
                decider.add("Review a renter", reviewHandler::reviewRenter);
            }else{
                decider.add("Book a listing", bookingHandler::selectListingToBook);
                decider.add("Show my bookings", bookingHandler::showBooking);
                decider.add("Cancel a booking", bookingHandler::deleteBooking);
                decider.add("Review a listing", reviewHandler::reviewListing);
                decider.add("Review a host", reviewHandler::reviewHost);
            }
            decider.add("Logout", userHandler::logout);
            decider.add("Delete current user", userHandler::deleteUserConfirmation);
            decider.displayOptions();
        }
    }

    public static void printNotFound(String items){
        System.out.println("...");
        System.out.println("No " + items + " could be found!");
    }
}
