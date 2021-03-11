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


public class EnergiDataFetcher extends FileHandler {
    private InputStream input;
    private BufferedReader reader;
    private FileHandler fileSaver;
    private ExecutorService executor;
    private JSONObject test;
    

    public EnergiDataFetcher(){
        fileSaver = new FileHandler();
    }

    //https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100

    public JSONObject fetchAPIData(String url) throws MalformedURLException, IOException, ParseException{
        input = new URL(url).openStream();

        reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        String inputData = readChaString(reader);
        JSONObject data = convertToJson(inputData);
        saveDataToFile(data, "spotpricing.json", "energinet\\src\\main\\java\\drenerginet\\Results\\");

        System.out.println(data.get("result"));
        return data;
    }   

    public Future<JSONObject> fetchData(String apiUrl){
        executor = Executors.newFixedThreadPool(5);
        test = new JSONObject();
        return (Future<JSONObject>) executor.submit(() -> {
            Thread.sleep(3000);
            System.out.println();
            return test = fetchAPIData(apiUrl);
        });
    }
}
