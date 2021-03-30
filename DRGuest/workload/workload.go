package workload

import (
	"fmt"
	"io"
	"log"

	"context"
	"os"
	"os/exec"

	// Docker SDK
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/client"

	//UUID
	uuid "github.com/nu7hatch/gouuid"
)

type WorkloadList struct {
	List []Workload `json:"workloads"`
}

type Workload struct {
	Name      string         `json:"name"`
	IP        string         `json:"ip"`
	Port      string         `json:"port"`
	Available bool           `json:"available"`
	Type      string         `json:"type"`
	SharedDir string         `json:"sharedDir"`
	Docker    DockerWorkload `json:"dockerWorkload"`
	VM        VMWorkload     `json:"VMWorkload"`
}
type DockerWorkload struct {
	ContainerDir   string   `json:"container_Dir"`
	ContainerID    string   `json:"container_ID"`
	ContainerImage string   `json:"container_image"`
	Checkpoint     bool     `json:"checkpoint"`
	CheckpointDir  string   `json:"container_CPDir"`
	CheckpointIDs  []string `json:"container_CPs"`
}
type VMWorkload struct {
	SharedStorage string `json:"shared_storage"`
}

func logFatal(err error) {
	if err != nil {
		log.Fatal(err)
	}
}

func (wl Workload) DockerSaveAndStoreCheckpoint(restore bool) {
	// Init docker environment
	ctx, cli := initDocker()

	// Create UUID for checkpoint and store it
	cpUUID, err := uuid.NewV4()
	wl.Docker.CheckpointIDs = append(wl.Docker.CheckpointIDs, cpUUID.String())
	logFatal(err)

	err = cli.ContainerStart(ctx, wl.Docker.ContainerID, types.ContainerStartOptions{})
	logFatal(err)

	resp, err := cli.ImageSave(ctx, []string{wl.Docker.ContainerImage})
	logFatal(err)
	defer resp.Close()

	// Write resp to file
	outFile, err := os.Create(wl.SharedDir + "/" + wl.Docker.ContainerID + ".tar.gz")
	logFatal(err)

	if restore {
		err = cli.CheckpointCreate(ctx, wl.Docker.ContainerID, types.CheckpointCreateOptions{
			CheckpointID:  wl.Docker.CheckpointIDs[len(wl.Docker.CheckpointIDs)-1],
			CheckpointDir: wl.Docker.CheckpointDir,
		})
		logFatal(err)
	}

	defer outFile.Close()
	_, err = io.Copy(outFile, resp)
	logFatal(err)
}

func (wl Workload) DockerStartLastFromCheckpoint() {
	// Init docker environment
	ctx, cli := initDocker()

	err := cli.ContainerStart(ctx, wl.Docker.ContainerID, types.ContainerStartOptions{
		CheckpointID:  wl.Docker.CheckpointIDs[len(wl.Docker.CheckpointIDs)-1], //Last recorded checkpoint
		CheckpointDir: wl.Docker.CheckpointDir,
	})
	logFatal(err)
}

func (wl Workload) DockerLoadContainer(restore bool) {
	// Init docker environment
	ctx, cli := initDocker()

	tarFile, err := os.Open(wl.SharedDir + "/" + wl.Docker.ContainerID + ".tar.gz")
	logFatal(err)
	defer tarFile.Close()

	resp, err := cli.ImageLoad(ctx, tarFile, true)
	logFatal(err)
	defer resp.Body.Close()

	out, err := cli.ContainerCreate(ctx, &container.Config{
		Image: wl.Docker.ContainerImage,
	}, nil, nil, nil, wl.Docker.ContainerID)
	logFatal(err)

	if restore {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{
			CheckpointID:  wl.Docker.CheckpointIDs[len(wl.Docker.CheckpointIDs)-1],
			CheckpointDir: wl.Docker.CheckpointDir,
		})
		logFatal(err)
	} else {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{})
		logFatal(err)
	}
}

func (wl Workload) DockerCreateContainer(imageName string, restore bool) {
	// Init docker environment
	ctx, cli := initDocker()

	resp, err := cli.ImagePull(ctx, imageName, types.ImagePullOptions{})
	logFatal(err)

	defer resp.Close()
	io.Copy(os.Stdout, resp)

	out, err := cli.ContainerCreate(ctx, &container.Config{
		Image: imageName,
	}, nil, nil, nil, "")
	logFatal(err)

	if restore {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{
			CheckpointID:  wl.Docker.ContainerID,
			CheckpointDir: wl.Docker.CheckpointDir,
		})
		logFatal(err)
	} else {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{})
		logFatal(err)
	}

	fmt.Println(out.ID)
}

