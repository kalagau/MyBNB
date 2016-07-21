package com.company;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-19.
 */
public class Listing {

    private String address, postalCode, country, city;
    private Double mainPrice;
    private BigDecimal longitude, latitude;
    private ArrayList<String> characteristics;

    public Listing(Map listing) {
        this.address = (String)listing.get("address");
        this.postalCode = (String)listing.get("postalCode");
        this.country = (String)listing.get("country");
        this.city = (String)listing.get("city");
        this.longitude = BigDecimal.valueOf((Double)listing.get("longitude"));
        this.latitude = BigDecimal.valueOf((Double)listing.get("latitude"));
        this.mainPrice = (Double)listing.get("mainPrice");
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

    public void setCharacteristics(ArrayList<String> characteristics) {
        this.characteristics = characteristics;
    }
}


