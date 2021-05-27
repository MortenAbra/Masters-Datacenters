package mastercontroller.Models;

public class ContainerWorkload extends Workload {
    String containerID;
    String containerImage;
    boolean checkpoint;


    public ContainerWorkload(String wl_name, String wl_ip, String wl_port, boolean wl_status, boolean wl_autoMigration, String wl_sharedDir, WorkloadType wl_type,
        String containerID, String containerImage, boolean checkpoint) {
        super(wl_name, wl_ip, wl_port, wl_status, wl_autoMigration, wl_sharedDir, wl_type);
        this.containerID = containerID;
        this.containerImage = containerImage;
        this.checkpoint = checkpoint;
    }


    public String getContainerID() {
        return containerID;
    }


    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }


    public String getContainerImage() {
        return containerImage;
    }


    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }


    public boolean isCheckpoint() {
        return checkpoint;
    }


    public void setCheckpoint(boolean checkpoint) {
        this.checkpoint = checkpoint;
    }

    
}