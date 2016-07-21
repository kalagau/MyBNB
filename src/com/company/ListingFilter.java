package com.company;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-19.
 */
public class ListingFilter {


    private String address, postalCode, country;
    private Double longitude= -1.0, latitude= -1.0, lowestPrice= -1.0, highestPrice= -1.0, maxDistance= -1.0;
    private Date firstDate, lastDate;
    private int minNumberOfBedrooms = -1;

    public ListingFilter(Map listing) {
        if (listing.containsKey("address")) this.address = (String) listing.get("address");
        if (listing.containsKey("postalCode")) this.postalCode = (String) listing.get("postalCode");
        if (listing.containsKey("country")) this.country = (String) listing.get("country");
        if (listing.containsKey("longitude")) this.longitude = (Double) listing.get("longitude");
        if (listing.containsKey("latitude")) this.latitude = (Double) listing.get("latitude");
        if (listing.containsKey("lowestPrice")) this.lowestPrice = (Double) listing.get("lowestPrice");
        if (listing.containsKey("highestPrice")) this.highestPrice = (Double) listing.get("highestPrice");
        if (listing.containsKey("maxDistance")) this.maxDistance = (Double) listing.get("maxDistance");
        if (listing.containsKey("firstDate")) this.firstDate = (Date) listing.get("firstDate");
        if (listing.containsKey("lastDate")) this.lastDate = (Date) listing.get("lastDate");
        if (listing.containsKey("minNumberOfBedrooms")) this.minNumberOfBedrooms = (int) listing.get("minNumberOfBedrooms");
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLowestPrice() {
        return lowestPrice;
    }

    public Double getHighestPrice() {
        return highestPrice;
    }

    public Double getMaxDistance() {
        return maxDistance;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

}
