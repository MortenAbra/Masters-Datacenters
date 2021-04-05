package main

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/mitchellh/mapstructure"

	"GuestController/consolemanager"
	"GuestController/drlogger"
	"GuestController/guest"
	"GuestController/jsonmanager"
	"GuestController/migrate"
	"GuestController/workload"
	"GuestController/workload/containerworkload"
	"GuestController/workload/vmworkload"
)

var guestInformation = guest.Guest{}

func main() {
	Init()
}

// Logging fatal errors.
// Replaces the use of "if err != nil { panic(err) }"
func logErr(err error) {
	if err != nil {
		drlogger.DRLog(drlogger.ERR, err.Error())
		panic(err)
	}
}
func logInfo(info string) {
	drlogger.DRLog(drlogger.INFO, info)
}
func logWarn(warn string) {
	drlogger.DRLog(drlogger.WARN, warn)
}

// Initialize:
// - Logger
// - Workload.json file
// - User inputs for IP and Port
func Init() {
	drlogger.InitDRLogger()
	err := jsonmanager.CreateWorkloadFile()
	logErr(err)
	serverIP, serverPort, serverSharedDir, err := consolemanager.ReadServerIPAndPortFromUser()
	logErr(err)

	guestInformation = guest.Guest{IP: serverIP, Port: serverPort, StoragePath: serverSharedDir}

	logInfo("Initialization done for " + serverIP + ":" + serverPort)

	setupServer(serverIP, serverPort)
}

// Setup http listener
func setupServer(serverIP string, serverPort string) error {
	drlogger.DRLog(drlogger.INFO, "Setting up handlers")
	http.HandleFunc("/guest", guestHandler)
	http.HandleFunc("/guest/workloads", guestWorkloadsHandler)
	http.HandleFunc("/workloads", workloadsHandler)
	http.HandleFunc("/migrate", migrateHandler)

	logInfo("Listening on " + serverIP + ":" + serverPort)
	err := http.ListenAndServe(serverIP+":"+serverPort, nil)

	return err
}

func guestHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		logInfo("GET Request for /guest")
		// Read Active Workloads
		result, err := json.Marshal(guestInformation)
		logErr(err)

		fmt.Fprintf(w, string(result)+"")
		logInfo("Responding: " + string(result) + "")

	case "POST":
		fmt.Fprintf(w, "Request not supported!")
	}
}

func guestWorkloadsHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		logInfo("GET Request for /guest/workload")
		// Read Active Workloads
		// Each workload is at this point an interface.. Not a workload
		workloads := guestInformation.GetAllWorkloadsRunningOnGuest()
		result, err := json.Marshal(workloads)
		logErr(err)

		fmt.Fprintf(w, string(result)+"")
		logInfo("Responding: " + string(result) + "")

	case "POST":
		fmt.Fprintf(w, "Request not supported!")
	}
}

func workloadsHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		logInfo("GET Request for /guest/workload")
		// Read Active Workloads
		result, err := jsonmanager.GetWorkloadFileAsJSON()
		logErr(err)
		fmt.Fprintf(w, string(result)+"")
		logInfo("Responding: " + string(result) + "")

	case "POST":
		logInfo("POST Request for /workloads")
		err := jsonmanager.AddWorkloadToSystem(r)
		logErr(err)
	}
}

func migrateHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		fmt.Fprintf(w, "Request not supported!")
	case "POST":
		logInfo("POST Request for /migrate")

		decoder := json.NewDecoder(r.Body)
		var migration migrate.Migrate

		err := decoder.Decode(&migration)
		logErr(err)

		workloadlist, err := jsonmanager.GetWorkloadFileAsWorkloads()
		logErr(err)
		var migrationWorkload workload.Workload
		for i := 0; i < len(workloadlist.Workloads); i++ {
			mapstructure.Decode(workloadlist.Workloads[i], &migrationWorkload)
			// Find the workload to migrate
			if migrationWorkload.Identifier == migration.Identifier {
				if len(migrationWorkload.Type) != 0 {
					switch migrationWorkload.Type {
					case workload.CONTAINERTYPE:
						// Container migration Start
						var container containerworkload.ContainerWorkload
						mapstructure.Decode(workloadlist.Workloads[i], &container)

						fmt.Println("Migrating container: " + migration.Identifier + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
						logInfo("Migrating container: " + migration.Identifier + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)

						initiateContainerMigration(migration, container) // Go Routine?

					case workload.VMTYPE:
						// VM migration start
						var vm vmworkload.VMWorkload
						mapstructure.Decode(workloadlist.Workloads[i], &vm)

						fmt.Println("Migrating VM: " + migration.Identifier + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
						logInfo("Migrating VM: " + migration.Identifier + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)

						// TODO
						initiateVMMigration(migration, vm)
					}
				}
			}
		}
	}
}

func initiateContainerMigration(migration migrate.Migrate, container containerworkload.ContainerWorkload) {
	container.DockerSaveAndStoreCheckpoint(container.Properties.Checkpoint)

}
func initiateVMMigration(migration migrate.Migrate, VM vmworkload.VMWorkload) {
}
