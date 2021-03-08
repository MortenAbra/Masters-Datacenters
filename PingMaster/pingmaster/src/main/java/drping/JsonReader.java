package drping;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;

import javax.print.event.PrintEvent;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class JsonReader {
    String filePath;

    public JsonReader(String filePath) {
        this.filePath = filePath;


        readJSON();
    }
    private void readJSON() {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
         
        try (FileReader reader = new FileReader(filePath))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray workloadJSONList = (JSONArray) obj;
            ArrayList<Workload> workloadList = new ArrayList<>();
             
            //Iterate over workload array
            for(int i = 0; i < workloadJSONList.size(); i++) {
                workloadList.add(parseWorkloadObject((JSONObject) workloadJSONList.get(i)));
            }

            for(Workload workload : workloadList) {
                System.out.println(workload.toString());
            }

 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private static Workload parseWorkloadObject(JSONObject json_workload) 
    {
        //Get workload object within list
        JSONObject workloadJSONObject = (JSONObject) json_workload.get("workload");

        //Get workload attributes
        String wl_name = (String) workloadJSONObject.get("name");    
        String wl_ip = (String) workloadJSONObject.get("ip");  
        String wl_port = (String) workloadJSONObject.get("port");    

        return new Workload(wl_name, wl_ip, wl_port);
    }

}
