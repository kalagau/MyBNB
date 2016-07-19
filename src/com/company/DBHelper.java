package com.company;

import java.util.ArrayList;

/**
 * Created by Daniel on 2016-07-16.
 */
public class DBHelper {

    public static String createNewUser(User user){
        return "abc";
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
