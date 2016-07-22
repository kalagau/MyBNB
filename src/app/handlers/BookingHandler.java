package app.handlers;

import app.Validators;
import app.modules.Asker;
import app.DBTalker;
import app.modules.Decider;
import app.Terminal;
import app.objects.CalendarEntry;
import app.objects.Info;
import app.objects.Rental;

import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public class BookingHandler extends BaseHandler {

    public BookingHandler(Decider decider, Asker asker, String userID, Scanner sc, Terminal tm) {
        super(decider, asker, userID, sc, tm);
    }

    public void bookListing(String listingID){
        asker.add(new Info("startDate", "Starting Date (yyyy-mm-dd):", Info.DataType.DATE, Validators.ValidatorKeys.FUTURE_START_DATE));
        asker.add(new Info("endDate", "Ending Date (yyyy-mm-dd):", Info.DataType.DATE, Validators.ValidatorKeys.FUTURE_END_DATE));
        Rental rental = new Rental(asker.askQuestions());

        DBTalker.createBooking(userID, listingID, rental);
        ArrayList<CalendarEntry> entries = tm.getListingHandler().produceCalendarEntries(rental.getStartDate(), rental.getEndDate(), -1., false);
        DBTalker.addCalendarEntries(listingID, entries);
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
            //todo update cal entry
            DBTalker.deleteBooking(bookingID);
        else
            Terminal.printNotFound("bookings");
    }

    public String selectFromMyBookings(){
        System.out.println("Select a booking:");
        decider.setOptions(DBTalker.getMyBookings(userID, tm.getUserHandler().isHost()));
        return getBookingID(decider.displayOptions());
    }

    public void selectListingToBook(){
        ArrayList<String> filteredListings = tm.getListingHandler().getFilteredListings();
        if(!filteredListings.isEmpty()) {
            System.out.println("Select a listing to book:");
            decider.setOptions(filteredListings);
            String listingID = ListingHandler.getListingID(decider.displayOptions());
            tm.getListingHandler().printListingInfo(listingID);
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
