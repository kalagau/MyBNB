package app.objects;

import java.util.Map;

/**
 * Created by Daniel on 2016-07-16.
 */
public class CreditCard {

    private String number, expiryDate, CCV, holderName;

    public CreditCard(String number, String expiryDate, String CCV, String holderName) {
        this.number = number;
        this.expiryDate = expiryDate;
        this.CCV = CCV;
        this.holderName = holderName;
    }

    public CreditCard(Map creditCardMap) {
        this.number = (String)creditCardMap.get("number");
        this.expiryDate = (String)creditCardMap.get("expiryDate");
        this.CCV = (String)creditCardMap.get("CCV");
        this.holderName = (String)creditCardMap.get("holderName");
    }

    public String getNumber() {
        return number;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getCCV() {
        return CCV;
    }

    public String getHolderName() {
        return holderName;
    }
}
