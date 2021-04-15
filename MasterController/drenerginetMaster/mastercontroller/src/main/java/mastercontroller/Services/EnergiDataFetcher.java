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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private double thresholdByDays;
    private double percentOffset;
    private double upperBound;
    private double lowerBound;
    private double offSet;
    private URL url;
    private HttpURLConnection urlConnection;
    private BufferedReader bufferedReader;
    private InputStream in;
    private JsonReader reader;
    private List<Record> recordList;
    private FilePaths fp;


    public EnergiDataFetcher(int daysToRetrieve, double percentage) {
        this.fp = new FilePaths();

        getEnergiData(daysToRetrieve);
        calculateThresholdMigration(percentage);
    }

    // Calculates the threshold based on persenage
    private void calculateThresholdMigration(double percentage) {
        //PLACEHOLDER
        generateThresholdByDays();
        generateOffset(percentage);
    }

    // Generates thresholdByDays
    private void generateThresholdByDays(){
        this.recordList = result.getRecords();
        double prices = 0;

        for (Record record : recordList) {
            prices += record.getSpotPriceDKK();
        }

        this.setThresholdByDays(prices/recordList.size());
    }

    // Calculates threshold values for upper and lowerbound.
    private void generateOffset(double percentage) {
        // Genereate offSet
        this.offSet = getThresholdByDays() * percentage;
        this.upperBound = getThresholdByDays() + offSet;
        this.lowerBound = getThresholdByDays() - offSet;
    }

    public void customThreshold(double input){
        this.setThreshold(input);
        System.out.println("Custom threshold set to: " + input);
    }

    public double getPercentOffset(){
        return this.percentOffset;
    }

    public void setPercentOffset(double input){
        this.percentOffset = input;
    }

    public void setThresholdByDays(double input){
        this.thresholdByDays = input;
    }

    public double getThresholdByDays(){
        return this.thresholdByDays;
    }

    public void convertJsonToObjects(String jsonString) {
        Gson gson = new Gson();

        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject().get("result").getAsJsonObject();
        result = gson.fromJson(jsonObject.toString(), Result.class);
    }

    private void getEnergiData(int days) {

        int responseCode;
        try {
            this.url = new URL(fp.getAPIURL(days));
            this.urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Content-length", "0");
            urlConnection.setUseCaches(true);
            urlConnection.setAllowUserInteraction(false);
            urlConnection.connect();
            responseCode = urlConnection.getResponseCode();

            switch (responseCode) {
                case 200:
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    convertJsonToObjects(readChaString(bufferedReader));
                    setThreshold(32);
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

    @Override
    public void update(Workload workload) {
        // DO NOTHING HERE

    }

    @Override
    public void update(double threshold) {
        // TODO Auto-generated method stub

    }

    public double getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public double getLowerBound() {
        return lowerBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public double getOffSet() {
        return offSet;
    }

    public void setOffSet(double offSet) {
        this.offSet = offSet;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public List<Record> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<Record> recordList) {
        this.recordList = recordList;
    }

    public Record getCurrentRecord() {
        // Current date (This is now)
        Date now = new Date();
        // Fomratter for Record Date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        for (Record record : recordList) {
            try {
                // Parse record date
                Date recordData = formatter.parse(record.HourDK);
                if (isSameHour(now, recordData)) {
                    return record;
                }
                
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }  
           
        }

        return null;
    }

    public boolean isSameHour(Date date1, Date date2) {
        // No need to care about formatting. 
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHH");
        return fmt.format(date1).equals(fmt.format(date2));
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