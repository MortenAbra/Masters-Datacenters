package main

import (
	"encoding/json"
	"fmt"
	"net/http"

	"GuestController/consolemanager"
	"GuestController/drlogger"
	"GuestController/guest"
	"GuestController/jsonmanager"
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
		result, err := jsonmanager.GetAllWorkloadsInSystem()
		logErr(err)
		fmt.Fprintf(w, string(result)+"")
		logInfo("Responding: " + string(result) + "")

	case "POST":
		logInfo("POST Request for /workloads")
		err := jsonmanager.AddWorkloadToSystem(r)
		logErr(err)
	}
}
