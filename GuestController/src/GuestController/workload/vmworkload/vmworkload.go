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

func (wl VMWorkload) Migrate(destinationURI string) error {
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
		dname, err := dom.GetName()
		if err != nil {
			return err
		}

		if dname == wl.Identifier {
			err = dom.MigrateToURI3(destinationURI, &libvirt.DomainMigrateParameters{}, libvirt.MIGRATE_ABORT_ON_ERROR)
			if err != nil {
				return err
			}
		}
	}
	return err
}
