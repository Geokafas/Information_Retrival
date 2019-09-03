package models;

import java.util.ArrayList;

public class Review {
    private String text;
    private long review_id;
    private String business_id;
    private int starts;

    public Review(String text, long review_id, String business_id, int starts){
        this.text = text;
        this.review_id = review_id;
        this.business_id = business_id;
        this.starts = starts;
    }

    public String getReviewText(){
        return text;
    }

    public long getReview_id(){
        return review_id;
    }

    public String getBusiness_id(){
        return business_id;
    }

    public int getStarts(){
        return starts;
    }
}
