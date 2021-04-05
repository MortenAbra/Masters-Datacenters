package containerworkload

import (
	"GuestController/workload"
)

type ContainerWorkload struct {
	workload.Workload
	Properties ContainerProperties `json:"Containerproperties"`
}

type ContainerProperties struct {
	ContainerID string `json:"ContainerID"`
	Image       string `json:"Image"`
	Checkpoint  bool   `json:"Checkpoint"`
}
