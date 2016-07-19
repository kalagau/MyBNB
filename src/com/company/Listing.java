package com.company;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-19.
 */
public class Listing {

    private String address, postalCode, country;
    private Double longitude, latitude;
    private ArrayList<String> characteristics;

    public Listing(Map listing) {
        this.address = (String)listing.get("address");
        this.postalCode = (String)listing.get("postalCode");
        this.country = (String)listing.get("country");
        this.longitude = (Double)listing.get("longitude");
        this.latitude = (Double)listing.get("latitude");
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

    public ArrayList<String> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(ArrayList<String> characteristics) {
        this.characteristics = characteristics;
    }
}


