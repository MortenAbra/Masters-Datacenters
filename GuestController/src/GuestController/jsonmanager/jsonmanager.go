package jsonmanager

import (
	"GuestController/jsonmanager/timerresponse"
	"GuestController/workload"
	"GuestController/workload/containerworkload"
	"GuestController/workload/vmworkload"
	"bytes"
	"encoding/json"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"

	"github.com/mitchellh/mapstructure"
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

func AddContainerWorkloadToSystem(container containerworkload.ContainerWorkload) error {
	workloads, err := GetWorkloadFileAsWorkloads()
	if err != nil {
		return err
	}

	// Append new Workload into existing Workloads
	workloads.Workloads = append(workloads.Workloads, container)

	result, err := json.Marshal(workloads)
	if err != nil {
		return err
	}

	err = ioutil.WriteFile("workloads.json", result, 0644)
	return err
}

func AddVMWorkloadToSystem(vm vmworkload.VMWorkload) error {
	workloads, err := GetWorkloadFileAsWorkloads()
	if err != nil {
		return err
	}

	// Append new Workload into existing Workloads
	workloads.Workloads = append(workloads.Workloads, vm)

	result, err := json.Marshal(workloads)
	if err != nil {
		return err
	}

	err = ioutil.WriteFile("workloads.json", result, 0644)
	return err
}

func AddWorkloadToSystemHTTP(r *http.Request) error {
	workloads, err := GetWorkloadFileAsWorkloads()
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

	// Remove if the workload already exists
	for i := 0; i < len(workloads.Workloads); i++ {
		list := workloads.Workloads[i].(map[string]interface{})
		if list["Identifier"] == wl.Identifier {
			workloads.Workloads = removeIndexFromSlice(workloads.Workloads, i)
		}
	}

	// Check the type of workload
	switch wl.Type {
	case workload.CONTAINERTYPE:
		var wl containerworkload.ContainerWorkload

		newdecoder := json.NewDecoder(ioutil.NopCloser(bytes.NewReader(data)))
		err = newdecoder.Decode(&wl)
		if err != nil {
			return err
		}

		workloads.Workloads = append(workloads.Workloads, wl)

	case workload.VMTYPE:
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

func RemoveContainerFromWorkloadFile(container containerworkload.ContainerWorkload) error {
	workloads, err := GetWorkloadFileAsWorkloads()
	if err != nil {
		return err
	}

	var tempWorkload workload.Workload
	for i := 0; i < len(workloads.Workloads); i++ {
		mapstructure.Decode(workloads.Workloads[i], &tempWorkload)
		// Find the workload to migrate
		if tempWorkload.Identifier == container.Identifier {
			workloads.Workloads = removeIndexFromSlice(workloads.Workloads, i)
		}
	}

	result, err := json.Marshal(workloads)
	if err != nil {
		return err
	}

	err = ioutil.WriteFile("workloads.json", result, 0644)

	return err
}
func RemoveVMFromWorkloadFile(vm vmworkload.VMWorkload) error {
	workloads, err := GetWorkloadFileAsWorkloads()
	if err != nil {
		return err
	}

	var tempWorkload workload.Workload
	for i := 0; i < len(workloads.Workloads); i++ {
		mapstructure.Decode(workloads.Workloads[i], &tempWorkload)
		// Find the workload to migrate
		if tempWorkload.Identifier == vm.Identifier {
			workloads.Workloads = removeIndexFromSlice(workloads.Workloads, i)
		}
	}

	result, err := json.Marshal(workloads)
	if err != nil {
		return err
	}

	err = ioutil.WriteFile("workloads.json", result, 0644)

	return err
}

// Remove element from a slice (Reslicing)
func removeIndexFromSlice(slice []interface{}, index int) []interface{} {
	return append(slice[:index], slice[index+1:]...)
}

func GetJSONResponseAsJSON(timer timerresponse.TimerResponse) string {
	cpu := fmt.Sprint(timer.CPU.Seconds())
	disc := fmt.Sprint(timer.Disc.Seconds())
	ram := fmt.Sprint(timer.Memory.Seconds())
	network := fmt.Sprint(timer.Network.Seconds())
	return "CPU:" + cpu + " Disc:" + disc + " RAM:" + ram + " Network:" + network
}
