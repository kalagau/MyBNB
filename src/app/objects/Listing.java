package app.objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-19.
 */
public class Listing {

    private String postalCode, country, city, type;
    private BigDecimal longitude, latitude, mainPrice;

    private int numberOfBedrooms;
    private ArrayList<String> characteristics;

    public Listing(Map listing) {
        this.postalCode = (String)listing.get("postalCode");
        this.country = (String)listing.get("country");
        this.city = (String)listing.get("city");
        this.numberOfBedrooms = (int)listing.get("numberOfBedrooms");
        this.type = (String)listing.get("type");

        if (listing.get("longitude") instanceof BigDecimal) this.longitude = (BigDecimal)listing.get("longitude");
        else this.longitude = BigDecimal.valueOf((double)listing.get("longitude"));
        if (listing.get("latitude") instanceof BigDecimal) this.latitude = (BigDecimal)listing.get("latitude");
        else this.latitude = BigDecimal.valueOf((double)listing.get("latitude"));
        if (listing.get("mainPrice") instanceof BigDecimal) this.mainPrice = (BigDecimal)listing.get("mainPrice");
        else this.mainPrice = BigDecimal.valueOf((double)listing.get("mainPrice"));
    }

    @Override
    public String toString() {
        return  "\nAddress: " +
                "\n   Postal Code: " + postalCode +
                "\n   Country: " + country +
                "\n   City: " + city +
                "\nLocation: " +
                "\n   Longitude: " + longitude +
                "\n   Latitude: " + latitude +
                "\nType: " + type +
                "\nPrice: " + mainPrice +
                "\nNumber of Bedrooms: " + numberOfBedrooms +
                "\nCharacteristics: " + characteristics.toString() + "\n";
    }

    public BigDecimal getMainPrice() {
        return mainPrice;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public ArrayList<String> getCharacteristics() {
        return characteristics;
    }

    public int getNumberOfBedrooms() {
        return numberOfBedrooms;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMainPrice(BigDecimal mainPrice) {
        this.mainPrice = mainPrice;
    }

    public void setCharacteristics(ArrayList<String> characteristics) {
        this.characteristics = characteristics;
    }
}


