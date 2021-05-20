package vmworkload

import (
	"GuestController/workload"

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

	dom, err := conn.LookupDomainByName(wl.Identifier)
	if err != nil {
		return err
	}

	defer dom.Free()
	err = dom.MigrateToURI(destinationURI, libvirt.MIGRATE_PEER2PEER|libvirt.MIGRATE_TUNNELLED|libvirt.MIGRATE_UNSAFE, wl.Identifier, 0)
	if err != nil {
		return err
	}

	return err
}
