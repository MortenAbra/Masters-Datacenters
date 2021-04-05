package vmworkload

import (
	"GuestController/workload"
)

type VMWorkload struct {
	workload.Workload
	Properties VMProperties `json:"VMProperties"`
}

type VMProperties struct {
	DomainName    string `json:"DomainName"`
	ConnectionURI string `json:"ConnectionURI"`
}
