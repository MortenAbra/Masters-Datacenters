package drenerginet.Models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Record{


    public String hourUTC;

    public String priceArea;

    public double spotPriceDKK;

    public String hourDK;

    public double spotPriceEUR;

    
    /**
     * @param hourUTC
     * @param priceArea
     * @param spotPriceDKK
     * @param hourDK
     * @param spotPriceEUR
     */
    public Record(String hourDK, String HourUTC, String priceArea, double SpotPriceDKK, double SpotPriceEUR) {
        this.hourDK = hourDK;
        this.hourUTC = HourUTC;
        this.priceArea = priceArea;
        this.spotPriceDKK = SpotPriceDKK;
        this.spotPriceEUR = SpotPriceEUR;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    
    @Override
    public String toString() {
        return "Record [hourDK=" + hourDK + ", hourUTC=" + hourUTC + ", priceArea=" + priceArea + ", spotPriceDKK="
                + spotPriceDKK + ", spotPriceEUR=" + spotPriceEUR + "]";
    }

    

}
