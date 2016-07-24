package app;
import app.objects.Listing;
import app.objects.Review;
import app.objects.User;
import app.objects.Rental;
import app.objects.CalendarEntryRange;
import app.objects.ListingFilter;
import javafx.util.Pair;


import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



                /**
                 * Created by Daniel on 2016-07-16.
                 */
                public class DBHelper {
                    private static final String dbClassName = "com.mysql.jdbc.Driver";
                    private static final String CONNECTION = "jdbc:mysql://127.0.0.1/mydb";
                    private static boolean active = false;
                    private static Connection conn;
                    private static String dateSQL = "select * from " +
                            "(select adddate('1970-01-01',t4.i*10000 + t3.i*1000 + t2.i*100 + t1.i*10 + t0.i) selected_date from\n" +
                            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t0,\n" +
                            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t1,\n" +
                            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t2,\n" +
                            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t3,\n" +
                            " (select 0 i union select 1 union select 2 union select 3 union select 4 union select 5 union select 6 union select 7 union select 8 union select 9) t4) v\n" +
                            "where selected_date between ? and ?";

                    public static void createConnection() throws ClassNotFoundException, SQLException {
                        //Register JDBC driver
                        //if (active)
                        //    return;
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
                        //if (!active)
                        //   return;
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
                            usrStmt = conn.prepareStatement("SELECT user_id,SIN,address_id FROM user where SIN=?;");

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
                                addrStmt.setString(3, user.getCity());
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
                                    card.setString(4, user.getCreditCard().getExpiryDate());
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
                            updateListings = conn.prepareStatement("UPDATE listing INNER JOIN host ON listing.user_id=host.user_id SET listing.isActive =? where listing.isActive =? AND host.user_id =?;");
                            updateRental = conn.prepareStatement("UPDATE rental INNER JOIN listing ON rental.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET cancelled_By =?,cancelled_On = CURDATE() WHERE host.user_id=? AND listing.isActive =? AND rental.end_date >= CURDATE();");
                            updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN listing ON calendar_entry.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET isAvailable = ? WHERE host.user_id=? AND listing.isActive =? AND calendar_entry.date >= CURDATE();");
                            deleteUser.setInt(1, Integer.parseInt(userID));
                            updateRenter.setBoolean(1,false);
                            updateRenter.setInt(2, Integer.parseInt(userID));
                            updateHost.setBoolean(1,false);
                            updateHost.setInt(2, Integer.parseInt(userID));
                            updateListings.setBoolean(1,false);
                            updateListings.setBoolean(2,true);
                            updateListings.setInt(3,Integer.parseInt(userID));
                            if(updateListings.executeUpdate() < 0)
                                cleanDelete = false;
                            updateRental.setInt(1,1);
                            //updateRental.setBoolean(2,false);
                            updateRental.setInt(2,Integer.parseInt(userID));
                            updateRental.setBoolean(3,true);
                            if(updateRental.executeUpdate() < 0)
                                cleanDelete = false;
                            updateCalendar.setBoolean(1,false);
                            updateCalendar.setInt(2,Integer.parseInt(userID));
                            updateCalendar.setBoolean(3,true);
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
                                list.add(rs.getString("user.name")+ ":" + String.valueOf(rs.getInt("user_id")));

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
                    public static String createListing(Listing listing,String userID) throws Exception{

                        PreparedStatement createListing = null;
                        PreparedStatement createAddress = null;
                        PreparedStatement createLocation = null;
                        ResultSet rsAddr = null;
                        ResultSet rsLoc = null;
                        ResultSet rs = null;
                        int addrId = -1;
                        int locId = -1;
                        int listingId = -1;
                        String sql = "INSERT INTO listing (location_id, address_id, type, price, num_bedrooms #%#%# )  VALUES (?,?,?,?,?";
                        String colNames ="";
                        int cCount = 0;

                        for (int i = 0; i < listing.getCharacteristics().size(); i++) {
                            colNames += ", " + listing.getCharacteristics().get(i);
                            sql += ",?";
                            cCount++;
                        }
                        sql.replace("#%#%#",colNames);
                        sql+= ");";


                        try{

                            if (commercialCheck(Integer.parseInt(userID),listing.getCountry(),listing.getCity())){
                                return "You have exceeded the max number of acceptable listings in the specified location";
                            }
                            createConnection();
                            createAddress = conn.prepareStatement("INSERT INTO  address (pCode, country, city)  VALUES (?,?,?);",
                                    PreparedStatement.RETURN_GENERATED_KEYS);
                            createAddress.setString(1,listing.getPostalCode());
                            createAddress.setString(2,listing.getCity());
                            createLocation = conn.prepareStatement("INSERT INTO location (latitude, longitude)  VALUES (?,?);",
                                    PreparedStatement.RETURN_GENERATED_KEYS);
                            createLocation.setBigDecimal(1, listing.getLatitude());
                            createLocation.setBigDecimal(2, listing.getLongitude());
                            int checkAddr = createAddress.executeUpdate();
                            int checkLoc = createLocation.executeUpdate();
                            if (checkAddr >0 && checkLoc > 0){
                                rsAddr = createAddress.getGeneratedKeys();
                                rsLoc = createLocation.getGeneratedKeys();
                                if (rsAddr.next() && rsLoc.next()) {
                                    addrId = (rsAddr.getInt(1));
                                    locId = (rsLoc.getInt(1));

                                }
                                rsAddr.close();
                                rsLoc.close();
                            }
                            if (addrId != -1 && locId != -1 ){
                                createListing = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
                                createListing.setInt(1,Integer.parseInt(userID));
                                createListing.setInt(3,addrId);
                                createListing.setInt(2,locId);
                                createListing.setString(4, listing.getType());
                                createListing.setBigDecimal(5,listing.getMainPrice());
                                createListing.setInt(6, listing.getNumberOfBedrooms());
                                for (int i = 7; i <= 6+cCount; i++) {
                                    createListing.setBoolean(i, true);

                                }
                                int result = createListing.executeUpdate();
                                rs = createListing.getGeneratedKeys();

                                if (result > 0 && rs.next()){
                                    listingId = rs.getInt(1);
                                }
                                createListing.close();
                                rs.close();

                            }
                            closeConnection();
                            createAddress.close();
                            createLocation.close();

                        }catch (Exception e){
                            throw e;
                        }finally {
                            try  {
                                createAddress.close();
                                createLocation.close();
                                rsAddr.close();
                                rsLoc.close();
                                createListing.close();
                                rs.close();
                            } catch (Exception e){
                                int a =0;
                            }
                        }

                        return String.valueOf(listingId);
                    }

                    public static String deleteListing(String listingID) throws Exception{
                        boolean cleanDelete = true;
                        PreparedStatement updateListing = null;
                        PreparedStatement updateRental = null;
                        PreparedStatement updateCalendar = null;

                        try {
                            createConnection();

                            updateListing = conn.prepareStatement("UPDATE listing SET listing.isActive =? where listing.isActive =? AND listing.listing_id =?;");
                            updateRental = conn.prepareStatement("UPDATE rental INNER JOIN listing ON rental.listing_id = listing.listing_id  SET cancelled_By =?,cancelled_On = CURDATE() WHERE listing.isActive =? AND rental.end_date >= CURDATE();");
                            updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN listing ON calendar_entry.listing_id = listing.listing_id SET isAvailable = ? WHERE listing.isActive =? AND calendar_entry.date >= CURDATE();");

                            updateListing.setBoolean(1,false);
                            updateListing.setBoolean(2,true);
                            updateListing.setInt(3,Integer.parseInt(listingID));
                            if(updateListing.executeUpdate() < 0)
                                cleanDelete = false;
                            updateRental.setInt(1,1);
                            //updateRental.setBoolean(2,false);
                            //updateRental.setInt(2,Integer.parseInt(userID));
                            updateRental.setBoolean(2,true);
                            if(updateRental.executeUpdate() < 0)
                                cleanDelete = false;
                            updateCalendar.setBoolean(1,false);
                            updateCalendar.setBoolean(2,true);
                            if(updateCalendar.executeUpdate() < 0)
                                cleanDelete = false;



                            updateListing.close();
                            updateRental.close();
                            updateCalendar.close();
                            closeConnection();
                        } catch (Exception e) {
                            throw e;
                        }finally{
                            try{

                                updateListing.close();
                                updateRental.close();
                                updateCalendar.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                            return String.valueOf(cleanDelete);
                    }


                    public static boolean commercialCheck(int userID, String country, String city) throws Exception{
                        PreparedStatement numListings = null;
                        ResultSet rs = null;
                        String sql = "select * from " +
                                "(select address.country,address.city,user_id, count(*) as numListing, s.totListing from listing " +
                                "INNER JOIN address on address.address_id=listing.address_id CROSS JOIN (select address.country,address.city,count(*) as totListing from listing " +
                                "INNER JOIN address on address.address_id=listing.address_id GROUP BY address.country,address.city)s ON s.country=address.country AND s.city=address.city " +
                                "WHERE user_id=? and isActive=? address.country=? and address.city =? GROUP BY user_id,address.country,address.city)t " +
                                "WHERE t.numListing >= 0.1*t.totListing and t.numListing >= 5;";



                        boolean result = false;
                        try {
                            createConnection();
                            numListings = conn.prepareStatement(sql);
                            numListings.setInt(1,userID);
                            numListings.setBoolean(2,true);
                            numListings.setString(3,country);
                            numListings.setString(4,city);
                            rs = numListings.executeQuery();
                            if (rs.next())
                                result = true;

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

                    public static ArrayList<String> getHostListings(String userID) throws Exception {
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT listing_id, type,address.city,address.country,address.pCode FROM listing " +
                                    "INNER JOIN address ON listing.address_id=address.address_id WHERE listing.user_id=?;");
                            listings.setInt(1,Integer.parseInt(userID));
                            rs = listings.executeQuery();
                            while (rs.next())
                                list.add(String.format("%d:%s:%s:%s:%s",rs.getInt("listing_id"),
                                        rs.getString("type"),rs.getString("country"),rs.getString("city"),rs.getString("pCode")));


                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try{
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                        return list;
                    }

                    public static ArrayList<String> getRenterBookings(String userID) throws Exception {
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT rental_id,listing.listing_id,address.city,address.country FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id WHERE rental.user_id=? AND rental.end_date >= CURDATE());");
                            listings.setInt(1,Integer.parseInt(userID));
                            rs = listings.executeQuery();
                            while (rs.next())
                                list.add(String.format("%d:Listing:%s:Country:%s:City:%s:Postal Code:%s",rs.getInt("rental_id")
                                        ,rs.getInt("listing.listing_id"), rs.getString("address.country"),rs.getString("address.city")));


                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try{
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                        return list;
                    }




                    //WARNING NEED TO CHECK
                    public static Listing getListingInfo(String listingID) throws Exception{
                        Listing listing = null;
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        Map map = new HashMap();
                        ArrayList<String> list = new ArrayList<String>();

                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT * from listing INNER JOIN address ON listing.address_id = address.address_id INNER JOIN location on location.location_id=listing.location_id WHERE listing_id=?;");
                            listings.setInt(1,Integer.parseInt(listingID));
                            rs = listings.executeQuery();
                            if (rs.next()) {

                                int count = rs.getMetaData().getColumnCount();
                                //maa = (String)listing.get("address");
                                //map.put  = (String)listing.get("postalCode");
                                map.put("postalCode",rs.getString("pCode"));
                                map.put("country",rs.getString("country"));
                                map.put("city",rs.getString("city"));
                                map.put("longitude",rs.getBigDecimal("longitude"));
                                map.put("latitude",rs.getBigDecimal("latitude"));
                                map.put("mainPrice",rs.getBigDecimal("price"));
                                map.put("numberOfBedrooms",rs.getInt("num_bedrooms"));
                                map.put("type",rs.getString("type"));
                                for (int i=9;i <=39 ;i++){
                                    if (rs.getBoolean(i))
                                        list.add(rs.getMetaData().getColumnName(i));

                                }



                            }
                            listing = new Listing(map);
                            listing.setCharacteristics(list);


                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try{
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                        return listing;
                    }


                    public static ArrayList<String> getReviewableHosts(String userID) throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT rental_id,listing.listing_id,listing.user_id FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id WHERE rental.user_id=? AND rental.end_date >= CURDATE() - INTERVAL 30 DAY;");
                            listings.setInt(1,Integer.parseInt(userID));
                            rs = listings.executeQuery();
                            while (rs.next())
                                list.add(String.format("%d:Rental:%d:Listing:%d:Host",rs.getInt("rental_id")
                                        ,rs.getInt("listing.listing_id"),rs.getInt("listing.user_id")));


                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try{
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                        return list;
                    }

                    public static ArrayList<String> getReviewableRenters(String userID) throws Exception{

                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT rental_id,listing.listing_id,rental.user_id FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.listing_id=address.address_id WHERE listing.user_id=? AND rental.end_date >= CURDATE() - INTERVAL 30 DAY;");
                            listings.setInt(1,Integer.parseInt(userID));
                            rs = listings.executeQuery();
                            while (rs.next())
                                list.add(String.format("%d:Rental:%d:Listing:%d:Renter",rs.getInt("rental_id")
                                        ,rs.getInt("listing.listing_id"),rs.getInt("rental.user_id")));


                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try{
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                        return list;
                    }

                    public static ArrayList<String> getReviewableListings(String userID) throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT rental_id,listing.listing_id FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id WHERE rental.user_id=? AND rental.end_date >= CURDATE() - INTERVAL 30 DAY;");
                            listings.setInt(1,Integer.parseInt(userID));
                            rs = listings.executeQuery();
                            while (rs.next())
                                list.add(String.format("%d:Rental:%d:Listing:%d:Renter",rs.getInt("rental_id")
                                        ,rs.getInt("listing.listing_id"),rs.getInt("rental.user_id")));


                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try{
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                        return list;
                    }


                    public static String createReview(String userID, Review review) throws Exception{
                        String retId ="";
                        PreparedStatement insReview= null;
                        ResultSet rs= null;
                        String sql= "INSERT INTO review (#,renter_id,rating,description,type)  VALUES (?,?,?,?,?);";

                        try {

                            createConnection();
                            String colName ="";
                            int type = 0;
                            if (review.getType().equals(Review.RevieweeType.HOST)) {
                                colName ="host_id";
                                type = 1;
                            }
                            else if (review.getType().equals(Review.RevieweeType.LISTING)) {
                                colName ="listing_id";
                                type = 2;

                            }else{
                                colName ="host_id";
                                type = 3;
                            }
                            sql.replace("#",colName);
                            insReview =conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                            insReview.setInt(1,Integer.parseInt(review.getRevieweeID()));
                            insReview.setInt(2, Integer.parseInt(userID));
                            insReview.setInt(3,review.getRating());
                            insReview.setString(4,review.getDescription());
                            insReview.setInt(5,type);
                            int complete = insReview.executeUpdate();
                            rs = insReview.getGeneratedKeys();
                            if (complete > 0 && rs.next())
                                retId = String.valueOf(rs.getInt(1));
                            closeConnection();
                            rs.close();
                            insReview.close();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try {
                                rs.close();
                                insReview.close();
                            }catch (Exception e){
                                int a =0;
                            }

                        }



                        return retId;
                    }


                    public static String deleteBooking(String bookingID ,boolean isHost) throws Exception {
                        String retval ="";
                        PreparedStatement updRental= null;
                        PreparedStatement updateCalendar= null;
                        ResultSet rs= null;
                        String sql= "UPDATE rental SET rental.cancelled_ON = CURDATE(),rental.cancelled_By=? WHERE rental.rental_id=?;";


                        try {

                            createConnection();
                            updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN rental ON calendar_entry.listing_id = rental.listing_id SET isAvailable = ? WHERE rental.rental_id=? AND rental.start_date <= calendar_entry.date AND rental.end_date>=calendar_entry.date;");
                            updRental = conn.prepareStatement(sql);
                            int cancelled = 0;
                            if (isHost) {
                                cancelled = 1;
                            }else{
                                cancelled = 2;
                            }

                            updRental.setInt(1,cancelled);
                            updRental.setInt(2, Integer.parseInt(bookingID));
                            updateCalendar.setInt(1,Integer.parseInt(bookingID));
                            int complete = updRental.executeUpdate();
                            int completeCal = updateCalendar.executeUpdate();

                            if (complete > 0 && completeCal > 0)
                                retval = "success";
                            updRental.close();
                            updateCalendar.close();
                            closeConnection();


                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try {
                                updRental.close();
                                updateCalendar.close();
                            }catch (Exception e){
                                int a =0;
                            }

                        }



                        return retval;
                    }

                    public static ArrayList<String> getMyBookings(String userID,boolean isHost) throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            if (!isHost)
                                return getRenterBookings(userID);
                            createConnection();
                            listings = conn.prepareStatement("SELECT start_date,end_date,rental_id,listing.listing_id,address.city,address.country FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id WHERE listing.user_id=? AND rental.end_date >= CURDATE();");
                            listings.setInt(1,Integer.parseInt(userID));

                            rs = listings.executeQuery();


                            while (rs.next()) {
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                String start = df.format(rs.getDate("start_date"));
                                String end = df.format(rs.getDate("start_date"));
                                list.add(String.format("%d:%s:%s:%s:%s:%s:%s ", rs.getInt("rental_id")
                                        , rs.getInt("listing_id"), rs.getString("country"), rs.getString("city"), start, end));

                            }
                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try{
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a=0;
                            }
                        }
                        return list;
                    }

                    public static String createBooking(String userID, String listingID, Rental rental) throws Exception{
                        String retval ="";
                        PreparedStatement listingPrice= null;
                        PreparedStatement conflicts= null;
                        PreparedStatement insertRental= null;
                        PreparedStatement insertCalendar= null;
                        PreparedStatement updateCalendar= null;
                        PreparedStatement getPrice= null;
                        ResultSet rs= null;
                        ResultSet rsPrice= null;
                        ResultSet rsGetPrice= null;
                        ResultSet rsInsRental= null;
                        String sql= "SELECT * FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? and calendar_entry.isAvailable =? and listing_id=? ;";
                        String priceSQL= "select listing.price from listing WHERE listing_id=?";
                        String updCal= "UPDATE calendar_entry SET  calendar_entry.isAvailable =? where calendar_entry.date >= ? AND calendar_entry.date<=? AND and listing_id=?;";
                        String insCal= "INSERT into calendar_entry (listing_id isAvailable,price,date) select ,? ?,?, t.selected_date from (" +dateSQL + ")t where t.selected_date NOT IN (SELECT date as selected_date FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? AND calendar_entry.listing_id=?); ";
                        String insRental = "INSERT INTO rental(user_id,listing_id,start_date,end_date) VALUES(?,?,?,?);";
                        String totalPrice = "Select SUM(price) from calendar_entry where date>=? and date<=?";


                        try {

                            createConnection();
                            conflicts = conn.prepareStatement(sql);
                            conflicts.setDate(1,rental.getStartDate());
                            conflicts.setDate(2,rental.getEndDate());
                            conflicts.setBoolean(3,false);
                            conflicts.setInt(4,Integer.parseInt(listingID));

                            rs = conflicts.executeQuery();
                            if (!rs.next()){
                                listingPrice = conn.prepareStatement(priceSQL);
                                listingPrice.setInt(1,Integer.parseInt(listingID));
                                rsPrice = listingPrice.executeQuery();
                                if (rsPrice.next()){
                                    BigDecimal price = rsPrice.getBigDecimal(1);
                                    updateCalendar = conn.prepareStatement(updCal);
                                    updateCalendar.setDate(2,rental.getStartDate());
                                    updateCalendar.setDate(3,rental.getEndDate());
                                    updateCalendar.setInt(4,Integer.parseInt(listingID));
                                    updateCalendar.setBoolean(1,false);
                                    if (updateCalendar.executeUpdate()>0){
                                        insertCalendar = conn.prepareStatement(insCal);
                                        insertCalendar.setInt(1,Integer.parseInt(listingID));
                                        insertCalendar.setBoolean(2,false);
                                        insertCalendar.setBigDecimal(3,price);
                                        insertCalendar.setDate(4,rental.getStartDate());
                                        insertCalendar.setDate(5,rental.getEndDate());
                                        insertCalendar.setDate(6,rental.getStartDate());
                                        insertCalendar.setDate(7,rental.getEndDate());
                                        insertCalendar.setInt(8,Integer.parseInt(listingID));
                                        if(insertCalendar.executeUpdate()> 0){
                                            getPrice  = conn.prepareStatement(totalPrice);
                                            getPrice.setDate(1,rental.getStartDate());
                                            getPrice.setDate(2,rental.getEndDate());
                                            rsGetPrice = getPrice.executeQuery();
                                            if (rsGetPrice.next()){
                                                BigDecimal tPrice = rsGetPrice.getBigDecimal(1);
                                                insertRental = conn.prepareStatement(insRental,PreparedStatement.RETURN_GENERATED_KEYS);
                                                int res = insertRental.executeUpdate();
                                                rsInsRental = insertRental.getGeneratedKeys();
                                                if (rsInsRental.next()){
                                                    retval = String.valueOf(rsInsRental.getInt(1));
                                                }

                                            }


                                        }
                                    }

                                }

                            }
                            rsInsRental.close();
                            insertRental.close();
                            rsGetPrice.close();
                            getPrice.close();
                            insertCalendar.close();
                            updateCalendar.close();
                            rsPrice.close();
                            listingPrice.close();
                            rs.close();
                            conflicts.close();




                            closeConnection();


                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try {
                                rsInsRental.close();
                                insertRental.close();
                                rsGetPrice.close();
                                getPrice.close();
                                insertCalendar.close();
                                updateCalendar.close();
                                rsPrice.close();
                                listingPrice.close();
                                rs.close();
                                conflicts.close();
                            }catch (Exception e){
                                int a =0;
                            }

                        }



                        return retval;
                    }


                    public static String setListingPrice(String listingID, CalendarEntryRange calendarEntryRange) throws Exception{
                        String retval ="";
                        PreparedStatement conflicts= null;
                        PreparedStatement insertCalendar= null;
                        PreparedStatement updateCalendar= null;
                        ResultSet rs= null;
                        String sql= "SELECT * FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? and calendar_entry.isAvailable =? and listing_id=?;";
                        String updCal= "UPDATE calendar_entry SET  calendar_entry.price =? where calendar_entry.date >= ? AND calendar_entry.date<=? and listing_id=? ;";
                        String insCal= "INSERT into calendar_entry (listing_id isAvailable,price,date) select ,? ?,?, t.selected_date from (" +dateSQL + ")t where t.selected_date NOT IN (SELECT date as selected_date FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? AND calendar_entry.listing_id=?); ";
                        try {

                            createConnection();
                            conflicts = conn.prepareStatement(sql);
                            conflicts.setDate(1,calendarEntryRange.getStartDate());
                            conflicts.setDate(2,calendarEntryRange.getEndDate());
                            conflicts.setBoolean(3,false);
                            conflicts.setInt(4,Integer.parseInt(listingID));
                            rs = conflicts.executeQuery();
                            if (!rs.next()){

                                updateCalendar = conn.prepareStatement(updCal);
                                updateCalendar.setDate(2,calendarEntryRange.getStartDate());
                                updateCalendar.setDate(3,calendarEntryRange.getEndDate());
                                updateCalendar.setInt(4,Integer.parseInt(listingID));
                                updateCalendar.setBigDecimal(1,calendarEntryRange.getPrice());
                                if (updateCalendar.executeUpdate()>0){
                                    insertCalendar = conn.prepareStatement(insCal);
                                    insertCalendar.setInt(1,Integer.parseInt(listingID));
                                    insertCalendar.setBoolean(2,true);
                                    insertCalendar.setBigDecimal(3,calendarEntryRange.getPrice());
                                    insertCalendar.setDate(4,calendarEntryRange.getStartDate());
                                    insertCalendar.setDate(5,calendarEntryRange.getEndDate());
                                    insertCalendar.setDate(6,calendarEntryRange.getStartDate());
                                    insertCalendar.setDate(7,calendarEntryRange.getEndDate());
                                    insertCalendar.setInt(8,Integer.parseInt(listingID));
                                    if(insertCalendar.executeUpdate()> 0){
                                        retval ="success";
                                    }

                                }

                            }

                            insertCalendar.close();
                            updateCalendar.close();
                            rs.close();
                            conflicts.close();




                            closeConnection();


                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try {
                                insertCalendar.close();
                                updateCalendar.close();
                                rs.close();
                                conflicts.close();

                            }catch (Exception e){
                                int a =0;
                            }

                        }



                        return retval;
                    }


                    public static String setListingAvailability(String listingID, CalendarEntryRange calendarEntryRange) throws Exception{
                        String retval ="";
                        PreparedStatement listingPrice= null;
                        PreparedStatement conflicts= null;
                        PreparedStatement insertCalendar= null;
                        PreparedStatement updateCalendar= null;
                        ResultSet rs= null;
                        ResultSet rsPrice= null;
                        String sql= "SELECT * FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? and calendar_entry.isAvailable =? and listing_id=?;";
                        String priceSQL= "select listing.price from listing WHERE listing_id=?";
                        String updCal= "UPDATE calendar_entry SET  calendar_entry.isAvailable =? where calendar_entry.date >= ? AND calendar_entry.date<=? and listing_id=?;";
                        String insCal= "INSERT into calendar_entry (listing_id isAvailable,price,date) select ,? ?,?, t.selected_date from (" +dateSQL + ")t where t.selected_date NOT IN (SELECT date as selected_date FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? AND calendar_entry.listing_id=?); ";


                        try {

                            createConnection();
                            conflicts = conn.prepareStatement(sql);
                            conflicts.setDate(1,calendarEntryRange.getStartDate());
                            conflicts.setDate(2,calendarEntryRange.getEndDate());
                            conflicts.setBoolean(3,false);
                            conflicts.setInt(4,Integer.parseInt(listingID));

                            rs = conflicts.executeQuery();
                            if (!rs.next()){
                                listingPrice = conn.prepareStatement(priceSQL);
                                listingPrice.setInt(1,Integer.parseInt(listingID));
                                rsPrice = listingPrice.executeQuery();
                                if (rsPrice.next()){
                                    BigDecimal price = rsPrice.getBigDecimal(1);
                                    updateCalendar = conn.prepareStatement(updCal);
                                    updateCalendar.setDate(2,calendarEntryRange.getStartDate());
                                    updateCalendar.setDate(3,calendarEntryRange.getEndDate());
                                    updateCalendar.setInt(4,Integer.parseInt(listingID));
                                    updateCalendar.setBoolean(1,calendarEntryRange.isAvailable());
                                    if (updateCalendar.executeUpdate()>0){
                                        insertCalendar = conn.prepareStatement(insCal);
                                        insertCalendar.setInt(1,Integer.parseInt(listingID));
                                        insertCalendar.setBoolean(2,calendarEntryRange.isAvailable());
                                        insertCalendar.setBigDecimal(3,price);
                                        insertCalendar.setDate(4,calendarEntryRange.getStartDate());
                                        insertCalendar.setDate(5,calendarEntryRange.getEndDate());
                                        insertCalendar.setDate(6,calendarEntryRange.getStartDate());
                                        insertCalendar.setDate(7,calendarEntryRange.getEndDate());
                                        insertCalendar.setInt(8,Integer.parseInt(listingID));
                                        if(insertCalendar.executeUpdate()> 0){
                                            retval = "success";
                                        }
                                    }

                                }

                            }

                            insertCalendar.close();
                            updateCalendar.close();
                            rsPrice.close();
                            listingPrice.close();
                            rs.close();
                            conflicts.close();




                            closeConnection();


                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try {
                                insertCalendar.close();
                                updateCalendar.close();
                                rsPrice.close();
                                listingPrice.close();
                                rs.close();
                                conflicts.close();
                            }catch (Exception e){
                                int a =0;
                            }

                        }



                        return retval;
                    }


                    //QUERY SEARCHING_________________________________________________

                    public static ArrayList<String> getListings(ListingFilter filter) throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        ArrayList<Pair<Double,String>> distance = new ArrayList<Pair<Double,String>>();
                        PreparedStatement exactSearch = null;
                        PreparedStatement calendarCheck = null;
                        ResultSet rs = null;
                        ResultSet rsCal = null;


                        try {

                            createConnection();
                            if (!filter.getCountry().equals("")) {
                                exactSearch = conn.prepareStatement("select listing_id from listing INNER JOIN " +
                                        "address on listing.address_id = address.address_id WHERE address.city=? " +
                                        "and address.country=? and address.postal_code=?;");
                                exactSearch.setString(1, filter.getCity());
                                exactSearch.setString(2, filter.getCountry());
                                exactSearch.setString(3, filter.getPostalCode());
                                rs = exactSearch.executeQuery();
                                if (rs.next()) {
                                    list.add(String.format("%d:%s:%s:%s", rs.getInt(1), filter.getCountry(), filter.getCity(),
                                            filter.getPostalCode()));

                                }


                                rs.close();

                            } else {
                                String sql = "SELECT * FROM listing INNER JOIN (select pCode,city,country,address_id as addr_id from address)a ON a.addr_id=listing.address_id INNER JOIN (select latitude,longitude,location_id as loc_id from location)b ON b.loc_id=listing.location_id WHERE listing.isActive=TRUE";
                                if (!filter.getPostalCode().equals("")) {
                                    String sub = filter.getPostalCode().substring(0, 3);
                                    sql = "SELECT * from (" + sql + ")c where pCode REGEXP '^" + sub + "|[0,9][a,Z][0-9]$' ";


                                }
                                if (filter.isSortByPrice()) {
                                    if (filter.isSortAscending()) {
                                        sql = "SELECT * FROM (" + sql + ")d ORDER BY price ASC";
                                    } else {
                                        sql = "SELECT * FROM (" + sql + ")d ORDER BY price DESC";
                                    }


                                }
                                if (!filter.getLowestPrice().toPlainString().equals("-1")) {
                                    if (!filter.getHighestPrice().toPlainString().equals("-1")) {
                                        sql = "SELECT * FROM (" + sql + ")e where price>=? and price <=?";
                                        exactSearch = conn.prepareStatement(sql + ";");
                                        exactSearch.setBigDecimal(1,filter.getLowestPrice());
                                        exactSearch.setBigDecimal(2,filter.getHighestPrice());


                                    } else {
                                        sql = "SELECT * FROM (" + sql + ")e where price>=?";
                                        exactSearch = conn.prepareStatement(sql + ";");
                                        exactSearch.setBigDecimal(1,filter.getLowestPrice());

                                    }



                                } else if(!filter.getHighestPrice().toPlainString().equals("-1")){
                                    sql = "SELECT * FROM (" + sql + ")e where price<=?";
                                    exactSearch = conn.prepareStatement(sql + ";");
                                    exactSearch.setBigDecimal(2,filter.getHighestPrice());


                                }else {
                                    exactSearch = conn.prepareStatement(sql + ";");

                                }


                                rs = exactSearch.executeQuery();
                                while (rs.next()) {
                                    int listingID = rs.getInt("listing_id");
                                    boolean passCheck = true;
                                    if (filter.getFirstDate() != null) {
                                        calendarCheck = conn.prepareStatement("select calendar_entry_id from calendar_entry where listing_id =? and is_available = ? and date>=? and date<=?; ");
                                        calendarCheck.setInt(1, listingID);
                                        calendarCheck.setBoolean(2, false);
                                        calendarCheck.setDate(3, filter.getFirstDate());
                                        calendarCheck.setDate(3, filter.getLastDate());
                                        rsCal = calendarCheck.executeQuery();
                                        if (rsCal.next())
                                            passCheck = false;

                                    }
                                    if (passCheck && !(filter.getCharacteristics().size() == 0)) {
                                        for (String s : filter.getCharacteristics()) {
                                            if (!rsCal.getBoolean(s)) {
                                                passCheck = false;
                                            }

                                        }
                                    }
                                    String listing = String.format("%d:%s:%s:%s:%s:", listingID, rs.getString("Country"),
                                            rs.getString("City"), rs.getString("pCode"),
                                            rs.getBigDecimal("price").toPlainString());
                                    if (passCheck) {
                                        list.add(listing);
                                    }
                                    if (passCheck && !filter.isSortByPrice()) {
                                        double latitude = rs.getBigDecimal("latitude").doubleValue();
                                        double longitude = rs.getBigDecimal("longitude").doubleValue();
                                        double calcDistance = distance(latitude, longitude, filter.getLatitude().doubleValue()
                                                , filter.getLongitude().doubleValue(), 'K');
                                        listing += String.valueOf(calcDistance) + " km";
                                        distance.add(new Pair<Double,String>(calcDistance, listing));


                                    }


                                }


                            }
                            rsCal.close();
                            calendarCheck.close();
                            rs.close();
                            exactSearch.close();
                            closeConnection();
                            if (distance.size()==0){
                                return list;
                            }else{
                                if (filter.isSortAscending()){
                                    distance.sort((o1, o2) -> ((Double)o1.getKey()).compareTo((Double)o1.getKey()));
                                }else{
                                    distance.sort((o1, o2) -> -((Double)o1.getKey()).compareTo((Double)o1.getKey()));

                                }
                                ArrayList<String> sortedList = new ArrayList<String>();
                                for (int i=0;i<distance.size() ;i++){
                                    String data = distance.get(i).getValue();
                                    sortedList.add(data);
                                }
                                return sortedList;
                            }





                        } catch (Exception e) {
                            throw e;
                        } finally {
                            try {
                                rsCal.close();
                                calendarCheck.close();
                                rs.close();
                                exactSearch.close();
                                closeConnection();
                            } catch (Exception e) {
                                int a = 0;
                            }

                        }
                    }


                    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
                        double theta = lon1 - lon2;
                        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
                        dist = Math.acos(dist);
                        dist = rad2deg(dist);
                        dist = dist * 60 * 1.1515;
                        if (unit == 'K') {
                            dist = dist * 1.609344;
                        } else if (unit == 'N') {
                            dist = dist * 0.8684;
                        }
                        return (dist);
                    }


                    private static double deg2rad(double deg) {
                        return (deg * Math.PI / 180.0);
                    }


                    private static double rad2deg(double rad) {
                        return (rad * 180.0 / Math.PI);
                    }




                    //________________________________________________________________________





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
                                "s.country=address.country AND s.city=address.city WHERE isActive=? GROUP BY user_id,address.country,address.city)t " +
                                "WHERE t.numListing >= 0.1*t.totListing GROUP BY t.country ASC,t.city ASC,numListing DESC,t.user_id;";



                        int result = -1;
                        try {
                            createConnection();
                            numListings = conn.prepareStatement(sql);
                            numListings.setBoolean(1,true);
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

                    public static int reportCancelledRentals(boolean host) throws Exception {
                        PreparedStatement numListings = null;
                        ResultSet rs = null;
                        String sql = "select * from (select user_id, count(*) as" +
                                " numRental from rental WHERE cancelled_By=2 AND  cancelled_On>= DATE_SUB(CURDATE(), " +
                                "INTERVAL 1 YEAR) AND cancelled_On <= CURDATE() GROUP BY " +
                                "user_id)t group by t.numRental DESC,t.user_id;";
                        if (host)
                            sql = "select user_id, numRental from (select listing.user_id, count(*) as numRental from rental" +
                                    " INNER JOIN listing ON rental.listing_id =listing.listing_id WHERE cancelled_By=1 AND" +
                                    "  cancelled_On>= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) AND cancelled_On <= CURDATE()" +
                                    " group by user_id)t   group by t.numRental DESC,user_id;";
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

                    public static int reportNounPhrases(boolean host) throws Exception {
                        PreparedStatement listings = null;
                        PreparedStatement text = null;
                        ResultSet rs = null;
                        ResultSet rsText = null;
                        String sql = "select listing_id from listing where isActive=1)";
                        String textSQL = "SELECT description from listing INNER JOIN review on listing.listing_id=review.listing_id where review.listng_id=?; ";
                        int result = -1;
                        try {
                            createConnection();
                            listings = conn.prepareStatement(sql);
                            text = conn.prepareStatement(textSQL);

                            rs = listings.executeQuery();
                            ArrayList<String> inputList = new ArrayList<String>();
                            ArrayList<String> outputList = new ArrayList<String>();
                            while (rs.next()) {
                                result = rs.getInt(1);
                                text.setInt(1, result);
                                rsText = listings.executeQuery();
                                while (rsText.next()) {
                                    String description = rsText.getString(1);
                                    for (String s: description.split(". ")) {
                                        inputList.add(s+" .");
                                    }
                                }
                                outputList= NounPhraseParser.parseNouns(inputList);
                                String fileName = FileWriter.createFile("reportNounPhrases");
                                outputList.add(0,String.valueOf(result)+" Popular Noun phrases");
                                FileWriter.writeToFile(outputList,fileName);
                            }


                            rsText.close();
                            text.close();
                            rs.close();
                            listings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally{
                            try{
                                rsText.close();
                                text.close();
                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a =0;
                            }
                        }
                        return result;
                    }







                }
