import wiotp.sdk.device

options = wiotp.sdk.device.parseConfigFile("device_4321.yaml")
client = wiotp.sdk.device.DeviceClient(options)

def eventPublishCallback():
    print("Device Publish Event done!!!")

client.connect()
myData={'mesg' : 'Hello World'}
client.publishEvent("status", "json", myData, 0, eventPublishCallback)
client.disconnect()
