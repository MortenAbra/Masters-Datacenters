package drping;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;


public class App  {
    static String workloadPath = "PingMaster/pingmaster/src/main/java/drping/workloads.json";
    static String outputPath = "PingMaster/pingmaster/src/main/java/drping/output.csv";

    
    public static void main( String[] args ) throws IOException
    {
        ArrayList<Workload> workloads = new JsonReader(workloadPath).readJSON();

        
        // Check if output file exists. If not create one and append header.
        FileWriter csvWriter;
        if (!fileExists(outputPath)) {
            new File(outputPath).createNewFile();
            csvWriter = new FileWriter(outputPath, false);

            appendCSVHeader(csvWriter);
        } 

        csvWriter = new FileWriter(outputPath, true);
        

        // Main Loop for pinging
        while(true) {
            for (int i = 0; i < workloads.size(); i++) {
                pingHost(workloads.get(i), csvWriter);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }    
    }


    private static void appendCSVHeader(FileWriter csvWriter) throws IOException {
        csvWriter.append("Timestamp");
        csvWriter.append(",");
        csvWriter.append("Name");
        csvWriter.append(",");
        csvWriter.append("Downtime");
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }


    public static void pingHost(Workload workload, FileWriter csvWriter) {
        int timeout = 10;
        if (workload.wl_port != -1) {
            pingWithPort(workload, timeout, csvWriter);
        } else {
            pingWithoutPort(workload, timeout, csvWriter);
        }
    }

    private static void pingWithoutPort(Workload workload, int timeout, FileWriter csvWriter) {
        try {
            if (InetAddress.getByName(workload.wl_ip).isReachable(timeout)) {
                // Connection is established
                if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                    // Downtime happend recently
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    
                    writeWorkloadLineToCSV(workload, csvWriter);
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

    private static void pingWithPort(Workload workload, int timeout, FileWriter csvWriter) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(workload.wl_ip, workload.wl_port), timeout);
            // Connection is established
            if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                // Downtime happend recently
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                writeWorkloadLineToCSV(workload, csvWriter);
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


    private static void writeWorkloadLineToCSV(Workload workload, FileWriter csvWriter) throws IOException {
        csvWriter.append(new Timestamp(System.currentTimeMillis()) + "");
        csvWriter.append(",");
        csvWriter.append(workload.wl_name);
        csvWriter.append(",");
        csvWriter.append(workload.getElapsedTime() + "");
        csvWriter.append("\n");
        csvWriter.flush();
        csvWriter.close();
    }

    private static boolean fileExists(String filePathString) {
        File f = new File(filePathString);
        if(f.exists() && !f.isDirectory()) { 
            return true;
        } else {
            return false;
        }
    }

    
}