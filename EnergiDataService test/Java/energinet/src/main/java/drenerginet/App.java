package drenerginet;

import java.util.concurrent.Future;

import org.json.JSONObject;

/* 
    Taken from https://www.jokecamp.com/blog/code-examples-api-http-get-json-different-languages/#java
*/

public class App {

    public static void main(String[] args) throws Exception {
        EnergiDataFetcher edf = new EnergiDataFetcher();
        Future future = new EnergiDataFetcher().fetchData("https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100");
        while(!future.isDone()){
            System.out.println("Fetching data....");
            Thread.sleep(3000);
        }
        JSONObject data = (JSONObject) future.get();
        System.out.println(data.toString());
        System.out.println("System exits..");
        RootModel root = new RootModel();
        System.out.println(root.toString());
        System.exit(1);


    }
}
