package app;

import app.objects.Info;

import java.sql.Date;

/**
 * Created by Daniel on 2016-07-23.
 */
public class DBReportsTalker {

    public static int numberOfBookingsPerCityByDate(Date start, Date end){
         try { return DBHelper.reportRentalRanksByDate(null, null, true); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int numberOfBookingsPerCityByZip(String zip){
         try { return DBHelper.reportRentalRanksByDate(null, null, true); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int numberOfListings(String country, String city, String postal){
         try { return DBHelper.reportRentalRanksByDate(null, null, true); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int hostsByNumberOfListings(String country, String city){
         try { return DBHelper.rankHosts(true); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int hostsWithMoreThanTenth(String country, String city){
         try { return DBHelper.commercialHosts(); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int rentersByNumberOfBookings(Date start, Date end, Boolean isCity){
        try { return DBHelper.reportRentalRanksByDate(start, end, isCity); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static void hostsAndRentersWithMostCancelations(){
//        try { return DBHelper.getHostListings(userID); }
//        catch (Exception e) {
//            errorOccurred(e);
//            return -1;
//        }
    }

    public static void popularNounPhrasePerListing(){
//        try { return DBHelper.reportNounPhrases(); }
//        catch (Exception e) {
//            errorOccurred(e);
//            return -1;
//        }
    }

    private static void errorOccurred(Exception e){
        System.out.println("An error has occurred");
        e.printStackTrace();
    }

}
