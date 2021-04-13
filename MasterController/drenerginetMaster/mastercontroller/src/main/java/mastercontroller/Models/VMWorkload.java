package mastercontroller.Models;

public class VMWorkload extends Workload {

    String domainName;
    String connectionURI;



    public VMWorkload(String wl_name, String wl_ip, int wl_port, boolean wl_status, boolean wl_autoMigration, String wl_sharedDir, WorkloadType wl_type, String domainName, String connectionURI) {
        super(wl_name, wl_ip, wl_port, wl_status, wl_autoMigration, wl_sharedDir, wl_type);
        this.domainName = domainName;
        this.connectionURI = connectionURI;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getConnectionURI() {
        return connectionURI;
    }

    public void setConnectionURI(String connectionURI) {
        this.connectionURI = connectionURI;
    }
}
