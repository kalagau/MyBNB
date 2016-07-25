package app.handlers;

import app.DBReportsTalker;
import app.Terminal;
import app.modules.Asker;
import app.modules.Decider;
import app.objects.Info;
import app.objects.Info.DataType;
import app.Validators.ValidatorKeys;

import java.sql.Date;
import java.util.*;

/**
 * Created by Daniel on 2016-07-23.
 */
public class ReportsHandler extends BaseHandler {

    public ReportsHandler(Decider decider, Asker asker, String userID, Scanner sc, Terminal tm) {
        super(decider, asker, userID, sc, tm);
    }

    public void startReports() {
        while(true) {
            decider.add("Number of bookings per city", this::numberOfBookingsPerCity);
            decider.add("Number of listings per by...", this::numberOfListings);
            decider.add("Hosts by number of listing per...", this::hostsByNumberOfListings);
            decider.add("Hosts with more than 10% of listings in country or city", this::hostsWithMoreThanTenth);
            decider.add("Renters by number of bookings within...", this::rentersByNumberOfBookings);
            decider.add("Most cancellations since last year for...", this::hostsAndRentersWithMostCancellations);
            decider.add("Most popular noun phrase per listing", this::popularNounPhrasePerListing);

            decider.add("Back", tm::startSession);
            decider.displayOptions();
        }
    }

    public void numberOfBookingsPerCity() {
        asker.add(new Info("postal", "by postal code? (y/n)", DataType.BOOLEAN));
        asker.add(new Info("startDate", "Start Date:", DataType.DATE, ValidatorKeys.START_DATE));
        asker.add(new Info("endDate", "End Date:", DataType.DATE, ValidatorKeys.END_DATE));
        Map map = asker.askQuestions();
        Date start = (Date) map.get("startDate");
        Date end = (Date) map.get("endDate");
        Boolean postal = (Boolean) map.get("postal");
        DBReportsTalker.numberOfBookingsPerCityByDate(start, end, postal);
    }

    public void numberOfListings() {
        ArrayList<String> options = new ArrayList<>();
        options.add("Country");
        options.add("Country and City");
        options.add("Country, City, and Postal Code");
        options.forEach(s->decider.add(s));
        int type = options.indexOf(decider.displayOptions()) + 1;

        DBReportsTalker.numberOfListings(type);
    }

    public void hostsByNumberOfListings() {
        decider.add("Country");
        decider.add("City");
        Boolean isCity = decider.displayOptions().equals("City");
        DBReportsTalker.hostsByNumberOfListings(isCity);
    }

    public void hostsWithMoreThanTenth() {
        DBReportsTalker.hostsWithMoreThanTenth();
    }

    public void rentersByNumberOfBookings() {
        asker.add(new Info("rankedByCity", "Rank by city? (y/n)", DataType.BOOLEAN));
        asker.add(new Info("startDate", "Start Date:", DataType.DATE, ValidatorKeys.START_DATE));
        asker.add(new Info("endDate", "End Date:", DataType.DATE, ValidatorKeys.END_DATE));
        Map map = asker.askQuestions();
        Boolean isCity = (Boolean) map.get("rankedByCity");
        Date start = (Date) map.get("startDate");
        Date end = (Date) map.get("endDate");
        DBReportsTalker.rentersByNumberOfBookings(start, end, isCity);
    }

    public void hostsAndRentersWithMostCancellations() {

        decider.add("Hosts");
        decider.add("Renters");
        Boolean isHost = decider.displayOptions().equals("Hosts");
        DBReportsTalker.hostsAndRentersWithMostCancelations(isHost);
    }

    public void popularNounPhrasePerListing() {
        DBReportsTalker.popularNounPhrasePerListing();
    }

}
