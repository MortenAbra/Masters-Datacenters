package drenerginet;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.concurrent.Future;

import org.json.simple.JSONObject;

import drenerginet.Models.Workload;
import drenerginet.Pinger.DockerPinger;



/* 
    Taken from https://www.jokecamp.com/blog/code-examples-api-http-get-json-different-languages/#java
*/

public class App {
    static String workloadPath = "energinet\\src\\main\\java\\drenerginet\\Results\\workloads.json";
    static String outputPath = "energinet\\src\\main\\java\\drenerginet\\Results\\output.csv";
    public static void main(String[] args) throws Exception {
        EnergiDataFetcher edf = new EnergiDataFetcher();
        DockerPinger pinger = new DockerPinger();
        Future future = new EnergiDataFetcher().fetchData("https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100");
        while(!future.isDone()){
            System.out.println("Fetching data....");
            Thread.sleep(3000);
        }
        JSONObject data = (JSONObject) future.get();
        ArrayList<Workload> workloads = edf.addJSONWorkloadToList(workloadPath);
        while(true){
            for (int i = 0; i < edf.getWorkloadList().size(); i++) {
                try {
                    Thread.sleep(100);
                    pinger.pingHost(workloads.get(i), new FileWriter(outputPath));
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

}
