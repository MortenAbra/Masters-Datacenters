package guest

import (
	"GuestController/drlogger"
	"GuestController/workload"
	"GuestController/workload/containerworkload"
	"GuestController/workload/vmworkload"
	"context"
	"log"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/libvirt/libvirt-go"
)

type Guest struct {
	IP          string `json:"Ip"`
	Port        string `json:"Port"`
	StoragePath string `json:"StoragePath"`
	LibvirtURI  string `json:"LibvirtURI"`
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

	conn, err := libvirt.NewConnect("qemu:///system")
	if err != nil {
		panic(err)
	}

	defer conn.Close()

	activeDoms, err := conn.ListAllDomains(libvirt.CONNECT_LIST_DOMAINS_ACTIVE)
	if err != nil {
		panic(err)
	}
	inactiveDoms, err := conn.ListAllDomains(libvirt.CONNECT_LIST_DOMAINS_ACTIVE)
	if err != nil {
		panic(err)
	}

	domains := append(activeDoms, inactiveDoms...)
	connURI, _ := conn.GetURI()

	for _, domain := range domains {
		domainname, _ := domain.GetName()
		workload := vmworkload.VMWorkload{
			Workload: workload.Workload{
				Identifier: "Incomplete Workload:" + domainname,
				AccessIP:   "",
				AccessPort: "",
				Available:  false,
				SharedDir:  g.StoragePath,
				Type:       "VM",
			},
			Properties: vmworkload.VMProperties{
				DomainName:    domainname,
				ConnectionURI: connURI,
			}}
		resultSlice = append(resultSlice, workload)
	}

	return resultSlice
}

func initDocker() (context.Context, *client.Client) {
	ctx := context.Background()
	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())

	logFatal(err)
	return ctx, cli
}
