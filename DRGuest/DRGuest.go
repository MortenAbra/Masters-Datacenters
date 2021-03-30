package main

import (
	"bytes"
	"fmt"
	"log"
	"strings"
	"time"

	// Console input
	"bufio"
	"os"

	// File IO
	"io/ioutil"

	// Server
	"net/http"

	// JSON
	"encoding/json"

	// Import structs
	"DRGuest/guest"
	"DRGuest/migrate"
	"DRGuest/workload"
)

var serverIP string = "localhost"
var serverPort string = "1234"

func logFatal(err error) {
	if err != nil {
		addLineToLog(err.Error())
		log.Fatal(err)
	}
}

func addLineToLog(line string) {
	// Find logfile (Should exists from createfile in main)
	file, err := os.Create("drguest.log")
	if err != nil {
		log.Fatal(err)
	}
	// Close the file at the end
	defer file.Close()

	// Get current time
	now := time.Now()

	// Compile lineToWrite
	var lineToWrite = now.String() + ":" + line

	// Write to file
	w := bufio.NewWriter(file)
	fmt.Fprintln(w, lineToWrite)
	w.Flush()
}

func main() {
	fmt.Println("Welcome to DRGuest")
	// Create internal log file
	createFile("drguest.log")

	// Create internal workloadfile
	createFile("workloads.json")

	fmt.Print("Please enter IP: ")
	serverIP = readResonse()

	fmt.Print("Please enter port: ")
	serverPort = readResonse()

	setupServer()
}

func setupServer() {
	// Setup filehandler
	addLineToLog("Setting up handlers")
	fileServer := http.FileServer(http.Dir("./static"))
	http.Handle("/", fileServer)
	//http.HandleFunc("/hello", HTTPHandler)
	http.HandleFunc("/workloads", workloadsHandler)
	http.HandleFunc("/guest", guestHandler)
	http.HandleFunc("/migrate", migrateHandler)
	http.HandleFunc("/transfer", transferHandler)

	addLineToLog("Listening on: " + serverIP + ":" + serverPort)
	fmt.Println("Listening on: " + serverIP + ":" + serverPort)
	err := http.ListenAndServe(serverIP+":"+serverPort, nil)
	logFatal(err)
}

func workloadsHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		// Read Active Workloads
		workloads := readWorkloadsFile()

		for i := 0; i < len(workloads.List); i++ {
			fmt.Fprintf(w, workloads.List[i].Name+"\t"+workloads.List[i].IP+":"+workloads.List[i].Port+"\n")
		}
	case "POST":
		addLineToLog("POST Request for /workloads")

		// Add Workload to JSON
		decoder := json.NewDecoder(r.Body)

		var workload workload.Workload
		err := decoder.Decode(&workload)
		logFatal(err)

		addWorkloadToWorkloadsFile(workload)
	}
}

func guestHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		// Read Active Workloads
		g := guest.New(serverIP, serverPort)
		result, err := json.Marshal(g)
		logFatal(err)

		fmt.Fprintf(w, string(result))

	case "POST":
		addLineToLog("POST Request for /guest")
		fmt.Fprintf(w, "Request not supported!")
	}
}

func migrateHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		fmt.Fprintf(w, "Request not supported!")
	case "POST":
		addLineToLog("POST Request for /migrate")

		decoder := json.NewDecoder(r.Body)
		var migration migrate.Migrate

		err := decoder.Decode(&migration)
		logFatal(err)

		wl_list := readWorkloadsFile()
		var workload workload.Workload
		for i := 0; i < len(wl_list.List); i++ {
			// Find the workload to migrate
			if wl_list.List[i].Name == migration.Name {
				workload = wl_list.List[i]
			}
		}

		if len(workload.Type) != 0 {
			if workload.Type == "container" {
				// Container migration Start
				addLineToLog("Migrating container: " + migration.Name + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
				fmt.Println("Migrating container: " + migration.Name + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
				go initiateContainerMigration(migration, workload)
			} else if workload.Type == "VM" {
				// VM migration start
				addLineToLog("Migrating VM: " + migration.Name + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
				fmt.Println("Migrating VM: " + migration.Name + "   to: " + migration.TargetGuest.IP + ":" + migration.TargetGuest.Port)
			}

		} else {
			fmt.Fprintf(w, "Invalid post!")
		}
	}
}
func transferHandler(w http.ResponseWriter, r *http.Request) {
	switch r.Method {
	case "GET":
		fmt.Fprintf(w, "Request not supported!")
	case "POST":
		addLineToLog("POST Request for /transfer")

		decoder := json.NewDecoder(r.Body)
		var workload workload.Workload

		err := decoder.Decode(&workload)

		fmt.Println("Receiving Migration:" + workload.Name)

		logFatal(err)
		if err != nil {
			fmt.Fprintf(w, "false")
		}

		if len(workload.Name) != 0 {
			if workload.Type == "container" {
				// Container migration recieved
				addLineToLog("Revieving Migration of container: " + workload.Name)
				fmt.Println("Revieving Migration of container: " + workload.Name)
				go processContainerMigration(workload)
			} else if workload.Type == "VM" {
				// VM migration recieved
				addLineToLog("Revieving Migration of container: " + workload.Name)
				fmt.Println("Revieving Migration of container: " + workload.Name)
			}

		}

	}
}

func initiateContainerMigration(migration migrate.Migrate, workload workload.Workload) {
	addLineToLog("Initiating Migration of workload: " + workload.Name + " to:" + migration.TargetGuest.IP)
	workload.DockerSaveAndStoreCheckpoint(workload.Docker.Checkpoint)
	postMigrationInfoToTarget(migration, workload)
}

func processContainerMigration(workload workload.Workload) {
	addLineToLog("Migration of workload: " + workload.Name)
	addWorkloadToWorkloadsFile(workload)
	workload.DockerLoadContainer(workload.Docker.Checkpoint)
}

func postMigrationInfoToTarget(migration migrate.Migrate, workload workload.Workload) {
	jsonValue, _ := json.Marshal(workload)

	// Send workload to target via /transfer.
	fmt.Println("Transfering Workload Information: " + workload.Name + " to:" + migration.TargetGuest.IP)
	prefix := migration.TargetGuest.IP + ":" + migration.TargetGuest.Port
	suffix := "/transfer"
	_, err := http.Post("http://"+prefix+suffix, "application/json", bytes.NewBuffer(jsonValue))
	logFatal(err)

	// If migration was successfull then delete workload from list
	if err == nil {
		addLineToLog("Cleanup after migration of workload:" + workload.Name)
		cleanUpAfterMigration(workload)
	}
}

// Stops, deletes and removes workload from workload.json
func cleanUpAfterMigration(workload workload.Workload) {
	removeWorkloadFromWorkloadFile(workload)
	workload.DockerDeleteContainer()
}

// Read workload.json containing active workloads
func readWorkloadsFile() workload.WorkloadList {
	jsonFile, err := os.Open("workloads.json")
	logFatal(err)

	byteValue, err := ioutil.ReadAll(jsonFile)
	logFatal(err)

	var workloads workload.WorkloadList
	json.Unmarshal(byteValue, &workloads)

	return workloads
}

// Add workload to the workload.json file
func addWorkloadToWorkloadsFile(wl workload.Workload) {
	addLineToLog("Adding workload to workload.json" + wl.Name)
	workloads := readWorkloadsFile()

	// Append new Workload into existing Workloads
	workloads.List = append(workloads.List, wl)

	result, err := json.Marshal(workloads)
	logFatal(err)

	err = ioutil.WriteFile("workloads.json", result, 0644)
	logFatal(err)
}

// Removes workload from the workloads.json file
func removeWorkloadFromWorkloadFile(wl workload.Workload) {
	addLineToLog("Removing workload from workload.json" + wl.Name)

	workloads := readWorkloadsFile()

	for i := 0; i < len(workloads.List); i++ {
		if workloads.List[i].Name == wl.Name {
			workloads.List = removeIndexFromSlice(workloads.List, i)
		}
	}
}

// Remove element from a slice (Reslicing)
func removeIndexFromSlice(slice []workload.Workload, index int) []workload.Workload {
	return append(slice[:index], slice[index+1:]...)
}

// Read next console line as user input
func readResonse() string {
	reader := bufio.NewReader(os.Stdin)
	text, err := reader.ReadString('\n')
	logFatal(err)
	// convert CRLF to LF
	text = strings.Replace(text, "\n", "", -1)

	return text
}

// Create a file in root directory
func createFile(fileName string) {
	f, err := os.OpenFile(fileName, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Fatal(err)
	}

	defer f.Close()
}
