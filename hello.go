package main

import (
	"context"
	"os"

	"github.com/docker/docker/api/types"
	"github.com/docker/docker/client"
	"github.com/docker/docker/pkg/stdcopy"
)

func main() {
	ctx := context.Background()

	var containername = "cr"

	cli, err := client.NewClientWithOpts(client.FromEnv, client.WithAPIVersionNegotiation())
	if err != nil {
		panic(err)
	}

	if err := cli.ContainerStart(ctx, containername, types.ContainerStartOptions{}); err != nil {
		panic(err)
	}

	// Checkpoint
	if err := cli.CheckpointCreate(ctx, containername, types.CheckpointCreateOptions{
		CheckpointID:  "cp1",
		CheckpointDir: "",
		Exit:          true,
	}); err != nil {
		panic(err)
	}
	if err := cli.ContainerStart(ctx, containername, types.ContainerStartOptions{
		CheckpointID:  "cp1",
		CheckpointDir: "",
	}); err != nil {
		panic(err)
	}

	out, err := cli.ContainerLogs(ctx, containername, types.ContainerLogsOptions{ShowStdout: true})
	if err != nil {
		panic(err)
	}

	//resp, err := cli.ContainerStats(ctx, "cr", true)

	stdcopy.StdCopy(os.Stdout, os.Stderr, out)
}
