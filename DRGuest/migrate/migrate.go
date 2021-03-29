package migrate

import (
	"DRGuest/guest"
)

type Migrate struct {
	Name        string      `json:"wl_name"`
	TargetGuest guest.Guest `json:"target_guest"`
}

func New(_name string, guest guest.Guest) Migrate {
	return Migrate{Name: _name, TargetGuest: guest}
}
