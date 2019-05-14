package initDataProcessing;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

public class writeToDataBase {

    private Connection connect = null;
    private Statement statement = null;
    private ResultSet resultSet = null;

    //connection related
    final private String host = "localhost";
    final private String user = "root";
    final private String passwd = "";

    public void openDataBaseConnection() throws Exception {
        try {
            // This will load the MySQL driver, each DB has its own driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Setup the connection with the DB
            connect = DriverManager
                    .getConnection("jdbc:mysql://" + host + "/yelp_reviews?"
                            + "user=" + user + "&password=" + passwd );
            System.out.println("READY.. .. ..");

            // Statements allow to issue SQL queries to the database

            statement = connect.createStatement();
        } catch (Exception e) {
            throw e;
        }
    }
    //TODO epilogh append h clear to database prin apo kathe ektelesh
    public void writeBusinessTable(HashMap<String, String> businessData, HashMap<String, String> businessNameMap)throws Exception{

        Iterator it = businessNameMap.entrySet().iterator();
        openDataBaseConnection();

        while (it.hasNext()) {
            try {
                Map.Entry pair = (Map.Entry) it.next();
                //String doc = "D:\\documents\\intellijProjects\\yelpProcessed\\" + pair.getValue() + ".txt";

                String line = businessData.get(pair.getValue());
                String[] lineToColumn = line.split("\t");
                String[] lineToColumnNEW = removeApostrophe(lineToColumn);
                //to executeQuery den kanei gia data Manipulation!!!! ama thelw na valw h na vgalw data apo table
                //xrhsimopoiw to executeUpdate

                String fs = String.format("INSERT INTO business \n " + "VALUES ( '%s', '%s', '%s', '%s', '%s' );",lineToColumnNEW[0],lineToColumnNEW[1],lineToColumnNEW[2],lineToColumnNEW[3],lineToColumnNEW[4]);
                //System.out.println(fs);
                statement.executeUpdate(fs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void writeTipsAndReviewsTable(HashMap<String, ArrayList<String>> Map,String identifier)throws Exception{

        Iterator<java.util.Map.Entry<java.lang.String,java.util.ArrayList<java.lang.String>>> it = Map.entrySet().iterator();

        while (it.hasNext()) {

            Map.Entry<String, ArrayList<String>> pair = it.next();
            ArrayList<String> line = pair.getValue();
            for(int i=0; i<line.size(); i++) {
                try {

                    //exw ena Araylist gia kathe epixeirhsh. Afou to hashmap key prepei na einai monadiko, den mporw na
                    //exw arraylist gia kathe review. Opote se kathe arraylist uparxei: business_id +"\t"+ stars/date +"\t"+ review/tip,  ...
                    String[] lineToColumn = line.get(i).split("\t");
                    String[] lineToColumnNEW = removeApostrophe(lineToColumn);
                    String fs = String.format("INSERT INTO tips \n " + "VALUES ( '%s','%s','%s');", lineToColumn[0], lineToColumn[1], lineToColumnNEW[2]);
                    if (identifier.compareTo("review")==0) {
                        fs = String.format("INSERT INTO reviews \n " + "VALUES ( '%s','%s','%s');", lineToColumn[0], lineToColumn[1], lineToColumnNEW[2]);
                    }
                    //System.out.println(fs);
                    statement.executeUpdate(fs);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String[] removeApostrophe(String[] string){
        String[] newString = new String[string.length];

        for (int i=0; i<string.length; i++){
            newString[i] = string[i].replace("'","\\'");
        }
        return newString;
    }

    // You need to close the resultSet
    public void closeDataBaseConnection() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (connect != null) {
                connect.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

