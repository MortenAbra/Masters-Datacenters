package main

import (
	"bufio"
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"time"

	"github.com/mitchellh/mapstructure"

	"GuestController/consolemanager"
	"GuestController/drlogger"
	"GuestController/guest"
	"GuestController/jsonmanager"
	"GuestController/jsonmanager/timerresponse"
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

func logTime(time string, name string) {
	logInfo(name + " took " + time)
}

// Initialize:
// - Logger
// - Workload.json file
// - User inputs for IP and Port
func Init() {
	drlogger.InitDRLogger()
	err := jsonmanager.CreateWorkloadFile()
	logErr(err)
	serverIP, serverPort, serverSharedDir, serverLibvirtURI, err := consolemanager.ReadServerIPAndPortFromUser()
	logErr(err)

	guestInformation = guest.Guest{IP: serverIP, Port: serverPort, StoragePath: serverSharedDir, LibvirtURI: serverLibvirtURI}

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
	http.HandleFunc("/logs", logsHandler)

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
		logInfo("GET Request for /workload")
		// Read Active Workloads
		result, err := jsonmanager.GetWorkloadFileAsJSON()
		logErr(err)
		fmt.Fprintf(w, string(result)+"")
		logInfo("Responding: " + string(result) + "")

	case "POST":
		logInfo("POST Request for /workloads")
		err := jsonmanager.AddWorkloadToSystemHTTP(r)
		logErr(err)
	}
}

func migrateHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		fmt.Fprintf(w, "Request not supported!")
	case "POST":
		logInfo("POST Request for /migrate")

		// Timer Object
		data, err := ioutil.ReadAll(r.Body)
		logErr(err)
		decoder := json.NewDecoder(bytes.NewReader(data))

		var migration migrate.Migrate

		err = decoder.Decode(&migration)
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

						// Convert interface -> JSON then JSON -> ContainerWorkload
						// Fix because mapstructure.Decode doesn't decode workload correctly (Returns empty struct)
						wlJSON, _ := json.Marshal(workloadlist.Workloads[i])
						json.Unmarshal(wlJSON, &container)

						fmt.Println("Migrating container: " + migration.Identifier + " to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
						logInfo("Migrating container: " + migration.Identifier + " to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)

						start := time.Now()
						jsonResponse := initiateContainerMigration(migration, container) // Go Routine?
						elapsed := time.Since(start)
						jsonResponse.Network = elapsed - jsonResponse.CPU - jsonResponse.Disc - jsonResponse.Memory

						timerOutput := jsonmanager.GetJSONResponseAsJSON(jsonResponse)

						logErr(err)
						fmt.Fprintf(w, timerOutput)

					case workload.VMTYPE:
						// VM migration start
						var vm vmworkload.VMWorkload

						// Convert interface -> JSON then JSON -> ContainerWorkload
						// Fix because mapstructure.Decode doesn't decode workload correctly (Returns empty struct)
						wlJSON, _ := json.Marshal(workloadlist.Workloads[i])
						json.Unmarshal(wlJSON, &vm)

						fmt.Println("Migrating VM: " + migration.Identifier + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
						logInfo("Migrating VM: " + migration.Identifier + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)

						start := time.Now()
						jsonResponse := initiateVMMigration(migration, vm) // Go Routine?
						elapsed := time.Since(start)
						jsonResponse.Network = elapsed - jsonResponse.CPU - jsonResponse.Disc - jsonResponse.Memory

						timerOutput := jsonmanager.GetJSONResponseAsJSON(jsonResponse)

						logErr(err)
						fmt.Fprintf(w, timerOutput)
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

				// Send time back to Migrator
				jsonResponse := finishContainerMigration(wl)
				response, err := json.Marshal(jsonResponse)
				logErr(err)
				fmt.Fprintf(w, string(response))

			case workload.VMTYPE:
				// VM migration recieved
				logInfo("Revieving Migration of VM: " + wl.Identifier)
				fmt.Println("Revieving Migration of VM: " + wl.Identifier)

				var wl vmworkload.VMWorkload
				newdecoder := json.NewDecoder(ioutil.NopCloser(bytes.NewReader(data)))
				err = newdecoder.Decode(&wl)
				logErr(err)

				// Send time back to Migrator
				jsonResponse := finishVMMigration(wl)
				response, err := json.Marshal(jsonResponse)
				logErr(err)
				fmt.Fprintf(w, string(response))
			}

		}
	}
}

func logsHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		// Reads the drguest.log file and retrieves it line by line
		logInfo("GET Request for /logs")
		file, err := os.Open(drlogger.LogFileName)
		logErr(err)

		defer file.Close()
		scanner := bufio.NewScanner(file)
		for scanner.Scan() {
			fmt.Fprintf(w, scanner.Text()+"\n")
		}

	case "POST":
		fmt.Fprintf(w, "Request not supported!")
	}
}

// Initial Migration part of the Container Migration
func initiateContainerMigration(migration migrate.Migrate, container containerworkload.ContainerWorkload) timerresponse.TimerResponse {
	var jsonResponse timerresponse.TimerResponse
	// Stopwatch
	save_start := time.Now()
	cputime, disctime, ramtime, err := container.DockerSaveAndStoreCheckpoint(container.Properties.Checkpoint)
	logErr(err)
	logTime(time.Since(save_start).String(), "DockerSaveAndStoreCheckpoint("+container.Identifier+")")

	jsonResponse.CPU = cputime
	jsonResponse.Disc = disctime
	jsonResponse.Memory = ramtime

	timeResponse, err := ContainerPostMigration(migration, container)
	logErr(err)

	var postResponse timerresponse.TimerResponse
	json.Unmarshal(timeResponse, &postResponse)

	jsonResponse.CPU = postResponse.CPU + jsonResponse.CPU
	jsonResponse.Disc = postResponse.Disc + jsonResponse.Disc
	jsonResponse.Memory = postResponse.Memory + jsonResponse.Memory

	clean_start := time.Now()
	err = cleanUpAfterContainerMigration(container)
	logErr(err)
	logTime(time.Since(clean_start).String(), "cleanUpAfterContainerMigration("+container.Identifier+")")
	jsonResponse.CPU = time.Since(clean_start) + jsonResponse.CPU

	return jsonResponse
}

