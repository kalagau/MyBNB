package app;

import app.objects.Info;

import java.sql.Date;

/**
 * Created by Daniel on 2016-07-23.
 */
public class DBReportsTalker {

    public static int numberOfBookingsPerCityByDate(Date start, Date end, Boolean postal){
         try { return DBHelper.reportRentalsByDate(start, end, postal); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int numberOfListings(int type){
         try { return DBHelper.reportListings(type); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int hostsByNumberOfListings(Boolean isCity){
         try { return DBHelper.rankHosts(isCity); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int hostsWithMoreThanTenth(){
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

    public static int hostsAndRentersWithMostCancelations(Boolean isHost){
        try { return DBHelper.reportCancelledRentals(isHost); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    public static int popularNounPhrasePerListing(){
        try { return DBHelper.reportNounPhrases(); }
        catch (Exception e) {
            errorOccurred(e);
            return -1;
        }
    }

    private static void errorOccurred(Exception e){
        System.out.println("An error has occurred");
        e.printStackTrace();
    }

}
