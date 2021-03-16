package drenerginet.Models;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Result {
    public List<Record> records = new ArrayList<>();


    public void addToRecordsList(Record record){
        records.add(record);
    }

    public Record jsonRecordToPojo(JsonObject jsonObject){
        Record rec = new Record((String) jsonObject.get("HourDK").getAsString(), (String) jsonObject.get("HourUTC").getAsString(), (String) jsonObject.get("PriceArea").getAsString(), (double) jsonObject.get("SpotPriceDKK").getAsDouble(), (double) jsonObject.get("SpotPriceEUR").getAsDouble());
        return rec;
    }

    /**
     * @param records
     */
    public Result() {
    }   
}
