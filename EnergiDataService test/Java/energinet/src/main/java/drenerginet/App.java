package drenerginet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import org.json.JSONException;
import org.json.JSONObject;

/* 
    Taken from https://www.jokecamp.com/blog/code-examples-api-http-get-json-different-languages/#java
*/

public class App {

    public static String readChaString(Reader reader) throws IOException{
        StringBuilder sb = new StringBuilder();
        int i;
        while((i = reader.read()) != -1){
            sb.append((char) i);
        }
        return sb.toString();
    }
    
    public static JSONObject getEnergiData(String apiUrl) throws IOException, JSONException{
        InputStream in = new URL(apiUrl).openStream();
        try {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
        String jsonData = readChaString(reader);
        JSONObject object = new JSONObject(jsonData);

        return object;
        } finally {
            in.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String apiUrl = "https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100";
        JSONObject json = getEnergiData(apiUrl);
        System.out.println(json.toString());
        System.out.println(json.get("result"));
    }
}
