import wiotp.sdk.application
import time
import json
import signal
import sys
import logging

def myEventCallback(event):
    str = "%s event '%s' received from device [%s]: %s"
    print(str % (event.format, event.eventId, event.device, json.dumps(event.data)))

def interruptHandler(signal, frame):
    client.disconnect()
    sys.exit(0)

if __name__ == "__main__":
    signal.signal(signal.SIGINT, interruptHandler)

    options = wiotp.sdk.application.parseConfigFile("app_test2.yaml")
    client = wiotp.sdk.application.ApplicationClient(options)

    # client.logger.setLevel(logging.DEBUG)
    client.connect()
    client.deviceEventCallback = myEventCallback
    client.subscribeToDeviceEvents()

    while True:
        time.sleep(1)
