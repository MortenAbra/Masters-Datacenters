package migrate

import (
	"DRGuest/guest"
	"DRGuest/workload"
)

type Migration struct {
	workload    workload.Workload
	targetGuest guest.Guest
}
