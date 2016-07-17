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

    public static void createConnection(){
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
            conn = DriverManager.getConnection(CONNECTION,USER,PASS);
        } catch (SQLException e) {
            System.err.println("Connection error occured!");
        }
        active = !active;
    }

    public static void closeConnection(){
        if (!active)
            return;
        try {
            //Establish connection
            conn.close();
        } catch (SQLException e) {
            System.err.println("Connection error occured!");
        }

    }

    public static boolean activeUser(User user){
        createConnection();
        try {

            PreparedStatement usrStmt = conn.prepareStatement("SELECT SIN,address_id FROM user where SIN=?;");

            //WARNINBGGGGGG
            usrStmt.setInt(1, user.getSIN());


            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                int addr  = rs.getInt("address_id");
                PreparedStatement addrStmt = conn.prepareStatement("SELECT * FROM address where address_id=? and country =? ");
                addrStmt.setInt(1,addr);
                //nonexistant statement
                addrStmt.setString(2,user.getCountry());
                ResultSet addrSet = stmt.executeQuery();
                if(addrSet.next())
                    return true;
                addrStmt.close();
            }

            rs.close();
            stmt.close();


        } catch (SQLException e) {
            System.err.println("Connection error occured!");
        }

        closeConnection();
        return false;
    }

    public static String createNewUser(User user){
        if (activeUser(user))
            return "active";
        createConnection();

        try {

            //Execute a query
            PreparedStatement addrStmt = conn.prepareStatement("INSERT INTO  address (pCode, country, city)  VALUES (?,?,?);");
            PreparedStatement checkAddrStmt = conn.prepareStatement("SELECT address_id FROM address ORDER BY address_id DESC");
            PreparedStatement usrStmt = conn.prepareStatement("INSERT INTO user(address_id,occupation,name,DOB,SIN)  VALUES (?,?,?,?,?);");
            PreparedStatement checkUsrStmt = conn.prepareStatement("SELECT * FROM address where address_id=? and SIN =?;");
            addrStmt.setString(1,user.getPostalCode());
            addrStmt.setString(2,user.getCountry());
            addrStmt.setString(3,user.city());
            int success = addrStmt.executeUpdate();
            addrStmt.close();
            if (success >0){
                ResultSet addrSet = checkAddrStmt.executeQuery();
                int addr_id = -1;
                if (addrSet.next())
                    addr_id = addrSet.getInt("address_id");
                addrSet.close();
                if(addr_id > 0){
                    usrStmt.setInt(1,addr_id);
                    usrStmt.setString(2,user.getOccupation());
                    usrStmt.setString(3,user.getName());
                    //VERIFY DATE PRIOR TO HERE
                    usrStmt.setString(4,user.getDOB());
                    //ERROR CONVERT TO INT BEOFRE HERE
                    usrStmt.setInt(5,user.getSIN());
                    int secondSuccess = usrStmt.executeUpdate();
                    usrStmt.close();
                    if (secondSuccess >0){
                        checkUsrStmt.setInt(1,addr_id);
                        checkUsrStmt.setInt(2,user.getSIN());
                        ResultSet rs = checkUsrStmt.executeQuery();
                        if(rs.next()){
                            return rs.getInt("user_id");
                        }
                        rs.close();
                        checkUsrStmt.close();

                    }

                }



            }




        } catch (SQLException e) {
            return("Connection error occured!");
        }

        closeConnection();
        return "";
    }

    public static String deleteUser(String userID){
        return "abc";
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
