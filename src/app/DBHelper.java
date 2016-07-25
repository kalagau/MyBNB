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
                    //generates a list of dates between 2 dates
                    //taken from stackoverflow ---
                    //
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
                        //This method attempts to establish a connection with the database
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
                        // This method closes the active connectoin to the DB
                        //if (!active)
                        //   return;
                        try {
                            //Close connection
                            conn.close();
                        } catch (SQLException e) {
                            System.err.println("Connection error occured!");
                            throw e;
                        }

                    }
                    // Detect if potential user is already  active in the DB
                    public static int activeUser(User user) throws Exception {
                        int userId = -1;
                        PreparedStatement usrStmt = null;
                        PreparedStatement addrStmt =  null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            // get user ids of users wit the same sin accrsoss the world
                            usrStmt = conn.prepareStatement("SELECT user_id,SIN,address_id FROM user where SIN=?;");


                            usrStmt.setString(1, user.getSIN());


                            rs = usrStmt.executeQuery();
                            while (rs.next()) {
                                int addr = rs.getInt("address_id");
                                userId = rs.getInt("user_id");
                                //SINCE sins are unique in every country
                                //determine if the user exists in the same country
                                addrStmt = conn.prepareStatement("SELECT * FROM address where address_id=? and country =? ");
                                addrStmt.setInt(1, addr);
                                addrStmt.setString(2, user.getCountry());
                                ResultSet addrSet = addrStmt.executeQuery();
                                //if the query returns a value we know we foun the user
                                if (!addrSet.next())
                                    userId = -1;
                                addrStmt.close();
                            }

                            rs.close();
                            usrStmt.close();


                            closeConnection();
                        }catch(Exception e) {

                            throw e;
                        } finally{
                            // we run this block in every method to ensure there are no memory leaks in the event
                            // of an error. This is present in every method in this file from here.
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
                            // check if user was found
                            createConnection();
                            if (userId < 0) {
                                //Create address and user
                                addrStmt = conn.prepareStatement("INSERT INTO  address (pCode, country, city)  VALUES (?,?,?);",
                                        PreparedStatement.RETURN_GENERATED_KEYS);
                                usrStmt = conn.prepareStatement("INSERT INTO user(address_id,occupation,name,DOB,SIN)  VALUES (?,?,?,?,?);",
                                        PreparedStatement.RETURN_GENERATED_KEYS);
                                // we fill paramters to prevent SQL injections
                                addrStmt.setString(1, user.getPostalCode());
                                addrStmt.setString(2, user.getCountry());
                                addrStmt.setString(3, user.getCity());
                                //if the insert was sucessful we are able to track it and also retrive te new primary key
                                int success = addrStmt.executeUpdate();
                                 rs = addrStmt.getGeneratedKeys();
                                if (success > 0 && rs.next()) {
                                    addrId = (rs.getInt(1));
                                }
                                addrStmt.close();
                                rs.close();
                                //we proceed if creating thr address was cuessful
                                if (addrId > 0) {
                                    usrStmt.setInt(1, addrId);
                                    usrStmt.setString(2, user.getOccupation());
                                    usrStmt.setString(3, user.getName());
                                    usrStmt.setDate(4, user.getDOB());
                                    usrStmt.setString(5, user.getSIN());
                                    int secondSuccess = usrStmt.executeUpdate();
                                    rsUser = usrStmt.getGeneratedKeys();
                                    if (secondSuccess > 0 && rsUser.next()) {
                                        //we get the uesr ide after a sucessful insert
                                        userId = (rsUser.getInt(1));
                                    }
                                    usrStmt.close();
                                    rsUser.close();


                                }
                            }

                            // only proceed if we have the user in the tabel
                            if (userId > 0) {
                                // create a renter
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
                                    // create a host
                                    stmt = conn.prepareStatement("INSERT INTO  host (user_id, isActive)  VALUES (?,?);",
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
                        //PreparedStatement deleteUser = null;
                        PreparedStatement updateRenter = null;
                        PreparedStatement updateHost = null;
                        PreparedStatement updateListings = null;
                        PreparedStatement updateRental = null;
                        PreparedStatement updateCalendar = null;
                        PreparedStatement updateRenterRental = null;
                        PreparedStatement updateRenterCalendar = null;

                        try {
                            createConnection();
                            //when a user requests to delete their account
                            // we delete their host and rental acconts
                            //we cancel thier rentals and mark their listings as inactive
                            //we also upate
                            //deleteUser = conn.prepareStatement("DELETE FROM user where user_id =? ;");
                            updateRenter = conn.prepareStatement("UPDATE  renter SET  isActive=?  where user_id =? ;");
                            updateHost = conn.prepareStatement("UPDATE  host SET  isActive=?  where user_id =? ;");
                            updateListings = conn.prepareStatement("UPDATE listing INNER JOIN host ON listing.user_id=host.user_id SET listing.isActive =? where listing.isActive =? AND host.user_id =?;");

                            //cancel all rentals now or in the future where the user is a host
                            updateRental = conn.prepareStatement("UPDATE rental INNER JOIN listing ON rental.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET cancelled_By =?,cancelled_On = CURDATE() WHERE host.user_id=? AND listing.isActive =? AND rental.end_date >= CURDATE();");
                            //set all calendar entries for all listngs to unvailable from the host
                            updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN listing ON calendar_entry.listing_id = listing.listing_id INNER JOIN host ON listing.user_id=host.user_id SET isAvailable = ? WHERE host.user_id=? AND calendar_entry.date >= CURDATE();");
                           //cancelss all rentals as renter
                            updateRenterRental = conn.prepareStatement("UPDATE rental SET cancelled_By =?,cancelled_On = CURDATE() WHERE rental.user_id=? AND cancelled_By=? AND rental.end_date >= CURDATE();");
                            //set all calendar entries for all listngs to unvailable from the user
                            updateRenterCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN listing ON calendar_entry.listing_id = listing.listing_id INNER JOIN rental ON rental.listing_id=listing.listing_id SET isAvailable = ? WHERE rental.user_id=? AND listing.isActive=? AND calendar_entry.date >= CURDATE();");

                            updateRenter.setBoolean(1,false);
                            updateRenter.setInt(2, Integer.parseInt(userID));
                            updateHost.setBoolean(1,false);
                            updateHost.setInt(2, Integer.parseInt(userID));
                            updateListings.setBoolean(1,false);
                            updateListings.setBoolean(2,true);
                            updateListings.setInt(3,Integer.parseInt(userID));
                            //look for errors
                            updateListings.executeUpdate();

                            //cancelled by host
                            //value of 1
                            updateRental.setInt(1,1);
                            //updateRental.setBoolean(2,false);
                            updateRental.setInt(2,Integer.parseInt(userID));
                            updateRental.setBoolean(3,true);
                            updateRental.executeUpdate();
                            updateCalendar.setBoolean(1,false);
                            updateCalendar.setInt(2,Integer.parseInt(userID));

                            updateCalendar.executeUpdate();


                            updateRenterRental.setInt(1,2);
                            updateRenterRental.setInt(2,Integer.parseInt(userID));
                            updateRenterRental.setBoolean(3,true);
                            updateRenterRental.executeUpdate();

                            updateRenterCalendar.setBoolean(1,false);
                            updateRenterCalendar.setInt(2,Integer.parseInt(userID));
                            updateRenterCalendar.setBoolean(3,true);
                            updateRenterCalendar.executeUpdate();


                            updateRenter.executeUpdate();

                            updateHost.executeUpdate();


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
                                //deleteUser.close();
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


                    //Returns a list of all active Hosts
                    public static ArrayList<String> getAllHostsNamesAndIDs() throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement hostData = null;
                        ResultSet rs = null;

                        try {
                            createConnection();
                            hostData = conn.prepareStatement("SELECT host.user_id,user.name FROM host INNER JOIN user ON user.user_id=host.user_id WHERE host.isActive=TRUE;");
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
                    //GET ALL active renters for the initial screen
                    public static ArrayList<String> getAllRentersNamesAndIDs() throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement renterData = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            renterData = conn.prepareStatement("SELECT renter.user_id,user.name FROM renter INNER JOIN user ON user.user_id=renter.user_id  WHERE renter.isActive=TRUE;");
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


                    //Create a listing given the user specififed details and id
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
                        //SQL statement which has $%$% to be replaced by ammnenities the user selects
                        String sql = "INSERT INTO listing (user_id,location_id, address_id, type, price, num_bedrooms #%#%# )  VALUES (?,?,?,?,?,?";
                        String colNames ="";
                        int cCount = 0;
                        // Modify the uery to accomodate the number of ammenities the user selected
                        for (int i = 0; i < listing.getCharacteristics().size(); i++) {
                            colNames += ", " + listing.getCharacteristics().get(i);
                            sql += ",?";
                            cCount++;
                        }
                        //add in theaddtional colNames
                        sql = sql.replace("#%#%#",colNames);
                        sql+= ");";


                        try{
                            // We verify that the user wont violate the commercial check rules by adding this listing
                            if (commercialCheck(Integer.parseInt(userID),listing.getCountry(),listing.getCity())){
                                return "You have exceeded the max number of acceptable listings in the specified location";
                            }
                            createConnection();
                            //create an address entry
                            createAddress = conn.prepareStatement("INSERT INTO  address (pCode, country, city)  VALUES (?,?,?);",
                                    PreparedStatement.RETURN_GENERATED_KEYS);
                            createAddress.setString(1,listing.getPostalCode());
                            createAddress.setString(2,listing.getCountry());
                            createAddress.setString(3,listing.getCity());
                            //create a location entry
                            createLocation = conn.prepareStatement("INSERT INTO location (latitude, longitude)  VALUES (?,?);",
                                    PreparedStatement.RETURN_GENERATED_KEYS);
                            createLocation.setBigDecimal(1, listing.getLatitude());
                            createLocation.setBigDecimal(2, listing.getLongitude());
                            int checkAddr = createAddress.executeUpdate();
                            int checkLoc = createLocation.executeUpdate();
                            if (checkAddr >0 && checkLoc > 0){
                                // get keys of the addresss and locations entered in to the tables
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
                                // if we sucess fully created the two entires prepare and execute the query
                                //to insert the listing

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
                            //delete the specific listing
                            updateListing = conn.prepareStatement("UPDATE listing SET listing.isActive =? where listing.isActive =? AND listing.listing_id =?;");
                            // update alll rentals affected by the deletion
                            updateRental = conn.prepareStatement("UPDATE rental INNER JOIN listing ON rental.listing_id = listing.listing_id  SET cancelled_By =?,cancelled_On = CURDATE() WHERE listing.isActive =? AND rental.end_date >= CURDATE();");
                            //update the calendar entries and mark them as un available
                            updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN listing ON calendar_entry.listing_id = listing.listing_id SET isAvailable = ? WHERE listing.isActive =? AND calendar_entry.date >= CURDATE();");
                            // fill parameters
                            updateListing.setBoolean(1,false);
                            updateListing.setBoolean(2,true);
                            updateListing.setInt(3,Integer.parseInt(listingID));
                            if(updateListing.executeUpdate() <= 0)
                                cleanDelete = false;
                            //deleted by host
                            updateRental.setInt(1,1);
                            updateRental.setBoolean(2,true);
                            //check if it executed sucessfully
                            if(updateRental.executeUpdate() <= 0)
                                cleanDelete = false;
                            updateCalendar.setBoolean(1,false);
                            updateCalendar.setBoolean(2,true);
                            //check if executed successfully
                            if(updateCalendar.executeUpdate() <= 0)
                                cleanDelete = false;



                            updateListing.close();
                            updateRental.close();
                            updateCalendar.close();
                            closeConnection();
                            // force close incase of errors
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

                    // Runs commericial
                    public static boolean commercialCheck(int userID, String country, String city) throws Exception{
                        PreparedStatement numListings = null;
                        ResultSet rs = null;
                        // Get the users who would violate the the commercial hosting policy by adding 1 more listing in a city
                        String sql = "select * from " +
                                "(select address.country,address.city,user_id, count(*) as numListing, s.totListing from listing " +
                                "INNER JOIN address on address.address_id=listing.address_id CROSS JOIN (select address.country,address.city,count(*) as totListing from listing " +
                                "INNER JOIN address on address.address_id=listing.address_id GROUP BY address.country,address.city)s ON s.country=address.country AND s.city=address.city " +
                                "WHERE user_id=? and isActive=? and address.country=? and address.city =? GROUP BY user_id,address.country,address.city)t " +
                                "WHERE t.numListing+1 > 0.1*t.totListing and t.numListing+1 > 5;";



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
                    //retrieve a list of all of the hosts active listings
                    public static ArrayList<String> getHostListings(String userID) throws Exception {
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT listing_id, type,address.city,address.country,address.pCode FROM listing " +
                                    "INNER JOIN address ON listing.address_id=address.address_id WHERE listing.user_id=? AND listing.isActive =?;");
                            listings.setInt(1,Integer.parseInt(userID));
                            listings.setBoolean(2,true);
                            rs = listings.executeQuery();
                            while (rs.next()) {


                                list.add(String.format("%d:%s:%s:%s:%s", rs.getInt("listing_id"),
                                        rs.getString("type"), rs.getString("country"), rs.getString("city"), rs.getString("pCode")));
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
                    //Return a list of allof a renters Active bookings
                    public static ArrayList<String> getRenterBookings(String userID) throws Exception {
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT start_date, end_date, rental_id,listing.listing_id,address.city,address.country,address.pCode FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id WHERE rental.user_id=? AND rental.end_date >= CURDATE() AND rental.cancelled_By=?;");
                            listings.setInt(1,Integer.parseInt(userID));
                            //Active bookings
                            listings.setInt(2,0);
                            rs = listings.executeQuery();
                            while (rs.next()) {
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                String start = df.format(rs.getDate("start_date"));
                                String end = df.format(rs.getDate("start_date"));
                                list.add(String.format("%d:%s:%s:%s:%s:%s:%s", rs.getInt("listing_id"),rs.getInt("rental_id")
                                        , rs.getString("country"), rs.getString("city"), rs.getString("pCode"),start,end));
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




                    //could be iffy
                    //Gets all characteristics of a listing
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

                                map.put("postalCode",rs.getString("pCode"));
                                map.put("country",rs.getString("country"));
                                map.put("city",rs.getString("city"));
                                map.put("longitude",rs.getBigDecimal("longitude"));
                                map.put("latitude",rs.getBigDecimal("latitude"));
                                map.put("mainPrice",rs.getBigDecimal("price"));
                                map.put("numberOfBedrooms",rs.getInt("num_bedrooms"));
                                map.put("type",rs.getString("type"));
                                //get a list of available ammenties in this listing
                                for (int i=9;i <39 ;i++){
                                    if (rs.getBoolean(i))
                                        list.add(rs.getMetaData().getColumnName(i));

                                }



                            }
                            // feed in the listing details
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

                    //look for hosts for properties rented in the last 30 days
                    public static ArrayList<String> getReviewableHosts(String userID) throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            //look for rentals completed in the last 30 days
                            listings = conn.prepareStatement("SELECT rental_id,listing.listing_id,listing.user_id FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id WHERE rental.user_id=?  and rental.cancelled_By=? "+
                                    " AND rental.end_date >= DATE_SUB(CURDATE(),INTERVAL 30 DAY) and rental.end_date<= CURDATE();");
                            listings.setInt(1,Integer.parseInt(userID));
                            // NOT CANCELLED
                            listings.setInt(2,0);

                            rs = listings.executeQuery();
                            //need to check return format
                            while (rs.next())
                                list.add(String.format("%d:%d:%d:",rs.getInt("rental_id")
                                        ,rs.getInt("listing.user_id"),rs.getInt("listing.listing_id")));


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
                    //Get a list of reviewable renters from all active/inactive properties
                    //where they rented in the last 30 days
                    public static ArrayList<String> getReviewableRenters(String userID) throws Exception{

                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT rental_id,listing.listing_id,rental.user_id FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.listing_id=address.address_id WHERE listing.user_id=? and rental.cancelled_By=? "+
                                    " AND rental.end_date >= DATE_SUB(CURDATE(),INTERVAL 30 DAY) and rental.end_date<= CURDATE();");
                            listings.setInt(1,Integer.parseInt(userID));
                            // NOT CANCELLED
                            listings.setInt(2,0);

                            rs = listings.executeQuery();
                            while (rs.next())
                                list.add(String.format("%d:%d:%d:",rs.getInt("rental_id")
                                        ,rs.getInt("rental.user_id"),rs.getInt("listing.listing_id")));


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

                    // Get a list of all active/inactive listings from which the user rented from in the last 30 days
                    public static ArrayList<String> getReviewableListings(String userID) throws Exception{
                        ArrayList<String> list = new ArrayList<String>();
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        try {
                            createConnection();
                            listings = conn.prepareStatement("SELECT rental.user_id,rental_id,listing.listing_id FROM rental " +
                                    "INNER JOIN listing ON listing.listing_id=rental.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id WHERE rental.user_id=? and rental.cancelled_By=? "+
                                    " AND rental.end_date >= DATE_SUB(CURDATE(),INTERVAL 30 DAY) and rental.end_date<= CURDATE();");
                            listings.setInt(1,Integer.parseInt(userID));
                            // NOT CANCELLED
                            listings.setInt(2,0);
                            rs = listings.executeQuery();
                            while (rs.next())
                                list.add(String.format("%d:%d:%d:",rs.getInt("rental_id")
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

                    //INSERT a review into the table
                    public static String createReview(String userID, Review review) throws Exception{
                        String retId ="";
                        PreparedStatement insReview= null;
                        ResultSet rs= null;

                        //Create review
                        String sql= "INSERT INTO review (#,renter_id,rating,description,type)  VALUES (?,?,?,?,?);";

                        try {
                            //determine the type of review
                            createConnection();
                            String colName ="";
                            int type = 0;
                            int reviewee =Integer.parseInt(review.getRevieweeID());
                            int reviewer = Integer.parseInt(userID);
                            if (review.getType().equals(Review.RevieweeType.HOST)) {
                                colName ="host_id";
                                type = 1;
                            }
                            else if (review.getType().equals(Review.RevieweeType.LISTING)) {
                                colName ="listing_id";
                                type = 2;

                            }else{
                                colName ="host_id";
                                reviewer =Integer.parseInt(review.getRevieweeID());
                                reviewee = Integer.parseInt(userID);
                                type = 3;
                            }
                            //update insert statement based on review type.
                            sql = sql.replace("#",colName);
                            insReview =conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                            insReview.setInt(1,reviewee);
                            insReview.setInt(2, reviewer);
                            insReview.setInt(3,review.getRating());
                            insReview.setString(4,review.getDescription());
                            insReview.setInt(5,type);
                            int complete = insReview.executeUpdate();
                            rs = insReview.getGeneratedKeys();
                            //get id of new insertion
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
                        //mark rental as cancelled
                        String sql= "UPDATE rental SET rental.cancelled_ON = CURDATE(),rental.cancelled_By=? WHERE rental.rental_id=?;";


                        try {

                            createConnection();

                            // free up calendar
                            updateCalendar = conn.prepareStatement("UPDATE calendar_entry  INNER JOIN rental ON calendar_entry.listing_id = rental.listing_id SET isAvailable = ? WHERE rental.rental_id=? AND calendar_entry.date>=rental.start_date  AND calendar_entry.date<=rental.end_date;");
                            updRental = conn.prepareStatement(sql);
                            int cancelled = 0;
                            if (isHost) {
                                cancelled = 1;
                            }else{
                                cancelled = 2;
                            }

                            //fill
                            updRental.setInt(1,cancelled);
                            updRental.setInt(2, Integer.parseInt(bookingID));
                            updateCalendar.setInt(2,Integer.parseInt(bookingID));
                            updateCalendar.setBoolean(1,true);
                            int complete = updRental.executeUpdate();
                            int completeCal = updateCalendar.executeUpdate();
                            // verify success
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
                                    " listing.address_id=address.address_id WHERE listing.user_id=? AND rental.end_date >= CURDATE() AND rental.cancelled_By=?;");
                            listings.setInt(1,Integer.parseInt(userID));
                            // NOT CANCELLED
                            listings.setInt(2,0);

                            rs = listings.executeQuery();


                            while (rs.next()) {
                                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                                String start = df.format(rs.getDate("start_date"));
                                String end = df.format(rs.getDate("start_date"));
                                list.add(String.format("%d:%d:%s:%s:%s:%s ", rs.getInt("listing_id")
                                        ,rs.getInt("rental_id"), rs.getString("country"), rs.getString("city"), start, end));

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
                    //creates a booking
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
                        //trys to see if selected listing is unvavailabel in the specified date range
                        String sql= "SELECT * FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? and calendar_entry.isAvailable =? and listing_id=? ;";
                        //get listings normal price
                        String priceSQL= "select listing.price from listing WHERE listing_id=?";
                        //mark all spots for the pecified listing an date range as booked
                        String updCal= "UPDATE calendar_entry SET  calendar_entry.isAvailable =? where calendar_entry.date >= ? AND calendar_entry.date<=? AND  listing_id=?;";
                        //Inset entrys into calendar for days not in the calendar
                        //with default price and booked
                        String insCal= "INSERT into calendar_entry (listing_id, isAvailable,price,date) select ?, ?,?, t.selected_date from (" +dateSQL + ")t where t.selected_date NOT IN (SELECT date as selected_date FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? AND calendar_entry.listing_id=?); ";
                        //create rental
                        String insRental = "INSERT INTO rental(user_id,listing_id,start_date,end_date,price) VALUES(?,?,?,?,?);";
                        //get the rentals total price over the range
                        String totalPrice = "Select SUM(price) from calendar_entry where date>=? and date<=? and listing_id=?";


                        try {

                            createConnection();
                            //fill
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
                                    //get regular price
                                    //update calendar
                                    BigDecimal price = rsPrice.getBigDecimal(1);
                                    updateCalendar = conn.prepareStatement(updCal);
                                    updateCalendar.setDate(2,rental.getStartDate());
                                    updateCalendar.setDate(3,rental.getEndDate());
                                    updateCalendar.setInt(4,Integer.parseInt(listingID));
                                    updateCalendar.setBoolean(1,false);
                                    int a = updateCalendar.executeUpdate();
                                        //insert into ca;emdar
                                        insertCalendar = conn.prepareStatement(insCal);
                                        insertCalendar.setInt(1,Integer.parseInt(listingID));
                                        insertCalendar.setBoolean(2,false);
                                        insertCalendar.setBigDecimal(3,price);
                                        insertCalendar.setDate(4,rental.getStartDate());
                                        insertCalendar.setDate(5,rental.getEndDate());
                                        insertCalendar.setDate(6,rental.getStartDate());
                                        insertCalendar.setDate(7,rental.getEndDate());
                                        insertCalendar.setInt(8,Integer.parseInt(listingID));
                                        int b = insertCalendar.executeUpdate();
                                            //get total price
                                            getPrice  = conn.prepareStatement(totalPrice);
                                            getPrice.setDate(1,rental.getStartDate());
                                            getPrice.setDate(2,rental.getEndDate());
                                            getPrice.setInt(3,Integer.parseInt(listingID));
                                            rsGetPrice = getPrice.executeQuery();
                                            if (rsGetPrice.next()){
                                                // insert missing dates into cal
                                                BigDecimal tPrice = rsGetPrice.getBigDecimal(1);
                                                insertRental = conn.prepareStatement(insRental,PreparedStatement.RETURN_GENERATED_KEYS);
                                                insertRental.setInt(1, Integer.parseInt(userID));
                                                insertRental.setInt(2,Integer.parseInt(listingID));
                                                insertRental.setDate(3,rental.getStartDate());
                                                insertRental.setDate(4,rental.getEndDate());
                                                insertRental.setBigDecimal(5,tPrice);
                                                int res = insertRental.executeUpdate();
                                                rsInsRental = insertRental.getGeneratedKeys();
                                                if (rsInsRental.next()){
                                                    //get rental id
                                                    retval = String.valueOf(rsInsRental.getInt(1));
                                                }
                                                rsInsRental.close();
                                                insertRental.close();


                                            }
                                            rsGetPrice.close();
                                            getPrice.close();
                                    insertCalendar.close();
                                    updateCalendar.close();






                                }
                                rsPrice.close();
                                listingPrice.close();

                            }



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

                    //Set listing price in a date range
                    public static String setListingPrice(String listingID, CalendarEntryRange calendarEntryRange) throws Exception{
                        String retval ="";
                        PreparedStatement conflicts= null;
                        PreparedStatement insertCalendar= null;
                        PreparedStatement updateCalendar= null;
                        ResultSet rs= null;

                        //make sure that listing is avail during that range
                        String sql= "SELECT * FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? and calendar_entry.isAvailable =? and listing_id=?;";
                        // change price in calendar
                        String updCal= "UPDATE calendar_entry SET  calendar_entry.price =? where calendar_entry.date >= ? AND calendar_entry.date<=? and listing_id=? ;";
                        //insert dates not in calendar
                        String insCal= "INSERT into calendar_entry (listing_id, isAvailable,price,date) select ? ,?,?, t.selected_date from (" +dateSQL + ")t where t.selected_date NOT IN (SELECT date as selected_date FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? AND calendar_entry.listing_id=?); ";
                        try {

                            createConnection();
                            //fill queries and execute
                            conflicts = conn.prepareStatement(sql);
                            conflicts.setDate(1,calendarEntryRange.getStartDate());
                            conflicts.setDate(2,calendarEntryRange.getEndDate());
                            conflicts.setBoolean(3,false);
                            conflicts.setInt(4,Integer.parseInt(listingID));
                            rs = conflicts.executeQuery();
                            // no conflicts
                            if (!rs.next()){

                                updateCalendar = conn.prepareStatement(updCal);
                                updateCalendar.setDate(2,calendarEntryRange.getStartDate());
                                updateCalendar.setDate(3,calendarEntryRange.getEndDate());
                                updateCalendar.setInt(4,Integer.parseInt(listingID));
                                updateCalendar.setBigDecimal(1,calendarEntryRange.getPrice());
                                //update
                                int a =updateCalendar.executeUpdate();

                                    //insert
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
                                    insertCalendar.close();


                                updateCalendar.close();

                            }



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
                        //makesure avail
                        String sql= "SELECT * FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? and calendar_entry.isAvailable =? and listing_id=?;";
                        //get regular price
                        String priceSQL= "select listing.price from listing WHERE listing_id=?";
                        // update cal values
                        String updCal= "UPDATE calendar_entry SET  calendar_entry.isAvailable =? where calendar_entry.date >= ? AND calendar_entry.date<=? and listing_id=?;";
                        // insert missing dates
                        String insCal= "INSERT into calendar_entry (listing_id, isAvailable,price,date) select ?, ?,?, t.selected_date from (" +dateSQL + ")t where t.selected_date NOT IN (SELECT date as selected_date FROM calendar_entry where calendar_entry.date >= ? AND calendar_entry.date<= ? AND calendar_entry.listing_id=?); ";


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
                                    int a =updateCalendar.executeUpdate();

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
                                        insertCalendar.close();


                                    updateCalendar.close();


                                }
                                rsPrice.close();
                                listingPrice.close();

                            }



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
                            // search by exact address
                            if (!filter.getCountry().equals("")) {
                                //select listin with addr
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
                                //get all active listings with details
                                String sql = "SELECT * FROM listing INNER JOIN (select pCode,city,country,address_id as addr_id from address)a ON a.addr_id=listing.address_id INNER JOIN (select latitude,longitude,location_id as loc_id from location)b ON b.loc_id=listing.location_id WHERE listing.isActive=TRUE";
                                // add filter for adjacent postal code
                                if (!filter.getPostalCode().equals("")) {
                                    String sub = filter.getPostalCode().substring(0, 3);
                                    sql = "SELECT * from (" + sql + ")c where pCode REGEXP '^" + sub + "|[0,9][a,Z][0-9]$' ";


                                }
                                //rank by price
                                if (filter.isSortByPrice()) {
                                    if (filter.isSortAscending()) {
                                        sql = "SELECT * FROM (" + sql + ")d ORDER BY price ASC";
                                    } else {
                                        sql = "SELECT * FROM (" + sql + ")d ORDER BY price DESC";
                                    }


                                }
                                // filter by price limits
                                if (!filter.getLowestPrice().toPlainString().equals("-1.0")) {
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



                                } else if(!filter.getHighestPrice().toPlainString().equals("-1.0")){
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
                                    //filter by availability
                                    if (filter.getFirstDate() != null) {
                                        calendarCheck = conn.prepareStatement("select calendar_entry.listing_id from calendar_entry where listing_id =? and isAvailable = ? and date>=? and date<=?; ");
                                        calendarCheck.setInt(1, listingID);
                                        calendarCheck.setBoolean(2, false);
                                        calendarCheck.setDate(3, filter.getFirstDate());
                                        calendarCheck.setDate(4, filter.getLastDate());
                                        rsCal = calendarCheck.executeQuery();
                                        if (rsCal.next())
                                            passCheck = false;
                                        rsCal.close();
                                        calendarCheck.close();

                                    }
                                    //filter by character istics
                                    if (passCheck && !(filter.getCharacteristics()==null)) {
                                        for (String s : filter.getCharacteristics()) {
                                            if (!rs.getBoolean(s)) {
                                                passCheck = false;
                                            }

                                        }
                                    }
                                    // get return string value
                                    String listing = String.format("%d:%s:%s:%s:%s:", listingID, rs.getString("Country"),
                                            rs.getString("City"), rs.getString("pCode"),
                                            rs.getBigDecimal("price").toPlainString());
                                    // if all filers worked

                                    //sort by distacne is enabled
                                    if (passCheck && !filter.isSortByPrice()) {
                                        double latitude = rs.getBigDecimal("latitude").doubleValue();
                                        double longitude = rs.getBigDecimal("longitude").doubleValue();
                                        // get dstance
                                        double calcDistance = distance(latitude, longitude, filter.getLatitude().doubleValue()
                                                , filter.getLongitude().doubleValue(), 'K');
                                        listing += ":" + String.valueOf(calcDistance) + " km";
                                        //store distance array
                                        distance.add(new Pair<Double,String>(calcDistance, listing));


                                    }else if (passCheck && filter.getMaxDistance().signum()==1){
                                        double latitude = rs.getBigDecimal("latitude").doubleValue();
                                        double longitude = rs.getBigDecimal("longitude").doubleValue();
                                        // get dstance
                                        double calcDistance = distance(latitude, longitude, filter.getLatitude().doubleValue()
                                                , filter.getLongitude().doubleValue(), 'K');
                                        if (calcDistance> filter.getMaxDistance().doubleValue())
                                        {
                                            passCheck = false;
                                        }else{
                                            listing += ":" + String.valueOf(calcDistance) + " km";
                                        }

                                    }


                                    if (passCheck) {
                                        list.add(listing);
                                    }


                                }


                            }

                            rs.close();
                            exactSearch.close();
                            closeConnection();
                            if (distance.size()==0){
                                //return normal list if dist is not enabled
                                return list;
                            }else{
                                //sort ascendig or descending
                                if (filter.isSortAscending()){
                                    distance.sort((o1, o2) -> ((Double)o1.getKey()).compareTo((Double)o2.getKey()));
                                }else{
                                    distance.sort((o1, o2) -> -((Double)o1.getKey()).compareTo((Double)o2.getKey()));

                                }
                                //preserve sort and retrieve user display strings
                                ArrayList<String> sortedList = new ArrayList<String>();
                                for (int i=0;i<distance.size() ;i++){
                                    String data = distance.get(i).getValue();
                                    sortedList.add(data);
                                }
                                //return
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

                    //calculate distance betwen 2 points
                    //haversine formula
                    //taken from:
                    private static double distance(double lat1, double lon1, double lat2, double lon2, char unit) {
                        double theta = lon1 - lon2;
                        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
                        dist = Math.acos(dist);
                        dist = rad2deg(dist);
                        dist = dist * 60 * 1.1515;
                        if (unit == 'K') {
                            dist = dist * 1.609344;
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



                    // Reporting Begins

                    //checked
                    public static int reportRentalsByDate(java.sql.Date startDate, java.sql.Date endDate,boolean postal) throws Exception{
                        PreparedStatement numListings = null;
                        ArrayList<String> list = new ArrayList<String>();
                        String filename = "numberOfRentalsDateCITY";

                        ResultSet rs = null;
                        //query to get number of bookings in a specific date range by city
                        String sql = "SELECT address.city, COUNT(*) as numRentals FROM rental INNER JOIN listing ON" +
                                " rental.listing_id=listing.listing_id INNER JOIN address ON listing.address_id=address.address_id" +
                                " where rental.start_date >= ? and rental.start_date <=? GROUP BY address.city;";


                        if (postal) {
                            //query to get number of bookings in a specific date range by postal code per city
                             filename = "numberOfRentalsDateCITYPOSTAL";

                            sql = "SELECT address.city, address.pCode, COUNT(*) as numRentals FROM rental INNER JOIN listing ON" +
                                    " rental.listing_id=listing.listing_id INNER JOIN address ON" +
                                    " listing.address_id=address.address_id where rental.start_date >= ? " +
                                    "and rental.start_date <=? GROUP BY address.city, address.pCode;";
                        }
                        //Create File
                        filename = FileWriter.createFile(filename);
                        int result = -1;
                        try {
                            createConnection();
                            //fill query
                            numListings = conn.prepareStatement(sql);
                            numListings.setDate(1,startDate);
                            numListings.setDate(2,endDate);
                            //execute query
                            rs = numListings.executeQuery();
                            String header = "";
                            //Retrieve Column names for report
                            if (postal){
                                header = String.format("%s:%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2) ,rs.getMetaData().getColumnName(3));

                            }else {
                                header = String.format("%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2));
                            }
                            list.add(header);
                            String data = "";
                            while (rs.next()){
                                //select method of storing data
                                //dependant on query used
                                if (postal){
                                    data = String.format("%s:%s:%d", rs.getString(1) ,rs.getString(2) ,rs.getInt(3));
                                    list.add(data);
                                }else {
                                    data = String.format("%s:%d", rs.getString(1) ,rs.getInt(2));
                                    list.add(data);
                                }

                            }
                            //Write results
                            FileWriter.writeToFile(list,filename);

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


                    //this method takes in a int with values(1,2,3) to indicate report type
                    //number of listings per country/City
                    //cheeckd
                    //Double check needed
                    public static int reportListings(int type) throws Exception{
                        PreparedStatement numListings = null;
                        ArrayList<String> list = new ArrayList<String>();
                        String filename = "numberOfListingsCountry";
                        ResultSet rs = null;
                        String sql = "SELECT address.country,COUNT(*) as numListing FROM listing INNER JOIN address ON" +
                                " listing.address_id=address.address_id where listing.isActive=TRUE" +
                                "  GROUP BY address.country;";
                        int result = -1;
                        if (type==2) {


                            filename = "numberOfListingsCountryCity";
                            sql = "SELECT address.country,address.city, COUNT(*) as numListing FROM listing INNER JOIN address ON" +
                                    " listing.address_id=address.address_id where listing.isActive=TRUE" +
                                    "  GROUP BY address.country,address.city;";
                        }
                        if (type==3) {
                            filename = "numberOfListingsCountryCityPostal";

                        sql = "SELECT address.country,address.city,address.pCode, COUNT(*) as numListing FROM listing" +
                                    " INNER JOIN address ON listing.address_id=address.address_id where listing.isActive=TRUE " +
                                    "  GROUP BY address.country,address.city,address.pCode;";
                        }
                        filename = FileWriter.createFile(filename);

                        try {
                            createConnection();
                            numListings = conn.prepareStatement(sql);
                            rs = numListings.executeQuery();

                            String header = String.format("%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2)) ;
                            //Retrieve Column names for report
                            if (type==2){
                                header = String.format("%s:%s:%s", rs.getMetaData().getColumnName(1) ,
                                        rs.getMetaData().getColumnName(2) ,rs.getMetaData().getColumnName(3));

                            }else if (type == 3){
                                header = String.format("%s:%s:%s:%s", rs.getMetaData().getColumnName(1),
                                        rs.getMetaData().getColumnName(2),rs.getMetaData().getColumnName(3),
                                        rs.getMetaData().getColumnName(4));
                            }
                            list.add(header);
                            String data = "";
                            while (rs.next()){
                                //select method of storing data
                                //dependant on query used
                                if (type == 1){
                                    data = String.format("%s:%d", rs.getString(1) ,rs.getInt(2));
                                    list.add(data);
                                }else if (type ==2){
                                    data = String.format("%s:%s:%d", rs.getString(1) ,rs.getString(2),rs.getInt(3));
                                    list.add(data);
                                }else if(type==3){
                                    data = String.format("%s:%s:%s:%d", rs.getString(1) ,rs.getString(2),rs.getString(3),rs.getInt(4));
                                    list.add(data);

                                }


                            }
                            //Write results
                            FileWriter.writeToFile(list,filename);

                            rs.close();
                            numListings.close();
                            closeConnection();

                        } catch (Exception e) {
                            throw e;
                        } finally{
                            //force close potentially open streams
                            try{
                                rs.close();
                                numListings.close();
                            }catch (Exception e){
                                int a =0;
                            }
                        }
                        return result;
                    }


                    //This method generates a report which Ranks hosts based on number of listings per Country/City
                    //Checked
                    //Double Check NEEEDED
                    public static int rankHosts(boolean city) throws Exception{
                        PreparedStatement numListings = null;
                        ResultSet rs = null;
                        ArrayList<String> list = new ArrayList<String>();
                        String filename = "numberOfListingsCountry";

                        String sql = "select * from (select address.country,user_id, count(*) as numListing from listing" +
                                " INNER JOIN address on address.address_id=listing.address_id where listing.isActive=TRUE GROUP BY user_id,address.country)t" +
                                " GROUP BY t.country ASC,numListing DESC,t.user_id;";
                        if (city) {
                            filename = "numberOfListingsCountryCity";
                            sql = "select * from (select address.country,address.city,user_id, count(*) as numListing from listing" +
                                    " INNER JOIN address on address.address_id=listing.address_id where listing.isActive=TRUE GROUP BY user_id,address.country" +
                                    ",address.city)t GROUP BY t.country ASC,t.city ASC,numListing DESC,t.user_id;";
                        }
                        int result = -1;
                        //Create unique file name
                        filename =  FileWriter.createFile(filename);

                        try {
                            createConnection();
                            numListings = conn.prepareStatement(sql);
                            rs = numListings.executeQuery();
                            String header = "";
                            //Retrieve Column names for report
                            if (city){
                                header = String.format("%s:%s:%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2) ,rs.getMetaData().getColumnName(3),rs.getMetaData().getColumnName(4));

                            }else {
                                header = String.format("%s:%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2),rs.getMetaData().getColumnName(3));
                            }
                            list.add(header);
                            String data = "";
                            while (rs.next()){
                                //select method of storing data
                                //dependant on query used
                                if (city){
                                    data = String.format("%s:%s:%d:%d", rs.getString(1) ,rs.getString(2) ,rs.getInt(3),rs.getInt(4));
                                    list.add(data);
                                }else {
                                    data = String.format("%s:%d:%d", rs.getString(1) ,rs.getInt(2),rs.getInt(3));
                                    list.add(data);
                                }

                            }
                            //Write results
                            FileWriter.writeToFile(list,filename);


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


                    //needs to b checked with updated db
                    // Check for commercial hosts

                    public static int commercialHosts() throws Exception{
                        PreparedStatement numListings = null;
                        ArrayList<String> list = new ArrayList<String>();
                        String filename = FileWriter.createFile("commercialHosts");
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
                            String header = String.format("%s:%s:%s:%s", rs.getMetaData().getColumnName(1)
                                    ,rs.getMetaData().getColumnName(2) ,rs.getMetaData().getColumnName(3),rs.getMetaData().getColumnName(4));

                            list.add(header);
                            String data = "";
                            while (rs.next()){
                                //select method of storing data
                                //dependant on query used

                                data = String.format("%s:%s:%d:%d", rs.getString(1) ,rs.getString(2),rs.getInt(3) ,rs.getInt(4));
                                list.add(data);

                            }
                            //Write results
                            FileWriter.writeToFile(list,filename);
                            // close all open streams and connections
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

                    //V erify query
                    // This report ranks renters by number of rentals in a time frame and possibly per city
                    public static int reportRentalRanksByDate(java.sql.Date startDate, java.sql.Date endDate,boolean city) throws Exception {
                        PreparedStatement numListings = null;
                        ResultSet rs = null;
                        ArrayList<String> list = new ArrayList<String>();
                        String filename ="rentalsInPeriod";
                        String sql = "select * from (select user_id, count(*) as numRental from rental WHERE rental.cancelled_By=0 and rental.start_date >=? and start_date<=?  group by user_id)t group by" +
                                " t.numRental DESC,t.user_id ;";
                        if (city) {
                            filename = "rentalsInPeriodPerCity";
                            sql = "select * from (select address.city,rental.user_id, count(*) as numRental from rental INNER JOIN" +
                                    " listing ON rental.listing_id=listing.listing_id INNER JOIN address on address.address_id =" +
                                    " listing.address_id WHERE rental.cancelled_By = 0 and rental.start_date >=? and start_date<=?  group by rental.user_id,address.city)t where t.numRental > 1 group by" +
                                    " t.city ASC, t.numRental DESC,t.user_id;";
                        }
                        filename = FileWriter.createFile(filename);
                        int result = -1;
                        try {
                            createConnection();
                            numListings = conn.prepareStatement(sql);
                            numListings.setDate(1,startDate);
                            numListings.setDate(2,endDate);
                            rs = numListings.executeQuery();
                            String header = "";
                            //Retrieve Column names for report
                            if (city){
                                header = String.format("%s:%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2) ,rs.getMetaData().getColumnName(3));

                            }else {
                                header = String.format("%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2));
                            }
                            list.add(header);
                            String data = "";
                            while (rs.next()){
                                //select method of storing data
                                //dependant on query used
                                if (city){
                                    data = String.format("%s:%d:%d", rs.getString(1) ,rs.getInt(2) ,rs.getInt(3));
                                    list.add(data);
                                }else {
                                    data = String.format("%d:%d", rs.getInt(1) ,rs.getInt(2));
                                    list.add(data);
                                }

                            }
                            //Write results
                            FileWriter.writeToFile(list,filename);

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
                    // This report lists hosts/renters wit the most cancellations in the last year
                    //Chck
                    public static int reportCancelledRentals(boolean host) throws Exception {
                        PreparedStatement numListings = null;
                        ResultSet rs = null;
                        ArrayList<String> list = new ArrayList<String>();
                        String filename ="rentalsCancelledByRenters";
                        String sql = "select * from (select user_id, count(*) as numRental from rental " +
                                "WHERE cancelled_By=2 AND  cancelled_On>= DATE_SUB(CURDATE(), " +
                                "INTERVAL 1 YEAR) AND cancelled_On <= CURDATE() GROUP BY " +
                                "user_id)t group by t.numRental DESC,t.user_id;";
                        if (host) {
                            filename = "rentalsCancelledByHosts";
                            sql = "select * from (select listing.user_id, count(*) as numRental from rental" +
                                    " INNER JOIN listing ON rental.listing_id =listing.listing_id WHERE cancelled_By=1 AND" +
                                    "  cancelled_On>= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) AND cancelled_On <= CURDATE()" +
                                    " group by user_id)t  group by t.numRental DESC,user_id;";
                        }
                        filename = FileWriter.createFile(filename);
                        int result = -1;
                        try {
                            createConnection();
                            numListings = conn.prepareStatement(sql);

                            rs = numListings.executeQuery();

                            String header = "";
                            //Retrieve Column names for report
                            header = String.format("%s:%s", rs.getMetaData().getColumnName(1) ,rs.getMetaData().getColumnName(2));
                            list.add(header);
                            String data = "";
                            while (rs.next()){
                                //select method of storing data
                                //dependant on query used

                                data = String.format("%d:%d", rs.getInt(1) ,rs.getInt(2));
                                list.add(data);


                            }
                            //Write results
                            FileWriter.writeToFile(list,filename);

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

                    public static int reportNounPhrases() throws Exception {
                        PreparedStatement listings = null;
                        PreparedStatement text = null;
                        ResultSet rs = null;
                        ResultSet rsText = null;
                        String sql = "select listing_id from listing  ";
                        String textSQL = "SELECT description from listing INNER JOIN review on listing.listing_id=review.listing_id where review.type=2 and review.listing_id=?; ";
                        int result = -1;
                        try {
                            createConnection();
                            listings = conn.prepareStatement(sql);
                            text = conn.prepareStatement(textSQL);

                            rs = listings.executeQuery();
                            ArrayList<String> fin = new ArrayList<String>();

                            while (rs.next()) {
                                ArrayList<String> inputList = new ArrayList<String>();
                                ArrayList<String> outputList = new ArrayList<String>();
                                result = rs.getInt(1);
                                text.setInt(1, result);
                                rsText = text.executeQuery();
                                while (rsText.next()) {
                                    String description = rsText.getString(1);
                                    //for (String s: description.split(". ")) {
                                    inputList.add(description);
                                    //}
                                }
                                outputList= NounPhraseParser.parseNouns(inputList);

                                outputList.add(0,String.valueOf(result)+"(ID)- Popular Noun phrases");
                                for(String s : outputList){
                                    fin.add(s);
                                }

                            }
                            String fileName = FileWriter.createFile("reportNounPhrases");
                            FileWriter.writeToFile(fin,fileName);


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


                    //HOST TOOL KIT

                    //This function attempts to reasonably satisfy the requirements from the host toolkit
                    public static double getRecomendedPrice(String type,int numAmmenities) throws Exception{
                        //how This function works is:
                        // given the number of ammenties the host has provided and the type of listing
                        //this program takes all listings of similar type and sums up the "regular" prices of the listings
                        //with +- 2 ammenities and averages them out.
                        //The program wont return a suggested price if there isnt sufficient data in the database
                        PreparedStatement listings = null;
                        ResultSet rs = null;
                        int validMatches =0;
                        double totalPrice = 0;
                        try{
                            createConnection();
                            listings = conn.prepareStatement("Select * from listing where type =?;");
                            listings.setString(1,type);
                            rs = listings.executeQuery();

                            while(rs.next()){
                                int count = 0;
                                // we know the order of columns hence we can loop over fixed columns
                                for (int i=9;i <39 ;i++) {
                                    if (rs.getBoolean(i))
                                        count++;
                                }
                                // allow for similar listings to be inclded in price calculation
                                if(count>= numAmmenities - 2 && count<=numAmmenities + 2){
                                    validMatches++;
                                    totalPrice += rs.getBigDecimal("price").doubleValue();
                                }

                            }
                            rs.close();
                            listings.close();
                            closeConnection();
                            //force close all streams and connections
                        } catch (Exception e) {
                             throw e;
                        }finally{
                            try{

                                rs.close();
                                listings.close();
                            }catch (Exception e){
                                int a =0;
                            }
                        }
                        if (validMatches>0){
                            // return average price
                            return totalPrice/validMatches;
                        }
                        return totalPrice;
                    }








                }