func (wl Workload) DockerDeleteContainer() {
	// Init docker environment
	ctx, cli := initDocker()

	err := cli.ContainerStop(ctx, wl.Docker.ContainerID, nil)
	logFatal(err)

	err = cli.ContainerRemove(ctx, wl.Docker.ContainerID, types.ContainerRemoveOptions{})
	logFatal(err)
}

func initDocker() (context.Context, *client.Client) {
	ctx := context.Background()

	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	logFatal(err)

	return ctx, cli
}

// Todo
func (wl Workload) TestVM() {
	arg0 := "virsh" // Prefix
	arg1 := "migrate"
	arg2 := "ubuntu16.04"                             // Domain Name to migrate
	arg3 := "qemu+ssh://wolder@192.168.122.72/system" // Network
	arg4 := ""                                        // Interface

	cmd := exec.Command(arg0, arg1, arg2, arg3, arg4)
	output, err := cmd.Output()
	logFatal(err)
	print(output)
}

/*func (wl Workload) migrateVM(l *libvirt.Libvirt, domain libvirt.Domain, uri string) {
	DomXML, err := l.DomainGetXMLDesc(domain, 8)
	logFatal(err)
	fmt.Println(DomXML)

	flag := uint64(libvirt.MigrateLive)
	resource := uint64(0)
	cancelled := int32(0)
	URI_in := "unix:///tmp/migdir/test-sock-nbd"                     //disks-uri
	URI_dconn := "unix:///tmp/migdir/test-sock-qemu"                 // desturi
	URI := "qemu+unix:///system?socket=/tmp/migdir/test-sock-driver" // migrateuri

	fmt.Println("Begin")
	beginCookie, rXML, err := l.DomainMigrateBegin3(
		domain,
		libvirt.OptString{DomXML},
		flag,
		libvirt.OptString{domain.Name},
		resource)
	logFatal(err)
	fmt.Println(rXML)

	fmt.Println("Prepare")
	prepateCookie, URIprep, err := l.DomainMigratePrepare3(
		beginCookie,
		libvirt.OptString{URI_in},
		flag,
		libvirt.OptString{domain.Name},
		resource,
		rXML)
	logFatal(err)

	fmt.Println("Perform")
	performCookie, err := l.DomainMigratePerform3(
		domain,
		libvirt.OptString{DomXML},
		prepateCookie,
		libvirt.OptString{URI_dconn}, // Switch with below?
		URIprep,
		flag,
		libvirt.OptString{domain.Name},
		resource)
	logFatal(err)

	fmt.Println("Finish")
	rDom, finishCookie, err := l.DomainMigrateFinish3(
		domain.Name,
		performCookie,
		libvirt.OptString{URI_dconn},
		libvirt.OptString{URI},
		flag,
		cancelled)
	logFatal(err)

	fmt.Println("Confirm")
	err = l.DomainMigrateConfirm3(
		rDom,
		finishCookie,
		flag,
		cancelled)
	logFatal(err)

}

func (wl Workload) TestVM() {
	// This dials libvirt on the local machine, but you can substitute the first
	// two parameters with "tcp", "<ip address>:<port>" to connect to libvirt on
	// a remote machine.
	c, err := net.DialTimeout("unix", "/var/run/libvirt/libvirt-sock", 2*time.Second)
	if err != nil {
		log.Fatalf("failed to dial libvirt: %v", err)
	}

	l := libvirt.New(c)
	if err := l.Connect(); err != nil {
		log.Fatalf("failed to connect: %v", err)
	}
	defer l.Disconnect()

	domains, _, err := l.ConnectListAllDomains(2, 1)
	if err != nil {
		log.Fatalf("failed to retrieve domains: %v", err)

	}

	//for _, d := range domains

	fmt.Println("ID\tName\t\tUUID")
	fmt.Printf("--------------------------------------------------------\n")
	for _, d := range domains {
		fmt.Printf("%d\t%s\t%x\n", d.ID, d.Name, d.UUID)
	}
}
*/
