package mastercontroller.FileHandler;

public class FilePaths {

    private final String OUTPUTPATH ="energinet\\src\\main\\java\\drenerginet\\Results\\";
    private final String WORKLOADPATH ="energinet\\src\\main\\java\\drenerginet\\Workloads\\";
    private final String APIURL = "https://www.energidataservice.dk/proxy/api/datastore_search_sql?sql=SELECT%20%22HourUTC%22,%20%22HourDK%22,%20%22PriceArea%22,%20%22SpotPriceDKK%22,%20%22SpotPriceEUR%22%20FROM%20%22elspotprices%22%20ORDER%20BY%20%22HourUTC%22%20DESC%20LIMIT%20100";
    /**
     * @return the oUTPUTPATH
     */
    public String getOUTPUTPATH() {
        return OUTPUTPATH;
    }
    /**
     * @return the wORKLOADPATH
     */
    public String getWORKLOADPATH() {
        return WORKLOADPATH;
    }
    /**
     * @return the aPIURL
     */
    public String getAPIURL() {
        return APIURL;
    }

    public FilePaths(){}
    
}
