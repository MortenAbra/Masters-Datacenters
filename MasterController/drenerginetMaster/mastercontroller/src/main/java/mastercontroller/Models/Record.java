package mastercontroller.Models;


public class Record{


    public String HourDK;

    public String PriceArea;

    public double SpotPriceDKK;

    
    /**
     * @param hourUTC
     * @param priceArea
     * @param spotPriceDKK
     * @param hourDK
     * @param spotPriceEUR
     */
    public Record(String hourDK, String priceArea, double SpotPriceDKK) {
        this.HourDK = hourDK;
        this.PriceArea = priceArea;
        this.SpotPriceDKK = SpotPriceDKK;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    
    @Override
    public String toString() {
        return "Record [HourDK=" + HourDK + ", PriceArea=" + PriceArea + ", SpotPriceDKK=" + SpotPriceDKK + "]";
    }


    /**
     * @return the hourDK
     */
    public String getHourDK() {
        return HourDK;
    }


    /**
     * @param hourDK the hourDK to set
     */
    public void setHourDK(String hourDK) {
        HourDK = hourDK;
    }


    /**
     * @return the priceArea
     */
    public String getPriceArea() {
        return PriceArea;
    }


    /**
     * @param priceArea the priceArea to set
     */
    public void setPriceArea(String priceArea) {
        PriceArea = priceArea;
    }


    /**
     * @return the spotPriceDKK
     */
    public double getSpotPriceDKK() {
        return SpotPriceDKK;
    }


    /**
     * @param spotPriceDKK the spotPriceDKK to set
     */
    public void setSpotPriceDKK(double spotPriceDKK) {
        SpotPriceDKK = spotPriceDKK;
    }   

}

