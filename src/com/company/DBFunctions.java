package com.company;

import java.util.ArrayList;

/**
 * Created by Daniel on 2016-07-19.
 */
public class DBFunctions {

    public static ArrayList<String> getListings(ListingFilter filter){
        return null;
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
        return null;
    }
}
