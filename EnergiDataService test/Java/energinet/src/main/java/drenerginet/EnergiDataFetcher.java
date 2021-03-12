package drenerginet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;


public class EnergiDataFetcher {
    private InputStream input;
    private BufferedReader reader;
    private FileHandler fileSaver;
    private ExecutorService executor;
    private JSONObject test;
    private FilePaths fp;
    

    public EnergiDataFetcher(){
        fp = new FilePaths();
        fileSaver = new FileHandler();
        
    }

    public JSONObject fetchAPIData(String url, String filename) throws MalformedURLException, IOException, ParseException{
        input = new URL(url).openStream();

        reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        String inputData = fileSaver.readChaString(reader);
        JSONObject data = fileSaver.convertToJson(inputData);
        fileSaver.saveDataToFile(data, filename);

        //System.out.println(data.get("result"));
        return data;
    }   

    public Future<JSONObject> fetchData(String apiUrl, String filename){
        executor = Executors.newSingleThreadExecutor();
        test = new JSONObject();
        return (Future<JSONObject>) executor.submit(() -> {
            fileSaver.TextOutPut("Cycling...");
            return test = fetchAPIData(apiUrl, filename);
        });
    }
}
