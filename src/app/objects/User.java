package app.objects;

import java.sql.Date;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-16.
 */
public class User {

    private String name, occupation, SIN, postalCode, country, city;
    private boolean isRenter;
    private CreditCard creditCard;
    public Date DOB;

    public User(Map userMap) {
        this.name = (String)userMap.get("name");
        this.occupation = (String)userMap.get("occupation");
        this.DOB = (Date)userMap.get("DOB");
        this.SIN = (String)userMap.get("SIN");
        this.isRenter = (Boolean) userMap.get("isRenter");
        this.postalCode = (String) userMap.get("postalCode");
        this.country = (String) userMap.get("country");
        this.city = (String) userMap.get("city");
    }

    public String getName() {
        return name;
    }

    public String getOccupation() {
        return occupation;
    }

    public Date getDOB() {
        return DOB;
    }

    public String getSIN() {
        return SIN;
    }

    public boolean isRenter() {
        return isRenter;
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

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
