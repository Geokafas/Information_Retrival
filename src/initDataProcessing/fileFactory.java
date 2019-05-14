package initDataProcessing;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class fileFactory {
    //makes txt files named after the place they represent.
    // Writes in the first line:  business_id +"\t" +  nameOfBusiness + "\t" + stars + "\t" + categories +"\t" + review_count;
  public void createBussinessFileTXT(HashMap<String, String> businessData, HashMap<String, String> businessNameMap){

      Iterator it = businessNameMap.entrySet().iterator();

      while (it.hasNext()) {

          Map.Entry pair = (Map.Entry)it.next();
          String doc = "D:\\documents\\intellijProjects\\yelpProcessed\\"+pair.getValue()+".txt";

          try(FileWriter fw = new FileWriter(doc, false);
              BufferedWriter bw = new BufferedWriter(fw);
              PrintWriter out = new PrintWriter(bw))
          {

              out.println(businessData.get(pair.getValue()));
              out.println("\n");

          } catch (IOException e) {

              e.printStackTrace();
          }
      }
  }
    // appends the above files with data derived from the caller
    //it is called once for reviews and once for tips
  public void appendFileTXT(HashMap<String, ArrayList<String>> Map,String identifier){

      Iterator<java.util.Map.Entry<java.lang.String,java.util.ArrayList<java.lang.String>>> it = Map.entrySet().iterator();

      while (it.hasNext()) {

          Map.Entry<String, ArrayList<String>> pair = it.next();
          String doc = "D:\\documents\\intellijProjects\\yelpProcessed\\"+pair.getKey()+".txt";

          try(FileWriter fw = new FileWriter(doc, true);
              BufferedWriter bw = new BufferedWriter(fw);
              PrintWriter out = new PrintWriter(bw))
          {

              wordProcessing(pair.getValue(),out,identifier);

          } catch (IOException e) {

              e.printStackTrace();

          }
      }
  }
//identifier ?? isws kai oxi
  private void wordProcessing(ArrayList<String> text, PrintWriter out,String identifier)
  {
      for(int i=0; i<text.size(); i++){
          out.println("\n");
          out.println(identifier + "\t" + text.get(i));
          out.println("\n");
      }
  }
}
