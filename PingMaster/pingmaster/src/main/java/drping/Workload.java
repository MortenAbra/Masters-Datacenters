package drping;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Workload {

    String wl_name = "Undefined";
    String wl_ip = "0.0.0.0";
    int wl_port = -1;
    long startTime = 0;
    long elapsedTime = 0;
    long endTime = 0;

    public Workload(String wl_name, String wl_ip, int wl_port) {
        this.wl_name = wl_name;
        this.wl_ip = wl_ip;
        this.wl_port = wl_port;
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

}

