package drenerginet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import org.apache.commons.io.FileUtils;

import drenerginet.Models.Result;
import drenerginet.Models.Workload;




public class FileHandler extends FilePaths {

    private FileWriter file;
    private JsonObject jsonObject;
    private Timestamp time;
    private JsonParser parser;
    private ArrayList<Workload> workloadList;
    private FilePaths fp;
    private Result res;


    public FileHandler(){
        time = new Timestamp(System.currentTimeMillis());
        parser = new JsonParser();
        fp = new FilePaths();
        res = new Result();
    }

    
    public void saveDataToFile(JsonObject object, String fileName) throws IOException{
        file = new FileWriter(fp.getOUTPUTPATH() + fileName);
        file.write(object.toString());
        file.close();
        TextOutPut("DATA SAVED TO FILE");
    }

    public JsonObject convertToJson(String inputData) {
        jsonObject = (JsonObject) this.parser.parse(inputData);       
        TextOutPut("DATA CONVERTED TO JSON");
        return jsonObject;
    }

    public Workload parseWorkloadObject(JsonObject jsonWorkload){
        JsonObject workloadObject = (JsonObject) jsonWorkload.get("workload");
        //Workload workload = gson.fromJson(String.valueOf(jsonWorkload.getAsJsonObject("workload")), Workload.class);
        String wl_name = (String) workloadObject.get("name").getAsString(); //(String) workloadObject.get("name");
        String wl_ip = (String) workloadObject.get("ip").getAsString();
        int wl_port = (int) (long) workloadObject.get("port").getAsLong();

        return new Workload(wl_name, wl_ip, wl_port);
    }

    //Generating workload json objects and adding to Array
    public ArrayList addJSONWorkloadToList(){
        workloadList = new ArrayList<>();
        try (FileReader reader = new FileReader(fp.getWORKLOADPATH())){
            //Generating JSONObject based on a file
            Object obj = this.parser.parse(reader);
            JsonArray jsonWorkloadList = (JsonArray) obj;
            
            //Adding workloads to the list
            for (int i = 0; i < jsonWorkloadList.size(); i++) {
                workloadList.add(parseWorkloadObject((JsonObject) jsonWorkloadList.get(i)));
            }

            for(Workload workload : workloadList){
                TextOutPut(workload.toString());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return workloadList;
    }


    public void CSVWriter(){
        FileWriter csvWriter;
        if(!fileExists(fp.getOUTPUTPATH())){
            try {
                new File(fp.getOUTPUTPATH()).createNewFile();
                csvWriter = new FileWriter(fp.getOUTPUTPATH(), true);
                appendCSVHeader(csvWriter);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } if (fileExists(fp.getOUTPUTPATH())) {
            try {
                new File(fp.getOUTPUTPATH());
                File tmpFile = File.createTempFile("output_tmp", ".csv");
                boolean match = isFilesMatching(new File(fp.getOUTPUTPATH()), tmpFile);
                String out = String.valueOf(match);
                TextOutPut(out);
                if(!match){
                    TextOutPut("Something something");
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public boolean isFilesMatching(File file1, File file2) throws IOException{
        boolean isTwoEqual = FileUtils.contentEquals(file1, file2);
    
        return isTwoEqual;
    }

    public void appendCSVHeader(FileWriter csvWriter) throws IOException{
        csvWriter.append("Timestamp");
        csvWriter.append(",");
        csvWriter.append("Name");
        csvWriter.append(",");
        csvWriter.append("Downtime");
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }

    public static boolean fileExists(String filePathString) {
        File f = new File(filePathString);
        if(f.exists() && !f.isDirectory()) { 
            return true;
        } else {
            return false;
        }
    }


    public String readChaString(Reader reader) throws IOException{
        StringBuilder sb = new StringBuilder();
        int i;
        while((i = reader.read()) != -1){
            sb.append((char) i);
        }
        return sb.toString();
    }

    public void TextOutPut(String inpuString){
        System.out.print(time + ": " + inpuString + '\n');
    }

    /**
     * @return the workloadList
     */
    public ArrayList<Workload> getWorkloadList() {
        return workloadList;
    }

    /**
     * @param workloadList the workloadList to set
     */
    public void setWorkloadList(ArrayList<Workload> workloadList) {
        this.workloadList = workloadList;
    }    
}
