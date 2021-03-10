package drenerginet;

public class EnergiDataModel {

    private String time;
    private String sector;
    private double price;

    /**
     * @param time
     * @param sector
     * @param price
     */
    public EnergiDataModel(String time, String sector, double price) {
        this.time = time;
        this.sector = sector;
        this.price = price;
    } 
    
    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }
    /**
     * @return the time
     */
    public String getTime() {
        return time;
    }
    /**
     * @param time the time to set
     */
    public void setTime(String time) {
        this.time = time;
    }
    /**
     * @return the sector
     */
    public String getSector() {
        return sector;
    }
    /**
     * @param sector the sector to set
     */
    public void setSector(String sector) {
        this.sector = sector;
    }
    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }    
}
