package jsonmanager

import (
	"GuestController/workload"
	"GuestController/workload/containerworkload"
	"GuestController/workload/vmworkload"
	"bytes"
	"encoding/json"
	"io/ioutil"
	"net/http"
	"os"
)

const (
	WorkloadFileName string = "workloads.json"
)

// Create a file in root directory
func CreateWorkloadFile() error {
	f, err := os.OpenFile(WorkloadFileName, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		return err
	}

	defer f.Close()

	return err
}

func GetWorkloadFileAsWorkloads() (workload.WorkloadList, error) {
	jsonFile, err := os.Open(WorkloadFileName)

	if err != nil {
		return workload.WorkloadList{}, err
	}

	byteValue, err := ioutil.ReadAll(jsonFile)
	if err != nil {
		return workload.WorkloadList{}, err
	}

	var workloads workload.WorkloadList
	err = json.Unmarshal(byteValue, &workloads)
	if err != nil {
		// Big nono but a fix for an empty array
		err = nil
		return workload.WorkloadList{}, err
	}

	return workloads, err
}

func GetWorkloadFileAsJSON() (string, error) {
	jsonFile, err := os.Open(WorkloadFileName)
	if err != nil {
		return "", err
	}
	byteValue, err := ioutil.ReadAll(jsonFile)
	if err != nil {
		return "", err
	}

	return string(byteValue), err
}

func AddWorkloadToSystem(r *http.Request) error {
	workloads, err := getWorkloadFileAsWorkloads()
	if err != nil {
		return err
	}

	data, err := ioutil.ReadAll(r.Body)
	if err != nil {
		return err
	}

	decoder := json.NewDecoder(bytes.NewReader(data))

	var wl workload.Workload
	err = decoder.Decode(&wl)
	if err != nil {
		return err
	}

	// Check the type of workload
	if wl.Type == workload.CONTAINERTYPE {
		var wl containerworkload.ContainerWorkload

		newdecoder := json.NewDecoder(ioutil.NopCloser(bytes.NewReader(data)))
		err = newdecoder.Decode(&wl)
		if err != nil {
			return err
		}

		workloads.Workloads = append(workloads.Workloads, wl)

	} else if wl.Type == workload.VMTYPE {
		var wl vmworkload.VMWorkload

		newdecoder := json.NewDecoder(ioutil.NopCloser(bytes.NewReader(data)))
		err = newdecoder.Decode(&wl)
		if err != nil {
			return err
		}

		workloads.Workloads = append(workloads.Workloads, wl)
	}

	// Convert Slice to bytearray
	result, err := json.Marshal(workloads)
	if err != nil {
		return err
	}

	// write to file
	err = ioutil.WriteFile(WorkloadFileName, result, 0644)

	return err
}
