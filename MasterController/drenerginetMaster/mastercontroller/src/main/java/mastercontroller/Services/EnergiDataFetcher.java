package mastercontroller.Services;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.print.event.PrintEvent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;


import mastercontroller.Observer;
import mastercontroller.FileManager.FilePaths;
import mastercontroller.Models.Record;
import mastercontroller.Models.Result;
import mastercontroller.Models.Workload;

public class EnergiDataFetcher implements Observer {

    private Result result;
    private double threshold;
    private double weeklyAvg;
    private URL url;
    private HttpURLConnection urlConnection;
    private BufferedReader bufferedReader;
    private InputStream in;
    private JsonReader reader;
    private List<Record> recordList;

    public EnergiDataFetcher() {

    }

    public static void main(String[] args) {
        FilePaths fp = new FilePaths();
        EnergiDataFetcher en = new EnergiDataFetcher();
        en.getEnergiData(fp.getAPIURL(), 2000);

        en.calculateThreshold();
    }

    public void weeklyRunningAvg() {
        this.recordList = result.getRecords();
        double prices = recordList.get(0).getSpotPriceDKK();

        for (int i = 1; i < recordList.size(); i++) {
            prices += recordList.get(i).getSpotPriceDKK();
        }

        this.setWeeklyAvg(prices/recordList.size());

        System.out.println("Threshold based on weekly avg: " + this.getWeeklyAvg());

        this.setWeeklyAvg(weeklyAvg);    
    }

    public void calculateThreshold() {
        //DO SOMETHING THAT CREATES A THRESHOLD!
    }

    public void customThreshold(double input){
        this.setThreshold(input);
        System.out.println("Custom threshold set to: " + input);
    }

    public void convertJsonToObjects(String jsonString) {
        System.out.println("Init json conversion");

        Gson gson = new Gson();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject().get("result").getAsJsonObject();
        result = gson.fromJson(jsonObject.toString(), Result.class);
        System.out.println(result.getRecords());
    }

    public void getEnergiData(String apiUrl, int connectionTimeout) {

        int responseCode;
        try {
            System.out.println("Trying to open connection!");
            this.url = new URL(apiUrl);
            this.urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-length", "0");
            urlConnection.setUseCaches(true);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setConnectTimeout(connectionTimeout);
            urlConnection.setReadTimeout(connectionTimeout);
            urlConnection.connect();
            System.err.println("Connected!");
            responseCode = urlConnection.getResponseCode();
            System.out.println(responseCode);

            switch (responseCode) {
                case 200:
                    System.out.println("Case 200");
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    convertJsonToObjects(readChaString(bufferedReader));
                    break;
                case 400:
                    System.out.println("400 - Bad reqeust! Check syntax");
                    break;
                case 404:
                    System.out.println("404 - Not Found!");
            
                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Placeholder
    }

    public String readChaString(Reader reader) {
        StringBuilder sb = new StringBuilder();
        int i;
        try {
            while ((i = reader.read()) != -1) {
                sb.append((char) i);
            }
        } catch (IOException e) { // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
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
     * public JsonObject fetchAPIData(String url, String filename) { try { input =
     * new URL(url).openStream(); } catch (IOException e) { // TODO Auto-generated
     * catch block e.printStackTrace(); } //TextOutPut("Input stream open...");
     * reader = new BufferedReader(new InputStreamReader(input,
     * Charset.forName("UTF-8")));
     * 
     * String inputData = readChaString(reader); JsonObject data =
     * convertToJson(inputData); try { saveDataToFile(data, filename); } catch
     * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
     * 
     * //System.out.println(data.get("result")); return data; }
     * 
     * 
     * public JsonObject fetchData(String apiUrl, String filename){
     * 
     * Callable callable; JsonObject jsonResult = null; Future<JsonObject> future;
     * res = new Result();
     * 
     * try { callable = new Callable<JsonObject>(){
     * 
     * @Override public JsonObject call() throws Exception { JsonObject jsonObject =
     * fetchAPIData(apiUrl, filename); return jsonObject; } }; future =
     * es.submit(callable); jsonResult = future.get(); } catch (Exception e) {
     * e.printStackTrace(); } return jsonResult; }
     * 
     * public void saveDataToFile(JsonObject object, String fileName) throws
     * IOException{ file = new FileWriter(fp.getOUTPUTPATH() + fileName);
     * file.write(object.toString()); file.close();
     * //TextOutPut("DATA SAVED TO FILE"); }
     * 
     * public JsonObject convertToJson(String inputData) { jsonObject = (JsonObject)
     * this.parser.parse(inputData); //TextOutPut("DATA CONVERTED TO JSON"); return
     * jsonObject; }
     * 
     * public String readChaString(Reader reader) { StringBuilder sb = new
     * StringBuilder(); int i; try { while((i = reader.read()) != -1){
     * sb.append((char) i); } } catch (IOException e) { // TODO Auto-generated catch
     * block e.printStackTrace(); } return sb.toString(); }
     * 
     */
}