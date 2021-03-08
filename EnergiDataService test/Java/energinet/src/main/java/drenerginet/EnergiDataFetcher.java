package drenerginet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONObject;


public class EnergiDataFetcher {

    private InputStream input;
    private BufferedReader reader;
    private JSONObject jsonObject;
    private FileHandler fileSaver;

    public EnergiDataFetcher(){}

    //https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100

    public JSONObject fetchAPIData(String apiUrl) throws MalformedURLException, IOException{
        String url = apiUrl;
        fileSaver = new FileHandler();
        input = new URL(url).openStream();
        reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        String inputData = fileSaver.readChaString(reader);

        
        fileSaver.saveDataToFile(fileSaver.convertToJson(inputData), "SpotPrice.json", "src\\main\\java\\drenerginet\\Results\\");

        return fileSaver.convertToJson(inputData);
    }

    public void getSpotPrice(String string) {
    }
    
}
