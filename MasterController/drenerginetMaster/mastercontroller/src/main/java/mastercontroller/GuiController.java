package mastercontroller;

import java.util.ArrayList;
import java.util.List;

import mastercontroller.Models.Workload;
import mastercontroller.Services.VMManager;
import mastercontroller.Services.iVMManager;

public class GuiController {



    private iVMManager manager;
    private List<Workload> workloadList;

    public GuiController(Subject vmManager) {
        this.workloadList = new ArrayList<>();
    }

    public String[] fillVMList(ArrayList<Workload> workloadList){
        ArrayList<String> vms = new ArrayList<>();
        if(workloadList.isEmpty()){
            manager = new VMManager();
        } else {
            for (Workload workload : workloadList){
                System.out.println(workload.toString());
                vms.add(workload.toString());
            }
        }
        return vms.toArray(String[]::new);
    }

    public String[] fillVMList(){
        manager = new VMManager();
        ArrayList<String> allVMs = new ArrayList<>();
        for (Workload workloads : manager.getWorkloads()) {
            allVMs.add(workloads.getWl_name());
            System.out.println(workloads.toString());
        }
        
        return allVMs.toArray(String[]::new);
    }

    

    /**
     * @return the workloadList
     */
    public List<Workload> getWorkloadList() {
        return workloadList;
    }

    /**
     * @param workloadList the workloadList to set
     */
    public void setWorkloadList(List<Workload> workloadList) {
        this.workloadList = workloadList;
    }


}