package mastercontroller.Models;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Guest {
    String ip;
    String port;
    boolean online = false;
    ArrayList<Workload> workloads = new ArrayList<Workload>();
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



    public ArrayList<Workload> getWorkloads() {
        return workloads;
    }



    public void setWorkloads(ArrayList<Workload> workloads) {
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
}
