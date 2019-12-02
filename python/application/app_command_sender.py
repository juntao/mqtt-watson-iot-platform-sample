import wiotp.sdk.application
import logging

options = wiotp.sdk.application.parseConfigFile("app_test2.yaml")
client = wiotp.sdk.application.ApplicationClient(options)

client.logger.setLevel(logging.DEBUG)
client.connect()
myData={'rebootDelay' : 50}
client.publishCommand("test", "4321", "reboot", "json", myData)
client.disconnect()
