# Masters-Datacenters
Master project on SDU 2021

By Morten Abrahamsen & Nikolaj Steenberg


## Installation / Setup 
The solution consists of two pieces of software
- MasterController
- GuestController

### MasterController (UI)
The controller software is located in MasterController. 
It runs on Java and is built using Maven.

### GuestController
Run the executable loacted in GuestController/src/ called guestcontroller. 
If on a Windows based system, you will need to build the executable using "go build guestcontroller.go"
Dependencies for building the executable:
- Libvirt v6.0.0+


## Usage scenario
1. Create two VM's on a host machine. Each of these VM's will be refered to as VM1 and VM2.
2. Install a suitable OS on each VM. It has to be Linux based, and be able to install CRIU and Libvirt. (Debain 11 works perfectly)
3. Create shared storage between VM1 and VM2. This can also be facilitated through the host for easy file sharing between host and the VM's. 
4. Create a folder within the shared storage for each VM.
5. Copy the guestcontroller executable into each folder.
6. Add the IP and a suitable port for each VM into the guests.json located in MasterController/drenerginetMaster/mastercontroller/src/main/java/mastercontroller/Guests/guests.json
7. Start each guestcontroller on each VM. The executable will ask for an IP and a port. Choose the same as was written in guest.json. The two other arguments which the guestcontroller asks for is not needed, and can be excluded for this example.
8. Create a sample workload on VM1. This can be a Docker container or a VM within Virsh for example. However this example will focus on a Docker Container.
- To run a busybox container use the following command: docker run -d -i -t --name busybox_container busybox  /bin/sh 
9. Now start the MasterController and add the workload through the UI. Press the add workload button in the buttom left and paste the JSON specification of the sample workload (The specifications used during this example can be found at the end of the README. Remember to change the SharedDir to the filepath of the shared directory).
10. Then choose the guest which the workload is running on i.e. the IP and Port of VM1.
11. The software is now ready to be used. The workload should be showing within the left pane of the UI, and can be migrated to the other VM through the UI. 
```
{
    "Identifier": "busybox_container",
    "AccessIP": "0",
    "AccessPort": "0",
    "AutoMigrate": false,
    "Available": true,
    "SharedDir": "FILEPATH TO SHARED DIR",
    "Type": "Container",
    "Containerproperties": {
        "ContainerID": "busybox_container",
        "Image": "busybox",
        "Checkpoint": false
    }
}
```
