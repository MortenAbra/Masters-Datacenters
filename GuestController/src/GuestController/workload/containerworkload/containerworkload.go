package containerworkload

import (
	"context"
	"io"
	"os"

	// Docker SDK
	"github.com/docker/docker/api/types"
	"github.com/docker/docker/api/types/container"
	"github.com/docker/docker/api/types/network"
	"github.com/docker/docker/client"
	uuid "github.com/nu7hatch/gouuid"

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
	CheckpointIDs   []string                             `json:"CheckpointIDs"`
}

func initDocker() (context.Context, *client.Client, error) {
	ctx := context.Background()

	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())

	return ctx, cli, err
}

// Docker save - and save the exported as tar.gz
func (wl ContainerWorkload) DockerSaveAndStoreCheckpoint(restore bool) error {
	// Init docker environment
	ctx, cli, err := initDocker()
	if err != nil {
		return err
	}

	// Save NetworkSetitngs of the container
	containers, err := cli.ContainerList(ctx, types.ContainerListOptions{All: true})
	if err != nil {
		return err
	}

	for _, container := range containers {
		if container.ID == wl.Properties.ContainerID {
			wl.Properties.NetworkSettings = container.NetworkSettings.Networks
		}
	}

	// Create UUID for checkpoint and store it
	cpUUID, err := uuid.NewV4()
	if err != nil {
		return err
	}
	wl.Properties.CheckpointIDs = append(wl.Properties.CheckpointIDs, cpUUID.String())

	err = cli.ContainerStart(ctx, wl.Properties.ContainerID, types.ContainerStartOptions{})
	if err != nil {
		return err
	}
	resp, err := cli.ImageSave(ctx, []string{wl.Properties.Image})
	if err != nil {
		return err
	}
	defer resp.Close()

	// Write resp to file
	outFile, err := os.Create(wl.SharedDir + "/" + wl.Properties.ContainerID + ".tar.gz")
	if err != nil {
		return err
	}

	if restore {

		err = cli.CheckpointCreate(ctx, wl.Properties.ContainerID, types.CheckpointCreateOptions{
			CheckpointID:  wl.Properties.CheckpointIDs[len(wl.Properties.CheckpointIDs)-1],
			CheckpointDir: wl.SharedDir,
		})
		if err != nil {
			return err
		}
	}

	defer outFile.Close()
	_, err = io.Copy(outFile, resp)
	if err != nil {
		return err

	}

	return err
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
func (wl ContainerWorkload) DockerLoadAndStartContainer(restore bool) error {
	// Init docker environment
	ctx, cli, err := initDocker()
	if err != nil {
		return err
	}

	tarFile, err := os.Open(wl.SharedDir + "/" + wl.Properties.ContainerID + ".tar.gz")
	if err != nil {
		return err
	}
	defer tarFile.Close()

	resp, err := cli.ImageLoad(ctx, tarFile, true)
	if err != nil {
		return err
	}
	defer resp.Body.Close()

	out, err := cli.ContainerCreate(
		ctx,
		&container.Config{Image: wl.Properties.Image},
		nil,
		&network.NetworkingConfig{EndpointsConfig: wl.Properties.NetworkSettings},
		nil,
		wl.Properties.ContainerID)
	if err != nil {
		return err
	}

	if restore {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{
			CheckpointID:  wl.Properties.CheckpointIDs[len(wl.Properties.CheckpointIDs)-1],
			CheckpointDir: wl.SharedDir,
		})
		if err != nil {
			return err
		}
	} else {
		err := cli.ContainerStart(ctx, out.ID, types.ContainerStartOptions{})
		if err != nil {
			return err
		}
	}
	return err
}
