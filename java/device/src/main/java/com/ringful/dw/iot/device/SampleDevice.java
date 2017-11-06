package com.ringful.dw.iot.device;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.iotf.client.device.CommandCallback;
import com.ibm.iotf.client.device.Command;
import com.ibm.iotf.client.device.DeviceClient;

import java.util.Properties;
import java.util.concurrent.*;

public class SampleDevice {

    public static int count = 0;
    private static DeviceClient myClient;

    public static void main(String[] args) throws Exception {
        Properties options = new Properties();
        options.put("org", args[0]);
        options.put("type", args[1]);
        options.put("id", args[2]);
        options.setProperty("auth-method", "token");
        options.setProperty("auth-token", args[3]);

        System.out.println("Starting IoT device");

        try {
            myClient = new DeviceClient(options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        AppCommandHandler handler = new AppCommandHandler();
        myClient.setCommandCallback(handler);
        myClient.connect();

        // Run the event processing thread
        Thread thread = new Thread(handler);
        thread.start();

        Runnable countRunnable = new Runnable() {
            public void run() {
                JsonObject event = new JsonObject();
                event.addProperty("count", count);
                myClient.publishEvent("status", event);
                System.out.println("Posted event " + event.toString());

                count++;
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(countRunnable, 0, 5, TimeUnit.SECONDS);
    }

    public SampleDevice () throws Exception {
    }
}

class AppCommandHandler implements CommandCallback, Runnable {

    // A queue to hold and process the Events for smooth handling of MQTT messages
    // as some commands may take a long time to process
    private BlockingQueue<Command> cmdQueue = new LinkedBlockingQueue<Command>();

    @Override
    public void processCommand(Command c) {
        try {
            cmdQueue.put(c);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true) {
            Command c = null;
            try {
                c = cmdQueue.take();
                System.out.println("Command:: " + c.getCommand() + ":" + c.getPayload());

                JsonParser jsonParser = new JsonParser();
                JsonElement ele = jsonParser.parse(c.getPayload());

                if ("restart-counter".equalsIgnoreCase(ele.getAsJsonObject().get("name").getAsString())) {
                    SampleDevice.count = 0;
                }

            } catch (InterruptedException e1) {
                // Ignore the Interuppted exception, retry
                continue;
            }
        }
    }
}
