package models;

public class Form {
    private Review reviews;
    private Tip tips;
    private Store store;
    private String boldText;

    public Form(Store st, String boldText){
        this.store = st;
        this.boldText = boldText;
    }

    public Review getReviews(){
        return reviews;
    }

    public Tip getTips(){
        return tips;
    }

    public Store  getStore(){
        return store;
    }
    public String getBoldText(){
        return boldText;
    }
}
