package drenerginet;

import drenerginet.Models.Record;
import drenerginet.Models.Result;




/* 
    Taken from https://www.jokecamp.com/blog/code-examples-api-http-get-json-different-languages/#java
*/

public class App {
    static FileHandler fh = new FileHandler();
    static Record record;
    static Result res = new Result();
    public static void main(String[] args) throws Exception {
        //DockerPinger pinger = new DockerPinger();
        EnergiDataFetcher edf = new EnergiDataFetcher();

        edf.jsonToPojo(edf.fetchData(fh.getAPIURL(), "muttentester.json"));
        
        

        //res.addToRecordsList(res.jsonRecordToPojo(edf.fetchData(fh.getAPIURL(), "testing.json")));

        



        /*
        while(true){
            Future future = new EnergiDataFetcher().fetchData(fh.getAPIURL(), "somename.json");
            while(!future.isDone()){
                fh.TextOutPut("Fetching data...");
                Thread.sleep(10000);
            }
            JSONObject data = (JSONObject) future.get();
            
            /*
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
            
                    
            ArrayList<Workload> workloads = fh.addJSONWorkloadToList();

            for (int i = 0; i < fh.getWorkloadList().size(); i++) {
                pinger.pingHost(workloads.get(i), new FileWriter(fh.getOUTPUTPATH()));
                Thread.sleep(1000);
            }
        
        }
        */
    }
}
