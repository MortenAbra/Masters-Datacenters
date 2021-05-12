package mastercontroller.Models;

public class Workload {

    String wl_name = "Undefined";
    String wl_ip = "0.0.0.0";
    int wl_port = -1;
    long startTime = 0;
    long elapsedTime = 0;
    long endTime = 0;
    boolean wl_status;
    boolean wl_autoMigration;
    String wl_sharedDir;
    WorkloadType wl_type;

    public Workload(String wl_name, String wl_ip, int wl_port, boolean wl_status, boolean wl_autoMigration, String wl_sharedDir, WorkloadType wl_type) {
        this.wl_name = wl_name;
        this.wl_ip = wl_ip;
        this.wl_port = wl_port;
        this.wl_status = wl_status;
        this.wl_autoMigration = wl_autoMigration;
        this.wl_sharedDir = wl_sharedDir;
        this.wl_type = wl_type;
    }

    public enum WorkloadType {
        CONTAINER,
        VM
    }

    public long getElapsedTime() {
        return getEndTime() - getStartTime();
    }
    public long getEndTime() {
        return endTime;
    }
    public long getStartTime() {
        return startTime;
    }
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the wl_name
     */
    public String getWl_name() {
        return wl_name;
    }

    /**
     * @param wl_name the wl_name to set
     */
    public void setWl_name(String wl_name) {
        this.wl_name = wl_name;
    }

    /**
     * @return the wl_ip
     */
    public String getWl_ip() {
        return wl_ip;
    }

    /**
     * @param wl_ip the wl_ip to set
     */
    public void setWl_ip(String wl_ip) {
        this.wl_ip = wl_ip;
    }

    /**
     * @return the wl_port
     */
    public int getWl_port() {
        return wl_port;
    }

    /**
     * @param wl_port the wl_port to set
     */
    public void setWl_port(int wl_port) {
        this.wl_port = wl_port;
    }

    /**
     * @return the wl_status
     */
    public boolean isWl_status() {
        return wl_status;
    }

    /**
     * @param wl_status the wl_status to set
     */
    public void setWl_status(boolean wl_status) {
        this.wl_status = wl_status;
    }
    @Override
    public String toString() {
        return wl_name;
    }

    /**
     * @return the wl_autoMigration
     */
    public boolean isWl_autoMigration() {
        return wl_autoMigration;
    }

    /**
     * @param wl_autoMigration the wl_autoMigration to set
     */
    public void setWl_autoMigration(boolean wl_autoMigration) {
        this.wl_autoMigration = wl_autoMigration;
    }

    /**
     * @return the wl_sharedDir
     */
    public String getWl_sharedDir() {
        return wl_sharedDir;
    }

    /**
     * @param wl_sharedDir the wl_sharedDir to set
     */
    public void setWl_sharedDir(String wl_sharedDir) {
        this.wl_sharedDir = wl_sharedDir;
    }

    /**
     * @return the wl_type
     */
    public WorkloadType getWl_type() {
        return wl_type;
    }

    /**
     * @param wl_type the wl_type to set
     */
    public void setWl_type(WorkloadType wl_type) {
        this.wl_type = wl_type;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (elapsedTime ^ (elapsedTime >>> 32));
        result = prime * result + (int) (endTime ^ (endTime >>> 32));
        result = prime * result + (int) (startTime ^ (startTime >>> 32));
        result = prime * result + (wl_autoMigration ? 1231 : 1237);
        result = prime * result + ((wl_ip == null) ? 0 : wl_ip.hashCode());
        result = prime * result + ((wl_name == null) ? 0 : wl_name.hashCode());
        result = prime * result + wl_port;
        result = prime * result + ((wl_sharedDir == null) ? 0 : wl_sharedDir.hashCode());
        result = prime * result + (wl_status ? 1231 : 1237);
        result = prime * result + ((wl_type == null) ? 0 : wl_type.hashCode());
        return result;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Workload other = (Workload) obj;
        if (elapsedTime != other.elapsedTime) {
            return false;
        }
        if (endTime != other.endTime) {
            return false;
        }
        if (startTime != other.startTime) {
            return false;
        }
        if (wl_autoMigration != other.wl_autoMigration) {
            return false;
        }
        if (wl_ip == null) {
            if (other.wl_ip != null) {
                return false;
            }
        } else if (!wl_ip.equals(other.wl_ip)) {
            return false;
        }
        if (wl_name == null) {
            if (other.wl_name != null) {
                return false;
            }
        } else if (!wl_name.equals(other.wl_name)) {
            return false;
        }
        if (wl_port != other.wl_port) {
            return false;
        }
        if (wl_sharedDir == null) {
            if (other.wl_sharedDir != null) {
                return false;
            }
        } else if (!wl_sharedDir.equals(other.wl_sharedDir)) {
            return false;
        }
        if (wl_status != other.wl_status) {
            return false;
        }
        if (wl_type != other.wl_type) {
            return false;
        }
        return true;
    }

    
    
    
}
