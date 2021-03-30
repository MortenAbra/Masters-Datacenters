package mastercontroller.Services;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mastercontroller.FileManager.FilePaths;
import mastercontroller.Models.Result;

public class EnergiDataFetcher {
    private InputStream input;
    private BufferedReader reader;
    private FilePaths fp;
    private ExecutorService es;
    private Result res;
    private FileWriter file;
    private JsonObject jsonObject;
    private JsonParser parser;
    

    public EnergiDataFetcher(){
        fp = new FilePaths();
        es = Executors.newCachedThreadPool();    
    }

    public JsonObject fetchAPIData(String url, String filename) throws MalformedURLException, IOException{
        input = new URL(url).openStream();
        //TextOutPut("Input stream open...");
        reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

        String inputData = readChaString(reader);
        JsonObject data = convertToJson(inputData);
        saveDataToFile(data, filename);

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

    public String readChaString(Reader reader) throws IOException{
        StringBuilder sb = new StringBuilder();
        int i;
        while((i = reader.read()) != -1){
            sb.append((char) i);
        }
        return sb.toString();
    }
}