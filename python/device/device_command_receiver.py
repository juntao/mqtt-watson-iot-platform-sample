import wiotp.sdk.device
import time
import json
import signal
import sys
import logging

def myCommandCallback(cmd):
    print("Command received: %s" % cmd.data)

def interruptHandler(signal, frame):
    client.disconnect()
    sys.exit(0)

if __name__ == "__main__":
    signal.signal(signal.SIGINT, interruptHandler)

    options = wiotp.sdk.device.parseConfigFile("device_4321.yaml")
    client = wiotp.sdk.device.DeviceClient(options)

    client.logger.setLevel(logging.DEBUG)
    client.connect()
    client.commandCallback = myCommandCallback
    
    while True:
        time.sleep(1)
