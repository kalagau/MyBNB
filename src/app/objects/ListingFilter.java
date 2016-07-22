package app.objects;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-19.
 */
public class ListingFilter {


    private String address;
    private String postalCode;
    private String country;

    private String type;

    private BigDecimal longitude, latitude, lowestPrice, highestPrice, maxDistance;
    private Date firstDate, lastDate;
    private int minNumberOfBedrooms = -1;
    public ListingFilter(Map listing) {
        if (listing.containsKey("address")) this.address = (String) listing.get("address");
        if (listing.containsKey("postalCode")) this.postalCode = (String) listing.get("postalCode");
        if (listing.containsKey("country")) this.country = (String) listing.get("country");
        if (listing.containsKey("type")) this.type = (String) listing.get("type");
        if (listing.containsKey("longitude")) this.longitude = (BigDecimal) listing.get("longitude");
        if (listing.containsKey("latitude")) this.latitude = (BigDecimal) listing.get("latitude");
        if (listing.containsKey("lowestPrice")) this.lowestPrice = (BigDecimal) listing.get("lowestPrice");
        if (listing.containsKey("highestPrice")) this.highestPrice = (BigDecimal) listing.get("highestPrice");
        if (listing.containsKey("maxDistance")) this.maxDistance = (BigDecimal) listing.get("maxDistance");
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

    public void setType(String type) {
        this.type = type;
    }
}
