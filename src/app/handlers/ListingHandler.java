package app.handlers;

import app.DBTalker;
import app.Validators;
import app.modules.Asker;
import app.modules.Decider;
import app.objects.Info;
import app.objects.Listing;
import app.objects.ListingFilter;
import app.Terminal;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public class ListingHandler extends BaseHandler {

    public ListingHandler(Decider decider, Asker asker, String userID, Scanner sc) {
        super(decider, asker, userID, sc);
    }

    public void createListing(){
        asker.add(new Info("country", "Country:", Info.DataType.STRING));
        asker.add(new Info("city", "City:", Info.DataType.STRING));
        asker.add(new Info("postalCode", "Postal Code:", Info.DataType.STRING, Validators.ValidatorKeys.POSTAL_CODE));
        asker.add(new Info("longitude", "Longitude:", Info.DataType.DOUBLE));
        asker.add(new Info("latitude", "Latitude:", Info.DataType.DOUBLE));
        asker.add(new Info("mainPrice", "Price:", Info.DataType.DOUBLE));
        asker.add(new Info("numberOfBedrooms", "Number of Bedrooms:", Info.DataType.INTEGER));
        asker.add(new Info("hasKitchen", "Does it have a kitchen? (y/n):", Info.DataType.BOOLEAN));
        Listing listing = new Listing(asker.askQuestions());

        decider.add("full house");
        decider.add("room");
        decider.add("apartment");
        listing.setType(decider.displayOptions());

        System.out.println("Enter applicable amenities below, separated by commas");
        decider.setOptions(DBTalker.getListingCharacteristics());
        listing.setCharacteristics(decider.displayOptionsWithMultipleInput());
        DBTalker.createListing(userID, listing);
    }

    public void setListingAvailabilityOrPrice(Listing listing){

    }

    public void showMyListingInfo(){
        String listingID = getIDFromMySelectedListing();
        if(!listingID.isEmpty()){

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
    }

    public ArrayList<String> getFilteredListings(){
        ArrayList<String> filters = getFilters();
        if(filters.contains("Distance from Location")){
            asker.add(new Info("longitude", "Longitude:", Info.DataType.DOUBLE));
            asker.add(new Info("latitude", "Latitude:", Info.DataType.DOUBLE));
            asker.add(new Info("maxDistance", "Max Distance (km):", Info.DataType.DOUBLE));
        }
        if(filters.contains("Postal Code")){
            asker.add(new Info("postalCode", "Postal Code:", Info.DataType.STRING, Validators.ValidatorKeys.POSTAL_CODE));
        }
        if(filters.contains("Price Range")){
            asker.add(new Info("mainPrice", "Price:", Info.DataType.DOUBLE));
        }
        if(filters.contains("Address")){
            asker.add(new Info("country", "Country:", Info.DataType.STRING));
            asker.add(new Info("city", "City:", Info.DataType.STRING));
        }
        if(filters.contains("Available Date Range")){
            asker.add(new Info("firstDate", "First Date (yyyy-mm-dd):", Info.DataType.DATE, Validators.ValidatorKeys.FUTURE_START_DATE));
            asker.add(new Info("lastDate", "Last Date (yyyy-mm-dd):", Info.DataType.DATE, Validators.ValidatorKeys.FUTURE_END_DATE));
        }
        if(filters.contains("Minimum Number of Bedrooms")){
            asker.add(new Info("minNumberOfBedrooms", "Minimum number of bedrooms:", Info.DataType.INTEGER));
        }

        ListingFilter listingsFilter = new ListingFilter(asker.askQuestions());

        if(filters.contains("Type of Place")){
            decider.add("full house");
            decider.add("room");
            decider.add("apartment");
            listingsFilter.setType(decider.displayOptions());
        }

        return DBTalker.getListings(listingsFilter);
    }

    public ArrayList<String> getFilters(){
        System.out.println("Select filters:");
        decider.add("Distance from Location");
        decider.add("Postal Code");
        decider.add("Price Range");
        decider.add("Address");
        decider.add("Available Date Range");
        decider.add("Minimum Number of Bedrooms");
        decider.add("Type of Place");
        return decider.displayOptionsWithMultipleInput();
    }


    public static void showListingInfo(String listingID){

    }

    public static String getListingID(String listingText){
        return "";
//        return listingText.split(":")[0];
    }

}