package mastercontroller.Models;

public class VMWorkload extends Workload {
    


    public VMWorkload(String wl_name, String wl_ip, int wl_port, boolean wl_status, String wl_sharedDir, WorkloadType wl_type) {
        super(wl_name, wl_ip, wl_port, wl_status, wl_sharedDir, wl_type);
    }
    
}
