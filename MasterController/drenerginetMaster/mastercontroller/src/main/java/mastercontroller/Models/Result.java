package mastercontroller.Models;

import java.util.ArrayList;
import java.util.List;




public class Result {
    
    private List<Record> records;

    public Result(){
        this.records = new ArrayList<>();
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

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    
    @Override
    public String toString() {
        return "Result: " + records;
    }

    


}
