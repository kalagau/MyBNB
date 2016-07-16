package com.company;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-16.
 */
public class Terminal {

    Scanner sc;

    private String userID = "";
    private String userName = "";
    private boolean isRenter;

    public Terminal(){
        sc = new Scanner(System.in);
        startSession();
    }

    private void startSession(){
        if(userID.isEmpty())
            tryToLogin();

        String input = "";
        do {

        } while(!input.equals("exit"));
    }

    private void tryToLogin(){
        System.out.println("(r)egister or (l)ogin?");
        String input = sc.next();

        if (input.equals("r"))
            register();
        else if(input.equals("l"))
            login();
        else {
            System.out.println("invalid input!");
            tryToLogin();
        }
    }

    private void register() {
        System.out.println("Name:");
        String name = sc.next();

        System.out.println("Occupation:");
        String occupation = sc.next();

        System.out.println("Date of Birth:");
        String DOB = sc.next();

        System.out.println("SIN:");
        String SIN = sc.next();

        System.out.println("Will you be a host? (y/n):");
        boolean isRenter = !sc.next().equals("y");

        User user = new User(name, occupation, DOB, SIN, isRenter);

        if (isRenter) {
            System.out.println("Credit Card Number:");
            String number = sc.next();

            System.out.println("Credit Card Expiry Date:");
            String expiryDate = sc.next();

            System.out.println("Credit Card CCV:");
            String CCV = sc.next();

            System.out.println("Credit Card Holder Name:");
            String holderName = sc.next();

            user.setCreditCard(new CreditCard(number, expiryDate, CCV, holderName));
        }

        String response = DBHelpers.createNewUser(user);
        if (!response.equals("error")){
            userID = response;
            userName = name;
            System.out.println("Welcome, " + userName);
        }
    }

    private void login(){
        int count = 1;
        ArrayList<String> hosts = DBHelpers.getAllHostsNamesAndIDs();
        ArrayList<String> renters = DBHelpers.getAllRentersNamesAndIDs();

        System.out.println("Hosts:");
        for(String host : hosts)
            System.out.println("(" + count++ + ")" + host);

        System.out.println("Renters:");
        for(String renter : renters)
            System.out.println("(" + count++ + ") " + renter);

        int input = -1;
        try {
             input = Integer.parseInt(sc.next()) - 1;
        }catch (NumberFormatException e){}

        String[] parts = null;
        if(input >= 0 && input < hosts.size())
            parts = hosts.get(input).split(":");
        else if(input < renters.size() + hosts.size())
            parts = renters.get(input - hosts.size()).split(":");

        if(parts != null) {
            userName = parts[0];
            userID = parts[1];
            System.out.println("Welcome back, " + userName);
        }
    }
}
