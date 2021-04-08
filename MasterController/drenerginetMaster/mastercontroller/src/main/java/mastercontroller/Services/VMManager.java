package mastercontroller.Services;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mastercontroller.FileManager.FilePaths;
import mastercontroller.Models.Workload;

public class VMManager implements iVMManager {

    private FilePaths fp;
    private ArrayList<Workload> workloadList;
    private JsonParser parser = new JsonParser();

    public VMManager(){
        fp = new FilePaths();
        workloadList = new ArrayList<>();
        try {
            addJSONWorkloadToList();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
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

                //TextOutPut(timestamp + " : " + workload.getWl_name() + " : Downtime = " + workload.getElapsedTime() + " ms");
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

                    

                    //TextOutPut(timestamp + " : " + workload.getWl_name() + " : Downtime = " + workload.getElapsedTime() + " ms");
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

    @Override
    public Workload getAllVMs(ArrayList<Workload> workloadlist){
        for (Workload workload : workloadlist) {
            return workload;
        }
        return null;
    }

    @Override
    public ArrayList<Workload> findActiveVMs(ArrayList<Workload> vmList){
        ArrayList<Workload> activeVMs = new ArrayList<>();
        for (Workload vm : vmList) {
            //TextOutPut("VM: " + vm.getWl_name() + " is - " + vm.isWl_status());
            if(vm.isWl_status()){
                if(activeVMs.contains(vm)){
                    //TextOutPut("VM already in available list...");
                } else {
                    //TextOutPut("Adding vm to available list...");
                    activeVMs.add(vm);
                }
            } else {
                //TextOutPut(vm.getWl_name() + ": Unavailable...");
            }            
        }
        return activeVMs;
    }


    public Workload parseWorkloadObject(JsonObject jsonWorkload){
        //Workload workload = gson.fromJson(String.valueOf(jsonWorkload.getAsJsonObject("workload")), Workload.class);
        String wl_name = (String) jsonWorkload.get("name").getAsString(); //(String) workloadObject.get("name");
        String wl_ip = (String) jsonWorkload.get("ip").getAsString();
        int wl_port = (int) (long) jsonWorkload.get("port").getAsLong();
        boolean wl_status = (boolean) jsonWorkload.get("available").getAsBoolean();
        Workload wl = new Workload(wl_name, wl_ip, wl_port, wl_status);
        return wl;
    }



    //Generating workload json objects and adding to Array
    public void addJSONWorkloadToList() throws IOException{
        try (FileReader reader = new FileReader(fp.getWORKLOADPATH()+"workloads.json")){
            //Generating JSONObject based on a file
            Object obj = parser.parse(reader);
            //Object obj = (JsonObject) JsonParser.parseReader(new BufferedReader(reader));
            JsonObject jsonObject = (JsonObject) obj;

            JsonArray jsonArray = (JsonArray) jsonObject.get("workloads");
            

            //Adding workloads to the list
            
            for (int i = 0; i < jsonArray.size(); i++) {
                workloadList.add(parseWorkloadObject((JsonObject) jsonArray.get(i)));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Workload getActiveVMObject(ArrayList<Workload> activeVMList){
        Workload vmObject = activeVMList.get(0);
        activeVMList.get(0).setWl_status(true);
        activeVMList.remove(0);
        return vmObject;
    }

    @Override
    public ArrayList<String> getActiveVMsIPAsList(ArrayList<Workload> vmList){
        ArrayList<String> vmIpList = new ArrayList<>();
        for (Workload object : vmList) {
            vmIpList.add(object.getWl_ip());
        }
        return vmIpList;
    }

    @Override
    public ArrayList<Workload> getWorkloads() {
        return workloadList;
    }

    
}
