package com.company;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Daniel on 2016-07-16.
 */
public class DBHelper {
    private static final String dbClassName = "com.mysql.jdbc.Driver";
    private static final String CONNECTION = "jdbc:mysql://127.0.0.1/mydb";
    private static boolean active = false;
    private static Connection conn;
    private static String dateSQL = "select * from \n" +
            "(select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from\n" +
            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0,\n" +
            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1,\n" +
            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2,\n" +
            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3,\n" +
            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v\n" +
            "where selected_date between ? and ?";

    public static void createConnection() throws ClassNotFoundException, SQLException {
        //Register JDBC driver
        if (active)
            return;
        try {
            Class.forName(dbClassName);
        } catch (ClassNotFoundException e) {
            throw e;
        }
        //Database credentials
        final String USER = "root";
        final String PASS = "";
        try {
            //Establish connection
            conn = DriverManager.getConnection(CONNECTION, USER, PASS);
        } catch (SQLException e) {
            throw e;
        }
        active = !active;
    }

    public static void closeConnection() throws SQLException {
        if (!active)
            return;
        try {
            //Establish connection
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection error occured!");
            throw e;
        }

    }

    public static int activeUser(User user) throws Exception {
        int userId = -1;
        PreparedStatement usrStmt = null;
        PreparedStatement addrStmt =  null;
        ResultSet rs = null;
        try {
            createConnection();
            usrStmt = conn.prepareStatement("SELECT SIN,address_id FROM user where SIN=?;");

            //WARNINBGGGGGG
            usrStmt.setString(1, user.getSIN());


            rs = usrStmt.executeQuery();
            while (rs.next()) {
                int addr = rs.getInt("address_id");
                userId = rs.getInt("user_id");
                addrStmt = conn.prepareStatement("SELECT * FROM address where address_id=? and country =? ");
                addrStmt.setInt(1, addr);
                //nonexistant statement
                addrStmt.setString(2, user.getCountry());
                ResultSet addrSet = addrStmt.executeQuery();
                if (!addrSet.next())
                    userId = -1;
                addrStmt.close();
            }

            rs.close();
            addrStmt.close();

            closeConnection();
        } catch (SQLException e) {
            System.err.println("Connection error occured!");
            throw e;
        } catch(Exception e) {
            throw e;
        } finally{
            try {
                addrStmt.close();
                rs.close();
                usrStmt.close();
            }catch (Exception e){
                int a = 0;
            }
        }


        return userId;
    }

