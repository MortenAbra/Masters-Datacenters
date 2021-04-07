package migrate

import (
	"GuestController/guest"
)

type Migrate struct {
	Identifier  string      `json:"Identifier"`
	TargetGuest guest.Guest `json:"TargetGuest"`
}
