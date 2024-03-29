package mastercontroller.FileManager;

import java.io.File;

public class FilePaths {
   
    private final String APIURL = "https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourDK%22,%20%22SpotPriceDKK%22,%20%22PriceArea%22%20from%20%22elspotprices%22%20WHERE%20%22PriceArea%22=%27DK1%27%20or%20%22PriceArea%22=%27DK2%27%20%20ORDER%20BY%20%22HourDK%22%20DESC%20LIMIT%20";
    /**
     * @return the oUTPUTPATH
     */
    public String getOUTPUTPATH() {
        String basePath = new File("").getAbsolutePath();

        String resPath = null;
        switch (System.getProperty("os.name")){
            case "Linux":  resPath = "drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Results/";
                     break;
            case "Windows":  resPath = "drenerginetMaster\\mastercontroller\\src\\main\\java\\mastercontroller\\Results\\";
                     break;
        }

        return resPath;
    }
    /**
     * @return the wORKLOADPATH
     */
    public String getWORKLOADPATH() {
        String basePath = new File("").getAbsolutePath();

        String resPath = null;
        switch (System.getProperty("os.name")){
            case "Linux":  resPath = basePath + "/MasterController/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Workloads/";
                     break;
            case "Windows 10":  resPath = "drenerginetMaster\\mastercontroller\\src\\main\\java\\mastercontroller\\Workloads\\";
                     break;
        }

        // /home/wolder/Documents/Projects/Masters-Datacenters/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Workloads/workloads.json
        // /home/wolder/Documents/Projects/Masters-Datacenters/MasterController/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Workloads/workloads.json

        return resPath;
    }

    public String getGUESTPATH() {
        String basePath = new File("").getAbsolutePath();

        String resPath = null;
        switch (System.getProperty("os.name")){
            case "Linux":  resPath = basePath + "/MasterController/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Guests/";
                     break;
            case "Windows 10":  resPath = "drenerginetMaster\\mastercontroller\\src\\main\\java\\mastercontroller\\Guests\\";
                     break;
        }

        // /home/wolder/Documents/Projects/Masters-Datacenters/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Workloads/workloads.json
        // /home/wolder/Documents/Projects/Masters-Datacenters/MasterController/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Workloads/workloads.json

        return resPath;
    }
    /**
     * @return the aPIURL
     */
    public String getAPIURL() {
        String responseURL = APIURL + "24";
        return responseURL;
    }

    public String getAPIURL(int days) {
        int daysInHours = days * 24;
        String responseURL = APIURL + daysInHours;
        return responseURL;
    }

    public FilePaths(){}
    
}