// Final Migration part of the Container Migration on receiver
func finishContainerMigration(container containerworkload.ContainerWorkload) timerresponse.TimerResponse {
	var jsonResponse timerresponse.TimerResponse

	logInfo("Migration of workload: " + container.Identifier)
	start := time.Now()
	cputime, disctime, ramtime, err := container.DockerLoadAndStartContainer(container.Properties.Checkpoint)
	logErr(err)
	logTime(time.Since(start).String(), "DockerLoadAndStartContainer("+container.Identifier+")")

	jsonResponse.CPU = cputime
	jsonResponse.Disc = disctime
	jsonResponse.Memory = ramtime

	clean_start := time.Now()
	err = jsonmanager.AddContainerWorkloadToSystem(container)
	jsonResponse.CPU = time.Since(clean_start) + jsonResponse.CPU
	logErr(err)

	return jsonResponse
}

// Intermediary step of migration. Send workload information to receiver
func ContainerPostMigration(migration migrate.Migrate, container containerworkload.ContainerWorkload) ([]byte, error) {
	jsonValue, _ := json.Marshal(container)

	// Send workload to target via /transfer.
	fmt.Println("Transfering Workload Information: " + container.Identifier + " to:" + migration.TargetGuest.IP)
	prefix := migration.TargetGuest.IP + ":" + migration.TargetGuest.Port
	suffix := "/transfer"
	timeResponse, err := http.Post("http://"+prefix+suffix, "application/json", bytes.NewBuffer(jsonValue))
	if err != nil {
		return []byte{}, err
	}
	data, err := ioutil.ReadAll(timeResponse.Body)
	return data, err
}

// Clean up after migration by removing container workload from workload.json and stopping contianer
func cleanUpAfterContainerMigration(container containerworkload.ContainerWorkload) error {
	err := jsonmanager.RemoveContainerFromWorkloadFile(container)
	if err != nil {
		return err
	}
	err = container.DockeRemoveContainer()
	return err
}

// Initial Migration part of the VM Migration
func initiateVMMigration(migration migrate.Migrate, vm vmworkload.VMWorkload) timerresponse.TimerResponse {
	var jsonResponse timerresponse.TimerResponse
	start_time := time.Now()
	// MIGRATE FUNC
	err := vm.Migrate(migration.TargetGuest.LibvirtURI)
	logErr(err)
	logTime(time.Since(start_time).String(), "VMMigration("+vm.Identifier+")")
	jsonResponse.CPU = time.Since(start_time)

	timeResponse, err := VMPostMigration(migration, vm)
	logErr(err)

	var postResponse timerresponse.TimerResponse
	json.Unmarshal(timeResponse, &postResponse)

	jsonResponse.CPU = postResponse.CPU + jsonResponse.CPU
	jsonResponse.Disc = postResponse.Disc + jsonResponse.Disc
	jsonResponse.Memory = postResponse.Memory + jsonResponse.Memory

	clean_start := time.Now()
	err = cleanUpAfterVMMigration(vm)
	logErr(err)
	logTime(time.Since(clean_start).String(), "cleanUpAfterVMMigration("+vm.Identifier+")")
	jsonResponse.CPU = time.Since(clean_start) + jsonResponse.CPU

	return jsonResponse
}

// Intermediary step of migration. Send workload information to receiver
func VMPostMigration(migration migrate.Migrate, vm vmworkload.VMWorkload) ([]byte, error) {
	jsonValue, _ := json.Marshal(vm)

	// Send workload to target via /transfer.
	fmt.Println("Transfering Workload Information: " + vm.Identifier + " to:" + migration.TargetGuest.IP)
	prefix := migration.TargetGuest.IP + ":" + migration.TargetGuest.Port
	suffix := "/transfer"
	timeResponse, err := http.Post("http://"+prefix+suffix, "application/json", bytes.NewBuffer(jsonValue))
	if err != nil {
		return []byte{}, err
	}
	data, err := ioutil.ReadAll(timeResponse.Body)
	return data, err
}

// Final Migration part of the VM Migration on receiver
func finishVMMigration(vm vmworkload.VMWorkload) timerresponse.TimerResponse {
	var jsonResponse timerresponse.TimerResponse
	clean_start := time.Now()

	logInfo("Migration of workload: " + vm.Identifier)
	err := jsonmanager.AddVMWorkloadToSystem(vm)
	logErr(err)
	jsonResponse.CPU = time.Since(clean_start) + jsonResponse.CPU

	return jsonResponse
}

// Clean up after migration by removing VM workload from workload.json
func cleanUpAfterVMMigration(vm vmworkload.VMWorkload) error {
	err := jsonmanager.RemoveVMFromWorkloadFile(vm)
	return err
}

// Track time of methods. Dont use when methods use go routines
func timeTrack(start time.Time, name string) {
	elapsed := time.Since(start)
	logInfo(name + " took " + elapsed.String())
}
