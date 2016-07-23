package app.objects;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-22.
 */
public class CalendarEntryRange {

    private Date startDate, endDate;
    private BigDecimal price;
    private boolean isAvailable;

    public CalendarEntryRange(Date startDate, Date endDate, double price, boolean isAvailable) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = BigDecimal.valueOf(price);
        this.isAvailable = isAvailable;
    }

    public CalendarEntryRange(Map entryMap) {
        if (entryMap.containsKey("startDate")) this.startDate = (Date) entryMap.get("startDate");
        if (entryMap.containsKey("endDate")) this.endDate = (Date) entryMap.get("endDate");
        if (entryMap.containsKey("price")) this.price = BigDecimal.valueOf((double) entryMap.get("price"));
        if (entryMap.containsKey("isAvailable")) this.isAvailable = (boolean) entryMap.get("isAvailable");
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return isAvailable;
    }
}
