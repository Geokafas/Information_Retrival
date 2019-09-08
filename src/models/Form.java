package models;

import java.util.ArrayList;

public class Form {
    private ArrayList<Review> reviews;
    private ArrayList<Tip>  tips;
    private Store store;
    private String boldText;

    public Form(Store st, String boldText, ArrayList<Review> reviews){
        this.store = st;
        this.boldText = boldText;
        this.reviews = reviews;
    }

    public Form(Store st, ArrayList<Review> reviews, ArrayList<Tip> tips){
        this.store = st;
        this.tips = tips;
        this.reviews = reviews;
    }

    public ArrayList<Review> getReviews(){
        return reviews;
    }

    public ArrayList<Tip> getTips(){
        return tips;
    }

    public Store  getStore(){
        return store;
    }
    public String getBoldText(){
        return boldText;
    }
}
