import wiotp.sdk.application

options = wiotp.sdk.application.parseConfigFile("app_test1.yaml")
client = wiotp.sdk.application.ApplicationClient(options)

def eventPublishCallback():
    print("Device Publish Event done!!!")

client.connect()
myData={'mesg' : 'Hello World'}
client.publishEvent("test", "4321", "status", "json", myData, 0, eventPublishCallback)
client.disconnect()
