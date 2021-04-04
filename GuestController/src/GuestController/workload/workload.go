package workload

type Workload struct {
	Identifier string `json:"Identifier"`
	AccessIP   string `json:"AccessIP"`
	AccessPort string `json:"AccessPort"`
	Available  bool   `json:"Available"`
	SharedDir  string `json:"SharedDir"`
	Type       string `json:"Type"`
}
