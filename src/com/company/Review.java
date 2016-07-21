package com.company;

import java.util.Map;

/**
 * Created by Daniel on 2016-07-21.
 */
public class Review {

    public enum RevieweeType { LISTING, HOST, RENTER }
    private String description, revieweeID;
    private int rating;
    private RevieweeType type;

    public Review(String description, String revieweeID, int rating, RevieweeType type) {
        this.description = description;
        this.revieweeID = revieweeID;
        this.rating = rating;
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public String getRevieweeID() {
        return revieweeID;
    }

    public int getRating() {
        return rating;
    }

    public RevieweeType getType() {
        return type;
    }

}
