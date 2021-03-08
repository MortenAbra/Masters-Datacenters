package drenerginet;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import org.json.JSONObject;



public class FileHandler {

    private FileWriter file;
    private JSONObject jsonObject;

    public FileHandler(){

    }
    
    public void saveDataToFile(JSONObject object, String fileName, String path) throws IOException{
        file = new FileWriter(path + fileName);
        file.write(object.toString());
        file.close();
    }

    public JSONObject convertToJson(String inputData){
        jsonObject = new JSONObject(inputData);
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
    
}
