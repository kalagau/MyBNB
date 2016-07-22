package app.objects;

import java.sql.Date;
import java.util.Map;

/**
 * Created by Daniel on 2016-07-22.
 */
public class Rental {
    private Date startDate, endDate;

    public Rental(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Rental(Map rentalMap) {
        this.startDate = (Date)rentalMap.get("startDate");
        this.endDate = (Date)rentalMap.get("endDate");
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
}
