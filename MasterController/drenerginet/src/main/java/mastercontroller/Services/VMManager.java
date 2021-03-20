package mastercontroller.Services;

import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import mastercontroller.FileHandler.FileHandler;
import mastercontroller.Models.Workload;

public class VMManager {

    private FileHandler fh;
    private ArrayList<JsonObject> vmList;

    public VMManager(){
        fh = new FileHandler();
        vmList = new ArrayList<>();
    }

    public void pingHost(Workload workload, FileWriter writer){
        int timeout = 10;
        if(workload.getWl_port() != -1){
            pingWithPort(workload, timeout, writer);
        } else {
            pingWithoutPort(workload, timeout, writer);
        }
    }

    public void pingWithPort(Workload workload, int timeout, FileWriter writer){
        fh.TextOutPut("Test - 1");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(workload.getWl_ip(), workload.getWl_port()), timeout);
            fh.TextOutPut("Test - 2");
            // Connection is established
            if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                fh.TextOutPut("Test - 3");
                // Downtime happend recently
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                writeWorkloadLineToCSV(workload, writer);
                fh.TextOutPut(timestamp + " : " + workload.getWl_name() + " : Downtime = " + workload.getElapsedTime() + " ms");
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
                    fh.TextOutPut(timestamp + " : " + workload.getWl_name() + " : Downtime = " + workload.getElapsedTime() + " ms");
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

    private ArrayList<Workload> findActiveVMs(ArrayList<Workload> vmList){
        ArrayList<Workload> activeVMs = new ArrayList<>();
        for (Workload vm : vmList) {
            fh.TextOutPut("VM: " + vm.getWl_name() + " is - " + vm.isWl_status());
            if(vm.isWl_status()){
                if(activeVMs.contains(vm)){
                    fh.TextOutPut("VM already in available list...");
                } else {
                    fh.TextOutPut("Adding vm to available list...");
                    activeVMs.add(vm);
                }
            } else {
                fh.TextOutPut(vm.getWl_name() + ": Unavailable...");
            }            
        }
        return activeVMs;
    }

    private Workload getActiveVMObject(ArrayList<Workload> activeVMList){
        Workload vmObject = activeVMList.get(0);
        activeVMList.get(0).setWl_status(true);
        activeVMList.remove(0);
        return vmObject;
    }

    private String getSingleActiveVMsIP(Workload object){
        return object.getWl_ip();
    }

    private ArrayList<String> getActiveVMsIPAsList(ArrayList<Workload> vmList){
        ArrayList<String> vmIpList = new ArrayList<>();
        for (Workload object : vmList) {
            vmIpList.add(object.getWl_ip());
        }
        return vmIpList;
    }
    
}
