package app.handlers;

import app.modules.Asker;
import app.modules.Decider;
import app.objects.CreditCard;
import app.objects.Info;
import app.objects.User;
import app.DBTalker;
import app.Terminal;
import app.Validators;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public class UserHandler extends BaseHandler {

    private String userID = "";
    private String userName = "";
    private boolean isHost;

    public String getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isHost() {
        return isHost;
    }

    public UserHandler(Decider decider, Asker asker, String userID, Scanner sc, Terminal tm) {
        super(decider, asker, userID, sc, tm);
    }

    public void tryToLogin(){
        decider.add("Register", this::register);
        decider.add("Login", this::login);
        decider.add("Reports", tm.getReportsHandler()::startReports);
        decider.displayOptions();
    }

    public void register() {
        asker.add(new Info("name", "Name:", Info.DataType.STRING));
        asker.add(new Info("occupation", "Occupation:", Info.DataType.STRING));
        asker.add(new Info("DOB", "Date of Birth (yyyy-mm-dd):", Info.DataType.DATE, Validators.ValidatorKeys.IS_ADULT));
        asker.add(new Info("SIN", "Social Insurance Number:", Info.DataType.STRING, Validators.ValidatorKeys.ALL_NUMBERS));
        asker.add(new Info("country", "Country:", Info.DataType.STRING));
        asker.add(new Info("city", "City:", Info.DataType.STRING));
        asker.add(new Info("postalCode", "Postal Code:", Info.DataType.STRING, Validators.ValidatorKeys.POSTAL_CODE));
        asker.add(new Info("isRenter", "Will you be a renter? (y/n):", Info.DataType.BOOLEAN));
        User user = new User(asker.askQuestions());

        if (user.isRenter()) {
            asker.add(new Info("number", "Credit Card Number:", Info.DataType.STRING, Validators.ValidatorKeys.ALL_NUMBERS));
            asker.add(new Info("expiryDate", "Credit Card Expiry Date: (mm/yyyy)", Info.DataType.STRING, Validators.ValidatorKeys.CC_EXPIRY));
            asker.add(new Info("CCV", "Credit Card CCV:", Info.DataType.STRING));
            asker.add(new Info("holderName", "Credit Card Holder Name:", Info.DataType.STRING));
            user.setCreditCard(new CreditCard(asker.askQuestions()));
        }

        String response = DBTalker.createNewUser(user);
        if (response != null){
            isHost = !user.isRenter();
            userID = response;
            userName = user.getName();
            System.out.println("Welcome, " + userName);
        }
        tm.startSession();
    }

    public void login(){
        decider.add("login as host");
        decider.add("login as renter");
        String type = decider.displayOptions();
        showUsers(type);
    }

    public void showUsers(String type){
        isHost = type.equals("login as host");
        ArrayList<String> users = isHost ? DBTalker.getAllHostsNamesAndIDs() : DBTalker.getAllRentersNamesAndIDs();

        System.out.println("Who are you?");

        if(!users.isEmpty()) {
            decider.setOptions(users);
            String selected = decider.displayOptions();
            String[] parts = selected.split(":");

            userName = parts[0];
            userID = parts[1];
            tm.setUserID(userID);
            System.out.println("Welcome back, " + userName);
        } else{
            Terminal.printNotFound("users");
            tm.startSession();
        }
    }

    public void deleteUserConfirmation(){
        decider.add("Permanently delete " + userName + "'s account", this::deleteUser);
        decider.add("No, I've changed my mind!");
        decider.displayOptions();
    }

    public void deleteUser(){
        DBTalker.deleteUser(userID);
        logout();
    }

    public void logout(){
        System.out.println("Goodbye, " + userName);
        userID = "";
        userName = "";
        tm.setUserID("");
        tm.startSession();
    }

    public static String getUserIDFromText(String userText){
        return userText.split(":")[1];
    }


}
