package app.handlers;

import app.Terminal;
import app.modules.Asker;
import app.modules.Decider;

import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public abstract class BaseHandler {

    Decider decider;
    Asker asker;
    String userID;
    Scanner sc;
    Terminal tm;

    BaseHandler(Decider decider, Asker asker, String userID, Scanner sc, Terminal tm) {
        this.decider = decider;
        this.asker = asker;
        this.userID = userID;
        this.sc = sc;
        this.tm = tm;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
