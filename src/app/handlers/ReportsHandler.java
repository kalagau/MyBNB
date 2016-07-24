package app.handlers;

import app.DBReportsTalker;
import app.DBTalker;
import app.Terminal;
import app.Validators;
import app.modules.Asker;
import app.modules.Decider;
import app.objects.Info;
import app.objects.Info.DataType;
import app.Validators.ValidatorKeys;
import javafx.util.Pair;

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
        decider.add("Number of bookings per city by...", this::numberOfBookingsPerCity);
        decider.add("Number of listings per...", this::numberOfListings);
        decider.add("Hosts by number of listing per...", this::hostsByNumberOfListings);
        decider.add("Hosts with more than 10% of listings in...", this::hostsWithMoreThanTenth);
        decider.add("Renters by number of bookings within...", this::rentersByNumberOfBookings);
        decider.add("Hosts and renters with most cancellations since last year", this::hostsAndRentersWithMostCancelations);
        decider.add("Most popular noun phrase per listing", this::popularNounPhrasePerListing);

        decider.add("Back", tm::startSession);
    }

    public void numberOfBookingsPerCity() {
        decider.add("Date Range");
        decider.add("Postal Code");
        if (decider.displayOptions().equals("Date Range")) {
            asker.add(new Info("startDate", "Start Date:", DataType.DATE, ValidatorKeys.START_DATE));
            asker.add(new Info("endDate", "End Date:", DataType.DATE, ValidatorKeys.END_DATE));
            Map map = asker.askQuestions();
            DBReportsTalker.numberOfBookingsPerCityByDate((Date) map.get("startDate"), (Date) map.get("endDate"));
        } else {
            asker.add(new Info("postalCode", "Postal Code:", DataType.STRING, ValidatorKeys.POSTAL_CODE));
            Map map = asker.askQuestions();
            DBReportsTalker.numberOfBookingsPerCityByZip((String) map.get("postalCode"));
        }
    }

    public void numberOfListings() {
        decider.add("Country");
        decider.add("Country and City");
        decider.add("Country, City, and Postal Code");
        String type = decider.displayOptions();

        asker.add(new Info("country", "Country:", DataType.STRING));
        if (type.contains("City"))
            asker.add(new Info("city", "City:", DataType.STRING));
        if (type.contains("Postal"))
            asker.add(new Info("postal", "Postal Code:", DataType.STRING, ValidatorKeys.POSTAL_CODE));

        Map map = asker.askQuestions();
        String country = (String) map.get("country");
        String city = (String) map.getOrDefault("city", "");
        String postal = (String) map.getOrDefault("postal", "");
        DBReportsTalker.numberOfListings(country, city, postal);
    }

    public void hostsByNumberOfListings() {
        decider.add("Country");
        decider.add("City");
        Boolean isCity = decider.displayOptions().equals("City");

        DBReportsTalker.hostsByNumberOfListings(isCity);
    }

    public void hostsWithMoreThanTenth() {
        asker.add(new Info("country", "Country:", DataType.STRING));
        asker.add(new Info("city", "City:", DataType.STRING));

        Map map = asker.askQuestions();
        String country = (String) map.get("country");
        String city = (String) map.get("city");
        DBReportsTalker.hostsWithMoreThanTenth(country, city);
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

    public void hostsAndRentersWithMostCancelations() {

        decider.add("Hosts");
        decider.add("Renters");
        Boolean isHost = decider.displayOptions().equals("Hosts");
        DBReportsTalker.hostsAndRentersWithMostCancelations(isHost);
    }

    public void popularNounPhrasePerListing() {
        DBReportsTalker.popularNounPhrasePerListing();
    }

}
