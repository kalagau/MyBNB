package app.objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-19.
 */
public class Listing {

    private String address;
    private String postalCode;
    private String country;
    private String city;

    public void setType(String type) {
        this.type = type;
    }

    private String type;
    private Double mainPrice;
    private BigDecimal longitude, latitude;
    private int numberOfBedrooms;
    private ArrayList<String> characteristics;
    private boolean hasKitchen;

    public Listing(Map listing) {
        this.address = (String)listing.get("address");
        this.postalCode = (String)listing.get("postalCode");
        this.country = (String)listing.get("country");
        this.city = (String)listing.get("city");
        this.longitude = BigDecimal.valueOf((Double)listing.get("longitude"));
        this.latitude = BigDecimal.valueOf((Double)listing.get("latitude"));
        this.mainPrice = (Double)listing.get("mainPrice");
        this.numberOfBedrooms = (int)listing.get("numberOfBedrooms");
        this.hasKitchen = (boolean)listing.get("hasKitchen");
        this.type = (String)listing.get("type");
    }

    public Double getMainPrice() {
        return mainPrice;
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

    public boolean hasKitchen() {
        return hasKitchen;
    }

    public String getType() {
        return type;
    }

    public void setCharacteristics(ArrayList<String> characteristics) {
        this.characteristics = characteristics;
    }
}


