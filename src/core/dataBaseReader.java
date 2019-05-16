package core;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

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
    private static  String tableName="";
    private static luceneDocumentFactory fac;
    private static ArrayList<String> businessTolucene;
    private static ArrayList<String> reviewsTolucene;
    private static ArrayList<String> tipsTolucene;





    public static void readDataBase() throws NullPointerException {
        ArrayList<BusinessDetails> array;
        resultSet1 = null;
        resultSet2 = null;
        resultSet3 = null;
        statement1 = null;
        statement2 = null;
        statement3 = null;

        reviewsTolucene = new ArrayList<>();
        tipsTolucene = new ArrayList<>();

        fac = new luceneDocumentFactory();
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
            array = getBusinessDetails(resultSet1);
            System.out.println(array.size());

           for(int i=0; i< array.size(); i++){

               String id = array.get(i).toString();
               //clean arrays before assigning. Because the
               // same array is used to send data for each different business to lucene
               reviewsTolucene.clear();
               tipsTolucene.clear();

               //System.out.println(id);
               statement2 = connect.createStatement();
               resultSet2 = statement2.executeQuery("select * from reviews WHERE business_id = "+ "'"+id+"'");
               while(resultSet2.next()){
                   reviewsTolucene.add(resultSet2.getString("stars")
                           +"\t"+resultSet2.getString("review_text"));

               }
               statement3 = connect.createStatement();
               resultSet3 = statement3.executeQuery("select * from tips WHERE business_id = "+ "'"+id+"'");
               while(resultSet3.next()){
                   tipsTolucene.add(resultSet3.getString("date")
                           +"\t"+resultSet3.getString("tip_text"));
               }
           fac.addFileToIndex(businessTolucene.get(i),reviewsTolucene,tipsTolucene);
           }
        //There was a problem connecting to the database
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BusinessDetails> getBusinessDetails(ResultSet resultSet)  throws java.sql.SQLException{
        ArrayList<BusinessDetails> business_array;
        businessTolucene = new ArrayList<>();
        business_array = new ArrayList<>();
        BusinessDetails b;
        while(resultSet.next()){
            businessTolucene.add(resultSet1.getString("business_id")
                    +"\t"+resultSet1.getString("name")
                    +"\t"+resultSet1.getString("stars")
                    +"\t"+resultSet1.getString("categories")
                    +"\t"+resultSet1.getString("review_count"));
            b = new BusinessDetails(resultSet.getString("business_id"),
                    resultSet.getString("name"),
                    resultSet.getString("stars"),
                    resultSet.getString("categories"),
                    resultSet.getString("review_count"));
            business_array.add(b);
        }
        return business_array;

    }

   public static void main(String[] args) throws NullPointerException,java.io.IOException, org.apache.lucene.queryparser.classic.ParseException {
       readDataBase();
       close();
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
}
//currently not fully used
class BusinessDetails{
    private String id;
    private String name;
    private String stars;
    private String categories;
    private String review_counter;


    public BusinessDetails(String id, String name, String stars, String categories, String review_counter){
        this.id = id;
        this.name = name;
        this.stars = stars;
        this.categories = categories;
        this.review_counter = review_counter;

    }
    public String toString(){
        return id;

    }


}


