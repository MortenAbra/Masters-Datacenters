package drlogger

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"time"
)

type LogType string

const (
	ERR  LogType = "ERR"
	WARN LogType = "WARN"
	INFO LogType = "INFO"
)

const LogFileName = "drguest.log"

func InitDRLogger() {
	f, err := os.OpenFile(LogFileName, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Fatal(err)
	}

	defer f.Close()
}

func DRLog(logType LogType, line string) {
	// Find logfile (Should exists from createfile in main)
	file, err := os.OpenFile(LogFileName, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		log.Fatal(err)
	}
	// Close the file at the end
	defer file.Close()

	// Get current time
	now := time.Now()
	timeStamp := now.Format(time.StampMilli)

	// Compile lineToWrite
	var lineToWrite = "[" + string(logType) + "] " + timeStamp + ":" + line

	// Write to file
	w := bufio.NewWriter(file)
	fmt.Fprintln(w, lineToWrite)
	w.Flush()
}

func GetLogsScanner() (bufio.Scanner, error) {
	file, err := os.Open(LogFileName)
	if err != nil {
		panic(err)
	}

	defer file.Close()
	scanner := bufio.NewScanner(file)

	return *scanner, err
}
