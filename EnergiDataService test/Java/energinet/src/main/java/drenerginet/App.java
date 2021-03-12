package drenerginet;

import java.io.FileWriter;
import java.util.ArrayList;
import java.sql.Date;
import java.util.concurrent.Future;

import org.json.simple.JSONObject;

import drenerginet.Models.Record;
import drenerginet.Models.Result;
import drenerginet.Models.Workload;
import drenerginet.Pinger.DockerPinger;



/* 
    Taken from https://www.jokecamp.com/blog/code-examples-api-http-get-json-different-languages/#java
*/

public class App {
    static String workloadPath = "energinet\\src\\main\\java\\drenerginet\\Results\\workloads.json";
    static String outputPath = "energinet\\src\\main\\java\\drenerginet\\Results\\output.csv";
    static String apiUrl = "https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100";
    static FileHandler fh = new FileHandler();
    static Record record;
    static Result res = new Result();
    public static void main(String[] args) throws Exception {
        EnergiDataFetcher edf = new EnergiDataFetcher();
        DockerPinger pinger = new DockerPinger();
        
        while(true){
            Future future = new EnergiDataFetcher().fetchData(apiUrl);
            while(!future.isDone()){
                fh.TextOutPut("Fetching data...");
                Thread.sleep(10000);
            }
            JSONObject data = (JSONObject) future.get();
            
            for (int i = 0; i < data.size(); i++) {
                Date hourdk = (Date) data.get("HourDK");
                Date hourutc = (Date) data.get("HourUTC");
                String pricearea = (String) data.get("priceArea");
                double spotpricedkk = (double) data.get("SpotPriceDKK");
                double spotpriceeur = (double) data.get("SpotPriceEUR");
                record = new Record(hourdk, hourutc, pricearea, spotpricedkk, spotpriceeur);
                res.getRecords().add(record);
            }
            fh.TextOutPut(res.toString());
                    
            ArrayList<Workload> workloads = edf.addJSONWorkloadToList(workloadPath);

            for (int i = 0; i < edf.getWorkloadList().size(); i++) {
                pinger.pingHost(workloads.get(i), new FileWriter(outputPath));
                Thread.sleep(1000);
            }
        }
    }
}
