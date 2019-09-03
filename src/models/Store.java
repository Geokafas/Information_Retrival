package models;

public class Store {
    private String name;
    private int stars;
    private String business_id;
    private String categories;
    private int review_count;


    public Store(String name, String business_id, int stars, String categories,int review_count){
        this.name = name;
        this.stars = stars;
        this.business_id = business_id;
        this.categories = categories;
        this.review_count = review_count;
    }

    public Store(String name, String highText){
        this.name = name;
    }

    public String getStoreName(){
        return name;
    }

    public int getStoreStars(){
        return stars;
    }

    public String getBusiness_id(){
        return  business_id;
    }

    public String getCategories(){
        return categories;
    }

    public int getReview_count(){
        return review_count;
    }
}
