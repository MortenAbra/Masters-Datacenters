package mastercontroller.Services;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import mastercontroller.FileManager.FilePaths;
import mastercontroller.Models.Guest;
import mastercontroller.Models.Workload;

public class GuestManager {
    private FilePaths fp;
    private ArrayList<Guest> guestList = new ArrayList<Guest>();

    public GuestManager() {
        fp = new FilePaths();
    }

    public void initialize(VMManager vmManager) {
        try (FileReader reader = new FileReader(fp.getGUESTPATH() + "guests.json")) {
            // Generating JSONObject based on a file
            Object obj = JsonParser.parseReader(reader);
            JsonObject jsonObject = (JsonObject) obj;
            JsonArray jsonArray = (JsonArray) jsonObject.get("Guests");

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject guestObject = (JsonObject) jsonArray.get(i);
                Guest g = new Guest(guestObject.get("Ip").getAsString(), guestObject.get("Port").getAsString());
                guestList.add(g);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Ping all on ip:port/guest
        // retrieve workloads from ip:port/workloads
    }

    public ArrayList<Guest> getGuestList() {
        return guestList;
    }

    // Calls ip:port/workloads to get workloads running on guest
    public ArrayList<Workload> getWorkloadsFromGuest(Guest g, VMManager vmManager) {
        ArrayList<Workload> workloadList = new ArrayList<Workload>();
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
                        workloadList.add(vmManager.parseWorkloadObject((JsonObject) jsonArray.get(i)));
                    }
                }
            } catch (IOException | InterruptedException e) {
                System.out.println("Workloads not available: " + toString());
            }
        }

        return workloadList;
    }
}
