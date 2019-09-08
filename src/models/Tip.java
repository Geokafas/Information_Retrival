package models;

import java.util.ArrayList;

public class Tip {
    private String text;
    private long tip_id;
    private String business_id;
    private String date;

    public Tip(String text, long tip_id, String business_id, String date){
        this.text = text;
        this.tip_id = tip_id;
        this.business_id = business_id;
        this.date = date;
    }

    public String getTipText(){
        return text;
    }

    public long getTip_id(){
        return tip_id;
    }

    public String getBusiness_id(){
        return business_id;
    }

    public String getDate(){
        return date;
    }
}
