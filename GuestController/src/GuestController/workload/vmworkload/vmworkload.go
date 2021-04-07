package vmworkload

import (
	"GuestController/workload"
	"fmt"

	"github.com/libvirt/libvirt-go"
)

type VMWorkload struct {
	workload.Workload
	Properties VMProperties `json:"VMProperties"`
}

type VMProperties struct {
	DomainName    string `json:"DomainName"`
	ConnectionURI string `json:"ConnectionURI"`
}

func Migrate() error {
	conn, err := libvirt.NewConnect("qemu:///system")
	if err != nil {
		return err
	}
	defer conn.Close()

	doms, err := conn.ListAllDomains(libvirt.CONNECT_LIST_DOMAINS_ACTIVE)
	if err != nil {
		return err
	}

	fmt.Printf("%d running domains:\n", len(doms))
	for _, dom := range doms {
		name, err := dom.GetName()
		if err == nil {
			fmt.Printf("  %s\n", name)
		}
		dom.Free()
	}
	return err
}
