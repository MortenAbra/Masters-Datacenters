package mastercontroller.Services;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

import mastercontroller.Observer;
import mastercontroller.Models.Record;
import mastercontroller.Models.Result;
import mastercontroller.Models.Workload;

public class EnergiDataFetcher implements Observer {
    
    private Result result;
    private Record record;
    private double threshold;
    private double weeklyAvg;
    private URL url;
    private InputStream in;
    private JsonReader reader;
    

    public EnergiDataFetcher(){
           
    }


    public double weeklyRunningAvg(){

        return weeklyAvg;
    }

    public double calculateThreshold(double currentPrice){
        
        System.out.println("t3");
        return threshold;
    }

    public void convertJsonToObjects(String fileLocation){
        
        Gson gson = new Gson();

        try (Reader reader = new FileReader(fileLocation)){
            this.result = gson.fromJson(reader, Result.class);

            System.out.println(result);

        } catch (Exception e) {
            //TODO: handle exception
        }
        
    }

    public void getEnergiData(String url){

        //Placeholder
    }



    /**
     * @return the threshold
     */
    public double getThreshold() {
        return threshold;
    }


    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }


    /**
     * @return the weeklyAvg
     */
    public double getWeeklyAvg() {
        return weeklyAvg;
    }


    /**
     * @param weeklyAvg the weeklyAvg to set
     */
    public void setWeeklyAvg(double weeklyAvg) {
        this.weeklyAvg = weeklyAvg;
    }


    @Override
    public void update(Workload workload) {
        // DO NOTHING HERE
        
    }


    @Override
    public void update(double threshold) {
        // TODO Auto-generated method stub
        
    }

    


     



    /*
    public JsonObject fetchAPIData(String url, String filename) {
        try {
            input = new URL(url).openStream();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //TextOutPut("Input stream open...");
        reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

        String inputData = readChaString(reader);
        JsonObject data = convertToJson(inputData);
        try {
            saveDataToFile(data, filename);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //System.out.println(data.get("result"));
        return data;
    }   

    
    public JsonObject fetchData(String apiUrl, String filename){

        Callable callable;
        JsonObject jsonResult = null;
        Future<JsonObject> future;
        res = new Result();

        try {
            callable = new Callable<JsonObject>(){
                @Override
                public JsonObject call() throws Exception {
                    JsonObject jsonObject = fetchAPIData(apiUrl, filename);
                    return jsonObject;
                } 
            }; 
        future = es.submit(callable);
        jsonResult = future.get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonResult;
    }

    public void saveDataToFile(JsonObject object, String fileName) throws IOException{
        file = new FileWriter(fp.getOUTPUTPATH() + fileName);
        file.write(object.toString());
        file.close();
        //TextOutPut("DATA SAVED TO FILE");
    }

    public JsonObject convertToJson(String inputData) {
        jsonObject = (JsonObject) this.parser.parse(inputData);       
        //TextOutPut("DATA CONVERTED TO JSON");
        return jsonObject;
    }

    public String readChaString(Reader reader) {
        StringBuilder sb = new StringBuilder();
        int i;
        try {
            while((i = reader.read()) != -1){
                sb.append((char) i);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }

    */
}