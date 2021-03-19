package mastercontroller.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import mastercontroller.FileHandler.FileHandler;
import mastercontroller.Models.Result;

public class EnergiDataFetcher {
    private InputStream input;
    private BufferedReader reader;
    private FileHandler fh;
    private ExecutorService es;
    private Result res;
    

    public EnergiDataFetcher(){
        fh = new FileHandler(); 
        es = Executors.newCachedThreadPool();    
    }

    public JsonObject fetchAPIData(String url, String filename) throws MalformedURLException, IOException{
        input = new URL(url).openStream();
        fh.TextOutPut("Input stream open...");
        reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));

        String inputData = fh.readChaString(reader);
        JsonObject data = fh.convertToJson(inputData);
        fh.saveDataToFile(data, filename);

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

    public void jsonToPojo(JsonObject object){
        Gson gson = new Gson();
        Result res = gson.fromJson(String.valueOf(object.getAsJsonObject("result")), Result.class);
        
        for (int i = 0; i < res.getRecords().size(); i++) {
            fh.TextOutPut(res.getRecords().get(i).toString());
        }
        fh.TextOutPut(res.toString());
    }
}