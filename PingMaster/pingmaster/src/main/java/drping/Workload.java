package drping;

public class Workload {

    String wl_name = "Undefined";
    String wl_ip = "0.0.0.0";
    String wl_port = "0000";

    public Workload(String wl_name, String wl_ip, String wl_port) {
        this.wl_name = wl_name;
        this.wl_ip = wl_ip;
        this.wl_port = wl_port;
    }

    @Override
    public String toString() {
        String s = wl_name + "  |  " + wl_ip + ":" + wl_port;
        return s;
    }

}
