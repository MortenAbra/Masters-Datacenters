package timerresponse

import "time"

type TimerResponse struct {
	Disc    time.Duration `json:"Disc"`
	CPU     time.Duration `json:"CPU"`
	Memory  time.Duration `json:"Memory"`
	Network time.Duration `json:"Network"`
}

func (timer TimerResponse) GetTotalTime() time.Duration {
	return timer.Disc + timer.CPU + timer.Memory + timer.Network
}
