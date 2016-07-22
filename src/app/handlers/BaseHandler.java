package app.handlers;

import app.modules.Asker;
import app.modules.Decider;

import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public abstract class BaseHandler {

    protected Decider decider;
    protected Asker asker;
    protected String userID;
    protected Scanner sc;

    public BaseHandler(Decider decider, Asker asker, String userID, Scanner sc) {
        this.decider = decider;
        this.asker = asker;
        this.userID = userID;
        this.sc = sc;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
