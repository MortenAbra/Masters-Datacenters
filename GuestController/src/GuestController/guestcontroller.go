package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
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
	http.HandleFunc("/transfer", transferHandler)

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

func transferHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		fmt.Fprintf(w, "Request not supported!")
	case "POST":
		logInfo("POST Request for /transfer")
		data, err := ioutil.ReadAll(r.Body)
		logErr(err)
		decoder := json.NewDecoder(bytes.NewReader(data))

		var wl workload.Workload
		err = decoder.Decode(&wl)
		logErr(err)

		fmt.Println("Receiving Migration:" + wl.Identifier)

		if len(wl.Identifier) != 0 {
			switch wl.Type {
			case workload.CONTAINERTYPE:
				// Container migration recieved
				logInfo("Revieving Migration of container: " + wl.Identifier)
				fmt.Println("Revieving Migration of container: " + wl.Identifier)

				var wl containerworkload.ContainerWorkload
				newdecoder := json.NewDecoder(ioutil.NopCloser(bytes.NewReader(data)))
				err = newdecoder.Decode(&wl)
				logErr(err)

				finishContainerMigration(wl)

			case workload.VMTYPE:
				// VM migration recieved
				logInfo("Revieving Migration of container: " + wl.Identifier)
				fmt.Println("Revieving Migration of container: " + wl.Identifier)

				var wl vmworkload.VMWorkload
				newdecoder := json.NewDecoder(ioutil.NopCloser(bytes.NewReader(data)))
				err = newdecoder.Decode(&wl)
				logErr(err)
			}
		}
	}
}

func initiateContainerMigration(migration migrate.Migrate, container containerworkload.ContainerWorkload) {
	err := container.DockerSaveAndStoreCheckpoint(container.Properties.Checkpoint)
	logErr(err)
	err = ContainerPostMigration(migration, container)
	logErr(err)
	cleanUpAfterContainerMigration(container)
	logErr(err)
}

func finishContainerMigration(container containerworkload.ContainerWorkload) {
	logInfo("Migration of workload: " + container.Identifier)
	container.DockerLoadAndStartContainer(container.Properties.Checkpoint)
	jsonmanager.AddContainerWorkloadToSystem(container)
}

func ContainerPostMigration(migration migrate.Migrate, container containerworkload.ContainerWorkload) error {
	jsonValue, _ := json.Marshal(container)

	// Send workload to target via /transfer.
	fmt.Println("Transfering Workload Information: " + container.Identifier + " to:" + migration.TargetGuest.IP)
	prefix := migration.TargetGuest.IP + ":" + migration.TargetGuest.Port
	suffix := "/transfer"
	_, err := http.Post("http://"+prefix+suffix, "application/json", bytes.NewBuffer(jsonValue))
	if err != nil {
		return err
	}
	return err
}

func cleanUpAfterContainerMigration(container containerworkload.ContainerWorkload) error {
	err := jsonmanager.RemoveContainerFromWorkloadFile(container)
	if err != nil {
		return err
	}
	err = container.DockeRemoveContainer()
	return err
}

func initiateVMMigration(migration migrate.Migrate, VM vmworkload.VMWorkload) {
}
