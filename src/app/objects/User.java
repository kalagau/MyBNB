package app.objects;

import java.sql.Date;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-16.
 */
public class User {

    private String name, occupation, SIN;
    private boolean isRenter;
    private CreditCard creditCard;
    public Date DOB;

    public User(String name, String occupation, Date DOB, String SIN, boolean isRenter) {
        this.name = name;
        this.occupation = occupation;
        this.DOB = DOB;
        this.SIN = SIN;
        this.isRenter = isRenter;
    }

    public User(Map userMap) {
        this.name = (String)userMap.get("name");
        this.occupation = (String)userMap.get("occupation");
        this.DOB = (Date)userMap.get("DOB");
        this.SIN = (String)userMap.get("SIN");
        this.isRenter = (Boolean) userMap.get("isRenter");
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

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }
}
