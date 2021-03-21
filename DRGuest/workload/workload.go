package workload

import (
	"fmt"
	"io"

	"context"
	"os"

	// Docker API
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
	Docker    DockerWorkload `json:"dockerWorkload"`
	VM        VMWorkload     `json:"VMWorkload"`
}
type DockerWorkload struct {
	ContainerDir  string   `json:"container_Dir"`
	ContainerID   string   `json:"container_ID"`
	DockerFileDir string   `json:"container_DockerFile"`
	CheckpointDir string   `json:"container_CPDir"`
	CheckpointIDs []string `json:"-"`
}
type VMWorkload struct {
	SharedStorage string `json:"shared_storage"`
}

func (wl Workload) DockerExportAndSaveCheckpoint(path string) {
	// Init docker environment
	ctx, cli := initDocker()

	// Create UUID for checkpoint and store it
	cpUUID, err := uuid.NewV4()
	wl.Docker.CheckpointIDs = append(wl.Docker.CheckpointIDs, cpUUID.String())
	if err != nil {
		panic(err)
	}
	// Checkpoint
	/*if err := cli.CheckpointCreate(ctx, wl.Name, types.CheckpointCreateOptions{
		CheckpointID:  cpUUID.String(),
		CheckpointDir: path,
		Exit:          true,
	}); err != nil {
		panic(err)
	}*/

	err = cli.ContainerStart(ctx, wl.Docker.ContainerID, types.ContainerStartOptions{})
	if err != nil {
		panic(err)
	}

	resp, err := cli.ContainerExport(ctx, wl.Docker.ContainerID)
	defer resp.Close()

	// Write resp to file
	outFile, err := os.Create(path + "/" + wl.Docker.ContainerID + ".tar.gz")
	if err != nil {
		panic(err)
	}
	defer outFile.Close()
	_, err = io.Copy(outFile, resp)
	if err != nil {
		panic(err)
	}

}

func (wl Workload) DockerStartLastFromCheckpoint() {
	// Init docker environment
	ctx, cli := initDocker()

	if err := cli.ContainerStart(ctx, wl.Name, types.ContainerStartOptions{
		CheckpointID:  wl.Docker.CheckpointIDs[len(wl.Docker.CheckpointIDs)-1], //Last recorded checkpoint
		CheckpointDir: wl.Docker.CheckpointDir,
	}); err != nil {
		panic(err)
	}
}

func (wl Workload) DockerSaveImage(destination string) {
	// Init docker environment
	//ctx, cli := initDocker()

}

func (wl Workload) DockerCreateContainer(imageName string, restore bool) {
	// Init docker environment
	ctx, cli := initDocker()

	out, err := cli.ImagePull(ctx, imageName, types.ImagePullOptions{})
	if err != nil {
		panic(err)
	}
	io.Copy(os.Stdout, out)

	resp, err := cli.ContainerCreate(ctx, &container.Config{
		Image: imageName,
	}, nil, nil, nil, "")
	if err != nil {
		panic(err)
	}

	if restore {
		if err := cli.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{
			CheckpointID:  wl.Docker.ContainerID,
			CheckpointDir: wl.Docker.CheckpointDir,
		}); err != nil {
			panic(err)
		}
	} else {
		if err := cli.ContainerStart(ctx, resp.ID, types.ContainerStartOptions{}); err != nil {
			panic(err)
		}
	}

	fmt.Println(resp.ID)
}

func initDocker() (context.Context, *client.Client) {
	ctx := context.Background()

	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		panic(err)
	}

	return ctx, cli
}
