package mastercontroller.Services;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mastercontroller.Observer;
import mastercontroller.Subject;
import mastercontroller.FileManager.FilePaths;
import mastercontroller.Models.ContainerWorkload;
import mastercontroller.Models.VMWorkload;
import mastercontroller.Models.Workload;
import mastercontroller.Models.Workload.WorkloadType;

public class VMManager implements iVMManager, Observer {

    private FilePaths fp;
    private ArrayList<Workload> workloadList;
    private JsonParser parser = new JsonParser();
    private ExecutorService es;

    public VMManager(Subject WorkloadManager) {
        WorkloadManager.registerObserver(this);
        this.workloadList = new ArrayList<>();
        fp = new FilePaths();
        this.es = Executors.newCachedThreadPool();
    }

    // https://github.com/beabetterdevv/DesignPatterns/blob/master/patterns/observer/ForecastDisplay.java

    @Override
    public void pingHost(Workload workload, FileWriter writer) {
        int timeout = 10;
        if (workload.getWl_port() != -1) {
            pingWithPort(workload, timeout, writer);
        } else {
            pingWithoutPort(workload, timeout, writer);
        }
    }

    public void pingWithPort(Workload workload, int timeout, FileWriter writer) {

        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(workload.getWl_ip(), workload.getWl_port()), timeout);

            // Connection is established
            if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {

                // Downtime happend recently
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                // TextOutPut(timestamp + " : " + workload.getWl_name() + " : Downtime = " +
                // workload.getElapsedTime() + " ms");
                workload.setEndTime(System.currentTimeMillis());
            }
            workload.setStartTime(System.currentTimeMillis());
            socket.close();
        } catch (IOException e) {
            // Connection is not reachable
            workload.setEndTime(System.currentTimeMillis());
        }
    }

    public void pingWithoutPort(Workload workload, int timeout, FileWriter writer) {
        try {
            if (InetAddress.getByName(workload.getWl_ip()).isReachable(timeout)) {
                // Connection is established
                if (workload.getElapsedTime() > 1000 && workload.getElapsedTime() < 1000000) {
                    // Downtime happend recently
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                    // TextOutPut(timestamp + " : " + workload.getWl_name() + " : Downtime = " +
                    // workload.getElapsedTime() + " ms");
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
    public Workload getAllVMs(ArrayList<Workload> workloadlist) {
        for (Workload workload : workloadlist) {
            return workload;
        }
        return null;
    }

    @Override
    public ArrayList<Workload> findActiveVMs(ArrayList<Workload> vmList) {
        ArrayList<Workload> activeVMs = new ArrayList<>();
        for (Workload vm : vmList) {
            // TextOutPut("VM: " + vm.getWl_name() + " is - " + vm.isWl_status());
            if (vm.isWl_status()) {
                if (activeVMs.contains(vm)) {
                    // TextOutPut("VM already in available list...");
                } else {
                    // TextOutPut("Adding vm to available list...");
                    activeVMs.add(vm);
                }
            } else {
                // TextOutPut(vm.getWl_name() + ": Unavailable...");
            }
        }
        return activeVMs;
    }

    public Workload parseWorkloadObject(JsonObject jsonWorkload) {
        // Workload workload =
        // gson.fromJson(String.valueOf(jsonWorkload.getAsJsonObject("workload")),
        // Workload.class);
        String wl_name = (String) jsonWorkload.get("Identifier").getAsString(); // (String) workloadObject.get("name");
        String wl_ip = (String) jsonWorkload.get("AccessIP").getAsString();
        int wl_port = (int) (long) jsonWorkload.get("AccessPort").getAsLong();
        boolean wl_status = (boolean) jsonWorkload.get("Available").getAsBoolean();
        String wl_sharedDir = (String) jsonWorkload.get("SharedDir").getAsString();
        String wl_type = (String) jsonWorkload.get("Type").getAsString();

        // Determines the type of the workload
        Workload.WorkloadType type;
        switch (wl_type) {
            case "Container":
                type = WorkloadType.CONTAINER;
                JsonObject containerProps = (JsonObject)jsonWorkload.get("Containerproperties");
                String containerID = (String) containerProps.get("ContainerID").getAsString();
                String containerImage = (String) containerProps.get("Image").getAsString();
                boolean checkpoint = (boolean) containerProps.get("Checkpoint").getAsBoolean();
                ContainerWorkload c_wl = new ContainerWorkload(wl_name, wl_ip, wl_port, wl_status, wl_sharedDir, type, containerID, containerImage, checkpoint);
                return c_wl;
            case "VM":
                type = WorkloadType.VM;
                JsonObject vmProps = (JsonObject)jsonWorkload.get("VMProperties");
                String domainName = (String) vmProps.get("DomainName").getAsString();
                String connectionURI = (String) vmProps.get("ConnectionURI").getAsString();
                VMWorkload vm_wl = new VMWorkload(wl_name, wl_ip, wl_port, wl_status, wl_sharedDir, type, domainName, connectionURI);
                return vm_wl;
            default: 
                Workload wl = new Workload(wl_name, wl_ip, wl_port, wl_status, wl_sharedDir, null);
                return wl;
        }
    }

    // Generating workload json objects and adding to Array
    public ArrayList<Workload> readJSONWorkloads() throws IOException {
        ArrayList<Workload> resultList = new ArrayList<>();
        try (FileReader reader = new FileReader(fp.getWORKLOADPATH() + "workloads.json")) {
            // Generating JSONObject based on a file
            Object obj = parser.parse(reader);
            // Object obj = (JsonObject) JsonParser.parseReader(new BufferedReader(reader));
            JsonObject jsonObject = (JsonObject) obj;

            JsonArray jsonArray = (JsonArray) jsonObject.get("Workloads");

            // Adding workloads to the list
            for (int i = 0; i < jsonArray.size(); i++) {
                resultList.add(parseWorkloadObject((JsonObject) jsonArray.get(i)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }

    @Override
    public Workload getActiveVMObject(ArrayList<Workload> activeVMList) {
        Workload vmObject = activeVMList.get(0);
        activeVMList.get(0).setWl_status(true);
        activeVMList.remove(0);
        return vmObject;
    }

    @Override
    public ArrayList<String> getActiveVMsIPAsList(ArrayList<Workload> vmList) {
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

    @Override
    public void update(Workload workload) {
        // TODO Auto-generated method stub
        workloadList.add(workload);
        System.out.println("Workload added to list - Current list size: " + workloadList.size());
    }

}
