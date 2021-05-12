package mastercontroller.Services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mastercontroller.Observer;
import mastercontroller.Subject;
import mastercontroller.WorkloadManager;
import mastercontroller.FileManager.FilePaths;
import mastercontroller.Models.ContainerWorkload;
import mastercontroller.Models.Guest;
import mastercontroller.Models.VMWorkload;
import mastercontroller.Models.Workload;
import mastercontroller.Models.Workload.WorkloadType;

public class VMManager implements Observer {

    private FilePaths fp;
    private ArrayList<Workload> workloadList;
    private ExecutorService es;

    public VMManager(Subject WorkloadManager) {
        WorkloadManager.registerObserver(this);
        this.workloadList = new ArrayList<>();
        fp = new FilePaths();
        this.es = Executors.newCachedThreadPool();
    }
    

    // https://github.com/beabetterdevv/DesignPatterns/blob/master/patterns/observer/ForecastDisplay.java
 
 
    public Workload parseWorkloadObject(JsonObject jsonWorkload) {
        String wl_name = (String) jsonWorkload.get("Identifier").getAsString(); // (String) workloadObject.get("name");
        String wl_ip = (String) jsonWorkload.get("AccessIP").getAsString();
        int wl_port = (int) (long) jsonWorkload.get("AccessPort").getAsLong();
        boolean wl_status = (boolean) jsonWorkload.get("Available").getAsBoolean();
        boolean wl_autoMigration = (boolean) jsonWorkload.get("AutoMigrate").getAsBoolean();
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
                ContainerWorkload c_wl = new ContainerWorkload(wl_name, wl_ip, wl_port, wl_status, wl_autoMigration, wl_sharedDir, type, containerID, containerImage, checkpoint);
                return c_wl;
            case "VM":
                type = WorkloadType.VM;
                JsonObject vmProps = (JsonObject)jsonWorkload.get("VMProperties");
                String domainName = (String) vmProps.get("DomainName").getAsString();
                String connectionURI = (String) vmProps.get("ConnectionURI").getAsString();
                VMWorkload vm_wl = new VMWorkload(wl_name, wl_ip, wl_port, wl_status, wl_autoMigration, wl_sharedDir, type, domainName, connectionURI);
                return vm_wl;
            default: 
                Workload wl = new Workload(wl_name, wl_ip, wl_port, wl_status, wl_autoMigration, wl_sharedDir, null);
                return wl;
        }
    }

    public void updateWorkloadsJSON(){
        try {
            JsonObject obj = new JsonObject(); // Convert to right JSON structure
            JsonArray arr = new JsonArray();
            for (Workload workload : getWorkloads()) {
                arr.add(constructJsonObjectFromWorkload(workload));
            }
            obj.add("Workloads", arr);
            Writer writer = new FileWriter(fp.getWORKLOADPATH() + "workloads.json", false);

            // convert map to JSON File
            new Gson().toJson(obj, writer);
            
            
            // close the writer
            writer.close();
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();

        }   
    }

    public JsonObject constructJsonObjectFromWorkload(Workload workload) {
        JsonObject obj = new JsonObject();
        obj.addProperty("Identifier", workload.getWl_name());
        obj.addProperty("AccessIP", workload.getWl_ip());
        obj.addProperty("AccessPort", workload.getWl_port());
        obj.addProperty("Available", workload.isWl_status());
        obj.addProperty("AutoMigrate", workload.isWl_autoMigration());
        obj.addProperty("SharedDir", workload.getWl_sharedDir());
        switch (workload.getWl_type()) {
            case CONTAINER: 
                obj.addProperty("Type", "Container"); 
                JsonObject containerProps = new JsonObject();
                containerProps.addProperty("ContainerID", ((ContainerWorkload) workload).getContainerID());
                containerProps.addProperty("Image", ((ContainerWorkload) workload).getContainerImage());
                containerProps.addProperty("Checkpoint", ((ContainerWorkload) workload).isCheckpoint());
                obj.add("Containerproperties", containerProps);
                break;
            case VM: 
                obj.addProperty("Type", "VM"); 
                JsonObject vmProps = new JsonObject();
                vmProps.addProperty("DomainName", ((VMWorkload) workload).getDomainName());
                vmProps.addProperty("ConnectionURI", ((VMWorkload) workload).getConnectionURI());
                obj.add("VMProperties", vmProps);
                break;
        }

        return obj;
    }

   
    public ArrayList<Workload> getWorkloads() {
        return workloadList;
    }



    public void workloadDuplicates(GuestManager guestManager){
        for(Guest guest : guestManager.getGuestList()){
            for(Workload workload : guestManager.getWorkloadsFromGuest(guest, this)){
                boolean duplicate = false;
                for (int i = 0; i < getWorkloads().size(); i++) {
                    if (getWorkloads().get(i).getWl_name().equals(workload.getWl_name())) {
                        duplicate = true;
                    }
                }
                if (!duplicate) {
                    getWorkloads().add(workload);
                }
            }
        }
    }

    @Override
    public void update(Workload workload) {
        workloadList.add(workload);
        System.out.println("Workload added to list - Current list size: " + workloadList.size());
    }

    // Migrate workload to guest
    public void migrateWorkload(Workload workload, Guest guest, GuestManager guestManager){
        // Construct Migrate JSON 
        JsonObject obj = new JsonObject();
        obj.addProperty("Identifier", workload.getWl_name());
        JsonObject targetGuest = new JsonObject();
        targetGuest.addProperty("Ip", guest.getIp());
        targetGuest.addProperty("Port", guest.getPort());
        targetGuest.addProperty("StoragePath", guest.getStoragePath());
        targetGuest.addProperty("LibvirtURI", guest.getLibvirtURI());
        obj.add("TargetGuest", targetGuest);

        // Find the guest which runs the workload
        Guest guestRunningWorkload = new Guest("", "");
        for (Guest g : guestManager.getGuestList()) {
            for (Workload w : g.getWorkloads()) {
                if (w.getWl_name().equals(workload.getWl_name())) {
                    guestRunningWorkload = g;
                    break;
                }
            }
        }

        System.out.println("Migrating from " + guestRunningWorkload.getURL()+ " to " + guest.getURL());
        if (guestRunningWorkload.equals(guest)) {
            System.out.println("Cannot migrate workload to the same guest");
        }
        else if (guestRunningWorkload.getIp().length() != 0) {
            HTTPMigrate(guestRunningWorkload, obj.toString(), "/migrate");

            ArrayList<Workload> workloadsToRemove = new ArrayList<Workload>();
            for (Workload wl: getWorkloads()) {
                if (wl.getWl_name() == workload.getWl_name()){
                    workloadsToRemove.add(wl);
                }
            }
            getWorkloads().removeAll(workloadsToRemove);
        } 
        else {
            System.out.println("Cannot find the guest who runs the workload, Cannot migrate");
        }
    }

    public void HTTPMigrate(Guest guest, String json, String urlSuffix) {
        try {
            URL url = new URL (guest.getURL() + urlSuffix);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }
            try(BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("Migration Response: " + response.toString());
            }
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void update(double threshold) {
        // DO NOTHING HERE
        
    }
}
