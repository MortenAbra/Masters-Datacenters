package drping;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class JsonReader {
    String filePath;

    public JsonReader(String filePath) {
        this.filePath = filePath;
    }
    public ArrayList readJSON() {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        ArrayList<Workload> workloadList = new ArrayList<>();
         
        try (FileReader reader = new FileReader(filePath))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray workloadJSONList = (JSONArray) obj;
             
            //Iterate over workload array
            for(int i = 0; i < workloadJSONList.size(); i++) {
                workloadList.add(parseWorkloadObject((JSONObject) workloadJSONList.get(i)));
            }

            for(Workload workload : workloadList) {
                System.out.println(workload);
            }

 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }

        return workloadList;
    }


    private static Workload parseWorkloadObject(JSONObject json_workload) 
    {
        //Get workload object within list
        JSONObject workloadJSONObject = (JSONObject) json_workload.get("workload");

        //Get workload attributes
        String wl_name = (String) workloadJSONObject.get("name");    
        String wl_ip = (String) workloadJSONObject.get("ip");  
        int wl_port = (int) (long) workloadJSONObject.get("port");

        return new Workload(wl_name, wl_ip, wl_port);
    }

}
