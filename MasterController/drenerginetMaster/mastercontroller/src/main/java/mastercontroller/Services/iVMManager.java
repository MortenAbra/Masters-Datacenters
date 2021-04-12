package mastercontroller.Services;

import java.io.FileWriter;
import java.util.ArrayList;

import mastercontroller.Models.Workload;

public interface iVMManager {
    
    public Workload getAllVMs(ArrayList<Workload> workloadList);
    public ArrayList<Workload> findAvailableVMs();
    public Workload getActiveVMObject(ArrayList<Workload> activeVMList);
    public ArrayList<String> getAvailableVMsIPAsList();
    public void pingHost(Workload workload, FileWriter writer);
    public ArrayList<Workload> getWorkloads();
    
}
