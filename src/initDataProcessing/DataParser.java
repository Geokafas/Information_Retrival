package initDataProcessing;

import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataParser {

	private static JSONParser parser = new JSONParser();
	private static JSONObject jsonObject;

	private static HashMap<Object, JSONObject> businessMap;
	private static HashMap<String, String> businessNameMap;
	private static HashMap<String,ArrayList<String>> businessReviewMap;
	private static HashMap<String,ArrayList<String>> businessTipsMap;
	private static HashMap<String, String> businessData;

    private static String business_file_input = "D:\\documents\\intellijProjects\\yelpRAW\\yelp\\business.json";
    private static String reviews_file_input = "D:\\documents\\intellijProjects\\yelpRAW\\yelp\\review.json";
    private static String tips_file_input = "D:\\documents\\intellijProjects\\yelpRAW\\yelp\\tip.json";

    private static fileFactory factory;
    private static writeToDataBase com;

	
	public static void main(String[] args) throws IOException,Exception{
    	try {
    		String city = "Mentor-on-the-Lake";
    		factory = new fileFactory();
            com = new writeToDataBase();

    		System.out.println("Starting parsing JSON file...");
        	
    		long startTime = System.nanoTime();
        	
        	readJsonFile(city);
        	
        	long endTime = System.nanoTime();
        	long totalTime = endTime-startTime;
        	
        	System.out.println("Total Excecution time: "+ totalTime/1000000000 +"s");
		
    	} catch (org.json.simple.parser.ParseException e) {

			e.printStackTrace();
		}
    }
    
    private static void readJsonFile(String city) throws IOException, org.json.simple.parser.ParseException,Exception{
    	System.out.println("Starting writing businesses...");
    	
    	long startTime = System.nanoTime();

        businessJSONtoHashMap(city);
    	
    	long endTime = System.nanoTime();
    	long totalTime = endTime-startTime;

    	System.out.println( "Writing Businesses took: "+ totalTime/1000000000 +"s");
    	System.out.println("Starting writing Reviews...");
    	
    	long startTime2 = System.nanoTime();

        reviewsJSONtoHashMap(city,businessNameMap);
    	
    	long endTime2 = System.nanoTime();
    	long totalTime2 = endTime2-startTime2;

    	System.out.println( "Writing Reviews took: "+ totalTime2/1000000000 +"s");
    	System.out.println("Starting writing Tips...");
    	
    	long startTime3 = System.nanoTime();

        tipsJSONtoHashMap(city, businessNameMap);
    	
    	long endTime3 = System.nanoTime();
    	long totalTime3 = endTime3-startTime3;
    	
    	System.out.println( "Writing Tips took: "+ totalTime3/1000000000 +"s");
    }

    private static void businessJSONtoHashMap(String city) throws IOException, org.json.simple.parser.ParseException,Exception{

        BufferedReader readBusiness = new BufferedReader (new FileReader(business_file_input));
        businessMap = new HashMap <Object, JSONObject>();

        String cLine;
        String lineToWrite="";
        int business_counter=0;
        //init hashmaps
        businessData = new HashMap<String, String>();
        businessNameMap = new HashMap<String, String>();

        while ((cLine= readBusiness.readLine())!=null) {

            Object obj = parser.parse(cLine);
            jsonObject = (JSONObject)obj;
            String nameOfBusiness = (String) jsonObject.get("name");
            String business_id = (String) jsonObject.get("business_id");
            String categories = (String) jsonObject.get("categories");
            long review_count = (long) jsonObject.get("review_count");
            double stars = (double) jsonObject.get("stars");

            if (jsonObject.get("city").equals(city)) {
                lineToWrite = nameOfBusiness + "\t" + stars + "\t" + categories +"\t" + review_count;
                //busineassData: key= name of business, value= id name stars categories review_count
                businessData.put(nameOfBusiness, lineToWrite);
                //businessMap: key= business_id, value= jsonObject with all the info for that id read from business.json
                businessMap.put(jsonObject.get("business_id"), jsonObject);
                //businessNameMap: key= business_id, value= business_name
                businessNameMap.put((String)jsonObject.get("business_id"), (String)jsonObject.get("name"));
                business_counter++;
            }
        }
        System.out.println("There are "+business_counter + " businesses in "+ city);
        readBusiness.close();
        //call helper factory class to create business txt
        factory.createBussinessFileTXT(businessData, businessNameMap);
        com.writeBusinessTable(businessData,businessNameMap);

    }
    
	public static void reviewsJSONtoHashMap(String city, HashMap<String,String> businessNameMap) throws IOException, org.json.simple.parser.ParseException,Exception {

        BufferedReader readMyReviews = new BufferedReader(new FileReader(reviews_file_input));
        businessReviewMap = new HashMap<>();
        String cLine;

        Iterator<Map.Entry<String, String>> itt = businessNameMap.entrySet().iterator();

        while (itt.hasNext()) {
            Map.Entry<String, String> pair = itt.next();
            businessReviewMap.put(pair.getValue(), new ArrayList<>());
        }


        int reviewsCounter = 0;
        while ((cLine = readMyReviews.readLine()) != null) {
            Object obj = parser.parse((cLine));
            jsonObject = (JSONObject) obj;
            String business_id = (String) jsonObject.get("business_id");
            String review_text = (String) jsonObject.get("text");
            double stars = (double) jsonObject.get("stars");

            if (businessMap.containsKey(jsonObject.get("business_id"))) {
                businessReviewMap.get(businessNameMap.get(business_id)).add(business_id + "\t" +stars + "\t" + review_text);
                //System.out.println(businessNameMap.get(business_id) + stars + "\t" + review_text);
                reviewsCounter++;
            }
        }
        System.out.println("There are " + reviewsCounter + " reviews for businesses in " + city);
        readMyReviews.close();
        factory.appendFileTXT(businessReviewMap,"review");
        com.writeTipsAndReviewsTable(businessReviewMap,"review");
    }
    
    public static void tipsJSONtoHashMap(String city, HashMap<String,String> businessNameMap) throws IOException, org.json.simple.parser.ParseException,Exception{

        BufferedReader readMyTips = new BufferedReader (new FileReader(tips_file_input));
    	businessTipsMap = new HashMap <>();
    	String cLine;

        Iterator<Map.Entry<String, String>> itt = businessNameMap.entrySet().iterator();

        while (itt.hasNext()) {
            Map.Entry<String, String> pair = itt.next();
            businessTipsMap.put(pair.getValue(), new ArrayList<>());
        }
    	
    	int tips_counter = 0;
    	while ((cLine= readMyTips.readLine())!=null) {
            Object obj = parser.parse((cLine));
            jsonObject = (JSONObject) obj;
    		String business_id = (String) jsonObject.get("business_id");
            String tip_text = (String) jsonObject.get("text");
            String date = (String) jsonObject.get("date");

    		
    		if (businessMap.containsKey(jsonObject.get("business_id"))) {
                businessTipsMap.get(businessNameMap.get(business_id)).add(business_id + "\t" +date + "\t" + tip_text);
    			tips_counter++;
				
    		}
    	}
    	System.out.println("There are "+tips_counter +" tips for businesses in " + city);
    	readMyTips.close();
    	factory.appendFileTXT(businessTipsMap,"tip");
        com.writeTipsAndReviewsTable(businessTipsMap,"tip");
        com.closeDataBaseConnection();
    }
}
