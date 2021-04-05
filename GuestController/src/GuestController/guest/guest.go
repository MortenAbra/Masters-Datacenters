package guest

import (
	"GuestController/drlogger"
	"GuestController/workload"
	"GuestController/workload/containerworkload"
	"GuestController/workload/vmworkload"
	"context"
	"log"
	"net"
	"time"

	"github.com/digitalocean/go-libvirt"
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
)

type Guest struct {
	IP          string `json:"Ip"`
	Port        string `json:"Port"`
	StoragePath string `json:"StoragePath"`
}

// Logging fatal errors.
// Replaces the use of "if err != nil { panic(err) }"
func logFatal(err error) {
	if err != nil {
		drlogger.DRLog(drlogger.ERR, err.Error())
		log.Fatal(err)
	}
}

func (g Guest) GetAllWorkloadsRunningOnGuest() []interface{} {
	ctx, cli := initDocker()
	resultSlice := []interface{}{}

	// Get a list of all containers on the system
	containers, err := cli.ContainerList(ctx, types.ContainerListOptions{All: true})
	logFatal(err)

	for _, container := range containers {
		workload := containerworkload.ContainerWorkload{
			Workload: workload.Workload{
				Identifier: "Incomplete Workload:" + container.Names[0],
				AccessIP:   container.NetworkSettings.Networks["bridge"].IPAddress,
				AccessPort: "",
				Available:  false,
				SharedDir:  g.StoragePath,
				Type:       "container",
			},
			Properties: containerworkload.ContainerProperties{
				ContainerID: container.ID,
				Image:       container.Image,
				Checkpoint:  false,
			}}
		resultSlice = append(resultSlice, workload)
	}

	c, err := net.DialTimeout("unix", "/var/run/libvirt/libvirt-sock", 2*time.Second)
	logFatal(err)

	l := libvirt.New(c)
	err = l.Connect()
	logFatal(err)

	activeDomains, _, err := l.ConnectListAllDomains(1, libvirt.ConnectListDomainsActive)
	logFatal(err)
	inactiveDomains, _, err := l.ConnectListAllDomains(1, libvirt.ConnectListDomainsInactive)
	logFatal(err)

	domains := append(activeDomains, inactiveDomains...)

	for _, domain := range domains {
		workload := vmworkload.VMWorkload{
			Workload: workload.Workload{
				Identifier: "Incomplete Workload:" + domain.Name,
				AccessIP:   "",
				AccessPort: "",
				Available:  false,
				SharedDir:  g.StoragePath,
				Type:       "VM",
			},
			Properties: vmworkload.VMProperties{
				DomainName:    domain.Name,
				ConnectionURI: "qemu+ssh://" + g.IP + "/system",
			}}
		resultSlice = append(resultSlice, workload)
	}

	err = l.Disconnect()
	logFatal(err)

	return resultSlice
}

func initDocker() (context.Context, *client.Client) {
	ctx := context.Background()
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())

	logFatal(err)
	return ctx, cli
}
