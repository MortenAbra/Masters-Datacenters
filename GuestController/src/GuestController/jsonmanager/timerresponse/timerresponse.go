package timerresponse

type TimerResponse struct {
	Disc    int64 `json:"Disc"`
	CPU     int64 `json:"CPU"`
	Memory  int64 `json:"Memory"`
	Network int64 `json:"Network"`
}
