package mastercontroller.Models;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Guest {
    String ip;
    String port;
    boolean online = false;
    HashSet<Workload> workloads = new HashSet<Workload>();
    String storagePath;
    String libvirtURI;

    public Guest(String ip, String port) {
        this.ip = ip;
        this.port = port;
    }


    // Calls ip:port/guest to see if guest is online
    public boolean isOnline() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getURL() + "/guest"))
                .build();

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.body().length() != 0) {
                Object obj = JsonParser.parseString(response.body());
                JsonObject jsonObject = (JsonObject) obj;
                storagePath = jsonObject.get("StoragePath").getAsString();
                libvirtURI = jsonObject.get("LibvirtURI").getAsString();

                online = true;
                return true;
            } else {
                return false;
            }
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public String getIp() {
        return ip;
    }


    public void setIp(String ip) {
        this.ip = ip;
    }


    public String getPort() {
        return port;
    }


    public void setPort(String port) {
        this.port = port;
    }


    @Override
    public String toString() {
        return getURL();
    }


    public String getURL() {
        return "http://" + getIp() + ":" + getPort();
    }



    public HashSet<Workload> getWorkloads() {
        return workloads;
    }



    public void setWorkloads(HashSet<Workload> workloads) {
        this.workloads = workloads;
    }


    public String getStoragePath() {
        return storagePath;
    }


    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }


    public String getLibvirtURI() {
        return libvirtURI;
    }


    public void setLibvirtURI(String libvirtURI) {
        this.libvirtURI = libvirtURI;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((libvirtURI == null) ? 0 : libvirtURI.hashCode());
        result = prime * result + (online ? 1231 : 1237);
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((storagePath == null) ? 0 : storagePath.hashCode());
        result = prime * result + ((workloads == null) ? 0 : workloads.hashCode());
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
        Guest other = (Guest) obj;
        if (ip == null) {
            if (other.ip != null) {
                return false;
            }
        } else if (!ip.equals(other.ip)) {
            return false;
        }
        if (libvirtURI == null) {
            if (other.libvirtURI != null) {
                return false;
            }
        } else if (!libvirtURI.equals(other.libvirtURI)) {
            return false;
        }
        if (online != other.online) {
            return false;
        }
        if (port == null) {
            if (other.port != null) {
                return false;
            }
        } else if (!port.equals(other.port)) {
            return false;
        }
        if (storagePath == null) {
            if (other.storagePath != null) {
                return false;
            }
        } else if (!storagePath.equals(other.storagePath)) {
            return false;
        }
        if (workloads == null) {
            if (other.workloads != null) {
                return false;
            }
        } else if (!workloads.equals(other.workloads)) {
            return false;
        }
        return true;
    }

    
}
