package drenerginet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.json.JSONObject;


public class EnergiDataFetcher extends FileHandler {

    private InputStream input;
    private BufferedReader reader;
    private FileHandler fileSaver;
    private final OkHttpClient client = new OkHttpClient();
    

    public EnergiDataFetcher(){
        fileSaver = new FileHandler();
    }

    //https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100

    public JSONObject fetchAPIData(String url) throws MalformedURLException, IOException{
        setUrl(url);
        input = new URL(getUrl()).openStream();

        reader = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
        String inputData = readChaString(reader);
        JSONObject data = convertToJson(inputData);
        saveDataToFile(data, "SpotPrice.json", "src\\main\\java\\drenerginet\\Results\\");

        return data;
    }   
}
