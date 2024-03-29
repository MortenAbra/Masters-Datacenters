package containerworkload

import (
	"context"
	"io"
	"os"
	"time"

	// Docker SDK
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/api/types/network"
	"github.com/docker/docker/client"
	"github.com/google/uuid"

	"GuestController/workload"
)

type ContainerWorkload struct {
	workload.Workload
	Properties ContainerProperties `json:"Containerproperties"`
}

type ContainerProperties struct {
	ContainerID     string                               `json:"ContainerID"`
	Image           string                               `json:"Image"`
	Checkpoint      bool                                 `json:"Checkpoint"`
	NetworkSettings map[string]*network.EndpointSettings `json:"NetworkSettings"`
	CheckpointID    string                               `json:"CheckpointID"`
}

func initDocker() (context.Context, *client.Client, error) {
	ctx := context.Background()

	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())

	return ctx, cli, err
}

// Docker save - and save the exported as tar.gz
// Returns Error, CPUTime, DiscTime, RamTime
func (wl *ContainerWorkload) DockerSaveAndStoreCheckpoint(restore bool) (time.Duration, time.Duration, time.Duration, error) {
	// Track first Timer for CPU
	cpu_start := time.Now()
	// Init docker environment
	ctx, cli, err := initDocker()
	if err != nil {
		return 0, 0, 0, err
	}

	// Save NetworkSetitngs of the container
	containers, err := cli.ContainerList(ctx, types.ContainerListOptions{All: true})
	if err != nil {
		return 0, 0, 0, err
	}

	for _, container := range containers {
		if container.ID == wl.Properties.ContainerID {
			wl.Properties.NetworkSettings = container.NetworkSettings.Networks
		}
	}

	// Create UUID for checkpoint and store it
	cpUUID := uuid.New().String()
	if err != nil {
		return 0, 0, 0, err
	}
	wl.Properties.CheckpointID = cpUUID

	err = cli.ContainerStart(ctx, wl.Properties.ContainerID, types.ContainerStartOptions{})
	if err != nil {
		return 0, 0, 0, err
	}

	cpu_elapsed := time.Since(cpu_start)

	// Track second Timer for Disc
	disc_start := time.Now()

	newImage, err := cli.ContainerCommit(ctx, wl.Properties.ContainerID, types.ContainerCommitOptions{})
	if err != nil {
		return 0, 0, 0, err
	}

	wl.Properties.Image = newImage.ID

	resp, err := cli.ImageSave(ctx, []string{newImage.ID})
	if err != nil {
		return 0, 0, 0, err
	}
	defer resp.Close()

	// Write resp to file
	outFile, err := os.Create(wl.SharedDir + "/" + wl.Properties.ContainerID + ".tar.gz")
	if err != nil {
		return 0, 0, 0, err
	}

	disc_elapsed := time.Since(disc_start)

	// Track third Timer for RAM
	ram_start := time.Now()
	if restore {
		err = cli.CheckpointCreate(ctx, wl.Properties.ContainerID, types.CheckpointCreateOptions{
			CheckpointID:  wl.Properties.CheckpointID,
			CheckpointDir: "",
		})
		if err != nil {
			return 0, 0, 0, err
		}
	}

	ram_elapsed := time.Since(ram_start)

	disc_start = time.Now()
	defer outFile.Close()
	_, err = io.Copy(outFile, resp)
	if err != nil {
		return 0, 0, 0, err
	}
	disc_elapsed = time.Since(disc_start) + disc_elapsed

	return cpu_elapsed, disc_elapsed, ram_elapsed, err
}

// Docker container stop - docker container rm
func (wl ContainerWorkload) DockeRemoveContainer() error {
	// Init docker environment
	ctx, cli, err := initDocker()
	if err != nil {
		return err
	}

	err = cli.ContainerStop(ctx, wl.Properties.ContainerID, nil)
	if err != nil {
		return err
	}

	err = cli.ContainerRemove(ctx, wl.Properties.ContainerID, types.ContainerRemoveOptions{})
	return err
}

// Docker load - docker run container
func (wl *ContainerWorkload) DockerLoadAndStartContainer(restore bool) (time.Duration, time.Duration, time.Duration, error) {
	disc_start := time.Now()

	// Init docker environment
	ctx, cli, err := initDocker()
	if err != nil {
		return 0, 0, 0, err
	}

	tarFile, err := os.Open(wl.SharedDir + "/" + wl.Properties.ContainerID + ".tar.gz")
	if err != nil {
		return 0, 0, 0, err
	}

	defer tarFile.Close()

	resp, err := cli.ImageLoad(ctx, tarFile, true)
	if err != nil {
		return 0, 0, 0, err
	}
	defer resp.Body.Close()

	disc_elapsed := time.Since(disc_start)
	cpu_start := time.Now()

	out, err := cli.ContainerCreate(
		ctx,
		&container.Config{Image: wl.Properties.Image},
		nil,
		&network.NetworkingConfig{EndpointsConfig: wl.Properties.NetworkSettings},
		nil,
		wl.Properties.ContainerID)
	if err != nil {
		return 0, 0, 0, err
	}
	cpu_elapsed := time.Since(cpu_start)

	ram_start := time.Now()

	println(wl.Properties.CheckpointID)

	if restore {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{
			CheckpointID:  wl.Properties.CheckpointID,
			CheckpointDir: "",
		})
		if err != nil {
			return 0, 0, 0, err
		}
	} else {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{})
		if err != nil {
			return 0, 0, 0, err
		}
	}
	ram_elapsed := time.Since(ram_start)

	return cpu_elapsed, disc_elapsed, ram_elapsed, err
}
