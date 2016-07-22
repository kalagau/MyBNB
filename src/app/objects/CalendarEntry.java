package app.objects;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by Daniel on 2016-07-22.
 */
public class CalendarEntry {

    private Date date;
    private BigDecimal price;
    private boolean isAvailable;

    public CalendarEntry(Date date, double price, boolean isAvailable) {
        this.date = date;
        this.price = BigDecimal.valueOf(price);
        this.isAvailable = isAvailable;
    }

    public Date getDate() {
        return date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