    public static String createNewUser(User user) throws Exception {
        int addrId = -1;
        int userId = -1;
        PreparedStatement addrStmt= null;
        PreparedStatement usrStmt= null;
        PreparedStatement card= null;
        PreparedStatement stmt= null;
        ResultSet rs= null;
        ResultSet rsUser= null;
        ResultSet rsIns= null;


        try {
            userId = activeUser(user);

            createConnection();
            if (userId < 0) {
                //Execute a query
                addrStmt = conn.prepareStatement("INSERT INTO  address (pCode, country, city)  VALUES (?,?,?);",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                usrStmt = conn.prepareStatement("INSERT INTO user(address_id,occupation,name,DOB,SIN)  VALUES (?,?,?,?,?);",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                addrStmt.setString(1, user.getPostalCode());
                addrStmt.setString(2, user.getCountry());
                addrStmt.setString(3, user.city());
                int success = addrStmt.executeUpdate();
                 rs = stmt.getGeneratedKeys();
                if (success > 0 && rs.next()) {
                    addrId = (rs.getInt(1));
                }
                addrStmt.close();
                rs.close();
                if (addrId > 0) {
                    usrStmt.setInt(1, addrId);
                    usrStmt.setString(2, user.getOccupation());
                    usrStmt.setString(3, user.getName());
                    //VERIFY DATE PRIOR TO HERE
                    usrStmt.setDate(4, user.getDOB());
                    //ERROR CONVERT TO INT BEOFRE HERE
                    usrStmt.setString(5, user.getSIN());
                    int secondSuccess = usrStmt.executeUpdate();
                    rsUser = stmt.getGeneratedKeys();
                    if (secondSuccess > 0 && rsUser.next()) {
                        userId = (rsUser.getInt(1));
                    }
                    usrStmt.close();
                    rsUser.close();


                }
            }


            if (userId > 0) {
                if (user.isRenter()) {
                    card = conn.prepareStatement("INSERT INTO  credit_card (card_number, name, CCV,expiry_date)  VALUES (?,?,?,?);");
                    card.setString(1, user.getCreditCard().getNumber());
                    card.setString(2, user.getName());
                    card.setString(3, user.getCreditCard().getCCV());
                    card.setDate(4, user.getCreditCard().getExpiryDate());
                    int success = card.executeUpdate();
                    card.close();
                    if (success > 0) {
                        stmt = conn.prepareStatement("INSERT INTO  renter (user_id,card_number,isActive)  VALUES (?,?,?);",
                                PreparedStatement.RETURN_GENERATED_KEYS);
                        stmt.setInt(1, userId);
                        stmt.setString(2, user.getCreditCard().getNumber());
                        stmt.setBoolean(3, true);
                        int complete = stmt.executeUpdate();
                        rsIns = stmt.getGeneratedKeys();
                        if (complete > 0 && rsIns.next()) {
                            userId = (rsIns.getInt(1));
                        } else {
                            userId = -1;
                        }
                        rsIns.close();
                        stmt.close();
                    } else {
                        userId = -1;
                    }

                } else {
                    stmt = conn.prepareStatement("INSERT INTO  host (pCode, country, city)  VALUES (?,?,?);",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, userId);
                    stmt.setBoolean(2, true);
                    int complete = stmt.executeUpdate();
                    rsIns = stmt.getGeneratedKeys();
                    if (complete > 0 && rsIns.next()) {
                        userId = (rsIns.getInt(1));
                    } else {
                        userId = -1;
                    }
                    rsIns.close();
                    stmt.close();
                }
            }

            closeConnection();
        } catch (Exception e) {
            throw e;
        } finally {
            try {

                addrStmt.close();
                usrStmt.close();
                card.close();
                stmt.close();
                rs.close();
                rsUser.close();
                rsIns.close();
            }catch (Exception e){
                int a =0;
            }

        }



        return String.valueOf(userId);
    }

    public static String deleteUser(String userID) throws Exception{
        boolean cleanDelete = true;
        PreparedStatement deleteUser = null;
        PreparedStatement updateRenter = null;
        PreparedStatement updateHost = null;
        PreparedStatement updateListings = null;
        PreparedStatement updateRental = null;
        PreparedStatement updateCalendar = null;

        try {
            createConnection();

            deleteUser = conn.prepareStatement("DELETE FROM user where user_id =? ;");
            updateRenter = conn.prepareStatement("UPDATE  renter SET  isActive=?  where user_id =? ;");
            updateHost = conn.prepareStatement("UPDATE  host SET  isActive=?  where user_id =? ;");
            updateListings = conn.prepareStatement("UPDATE listing INNER JOIN host ON listing.user_id=host.user_id SET listing.isActive=? where listing.isActive=? AND host.user_id=?;");
            updateRental = conn.prepareStatement("UPDATE rental INNER JOIN listing ON rental.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET cancelled_By =?,isAvailable = ? WHERE host.user_id=? AND listing.isActive =? AND rental.end_date >= CURDATE();");
            updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN listing ON calendar_entry.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET isAvailable = ? WHERE host.user_id=? AND listing.isActive =? AND calendar_entry.date >= CURDATE();");
            deleteUser.setInt(1, Integer.parseInt(userID));
            updateRenter.setBoolean(1,false);
            updateRenter.setInt(1, Integer.parseInt(userID));
            updateHost.setBoolean(1,false);
            updateHost.setInt(1, Integer.parseInt(userID));
            updateListings.setBoolean(1,false);
            updateListings.setBoolean(2,true);
            updateListings.setInt(3,Integer.parseInt(userID));
            if(updateListings.executeUpdate() < 0)
                cleanDelete = false;
            updateRental.setInt(1,1);
            updateRental.setBoolean(2,false);
            updateRental.setInt(3,Integer.parseInt(userID));
            updateRental.setBoolean(4,true);
            if(updateRental.executeUpdate() < 0)
                cleanDelete = false;
            updateCalendar.setBoolean(1,false);
            updateCalendar.setInt(2,Integer.parseInt(userID));
            updateCalendar.setBoolean(1,true);
            if(updateCalendar.executeUpdate() < 0)
                cleanDelete = false;

            if(cleanDelete){
                //NEED to determine account being deleted
                if(updateRenter.executeUpdate() < 0)
                    cleanDelete = false;
                if(updateHost.executeUpdate() < 0)
                    cleanDelete = false;
                if (cleanDelete) {
                    int a = deleteUser.executeUpdate();

                }
            }
            deleteUser.close();
            updateHost.close();
            updateRenter.close();
            updateListings.close();
            updateRental.close();
            updateCalendar.close();
            closeConnection();
        } catch (Exception e) {
            throw e;
        }finally{
            try{
                deleteUser.close();
                updateHost.close();
                updateRenter.close();
                updateListings.close();
                updateRental.close();
                updateCalendar.close();
            }catch (Exception e){
                int a=0;
            }
        }


        return String.valueOf(cleanDelete);
    }



    public static ArrayList<String> getAllHostsNamesAndIDs() throws Exception{
        ArrayList<String> list = new ArrayList<String>();
        PreparedStatement hostData = null;
        ResultSet rs = null;

        try {
            createConnection();
            hostData = conn.prepareStatement("SELECT host.user_id,user.name FROM host INNER JOIN user ON user.user_id=host.user_id;");
            rs = hostData.executeQuery();
            while (rs.next())
                list.add(rs.getString("user.name")+ ":" + String.valueOf(rs.getInt("host.user_id")));

            rs.close();
            hostData.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally {
            try{
                rs.close();
                hostData.close();
            }catch (Exception e){
                int a=0;
            }
        }
        return list;
    }

    public static ArrayList<String> getAllRentersNamesAndIDs() throws Exception{
        ArrayList<String> list = new ArrayList<String>();
        PreparedStatement renterData = null;
        ResultSet rs = null;
        try {
            createConnection();
            renterData = conn.prepareStatement("SELECT renter.user_id,user.name FROM renter INNER JOIN user ON user.user_id=renter.user_id;");
            rs = renterData.executeQuery();
            while (rs.next())
                list.add(rs.getString("user.name")+ ":" + String.valueOf(rs.getInt("host.user_id")));

            rs.close();
            renterData.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally {
            try{
                rs.close();
                renterData.close();
            }catch (Exception e){
                int a=0;
            }
        }
        return list;
    }

//REPORT FUNCTIONS VERY VOLATILE- NEED to be verified on how they are supposed to work????


    //SQL UPDATED
    public static int reportRentalsByDate(java.sql.Date startDate, java.sql.Date endDate,boolean postal) throws Exception{
        PreparedStatement numListings = null;
        ResultSet rs = null;
        String sql = "SELECT address.city, COUNT(*) as numRentals FROM rental INNER JOIN listing ON" +
                " rental.listing_id=listing.listing_id INNER JOIN address ON listing.address_id=address.address_id" +
                " where rental.start_date >= ? and rental.start_date <=? GROUP BY address.city;";
        if (postal)
            sql = "SELECT address.city, address.pCode, COUNT(*) as numRentals FROM rental INNER JOIN listing ON" +
                    " rental.listing_id=listing.listing_id INNER JOIN address ON" +
                    " listing.address_id=address.address_id where rental.start_date >= ? " +
                    "and rental.start_date <=? GROUP BY address.city, address.pCode;";
        int result = -1;
        try {
            createConnection();
            numListings = conn.prepareStatement(sql);
            numListings.setDate(1,startDate);
            numListings.setDate(2,endDate);
            rs = numListings.executeQuery();
            if (rs.next())
                result = rs.getInt(1);

            rs.close();
            numListings.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally{
            try{
                rs.close();
                numListings.close();
            }catch (Exception e){
                int a =0;
            }
        }
        return result;
    }


    public static int reportListings(int type) throws Exception{
        PreparedStatement numListings = null;
        ResultSet rs = null;
        String sql = "SELECT address.country,COUNT(*) as numListing FROM listing INNER JOIN address ON" +
                " listing.address_id=address.address_id " +
                "  GROUP BY address.country";
        int result = -1;
        if (type==2)
            sql = "SELECT address.country,address.city, COUNT(*) as numListing FROM listing INNER JOIN address ON" +
                    " listing.address_id=address.address_id " +
                    "  GROUP BY address.country,address.city";
        if (type==3)
            sql = "SELECT address.country,address.city,address.pCode, COUNT(*) as numListing FROM listing" +
                    " INNER JOIN address ON listing.address_id=address.address_id " +
                    "  GROUP BY address.country,address.city,address.pCode";
        try {
            //Group By Country
            createConnection();
            numListings = conn.prepareStatement(sql);
            rs = numListings.executeQuery();
            if (rs.next())
                result = rs.getInt(1);

            rs.close();
            numListings.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally{
            try{
                rs.close();
                numListings.close();
            }catch (Exception e){
                int a =0;
            }
        }
        return result;
    }


    public static int rankHosts(boolean city) throws Exception{
        PreparedStatement numListings = null;
        ResultSet rs = null;
        String sql = "select * from (select address.country,user_id, count(*) as numListing from listing" +
                " INNER JOIN address on address.address_id=listing.address_id GROUP BY user_id,address.country)t" +
                " GROUP BY t.country ASC,numListing DESC,t.user_id;";
        if (city)
            sql = "select * from (select address.country,address.city,user_id, count(*) as numListing from listing" +
                    " INNER JOIN address on address.address_id=listing.address_id GROUP BY user_id,address.country" +
                    ",address.city)t GROUP BY t.country ASC,t.city ASC,numListing DESC,t.user_id;";
        int result = -1;

        try {
            createConnection();
            numListings = conn.prepareStatement(sql);
            rs = numListings.executeQuery();
            if (rs.next())
                result = rs.getInt(1);

            rs.close();
            numListings.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally{
            try{
                rs.close();
                numListings.close();
            }catch (Exception e){
                int a =0;
            }
        }
        return result;
    }
    public static int commercialHosts() throws Exception{
        PreparedStatement numListings = null;
        ResultSet rs = null;
        String sql = "select * from " +
                "(select address.country,address.city,user_id, count(*) as numListing, s.totListing from listing " +
                "INNER JOIN address on address.address_id=listing.address_id CROSS JOIN (select address.country," +
                "address.city,count(*) as totListing from listing INNER JOIN address on " +
                "address.address_id=listing.address_id GROUP BY address.country,address.city)s ON " +
                "s.country=address.country AND s.city=address.city GROUP BY user_id,address.country,address.city)t " +
                "WHERE t.numListing >= 0.1*t.totListing GROUP BY t.country ASC,t.city ASC,numListing DESC,t.user_id;";



        int result = -1;
        try {
            createConnection();
            numListings = conn.prepareStatement(sql);
            rs = numListings.executeQuery();
            if (rs.next())
                result = rs.getInt(1);

            rs.close();
            numListings.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally{
            try{
                rs.close();
                numListings.close();
            }catch (Exception e){
                int a =0;
            }
        }
        return result;
    }


    public static int reportRentalRanksByDate(java.sql.Date startDate, java.sql.Date endDate,boolean city) throws Exception {
        PreparedStatement numListings = null;
        ResultSet rs = null;
        String sql = "select * from (select user_id, count(*) as numRental from rental WHERE rental.isActive=0 group by user_id)t group by" +
                " t.numRental DESC,t.user_id WHERE start_date >=? and start_date<=?";
        if (city)
            sql = "select * from (select address.country,rental.user_id, count(*) as numRental from rental INNER JOIN" +
                    " listing ON rental.listing_id=listing.listing_id INNER JOIN address on address.address_id =" +
                    " listing.address_id WHERE rental.isActive = 0 group by rental.user_id,address.country)t where t.numRental > 1 group by" +
                    " t.country ASC, t.numRental DESC,t.user_id;";
        int result = -1;
        try {
            createConnection();
            numListings = conn.prepareStatement(sql);
            numListings.setDate(1,startDate);
            numListings.setDate(2,endDate);
            rs = numListings.executeQuery();
            if (rs.next())
                result = rs.getInt(1);

            rs.close();
            numListings.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally{
            try{
                rs.close();
                numListings.close();
            }catch (Exception e){
                int a =0;
            }
        }
        return result;
    }
    //NOT DONE filler
    public static int reportCancelledRentals(boolean host) throws Exception {
        PreparedStatement numListings = null;
        ResultSet rs = null;
        String sql = "select * from (select user_id, count(*) as numRental from rental WHERE rental.isActive=0 group by user_id)t group by" +
                " t.numRental DESC,t.user_id WHERE start_date >=? and start_date<=?";
        if (host)
            sql = "select * from (select address.country,rental.user_id, count(*) as numRental from rental INNER JOIN" +
                    " listing ON rental.listing_id=listing.listing_id INNER JOIN address on address.address_id =" +
                    " listing.address_id WHERE rental.isActive = 0 group by rental.user_id,address.country)t where t.numRental > 1 group by" +
                    " t.country ASC, t.numRental DESC,t.user_id;";
        int result = -1;
        try {
            createConnection();
            numListings = conn.prepareStatement(sql);

            rs = numListings.executeQuery();
            if (rs.next())
                result = rs.getInt(1);

            rs.close();
            numListings.close();
            closeConnection();

        } catch (Exception e) {
            throw e;
        } finally{
            try{
                rs.close();
                numListings.close();
            }catch (Exception e){
                int a =0;
            }
        }
        return result;
    }





}
