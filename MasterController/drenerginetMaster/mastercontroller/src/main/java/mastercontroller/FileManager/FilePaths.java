package mastercontroller.FileManager;

import java.io.File;

public class FilePaths {

    private final String OUTPUTPATH ="/MasterController/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Results/";
    private final String WORKLOADPATH ="/MasterController/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Workloads/";
    private final String APIURL = "https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100";
    /**
     * @return the oUTPUTPATH
     */
    public String getOUTPUTPATH() {
        String basePath = new File("").getAbsolutePath();

        return basePath + OUTPUTPATH;
    }
    /**
     * @return the wORKLOADPATH
     */
    public String getWORKLOADPATH() {
        String basePath = new File("").getAbsolutePath();

        return basePath + WORKLOADPATH;
    }
    /**
     * @return the aPIURL
     */
    public String getAPIURL() {
        return APIURL;
    }

    public FilePaths(){}
    
}
