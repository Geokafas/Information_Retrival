package core;

import jdk.swing.interop.SwingInterOpUtils;

import java.io.*;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class fileReader{

    public static void tokenize(String filepath){
        Path path=Paths.get(filepath);
        File file=path.toFile();
        BufferedReader reader;
        ArrayList<ReviewEdit> reviews;
        ArrayList<TipEdit> tips;

        String line;
        String[] lineToFields;
        try {
            reader = new BufferedReader(new FileReader(file));
            line = reader.readLine();
            reviews = new ArrayList<>();
            tips = new ArrayList<>();
            lineToFields = line.split("\t");
            System.out.println("Name "+lineToFields[0]);
            System.out.println("Stars "+lineToFields[1]);
            System.out.println("Categories "+lineToFields[2]);
            System.out.println("Reviews counter "+lineToFields[3]);
            line = reader.readLine();

            while(line!=null){
               // System.out.println(line);

                lineToFields = line.split("\t");
                if(lineToFields[0].compareTo("review")==0){
                    ReviewEdit rw = new ReviewEdit(Float.parseFloat(lineToFields[1]) , lineToFields[2]);
                    reviews.add(rw);



                }else if(lineToFields[0].compareTo("tip")==0){
                    TipEdit te = new TipEdit(lineToFields[1] , lineToFields[2]);
                    tips.add(te);

                }
                line = reader.readLine();
            }

            for(int i =0; i<reviews.size(); i++){
                System.out.println(reviews.get(i).toString());

            }
            for(int i =0; i<tips.size(); i++) {
                System.out.println(tips.get(i).toString());

            }

            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

        tokenize("D:\\documents\\intellijProjects\\yelpProcessed\\China Buffet.txt");

    }
}

class ReviewEdit{
    private float stars;
    private String reviewText;
    public ReviewEdit(float stars, String reviewText){
        this.stars = stars;
        this.reviewText = reviewText;

    }
    public String toString(){
        return stars +" " +reviewText;

    }

}

class TipEdit{
    private String date;
    private String reviewText;
    public TipEdit(String date, String reviewText){
        this.date = date;
        this.reviewText = reviewText;

    }

    public String toString(){
        return date+" " +reviewText;

    }

}


