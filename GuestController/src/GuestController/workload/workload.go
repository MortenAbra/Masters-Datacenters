package workload

type Workload struct {
	Identifier string       `json:"Identifier"`
	AccessIP   string       `json:"AccessIP"`
	AccessPort string       `json:"AccessPort"`
	Available  bool         `json:"Available"`
	SharedDir  string       `json:"SharedDir"`
	Type       WorkloadType `json:"Type"`
}

type WorkloadList struct {
	Workloads []interface{} `json:"Workloads"`
}

type WorkloadType string

const (
	CONTAINERTYPE WorkloadType = "Container"
	VMTYPE        WorkloadType = "VM"
)
