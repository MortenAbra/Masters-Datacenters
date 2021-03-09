package drenerginet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.sql.Timestamp;
import org.json.JSONObject;



public class FileHandler {

    private FileWriter file;
    private JSONObject jsonObject;
    private String url;
    private Timestamp time;


    public FileHandler(){
        time = new Timestamp(System.currentTimeMillis());
    }
    
    public void saveDataToFile(JSONObject object, String fileName, String path) throws IOException{
        file = new FileWriter(path + fileName);
        file.write(object.toString());
        file.close();
        TextOutPut("DATA SAVED TO FILE");
    }

    public JSONObject convertToJson(String inputData){
        jsonObject = new JSONObject(inputData);
        TextOutPut("DATA CONVERTED TO JSON");
        return jsonObject;
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

    public void setUrl(String url){
        this.url = url;
    }

    public String getUrl(){
        return url;
    }

    public void setJsonObject(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }

    public JSONObject getJsonObject(){
        return jsonObject;
    }

    
}
