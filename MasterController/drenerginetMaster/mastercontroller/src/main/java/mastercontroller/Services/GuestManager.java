package mastercontroller.Services;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashSet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mastercontroller.FileManager.FilePaths;
import mastercontroller.Models.Guest;
import mastercontroller.Models.Workload;

public class GuestManager {
    private FilePaths fp;
    private HashSet<Guest> guestList;

    public GuestManager() {
        fp = new FilePaths();
        guestList = new HashSet<Guest>();
    }

    public void initialize(VMManager vmManager) {
        try (FileReader reader = new FileReader(fp.getGUESTPATH() + "guests.json")) {
            // Generating JSONObject based on a file
            Object obj = JsonParser.parseReader(reader);
            JsonObject jsonObject = (JsonObject) obj;
            JsonArray jsonArray = (JsonArray) jsonObject.get("Guests");

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject guestObject = (JsonObject) jsonArray.get(i);
                Guest g = new Guest(
                    guestObject.get("Ip").getAsString(), 
                    guestObject.get("Port").getAsString(),
                    guestObject.get("LibvirtURI").getAsString());
                
                
                boolean duplciate = false;
                for (Guest oldGuest : guestList) {
                    if (oldGuest.getIp().equals(g.getIp())) {
                        // Duplicate
                        duplciate = true;
                    }
                }
                if (!duplciate) {
                    // If not duplicate then add the guest
                    guestList.add(g);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HashSet<Guest> getGuestList() {
        return guestList;
    }

    // Calls ip:port/workloads to get workloads running on guest
    public void getWorkloadsFromGuests(VMManager vmManager) {
        vmManager.getWorkloads().clear();
        for (Guest g : guestList) {
            g.getWorkloads().clear();
            if (g.isOnline()) {
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(g.getURL() + "/workloads")).build();
                HttpResponse<String> response;
                try {
                    response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.body().length() != 0) {
                        Object obj = JsonParser.parseString(response.body());
                        JsonObject jsonObject = (JsonObject) obj;

                        JsonArray jsonArray = (JsonArray) jsonObject.get("Workloads");
                        for (int i = 0; i < jsonArray.size(); i++) {
                            Workload workload = vmManager.parseWorkloadObject((JsonObject) jsonArray.get(i));
                            vmManager.getWorkloads().add(workload);
                            g.getWorkloads().add(workload); // Add workload to guest.
                        }

                    }
                } catch (IOException | InterruptedException e) {
                    System.out.println("Workloads not available: " + toString());
                }
            }
        }
    }
}
