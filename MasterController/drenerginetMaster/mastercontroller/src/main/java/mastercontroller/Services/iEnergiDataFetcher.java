package mastercontroller.Services;

import com.google.gson.JsonObject;

public interface iEnergiDataFetcher {
    
    public JsonObject fetchAPIData(String url, String filename);
    public JsonObject fetchData(String apiUrl, String filename);

    
}
