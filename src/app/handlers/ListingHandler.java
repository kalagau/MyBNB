package app.handlers;

import app.DBTalker;
import app.Validators;
import app.modules.Asker;
import app.modules.Decider;
import app.objects.*;
import app.Terminal;
import app.Validators.ValidatorKeys;


import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public class ListingHandler extends BaseHandler {

    public ListingHandler(Decider decider, Asker asker, String userID, Scanner sc, Terminal tm) {
        super(decider, asker, userID, sc, tm);
    }

    public void createListing(){
        asker.add(new Info("country", "Country:", Info.DataType.STRING));
        asker.add(new Info("city", "City:", Info.DataType.STRING));
        asker.add(new Info("postalCode", "Postal Code:", Info.DataType.STRING, ValidatorKeys.POSTAL_CODE));
        asker.add(new Info("longitude", "Longitude:", Info.DataType.DOUBLE, ValidatorKeys.LONGITUDE));
        asker.add(new Info("latitude", "Latitude:", Info.DataType.DOUBLE, ValidatorKeys.LATITUDE));
        asker.add(new Info("numberOfBedrooms", "Number of Bedrooms:", Info.DataType.INTEGER, ValidatorKeys.NUM_BEDROOMS));
        asker.add(new Info("mainPrice", "Price:", Info.DataType.DOUBLE, ValidatorKeys.NOT_NEGATIVE));
        Listing listing = new Listing(asker.askQuestions());

        decider.add("full house");
        decider.add("room");
        decider.add("apartment");
        listing.setType(decider.displayOptions());

        System.out.println("Enter applicable amenities below, separated by commas");
        decider.setOptions(DBTalker.getListingCharacteristics());
        listing.setCharacteristics(decider.displayOptionsWithMultipleInput());

        asker.add(new Info("useToolkit", "Would you like to use Host Toolkit to recomend a price for this listing? (y/n)", Info.DataType.BOOLEAN));
        if((Boolean) asker.askQuestions().get("useToolkit")){
            double suggestedPrice = DBTalker.getRecomendedPrice(listing.getType(), listing.getCharacteristics().size());
            if(suggestedPrice > 0){
                System.out.println("Suggest price was " + String.valueOf(suggestedPrice));
                asker.add(new Info("useSuggested", "Would you like to use this price for this listing? (y/n)", Info.DataType.BOOLEAN));
                if((Boolean) asker.askQuestions().get("useSuggested"))
                    listing.setMainPrice(BigDecimal.valueOf(suggestedPrice));
            } else
                System.out.println("Sorry, not enough information");

        }

        DBTalker.createListing(userID, listing);
    }

    public void setListingPrice(){
        String listingID =getIDFromMySelectedListing();
        if(!listingID.isEmpty()) {
            askStartAndEndDate();
            asker.add(new Info("price", "Price over this time:", Info.DataType.DOUBLE));
            CalendarEntryRange calendarEntryRange = new CalendarEntryRange(asker.askQuestions());
            DBTalker.setListingPrice(listingID, calendarEntryRange);
        } else
            Terminal.printNotFound("listings");
    }

    public void setListingAvailability(){
        String listingID =getIDFromMySelectedListing();
        if(!listingID.isEmpty()) {
            askStartAndEndDate();
            asker.add(new Info("isAvailable", "Is this period of time available for rent? (y/n):", Info.DataType.BOOLEAN));
            CalendarEntryRange calendarEntryRange = new CalendarEntryRange(asker.askQuestions());
            DBTalker.setListingAvailability(listingID, calendarEntryRange);
        } else
            Terminal.printNotFound("listings");
    }

    private void askStartAndEndDate(){
        asker.add(new Info("startDate", "Starting Date (yyyy-mm-dd):", Info.DataType.DATE, ValidatorKeys.FUTURE_START_DATE));
        asker.add(new Info("endDate", "Ending Date (yyyy-mm-dd):", Info.DataType.DATE, ValidatorKeys.FUTURE_END_DATE));
    }

    public void showMyListingInfo(){
        String listingID = getIDFromMySelectedListing();
        if(!listingID.isEmpty()){
            printListingInfo(listingID);
        }else
            Terminal.printNotFound("listings");

    }

    public void deleteListing(){
        String listingID = getIDFromMySelectedListing();
        if(!listingID.isEmpty()){
            if(DBTalker.deleteListing(listingID) != null)
                System.out.println("Successfully deleted!");
        }else
            Terminal.printNotFound("Listings");
    }

    public String getIDFromMySelectedListing(){
        System.out.println("Select a listing:");
        ArrayList<String> listings = DBTalker.getHostListings(userID);
        listings.forEach(l-> decider.add(l));
        String listingText = decider.displayOptions();
        return getListingID(listingText);
    }

    public void printListingInfo(String listingID){
        Listing listing = DBTalker.getListingInfo(listingID);
        System.out.println(listing.toString());
    }

    public ArrayList<String> getFilteredListings(){
        ArrayList<String> filters = getFilters();
        ListingFilter listingsFilter = null;
        if(filters.contains("Address")){
            asker.add(new Info("country", "Country:", Info.DataType.STRING));
            asker.add(new Info("city", "City:", Info.DataType.STRING));
            asker.add(new Info("postalCode", "Postal Code:", Info.DataType.STRING, ValidatorKeys.POSTAL_CODE));
            listingsFilter = new ListingFilter(asker.askQuestions());
        } else if(!filters.contains("None")) {
            if (filters.contains("Distance from Location")) {
                asker.add(new Info("longitude", "Longitude:", Info.DataType.DOUBLE, ValidatorKeys.LONGITUDE));
                asker.add(new Info("latitude", "Latitude:", Info.DataType.DOUBLE, ValidatorKeys.LATITUDE));
                asker.add(new Info("maxDistance", "Max Distance (km):", Info.DataType.DOUBLE, ValidatorKeys.NOT_NEGATIVE));
            }
            if (filters.contains("Postal Code")) {
                asker.add(new Info("postalCode", "Postal Code:", Info.DataType.STRING, ValidatorKeys.POSTAL_CODE));
            }
            if (filters.contains("Price Range")) {
                asker.add(new Info("lowestPrice", "Min Price:", Info.DataType.DOUBLE, ValidatorKeys.NOT_NEGATIVE));
                asker.add(new Info("highestPrice", "Max Price:", Info.DataType.DOUBLE, ValidatorKeys.NOT_NEGATIVE));
            }

            if (filters.contains("Available Date Range")) {
                asker.add(new Info("firstDate", "First Date (yyyy-mm-dd):", Info.DataType.DATE, ValidatorKeys.FUTURE_START_DATE));
                asker.add(new Info("lastDate", "Last Date (yyyy-mm-dd):", Info.DataType.DATE, ValidatorKeys.FUTURE_END_DATE));
            }
            if (filters.contains("Minimum Number of Bedrooms")) {
                asker.add(new Info("minNumberOfBedrooms", "Minimum number of bedrooms:", Info.DataType.INTEGER, ValidatorKeys.NUM_BEDROOMS));
            }

            listingsFilter = new ListingFilter(asker.askQuestions());

            if (filters.contains("Amenities")) {
                System.out.println("Enter applicable amenities below, separated by commas");
                decider.setOptions(DBTalker.getListingCharacteristics());
                listingsFilter.setCharacteristics(decider.displayOptionsWithMultipleInput());
            }

            if (filters.contains("Type of Place")) {
                decider.add("full house");
                decider.add("room");
                decider.add("apartment");
                listingsFilter.setType(decider.displayOptions());
            }

            if (filters.contains("Distance from Location")) {
                System.out.println("Sort by:");
                decider.add("Price");
                decider.add("Distance");
                listingsFilter.setSortByPrice(decider.displayOptions().equals("Price"));
                decider.add("Ascending");
                decider.add("Descending");
                listingsFilter.setSortAscending(decider.displayOptions().equals("Ascending"));
            } else {
                System.out.println("Order by price:");
                decider.add("Ascending");
                decider.add("Descending");
                listingsFilter.setSortAscending(decider.displayOptions().equals("Ascending"));
            }
        }

        if(listingsFilter == null)
            listingsFilter = new ListingFilter(new HashMap());

        return DBTalker.getListings(listingsFilter);
    }

    public ArrayList<String> getFilters(){
        System.out.println("Select filters: (you can select multiple separated by commas)");
        decider.add("Distance from Location");
        decider.add("Postal Code");
        decider.add("Price Range");
        decider.add("Address");
        decider.add("Available Date Range");
        decider.add("Minimum Number of Bedrooms");
        decider.add("Type of Place");
        decider.add("Amenities");
        decider.add("None");
        return decider.displayOptionsWithMultipleInput();
    }

    public static String getListingID(String listingText){
        return listingText.split(":")[0];
    }

}
