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

    public static void createConnection() {
        //Register JDBC driver
        if (active)
            return;
        try {
            Class.forName(dbClassName);
        } catch (ClassNotFoundException e) {
            // PAss
        }
        //Database credentials
        final String USER = "root";
        final String PASS = "";
        try {
            //Establish connection
            conn = DriverManager.getConnection(CONNECTION, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Connection error occured!");
        }
        active = !active;
    }

    public static void closeConnection() {
        if (!active)
            return;
        try {
            //Establish connection
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection error occured!");
        }

    }

    public static int activeUser(User user) {
        int userId = -1;
        createConnection();
        try {

            PreparedStatement usrStmt = conn.prepareStatement("SELECT SIN,address_id FROM user where SIN=?;");

            //WARNINBGGGGGG
            usrStmt.setInt(1, user.getSIN());


            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int addr = rs.getInt("address_id");
                userId = rs.getInt("user_id");
                PreparedStatement addrStmt = conn.prepareStatement("SELECT * FROM address where address_id=? and country =? ");
                addrStmt.setInt(1, addr);
                //nonexistant statement
                addrStmt.setString(2, user.getCountry());
                ResultSet addrSet = stmt.executeQuery();
                if (!addrSet.next())
                    userId = -1;
                addrStmt.close();
            }

            rs.close();
            stmt.close();


        } catch (SQLException e) {
            System.err.println("Connection error occured!");
        }

        closeConnection();
        return user_id;
    }

    public static String createNewUser(User user) {
        int addrId = -1;
        int userId = activeUser(user);


        createConnection();

        try {
            if (userId < 0) {
                //Execute a query
                PreparedStatement addrStmt = conn.prepareStatement("INSERT INTO  address (pCode, country, city)  VALUES (?,?,?);",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement usrStmt = conn.prepareStatement("INSERT INTO user(address_id,occupation,name,DOB,SIN)  VALUES (?,?,?,?,?);",
                        PreparedStatement.RETURN_GENERATED_KEYS);
                addrStmt.setString(1, user.getPostalCode());
                addrStmt.setString(2, user.getCountry());
                addrStmt.setString(3, user.city());
                int success = addrStmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (success > 0 && rs.next()) {
                    addrId = (rs.getInt(1));
                }
                addrStmt.close();
                rs.close();
                if (addr_Id > 0) {
                    usrStmt.setInt(1, addr_id);
                    usrStmt.setString(2, user.getOccupation());
                    usrStmt.setString(3, user.getName());
                    //VERIFY DATE PRIOR TO HERE
                    usrStmt.setString(4, user.getDOB());
                    //ERROR CONVERT TO INT BEOFRE HERE
                    usrStmt.setInt(5, user.getSIN());
                    int secondSuccess = usrStmt.executeUpdate();
                    ResultSet rsUser = stmt.getGeneratedKeys();
                    if (secondSuccess > 0 && rsUser.next()) {
                        userId = (rsUser.getInt(1));
                    }
                    usrStmt.close();
                    rsUser.close();


                }
            }


            if (userId > 0) {
                if (user.isRenter()) {
                    PreparedStatement card = conn.prepareStatement("INSERT INTO  credit_card (card_number, name, CCV,expiry_date)  VALUES (?,?,?,?);");
                    card.setLong(1, user.getCreditCard().getNumber());
                    card.setString(2, user.getName());
                    card.setInt(3, user.getCreditCard().getCCV());
                    card.setDate(4, user.getCreditCard().getExpiryDate());
                    int success = card.executeUpdate();
                    card.close();
                    if (success > 0) {
                        PreparedStatement stmt = conn.prepareStatement("INSERT INTO  renter (user_id,card_number,isActive)  VALUES (?,?,?);",
                                PreparedStatement.RETURN_GENERATED_KEYS);
                        stmt.setInt(1, userId);
                        stmt.setLong(2, user.getCreditCard().getNumber());
                        stmt.setBoolean(3, true);
                        int complete = stmt.executeUpdate();
                        ResultSet rs = stmt.getGeneratedKeys();
                        if (complete > 0 && rs.next()) {
                            userId = (rs.getInt(1));
                        } else {
                            userId = -1;
                        }
                        rs.close();
                        stmt.close();
                    } else {
                        userId = -1;
                    }

                } else {
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO  host (pCode, country, city)  VALUES (?,?,?);",
                            PreparedStatement.RETURN_GENERATED_KEYS);
                    stmt.setInt(1, userId);
                    stmt.setBoolean(2, true);
                    int complete = stmt.executeUpdate();
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (complete > 0 && rs.next()) {
                        userId = (rs.getInt(1));
                    } else {
                        userId = -1;
                    }
                }
            }


        } catch (SQLException e) {
            return (e.getMessage());
        }

        closeConnection();
        return Integer.ToString(userId);
    }

    public static String deleteUser(String userID) {
        createConnection();
        boolean cleanDelete = true;
        try {
            PreparedStatement deleteUser = conn.prepareStatement("DELETE FROM user where user_id =? ;");
            PreparedStatement updateRenter = conn.prepareStatement("UPDATE  renter SET  isActive=?  where user_id =? ;");
            PreparedStatement updateHost = conn.prepareStatement("UPDATE  host SET  isActive=?  where user_id =? ;");
            PreparedStatement updateListings = conn.prepareStatement("UPDATE listing INNER JOIN host ON listing.user_id=host.user_id SET listing.isActive=? where listing.isActive=? AND host.user_id=?;");
            PreparedStatement updateRental = conn.prepareStatement("UPDATE rental INNER JOIN listing ON rental.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET cancelled_By =?,isAvailable = ? WHERE host.user_id=? AND listing.isActive =? AND rental.end_date >= CURDATE();");
            PreparedStatement updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN listing ON calendar_entry.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET isAvailable = ? WHERE host.user_id=? AND listing.isActive =? AND calendar_entry.date >= CURDATE();");
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

        } catch (SQLException e) {
            return (e.getMessage());
        }

        closeConnection();
        return String.valueOf(cleanDelete);
    }

    

    public static ArrayList<String> getAllHostsNamesAndIDs(){
        ArrayList<String> list = new ArrayList<String>();
        list.add("bob:001");
        list.add("fred:005");
        list.add("sam:003");
        return list;
    }

    public static ArrayList<String> getAllRentersNamesAndIDs(){
        ArrayList<String> list = new ArrayList<String>();
        list.add("sally:002");
        list.add("doug:004");
        list.add("marc:006");
        return list;
    }
}
