package mastercontroller.Services;

import java.io.FileWriter;
import java.util.ArrayList;

import mastercontroller.Models.Workload;

public interface iVMManager {
    
    public Workload getAllVMs(ArrayList<Workload> workloadList);
    public ArrayList<Workload> findActiveVMs(ArrayList<Workload> vmList);
    public Workload getActiveVMObject(ArrayList<Workload> activeVMList);
    public ArrayList<String> getActiveVMsIPAsList(ArrayList<Workload> vmList);
    public void pingHost(Workload workload, FileWriter writer);
    public ArrayList<Workload> getWorkloads();
    
}
