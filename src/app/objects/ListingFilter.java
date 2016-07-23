package app.objects;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-19.
 */
public class ListingFilter {

    private String city, postalCode, country, type;
    private BigDecimal longitude, latitude, lowestPrice, highestPrice, maxDistance;
    private Date firstDate, lastDate;
    private int minNumberOfBedrooms = -1;
    private boolean sortByPrice, sortAscending;

    private ArrayList<String> characteristics;

    public ListingFilter(Map listing) {
        this.city = (String) listing.getOrDefault("city", "");
        this.postalCode = (String) listing.getOrDefault("postalCode", "");
        this.country = (String) listing.getOrDefault("country", "");
        this.type = (String) listing.getOrDefault("type", "");
        this.longitude = BigDecimal.valueOf((double) listing.getOrDefault("longitude", -1));
        this.latitude = BigDecimal.valueOf((double) listing.getOrDefault("latitude", -1));
        this.lowestPrice = BigDecimal.valueOf((double) listing.getOrDefault("lowestPrice", -1));
        this.highestPrice = BigDecimal.valueOf((double) listing.getOrDefault("highestPrice", -1));
        this.maxDistance = BigDecimal.valueOf((double) listing.getOrDefault("maxDistance", -1));
        this.firstDate = (Date) listing.getOrDefault("firstDate", null);
        this.lastDate = (Date) listing.getOrDefault("lastDate", null);
        this.minNumberOfBedrooms = (int) listing.getOrDefault("minNumberOfBedrooms", -1);
        this.sortByPrice = (Boolean) listing.getOrDefault("sortByPrice", true);
        this.sortAscending = (Boolean) listing.getOrDefault("sortAscending", true);
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public BigDecimal getLowestPrice() {
        return lowestPrice;
    }

    public BigDecimal getHighestPrice() {
        return highestPrice;
    }

    public BigDecimal getMaxDistance() {
        return maxDistance;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public String getType() {
        return type;
    }

    public int getMinNumberOfBedrooms() {
        return minNumberOfBedrooms;
    }

    public boolean isSortByPrice() {
        return sortByPrice;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }


    public void setSortByPrice(boolean sortByPrice) {
        this.sortByPrice = sortByPrice;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(ArrayList<String> characteristics) {
        this.characteristics = characteristics;
    }

}
