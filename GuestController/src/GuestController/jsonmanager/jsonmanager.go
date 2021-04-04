package jsonmanager

import (
	"log"
	"os"
)

const (
	WorkloadFileName string = "workloads.json"
)

// Create a file in root directory
func CreateWorkloadFile() {
	f, err := os.OpenFile(WorkloadFileName, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Fatal(err)
	}

	defer f.Close()
}
