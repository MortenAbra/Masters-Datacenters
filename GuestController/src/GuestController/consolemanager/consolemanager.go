package consolemanager

import (
	"bufio"
	"fmt"
	"os"
	"strings"
)

// Read next console line as user input
func readResonse() (string, error) {
	reader := bufio.NewReader(os.Stdin)
	text, err := reader.ReadString('\n')

	text = strings.Replace(text, "\n", "", -1)
	return text, err
}

// Get user input for server ip and server port to run the service on and return them
func ReadServerIPAndPortFromUser() (string, string, string, error) {
	fmt.Print("Please enter IP: ")
	serverIP, err := readResonse()
	if err != nil {
		return "", "", "", err
	}

	fmt.Print("Please enter port: ")
	serverPort, err := readResonse()
	if err != nil {
		return "", "", "", err
	}

	fmt.Print("Please enter shared directory with host: ")
	serverSharedDir, err := readResonse()

	// TODO TEMPORARY WHILE TESTING
	serverSharedDir = "/home/wolder/shared_storage"

	return serverIP, serverPort, serverSharedDir, err
}
