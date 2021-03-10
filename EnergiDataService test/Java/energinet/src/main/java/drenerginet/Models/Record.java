package drenerginet.Models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Record{

    @JsonProperty("HourUTC")
    public Date hourUTC;
    @JsonProperty("PriceArea")
    public String priceArea;
    @JsonProperty("SpotPriceDKK")
    public double spotPriceDKK;
    @JsonProperty("HourDK")
    public Date hourDK;
    @JsonProperty("SpotPriceEUR")
    public double spotPriceEUR;
}
