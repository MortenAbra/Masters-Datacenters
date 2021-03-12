package drenerginet.Models;

import java.util.ArrayList;
import java.util.List;

public class Result {
    public List<Record> records = new ArrayList<>();

    /**
     * @return the records
     */
    public List<Record> getRecords() {
        return records;
    }

    /**
     * @param records the records to set
     */
    public void setRecords(List<Record> records) {
        this.records = records;
    }

    /**
     * @param records
     */
    public Result() {
    }   
}
