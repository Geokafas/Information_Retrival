package core;

import models.Review;
import models.Store;
import models.Tip;

import java.io.IOException;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Reads data from XAMPP and uses lucene to index them
 * access through XAMPP localhost
 * so XAMPP must be running
 */

public class dataBaseReader{
    private static Connection connect = null;
    private static Statement statement1;
    private static Statement statement2;
    private static Statement statement3;

    private static ResultSet resultSet1;
    private static ResultSet resultSet2;
    private static ResultSet resultSet3;
    //connection related
    final private static String host = "localhost";
    final private static String user = "root";
    final private static String passwd = "";
    private static luceneIndexer indexer;
    private static ArrayList<Store> businessTolucene;
    private static ArrayList<Review> reviewsTolucene;
    private static ArrayList<Tip> tipsTolucene;

    /**
     * Used to populate the 3 arrays that database fetchers returns
     */
    public dataBaseReader() {
        resultSet1 = null;
        resultSet2 = null;
        resultSet3 = null;
        statement1 = null;
        statement2 = null;
        statement3 = null;

        reviewsTolucene = new ArrayList<>();
        tipsTolucene = new ArrayList<>();
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://" + host + "/yelp_reviews?"
                            + "user=" + user + "&password=" + passwd );
            System.out.println("READY.. .. ..");

            // Statements allow to issue SQL queries to the database

            statement1 = connect.createStatement();

            resultSet1 = statement1.executeQuery("select * from business");
            getBusinessDetails(resultSet1);
            //System.out.println(array.size());

            for(Store biz : businessTolucene){

                String id = biz.getBusiness_id();
                System.out.println("biz id: " + id);
                //clean arrays before assigning. Because the
                // same array is used to send data for each different business to lucene
                reviewsTolucene.clear();
                tipsTolucene.clear();

                //System.out.println(id);
                statement2 = connect.createStatement();
                resultSet2 = statement2.executeQuery("select * from `reviews` WHERE `business_id` = "+ "'" + id + "'");
                long review_counter = 0;
                while(resultSet2.next()){
                    reviewsTolucene.add( new Review(resultSet2.getString("review_text"),
                            review_counter,
                            resultSet2.getString("business_id"),
                            Integer.parseInt(resultSet2.getString("stars"))));
                }
                System.out.println("reviews: \n" + reviewsTolucene);
                statement3 = connect.createStatement();
                resultSet3 = statement3.executeQuery("select * from `tips` WHERE `business_id` = "+ "'"+id+"'");
                long tip_counter = 0;
                while(resultSet3.next()){
                    tipsTolucene.add( new Tip(resultSet3.getString("tip_text"),
                            tip_counter,
                            resultSet3.getString("business_id"),
                            resultSet3.getString("date")));
                }
            }
            //There was a problem connecting to the database
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void getBusinessDetails(ResultSet resultSet)  throws java.sql.SQLException{
        businessTolucene = new ArrayList<>();
        while(resultSet.next()){
            businessTolucene.add( new Store(resultSet1.getString("name"),
                            resultSet1.getString("business_id"),
                            Integer.parseInt(resultSet1.getString("stars")),
                            resultSet1.getString("categories"),
                    Integer.parseInt(resultSet1.getString("review_count"))));
        }

    }


    public static void readDataBase() throws NullPointerException, IOException {
        resultSet1 = null;
        resultSet2 = null;
        resultSet3 = null;
        statement1 = null;
        statement2 = null;
        statement3 = null;

        reviewsTolucene = new ArrayList<>();
        tipsTolucene = new ArrayList<>();

        indexer = new luceneIndexer();
        indexer.initialize();
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://" + host + "/yelp_reviews?"
                            + "user=" + user + "&password=" + passwd );
            System.out.println("READY.. .. ..");

            // Statements allow to issue SQL queries to the database

            statement1 = connect.createStatement();

            resultSet1 = statement1.executeQuery("select * from business");
            getBusinessDetails(resultSet1);
            //System.out.println(array.size());
            long review_counter = 0;
            long tip_counter = 0;
            for(Store biz : businessTolucene){

               String id = biz.getBusiness_id();
                //System.out.println("biz id: " + id);
               //clean arrays before assigning. Because the
               // same array is used to send data for each different business to lucene
               reviewsTolucene.clear();
               tipsTolucene.clear();

               statement2 = connect.createStatement();
               resultSet2 = statement2.executeQuery("select * from `reviews` WHERE `business_id` = "+ "'" + id + "'");
               while(resultSet2.next()){
                   reviewsTolucene.add( new Review(resultSet2.getString("review_text"),
                           review_counter,
                           resultSet2.getString("business_id"),
                           Integer.parseInt(resultSet2.getString("stars"))));
               }
               //System.out.println("reviews: \n" + reviewsTolucene);
               statement3 = connect.createStatement();
               resultSet3 = statement3.executeQuery("select * from `tips` WHERE `business_id` = "+ "'"+id+"'");
               while(resultSet3.next()){
                   tipsTolucene.add( new Tip(resultSet3.getString("tip_text"),
                           tip_counter,
                           resultSet3.getString("business_id"),
                           resultSet3.getString("date")));
               }
                //System.out.println("tips: \n" + tipsTolucene);
               indexer.addFileToIndex(biz,reviewsTolucene,tipsTolucene);
             }
           indexer.terminate();
        //There was a problem connecting to the database
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void close() {
        try {
            if (resultSet1 != null) {
                resultSet1.close();
            }

            if (resultSet2 != null) {
                resultSet2.close();
            }

            if (resultSet3 != null) {
                resultSet3.close();
            }

            if (statement1 != null) {
                statement1.close();
            }

            if (statement2 != null) {
                statement2.close();
            }

            if (statement3 != null) {
                statement3.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Review> databaseReviewsFetcher(){
        return  reviewsTolucene;
    }


    public ArrayList<Tip> databaseTipsFetcher() {
        return  tipsTolucene;
    }

    public ArrayList<Store> databaseBusinessesFetcher() {
        return  businessTolucene;
    }

    public static void main(String[] args) throws NullPointerException,java.io.IOException {
        //TODO prin treksw thn readDB prepei na tsekarw to fakelo luceneResults. An den einai adeios prepei na ton adiasw gt meta tha m efanizei diplwtupa apotelesmata sta queries mou
        readDataBase();
        close();
    }

}



