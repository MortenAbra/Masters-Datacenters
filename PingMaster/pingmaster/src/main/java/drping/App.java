package drping;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Hello world!
 *
 */

public class App  {
    public static void main( String[] args )
    {
        ArrayList<Workload> workloads = new JsonReader("PingMaster/pingmaster/src/main/java/drping/workloads.json").readJSON();
       
        while(true) {
            for (int i = 0; i < workloads.size(); i++) {
                pingHost(workloads.get(i));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }    
    }

    public static void pingHost(Workload workload) {
        int timeout = 10;
        if (workload.wl_port != -1) {
            pingWithPort(workload, timeout);
        } else {

            pingWithoutPort(workload, timeout);
        }
    }

    private static void pingWithoutPort(Workload workload, int timeout) {
        try {
            if (InetAddress.getByName(workload.wl_ip).isReachable(timeout)) {
                // Connection is established
                if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                    // Downtime happend recently
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    System.out.println(timestamp + " : " + workload.wl_name + " : Downtime = " + workload.getElapsedTime() + " ms");
                    workload.setEndTime(System.currentTimeMillis());
                }
                workload.setStartTime(System.currentTimeMillis());
            } else {
                // Connection is not reachable
                workload.setEndTime(System.currentTimeMillis());
            }
        } catch (IOException e) {
        }
    }

    private static void pingWithPort(Workload workload, int timeout) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(workload.wl_ip, workload.wl_port), timeout);
            // Connection is established
            if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                // Downtime happend recently
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                System.out.println(timestamp + " : " + workload.wl_name + " : Downtime = " + workload.getElapsedTime() + " ms");
                workload.setEndTime(System.currentTimeMillis());
            }
            workload.setStartTime(System.currentTimeMillis());
            socket.close();
        } catch (IOException e) {
            // Connection is not reachable
            workload.setEndTime(System.currentTimeMillis());
        }
    }
}