package com.company;

/**
 * Created by Daniel on 2016-07-16.
 */
public class User {

    private String name, occupation, DOB, SIN;
    private boolean isRenter;
    private CreditCard creditCard;

    public User(String name, String occupation, String DOB, String SIN, boolean isRenter) {
        this.name = name;
        this.occupation = occupation;
        this.DOB = DOB;
        this.SIN = SIN;
        this.isRenter = isRenter;
    }

    public String getName() {
        return name;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getDOB() {
        return DOB;
    }

    public String getSIN() {
        return SIN;
    }

    public boolean isRenter() {
        return isRenter;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
