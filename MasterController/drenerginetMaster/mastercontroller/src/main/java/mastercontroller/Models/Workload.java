package mastercontroller.Models;

public class Workload {

    String wl_name = "Undefined";
    String wl_ip = "0.0.0.0";
    int wl_port = -1;
    long startTime = 0;
    long elapsedTime = 0;
    long endTime = 0;
    boolean wl_status;

    public Workload(String wl_name, String wl_ip, int wl_port, boolean wl_status) {
        this.wl_name = wl_name;
        this.wl_ip = wl_ip;
        this.wl_port = wl_port;
        this.wl_status = wl_status;
    }

    @Override
    public String toString() {
        String s = wl_name + "  |  " + wl_ip + ":" + wl_port;
        return s;
    }

    public long getElapsedTime() {
        return getEndTime() - getStartTime();
    }
    public long getEndTime() {
        return endTime;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the wl_name
     */
    public String getWl_name() {
        return wl_name;
    }

    /**
     * @param wl_name the wl_name to set
     */
    public void setWl_name(String wl_name) {
        this.wl_name = wl_name;
    }

    /**
     * @return the wl_ip
     */
    public String getWl_ip() {
        return wl_ip;
    }

    /**
     * @param wl_ip the wl_ip to set
     */
    public void setWl_ip(String wl_ip) {
        this.wl_ip = wl_ip;
    }

    /**
     * @return the wl_port
     */
    public int getWl_port() {
        return wl_port;
    }

    /**
     * @param wl_port the wl_port to set
     */
    public void setWl_port(int wl_port) {
        this.wl_port = wl_port;
    }

    /**
     * @return the wl_status
     */
    public boolean isWl_status() {
        return wl_status;
    }

    /**
     * @param wl_status the wl_status to set
     */
    public void setWl_status(boolean wl_status) {
        this.wl_status = wl_status;
    }
    
    
}
