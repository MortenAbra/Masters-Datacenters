package guest

type Guest struct {
	ip   string
	port string
}

func New(_ip string, _port string) Guest {
	return Guest{ip: _ip, port: _port}
}
