package mastercontroller.Models;

import java.util.ArrayList;
import java.util.List;




public class Result {
    public List<Record> records;


    public void addToRecordsList(Record record){
        records.add(record);
    }


    /**
     * @param records
     */
    public Result() {
    }


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
}
