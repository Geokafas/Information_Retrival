package GUI;

public class Store {
    private String name;
    private String review;
    private String tip;
    private int stars;


    public Store(String name, String review, String tip, int stars){
        this.name = name;
        this.review = review;
        this.tip = tip;
        this.stars = stars;
    }

    public String getStoreName(){
        return name;
    }

    public String getStoreReview(){
        return review;
    }
    public String getStoreTip(){
        return tip;
    }
    public int getStoreStars(){
        return stars;
    }
}
