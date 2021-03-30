package guest

type Guest struct {
	IP   string `json:"ip"`
	Port string `json:"port"`
}

func New(_ip string, _port string) Guest {
	return Guest{IP: _ip, Port: _port}
}
