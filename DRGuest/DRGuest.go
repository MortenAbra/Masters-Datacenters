package main

import (
	"fmt"
	"strings"

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
	"DRGuest/workload"
)

var serverIP string = "localhost"
var serverPort string = "1234"

func main() {
	fmt.Println("Welcome to DRGuest")
	createFile("workloads.json")

	fmt.Print("Please enter IP: ")
	serverIP = readResonse()

	fmt.Print("Please enter port: ")
	serverPort = readResonse()

	setupServer()
}

func setupServer() {
	fileServer := http.FileServer(http.Dir("./static"))
	http.Handle("/", fileServer)
	//http.HandleFunc("/hello", HTTPHandler)
	http.HandleFunc("/workloads", workloadsHandler)
	http.HandleFunc("/migrate", migrateHandler)

	fmt.Println("Listening on: " + serverIP + ":" + serverPort)
	if err := http.ListenAndServe(serverIP+":"+serverPort, nil); err != nil {
		panic(err)
	}
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
		// Add Workload to JSON
		decoder := json.NewDecoder(r.Body)

		var workload workload.Workload
		err := decoder.Decode(&workload)
		if err != nil {
			panic(err)
		}
		addWorkloadToWorkloadsFile(workload)
		//workload.DockerExportAndSaveCheckpoint("/home/wolder/test/")
		workload.DockerExportAndSaveCheckpoint("/home/wolder/test")
	}
}

func migrateHandler(w http.ResponseWriter, r *http.Request) {
	// TODO
	//
}

// Initializes JSON file for storing workloads
func createFile(fileName string) {
	f, err := os.OpenFile(fileName, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	defer f.Close()

	if err != nil {
		panic(err)
	}
}

func readWorkloadsFile() workload.WorkloadList {
	jsonFile, err := os.Open("workloads.json")
	if err != nil {
		panic(err)
	}

	byteValue, err := ioutil.ReadAll(jsonFile)
	if err != nil {
		panic(err)
	}
	var workloads workload.WorkloadList

	json.Unmarshal(byteValue, &workloads)

	return workloads
}

func addWorkloadToWorkloadsFile(new_wl workload.Workload) {
	workloads := readWorkloadsFile()

	// Append new Workload into existing Workloads
	workloads.List = append(workloads.List, new_wl)

	result, err := json.Marshal(workloads)
	if err != nil {
		panic(err)
	}

	err = ioutil.WriteFile("workloads.json", result, 0644)
	if err != nil {
		panic(err)
	}
}

// Read next console line as user input
func readResonse() string {
	reader := bufio.NewReader(os.Stdin)
	text, err := reader.ReadString('\n')
	if err != nil {
		panic(err)
	}
	// convert CRLF to LF
	text = strings.Replace(text, "\n", "", -1)

	return text
}
