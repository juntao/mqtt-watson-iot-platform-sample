package com.ringful.dw.iot.application;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.iotf.client.app.ApplicationClient;
import com.ibm.iotf.client.app.Command;
import com.ibm.iotf.client.app.Event;
import com.ibm.iotf.client.app.EventCallback;

import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SampleApp {

    public static void main(String[] args) throws Exception {
        Properties options = new Properties();
        options.put("org", args[0]);
        options.put("id", args[1]);
        options.put("Authentication-Method","apikey");
        options.put("API-Key", args[2]);
        options.put("Authentication-Token", args[3]);

        // new SampleApp ();

        System.out.println("Starting IoT App");
        ApplicationClient myClient = new ApplicationClient(options);
        myClient.connect();

        DeviceEventHandler handler = new DeviceEventHandler();
        handler.setClient(myClient);

        myClient.setEventCallback(handler);
        myClient.subscribeToDeviceEvents();

        // Run the event processing thread
        Thread thread = new Thread(handler);
        thread.start();
    }

    public SampleApp () throws Exception {
    }
}

class DeviceEventHandler implements EventCallback, Runnable {

    private ApplicationClient client;

    // A queue to hold and process the Events for smooth handling of MQTT messages
    // as some events may take a long time to process
    private BlockingQueue<Event> evtQueue = new LinkedBlockingQueue<Event>();

    public void processEvent(Event e) {
        System.out.println("Event received:: " + e);
        try {
            evtQueue.put(e);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void processCommand(Command cmd) {
        System.out.println("Command received:: " + cmd);
    }

    @Override
    public void run() {
        while(true) {
            Event e = null;
            try {
                e = evtQueue.take();
                System.out.println("Event:: " + e.getDeviceId() + ":" + e.getEvent() + ":" + e.getPayload());

                // Check count value
                JsonParser jsonParser = new JsonParser();
                JsonElement ele = jsonParser.parse(e.getPayload());

                int count = ele.getAsJsonObject().get("count").getAsInt();
                System.out.println("Receive count : " + count);

                if (count >= 4) {
                    // Send "restart" command back to this device
                    JsonObject data = new JsonObject();
                    data.addProperty("name", "restart-counter");
                    data.addProperty("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                    client.publishCommand(e.getDeviceType(), e.getDeviceId(), "restart", data);
                }

            } catch (InterruptedException e1) {
                // Ignore the Interuppted exception, retry
                continue;
            }
        }
    }

    public ApplicationClient getClient() {
        return client;
    }
    public void setClient(ApplicationClient client) {
        this.client = client;
    }
}
