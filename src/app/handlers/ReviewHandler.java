package app.handlers;

import app.modules.Asker;
import app.modules.Decider;
import app.objects.Review;
import app.DBTalker;
import app.Terminal;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Daniel on 2016-07-22.
 */
public class ReviewHandler extends BaseHandler {

    public ReviewHandler(Decider decider, Asker asker, String userID, Scanner sc, Terminal tm) {
        super(decider, asker, userID, sc, tm);
    }

    public void reviewHost(){
        System.out.println("Select a Host:");
        ArrayList<String> reviewees = DBTalker.getReviewableHosts(userID);
        if(!reviewees.isEmpty()){
            decider.setOptions(reviewees);
            String selectedRevieweeID = UserHandler.getUserIDFromText(decider.displayOptions());
            createReview(selectedRevieweeID, Review.RevieweeType.HOST);
        }else
            Terminal.printNotFound("reviewable hosts");
    }

    public void reviewRenter(){
        System.out.println("Select a Renter:");
        ArrayList<String> reviewees = DBTalker.getReviewableRenters(userID);
        if(!reviewees.isEmpty()){
            decider.setOptions(reviewees);
            String selectedRevieweeID = UserHandler.getUserIDFromText(decider.displayOptions());
            createReview(selectedRevieweeID, Review.RevieweeType.RENTER);
        }else
            Terminal.printNotFound("reviewable renters");
    }

    public void reviewListing(){
        System.out.println("Select a Listing:");
        ArrayList<String> reviewees = DBTalker.getReviewableListings(userID);
        if(!reviewees.isEmpty()){
            decider.setOptions(reviewees);
            String selectedRevieweeID = UserHandler.getUserIDFromText(decider.displayOptions());
            createReview(selectedRevieweeID, Review.RevieweeType.LISTING);
        }else
            Terminal.printNotFound("reviewable listings");
    }

    private void createReview(String revieweeID, Review.RevieweeType type){
        System.out.println("Description:");
        String description = sc.nextLine();

        System.out.println("Rating:");
        decider.add("1 star");
        decider.add("2 stars");
        decider.add("3 stars");
        decider.add("4 stars");
        decider.add("5 stars");
        int rating = Integer.parseInt(decider.displayOptions());

        Review review = new Review(description, revieweeID, rating, type);
        DBTalker.createReview(userID, review);
    }
}
