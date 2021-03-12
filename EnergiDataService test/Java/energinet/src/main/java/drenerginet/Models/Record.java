package drenerginet.Models;

import java.sql.Date;

public class Record{


    public Date hourUTC;

    public String priceArea;

    public double spotPriceDKK;

    public Date hourDK;

    public double spotPriceEUR;

    /**
     * @return the hourUTC
     */
    public Date getHourUTC() {
        return hourUTC;
    }

    /**
     * @param hourUTC the hourUTC to set
     */
    public void setHourUTC(Date hourUTC) {
        this.hourUTC = hourUTC;
    }

    /**
     * @return the priceArea
     */
    public String getPriceArea() {
        return priceArea;
    }

    /**
     * @param priceArea the priceArea to set
     */
    public void setPriceArea(String priceArea) {
        this.priceArea = priceArea;
    }

    /**
     * @return the spotPriceDKK
     */
    public double getSpotPriceDKK() {
        return spotPriceDKK;
    }

    /**
     * @param spotPriceDKK the spotPriceDKK to set
     */
    public void setSpotPriceDKK(double spotPriceDKK) {
        this.spotPriceDKK = spotPriceDKK;
    }

    /**
     * @return the hourDK
     */
    public Date getHourDK() {
        return hourDK;
    }

    /**
     * @param hourDK the hourDK to set
     */
    public void setHourDK(Date hourDK) {
        this.hourDK = hourDK;
    }

    /**
     * @return the spotPriceEUR
     */
    public double getSpotPriceEUR() {
        return spotPriceEUR;
    }

    /**
     * @param spotPriceEUR the spotPriceEUR to set
     */
    public void setSpotPriceEUR(double spotPriceEUR) {
        this.spotPriceEUR = spotPriceEUR;
    }

    /**
     * @param hourUTC
     * @param priceArea
     * @param spotPriceDKK
     * @param hourDK
     * @param spotPriceEUR
     */
    public Record(Date hourDK, Date HourUTC, String priceArea, double SpotPriceDKK, double SpotPriceEUR) {
        this.hourUTC = hourUTC;
        this.priceArea = priceArea;
        this.spotPriceDKK = spotPriceDKK;
        this.hourDK = hourDK;
        this.spotPriceEUR = spotPriceEUR;
    }

    

}
