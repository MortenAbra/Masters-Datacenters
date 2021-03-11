package drenerginet.Pinger;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;

import drenerginet.Models.Workload;

public class DockerPinger {

    public DockerPinger(){}

    public void pingHost(Workload workload, FileWriter writer){
        int timeout = 10;
        if(workload.getWl_port() != -1){
            pingWithPort(workload, timeout, writer);
        } else {
            pingWithoutPort(workload, timeout, writer);
        }
    }

    public void pingWithPort(Workload workload, int timeout, FileWriter writer){
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(workload.getWl_ip(), workload.getWl_port()), timeout);
            // Connection is established
            if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                // Downtime happend recently
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                writeWorkloadLineToCSV(workload, writer);
                System.out.println(timestamp + " : " + workload.getWl_name() + " : Downtime = " + workload.getElapsedTime() + " ms");
                workload.setEndTime(System.currentTimeMillis());
            }
            workload.setStartTime(System.currentTimeMillis());
            socket.close();
        } catch (IOException e) {
            // Connection is not reachable
            workload.setEndTime(System.currentTimeMillis());
        }
    }

    public void pingWithoutPort(Workload workload, int timeout, FileWriter writer){
        try {
            if (InetAddress.getByName(workload.getWl_ip()).isReachable(timeout)) {
                // Connection is established
                if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                    // Downtime happend recently
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    
                    writeWorkloadLineToCSV(workload, writer);
                    System.out.println(timestamp + " : " + workload.getWl_name() + " : Downtime = " + workload.getElapsedTime() + " ms");
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

    private void writeWorkloadLineToCSV(Workload workload, FileWriter writer) throws IOException {
        writer.append(new Timestamp(System.currentTimeMillis()) + "");
        writer.append(",");
        writer.append(workload.getWl_name());
        writer.append(",");
        writer.append(workload.getElapsedTime() + "");
        writer.append("\n");
        writer.flush();
        writer.close();
    }
    
}
